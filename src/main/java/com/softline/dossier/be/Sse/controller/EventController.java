package com.softline.dossier.be.Sse.controller;

import com.softline.dossier.be.Sse.Channel;
import com.softline.dossier.be.Sse.model.Event;
import com.softline.dossier.be.security.domain.Agent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.softline.dossier.be.Halpers.Functions.tap;

@RestController
@RequestMapping("/events")
@Slf4j(topic = "SSE")
public class EventController
{
    // used ConcurrentHashMap instead of normal Hashmap
    // because HashMap Iterators don't support modifying(removing) the items outside the iterator itself
    private static final ConcurrentHashMap <Channel, SseEmitter> channels = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService pingThread = Executors.newScheduledThreadPool(1);

    public EventController()
    {
        // to keep the connection alive in the client side
        // we must send at least 1 event every 45 seconds,
        // so we create a single thread which will send a ping(heart-beat) event
        // every 30 seconds
        pingThread.scheduleAtFixedRate(() ->
        {
            log.info("Sending heart-beat signal for all channels");
            EventController.sendForAllChannels(new Event("ping", System.currentTimeMillis()));
        }, 30, 30, TimeUnit.SECONDS);
    }

    @GetMapping(value = "/")
    public SseEmitter getEvents()
    {
        var agent = (Agent) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final Channel channel = new Channel((long) Math.floor(Math.random() * 1_000_000), agent.getId());
        // create new sse emitter with timeout of 1 hour
        // this will delete his linked channel emitter
        // and force the client to reconnect again
        SseEmitter emitter = tap(new SseEmitter(1000 * 60 * 60L),
                em -> em.onCompletion(() ->
                {
                    // will be called if the client closed the connection (browser tap closed)
                    // or of channel timeout was reached
                    log.info("emitter complete was called, removing channel: {}", channel);
                    synchronized (channels) {
                        channels.remove(channel);
                    }
                }),
                // will only happen if the channel was opened for more than 1 hour (previous timeout value)
                em -> em.onTimeout(() ->log.warn("emitter timeout for channel: {}", channel)),
                // called on event send error
                em -> em.onError(err -> log.error("emitter error for channel: {}", channel)));
        synchronized (channels) {
            channels.putIfAbsent(channel, emitter);
        }
        log.info("created new emitter for channel: {}", channel);
        return emitter;
    }

    /**
     * send the event for all registered channels
     */
    public static void sendForAllChannels(Event event)
    {
        synchronized (channels) {
            log.info("sendEventForAll, event : {}", event);
            channels.forEach((channel, em) -> internalSendForEmitter(em, event, channel));
        }
    }

    /**
     * used internally to send an event to a single channel
     */
    private static void internalSendForEmitter(SseEmitter emitter, Event event, Channel channel)
    {
        synchronized (channels) {
            try {
                emitter.send(SseEmitter.event().name(event.getName()).data(event.getPayload()));
                log.info("sendEventForChannel() sent data to channel: {}, event is: {}", channel, event);
            } catch (Throwable e) {
                log.info("sendEventForChannel() data not sent due to error, channel: {}, event is: {}", channel, event);
                emitter.complete();
            }
        }
    }

    /**
     * send an event to all channels opened by the user
     * @param agentId the id of the user
     */
    public static void sendForUser(long agentId, Event event)
    {
        synchronized (channels) {
            channels.forEach((channel, em) -> {
                if (channel.agentId == agentId) {
                    internalSendForEmitter(em, event, channel);
                }
            });
        }
    }
}
