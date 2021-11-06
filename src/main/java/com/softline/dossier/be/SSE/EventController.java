package com.softline.dossier.be.SSE;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.softline.dossier.be.events.types.Event;
import com.softline.dossier.be.security.domain.Agent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.*;

import static com.softline.dossier.be.Halpers.Functions.tap;

@RestController
@RequestMapping("/events")
@Slf4j(topic = "SSE")
public class EventController {
    private static final ScheduledExecutorService pingThread = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().setNameFormat("sse-ping-thread").build());
    // used ConcurrentHashMap instead of normal Hashmap
    // because HashMap Iterators don't support modifying(removing) the items outside the iterator itself
    private static final ConcurrentHashMap<Channel, SseEmitter> channels = new ConcurrentHashMap<>();
    // sometimes the SseEmitter.complete() action does not get executed
    // so we will manually clean the emitter in suck case by putting in a removal queue
    private static final ConcurrentLinkedDeque<Channel> scheduledForRemoval = new ConcurrentLinkedDeque<>();
    private static boolean threadIsRunning = false;

    public EventController() {
        // to keep the connection alive in the client side
        // we must send at least 1 event every 45 seconds,
        // so we create a single thread which will send a ping(heart-beat) event
        // every 30 seconds
        if (!threadIsRunning) {
            log.info("starting pingThread");
            pingThread.scheduleAtFixedRate(() ->
            {
                synchronized (channels) {// obtain lock
                    // remove any scheduled for removal channels
                    synchronized (scheduledForRemoval) {// obtain lock
                        if (scheduledForRemoval.size() > 0) {
                            scheduledForRemoval.forEach(ch -> {
                                if (channels.containsKey(ch)) {
                                    log.info("Removing scheduled channel removal, channel: {}", ch);
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
                EventController.sendForAllChannels(new Event<>("ping", System.currentTimeMillis()));
            }, 30, 30, TimeUnit.SECONDS);
            threadIsRunning = true;
        }
    }

    /**
     * send the event for all registered channels
     */
    public static void sendForAllChannels(Event<?> event) {
        synchronized (channels) {// obtain lock
            log.info("sendEventForAll, event : {}", event);
            channels.forEach((channel, em) -> internalSendForEmitter(em, event, channel));
        }
    }

    /**
     * used internally to send an event to a single channel
     */
    private static void internalSendForEmitter(SseEmitter emitter, Event<?> event, Channel channel) {
        synchronized (channels) {// obtain lock
            try {
                emitter.send(SseEmitter.event().name(event.getEvent()).data(event.getPayloadJson()));
                log.info("sendEventForChannel() sent data to channel: {}, event is: {}", channel, event);
            } catch (Throwable e) {
                log.info("sendEventForChannel() data not sent due to error: ({}), adding the channel to the clean queue, channel: {}, event is: {}", e.getMessage(), channel, event);
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
     * send an event to all channels opened by the user
     *
     * @param agentId the id of the user
     */
    public static void sendForUser(long agentId, Event<?> event) {
        synchronized (channels) {// obtain lock
            channels.forEach((channel, em) ->
            {
                if (channel.agentId == agentId) {
                    internalSendForEmitter(em, event, channel);
                }
            });
        }
    }

    @GetMapping(value = "/")
    public SseEmitter getEvents() {
        if (!(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Agent)) {
            log.error("attempt to listen for events without login");
            return null;
        }
        var agent = (Agent) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final Channel channel = new Channel((long) Math.floor(Math.random() * 1_000_000), agent.getId());
        // create new sse emitter with timeout of 1 hour
        // this will delete his linked channel emitter
        // and force the client to reconnect again
        // !! DO NOT OPEN AN SSE CHANNEL WITHOUT SETTING A TIMEOUT, IT IS VERY IMPORTANT FOR AUTO-CLEANING
        SseEmitter emitter = tap(new SseEmitter(1000 * 60 * 60L),
                em -> em.onCompletion(() ->
                {
                    // will be called if the client closed the connection (browser tap closed)
                    // or if channel timeout was reached
                    log.info("emitter complete was called, removing channel: {}", channel);
                    synchronized (channels) {// obtain lock
                        channels.remove(channel);
                    }
                }),
                // will only happen if the channel was opened for more than 1 hour (previous timeout value)
                em -> em.onTimeout(() -> log.warn("emitter timeout for channel: {}", channel)),
                // called on event send error
                em -> em.onError(err -> log.error("emitter error for channel: {}", channel)));
        synchronized (channels) {// obtain lock
            channels.putIfAbsent(channel, emitter);
        }
        log.info("created new emitter for channel: {}", channel);
        return emitter;
    }
}
