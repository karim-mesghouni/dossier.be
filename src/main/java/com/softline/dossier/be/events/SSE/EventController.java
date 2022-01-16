package com.softline.dossier.be.events.SSE;

import com.softline.dossier.be.domain.Concerns.HasId;
import com.softline.dossier.be.events.Event;
import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.security.repository.AgentRepository;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/events")
@Slf4j(topic = "SSE")
public class EventController {
    // !! SET LOGGING MODE TO DEBUG TO SEE ALL DEBUGGING MESSAGES FROM THIS CONTROLLER !!
    // we keep track of registered channels so that later we can send events to these channels

    private static final List<Channel> channels = Collections.synchronizedList(new ArrayList<>());
    // sometimes the SseEmitter.complete() action does not get executed,
    // so we will manually clean the channel in such case by putting it in this removal queue
    // and the ping task will clean this queue after sending the ping events
    private static final List<Channel> scheduledForRemoval = Collections.synchronizedList(new ArrayList<>());
    // all event send operations during when silent mode is active should be discarded and dismissed
    private static final AtomicBoolean silentModeActive = new AtomicBoolean();// initialValue: false

    /**
     * start this controller and start the ping scheduler
     *
     * @see com.softline.dossier.be.config.Beans#scheduler()
     */
    public EventController(ThreadPoolTaskScheduler scheduler, AgentRepository repo, PasswordEncoder encoder) {
        // to keep the connection alive in the client side
        // we must send at least 1 event every 45 seconds,
        // so we create a scheduled task which will be executed every 30 seconds to
        // send a ping(heart-beat) event to all channels
        if (log.isDebugEnabled())
            log.debug("creating ping schedule");
        scheduler.scheduleAtFixedRate(() ->
        {
            // remove any scheduled for removal channels
            if (scheduledForRemoval.size() > 0) {
                scheduledForRemoval.forEach(ch -> {
                    if (channels.contains(ch)) {
                        if (log.isDebugEnabled())
                            log.debug("Removing scheduled channel removal {}", ch);
                        channels.remove(ch);
                    }
                });
                scheduledForRemoval.clear();
            }
            if (channels.isEmpty()) {
                if (log.isDebugEnabled())
                    log.debug("didnt send heart-beat signal, no channel is connected");
                return;
            }
            if (log.isDebugEnabled())
                log.debug("Sending heart-beat signal for all channels");
            Event.pingEvent().fireToAll();
        }, Instant.now(), Duration.ofSeconds(30));
    }

    /**
     * used internally to send an event to a single channel
     */
    private static void sendForChannel(Channel channel, Event<?> event) {
        try {
            channel.setLastEvent(event);
            channel.send(SseEmitter.event().name(event.getType()).data(event.getData()));
            if (log.isDebugEnabled())
                log.debug("sent {} to {}", event, channel);
        } catch (Throwable e) {
            if (log.isDebugEnabled()) {
                log.debug("{} was not sent to {} because of [{}]", event, channel, e.getMessage());
            } else if (!Event.pingEvent().equals(event)
                    && (e.getMessage().contains("An established connection was aborted by the software in your host machine") || e.getMessage().contains("Broken pipe"))) {
                if (log.isWarnEnabled())
                    log.warn("{} was not sent to {} because of [{}]", event, channel, e.getMessage());
            } else if (!Event.pingEvent().equals(event)) {
                if (log.isErrorEnabled())
                    log.error("{} was not sent to {} because of [{}]", event, channel, e.getMessage());
            }
            if (log.isDebugEnabled())
                log.debug("adding {} to the clean queue", channel);
            channel.complete();
            // calling channel.complete() after "event send failure" (not a network error) has no effect,
            // so we will ensure that the channel gets removed from the list and no further events will be sent to it
            // the channel will be auto-cleaned after the timeout reaches (because we will stop sending events to it)
            scheduledForRemoval.add(channel);
        }
    }

    /**
     * send the event for all registered channels
     */
    public static void sendForAllChannels(Event<?> event) {
        if (silentModeActive.get() && !Event.pingEvent().equals(event)) {
            if (log.isDebugEnabled())
                log.debug("silent mode is active {} was discarded", event);
            return;
        }
        if (!event.equals(Event.pingEvent()) || log.isInfoEnabled())
            log.info("Sending {} to {} channel{}", event, channels.size(), channels.size() == 1 ? "" : 's');
        channels.forEach(channel -> {
            if (channel.canRead(event))
                sendForChannel(channel, event);
        });
    }

    /**
     * send an event to all channels opened by the user
     *
     * @param userId the id of the user
     */
    public static void sendForUser(long userId, Event<?> event) {
        if (silentModeActive.get()) {
            if (log.isDebugEnabled())
                log.debug("silent mode is active, send {} to user {} was discarded", event, userId);
            return;
        }
        if (log.isDebugEnabled())
            log.debug("Sending {} to user {}", event, userId);
        channels.forEach(channel -> {
            if (channel.user.getId() == userId && channel.canRead(event)) {
                sendForChannel(channel, event);
            }
        });
    }

    /**
     * send an event to all channels opened by the user
     */
    public static void sendForUser(HasId user, Event<?> event) {
        sendForUser(user.getId(), event);
    }

    private static void activateSilentMode() {
        silentModeActive.set(true);
        if (log.isDebugEnabled())
            log.debug("silent mode is now active");
    }

    private static void deactivateSilentMode() {
        silentModeActive.set(false);
        if (log.isDebugEnabled())
            log.debug("silent mode is now inactive");
    }

    /**
     * Run the action and discard any events that fired inside the action<br>*
     *
     * @return the value returned from the action
     * @throws RuntimeException if the action failed to perform
     */
    public static <T> T silently(Callable<T> action) throws RuntimeException {
        activateSilentMode();
        T returning;
        try {
            returning = action.call();
        } catch (Exception e) {
            if (log.isErrorEnabled())
                log.error("[SILENTLY] {}", e.getMessage());
            deactivateSilentMode();
            throw new RuntimeException(e);
        }
        deactivateSilentMode();
        return returning;
    }

    /**
     * Run the action and discard any events that fired inside the action<br>
     */
    public static void silently(@NotNull Runnable action) {
        activateSilentMode();
        try {
            action.run();
        } catch (Throwable e) {
            deactivateSilentMode();
            throw new RuntimeException(e);
        }
        deactivateSilentMode();
    }

    /**
     * subscribe the request to a sse channel and keep the connection open
     */
    @GetMapping(value = "/")
    public Channel subscribe() {
        if (Agent.notLoggedIn()) {
            if (log.isErrorEnabled())
                log.error("attempt to listen for events without login");
            return null;
        }
        // create new sse channel for this user with a timeout of 1 hour
        // this will delete the channel after 1 hour
        // and force the client to reconnect again
        // !! DO NOT OPEN AN SSE CHANNEL WITHOUT SETTING A TIMEOUT, IT IS VERY IMPORTANT FOR AUTO-CLEANING
        var ch = new Channel(Agent.thisAgent());
        ch.onCompletion(() -> {
            // will be called if the client closed the connection (browser tap closed)
            // or if channel timeout was reached
            if (log.isDebugEnabled())
                log.debug("channel complete was called, removing {}", ch);
            channels.remove(ch);
        });
        // will only happen if the channel was opened for more than 1 hour (previous timeout value)
        ch.onTimeout(() -> {
            if (log.isDebugEnabled())
                log.debug("timeout reached for {}", ch);
        });
        // called on event send error
        ch.onError(err -> {
            if (!Objects.equals(ch.getLastEvent(), Event.pingEvent()) && log.isErrorEnabled()) {
                log.error("channel error for {} last {} [{}]", ch, ch.getLastEvent(), err.toString());
            }
        });
        if (!channels.contains(ch)) {
            channels.add(ch);
        }
        return ch;
    }
}
