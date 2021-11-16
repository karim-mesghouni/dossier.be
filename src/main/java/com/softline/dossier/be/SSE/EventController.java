package com.softline.dossier.be.SSE;

import com.softline.dossier.be.config.Beans;
import com.softline.dossier.be.events.Event;
import com.softline.dossier.be.security.domain.Agent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.softline.dossier.be.Tools.Database.unsafeEntityManager;
import static com.softline.dossier.be.Tools.Functions.tap;
import static com.softline.dossier.be.security.domain.Agent.notLoggedIn;

@RestController
@RequestMapping("/events")
@Slf4j(topic = "SSE")
public class EventController {
    // used ConcurrentHashMap instead of normal Hashmap
    // because HashMap Iterators don't support modifying(removing) the items outside the iterator itself
    private static final ConcurrentHashMap<Channel, SseEmitter> channels = new ConcurrentHashMap<>();
    // sometimes the SseEmitter.complete() action does not get executed,
    // so we will manually clean the emitter in such case by putting in a removal queue
    private static final ConcurrentLinkedDeque<Channel> scheduledForRemoval = new ConcurrentLinkedDeque<>();
    private static final AtomicBoolean silentModeActive = new AtomicBoolean();

    /**
     * start the ping scheduler
     *
     * @see Beans#scheduler()
     */
    public EventController(ThreadPoolTaskScheduler scheduler) {
        // to keep the connection alive in the client side
        // we must send at least 1 event every 45 seconds,
        // so we create a single thread which will send a ping(heart-beat) event
        // to all open channels every 30 seconds
        log.info("starting ping thread");
        scheduler.scheduleAtFixedRate(() ->
        {
            synchronized (channels) {// obtain lock
                // remove any scheduled for removal channels
                synchronized (scheduledForRemoval) {// obtain lock
                    if (scheduledForRemoval.size() > 0) {
                        scheduledForRemoval.forEach(ch -> {
                            if (channels.containsKey(ch)) {
                                log.info("Removing scheduled channel removal {}", ch);
                                channels.remove(ch);
                            }
                        });
                        scheduledForRemoval.clear();
                    }
                }
                if (channels.isEmpty()) {
                    log.info("didnt send heart-beat signal, no channel is connected");
                    return;
                }
            }
            log.info("Sending heart-beat signal for all channels");
            Event.pingEvent().fireToAll();
        }, Instant.now(), Duration.ofSeconds(30));
    }

    /**
     * used internally to send an event to a single channel
     */
    private static void internalSendForEmitter(SseEmitter emitter, Event<?> event, Channel channel) {
        synchronized (channels) {// obtain lock
            try {
                channel.setLastEvent(event);
                emitter.send(SseEmitter.event().name(event.getType()).data(event.getData()));
                log.info("sent {} to {}", event, channel);
            } catch (Throwable e) {
                if (!Event.pingEvent().equals(event)) {
                    log.error("{} was not sent to {} because of [{}], adding the channel to the clean queue", event, channel, e.getMessage());
                } else {
                    log.info("{} was not sent to {} because of [{}], adding the channel to the clean queue", event, channel, e.getMessage());
                }
                emitter.complete();
                // calling emitter.complete() after "event send failure" (not a network error) has no effect,
                // so we will ensure that the channel gets removed from the list and no further events will be sent to it
                // the emitter will be auto-cleaned after the timeout reaches (because we will stop sending events to it)
                synchronized (scheduledForRemoval) {// obtain lock
                    scheduledForRemoval.add(channel);
                }
            }
        }
    }

    /**
     * send the event for all registered channels
     */
    public static void sendForAllChannels(Event<?> event) {
        if (silentModeActive.get() && !Event.pingEvent().equals(event)) {
            log.info("silent mode is active {}, send to [all] was discarded", event);
            return;
        }
        synchronized (channels) {// obtain lock
            log.info("sendEventForAll: {}", event);
            channels.forEach((channel, em) -> {
                if (channel.canRead(event))
                    internalSendForEmitter(em, event, channel);
            });
        }
    }

    /**
     * send an event to all channels opened by the user
     *
     * @param userId the id of the user
     */
    public static void sendForUser(long userId, Event<?> event) {
        if (silentModeActive.get()) {
            log.info("silent mode is active, send {} to [user:{}] was discarded", event, userId);
            return;
        }
        synchronized (channels) {// obtain lock
            channels.forEach((channel, em) ->
            {
                if (channel.userId == userId && channel.canRead(event)) {
                    internalSendForEmitter(em, event, channel);
                }
            });
        }
    }

    private static void activateSilentMode() {
        silentModeActive.set(true);
        log.info("silent mode is now active");
    }

    private static void deactivateSilentMode() {
        silentModeActive.set(false);
        log.info("silent mode is now inactive");
    }

    /**
     * force any pending changes on this hibernate session (current request)
     */
    private static void flushPendingDatabaseChanges() {
        var em = unsafeEntityManager();
        if (em != null) {
            em.flush();
            em.clear();
        }
    }

    /**
     * Run the action and discard any events that fired inside the action
     */
    public static void silently(Runnable action) {
        activateSilentMode();
        action.run();
        flushPendingDatabaseChanges();
        deactivateSilentMode();
    }

    /**
     * Run the action and discard any events that fired inside the action, returns the value returned from the action
     *
     * @throws RuntimeException if the action failed to perform
     */
    public static <T> T silently(Callable<T> action) throws RuntimeException {
        activateSilentMode();
        T returning;
        try {
            returning = action.call();
        } catch (Exception e) {
            log.error("[SILENTLY] {}", e.getMessage());
            deactivateSilentMode();
            throw new RuntimeException(e);
        }
        flushPendingDatabaseChanges();
        return returning;
    }

    /**
     * subscribe the request to a sse channel and keep the connection open
     */
    @GetMapping(value = "/")
    public SseEmitter subscribe() {
        if (notLoggedIn()) {
            log.error("attempt to listen for events without login");
            return null;
        }
        var agent = (Agent) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final Channel channel = new Channel((long) Math.floor(Math.random() * 1_000_000), agent.getId());
        log.info("creating new emitter for {}", channel);
        // create new sse emitter with timeout of 1 hour
        // this will delete his linked channel emitter
        // and force the client to reconnect again
        // !! DO NOT OPEN AN SSE CHANNEL WITHOUT SETTING A TIMEOUT, IT IS VERY IMPORTANT FOR AUTO-CLEANING
        return tap(new SseEmitter(1000 * 60 * 60L),
                em -> em.onCompletion(() -> {
                    // will be called if the client closed the connection (browser tap closed)
                    // or if channel timeout was reached
                    log.info("emitter complete was called, removing {}", channel);
                    synchronized (channels) {// obtain lock
                        channels.remove(channel);
                    }
                }),
                // will only happen if the channel was opened for more than 1 hour (previous timeout value)
                em -> em.onTimeout(() -> log.info("emitter timeout for {}", channel)),
                // called on event send error
                em -> em.onError(err -> {
                    if (!Objects.equals(channel.getLastEvent(), Event.pingEvent())) {
                        log.error("emitter error for {} last {} {}", channel, channel.getLastEvent(), err.toString());
                    } else {
                        log.info("emitter error for {} last {} {}", channel, channel.getLastEvent(), err.toString());
                    }
                }),
                em -> {
                    synchronized (channels) {
                        channels.putIfAbsent(channel, em);
                    }
                });
    }
}
