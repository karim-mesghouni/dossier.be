package com.softline.dossier.be.Sse.controller;

import com.softline.dossier.be.Sse.service.EmitterService;
import com.softline.dossier.be.Sse.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController
{
    public static final String MEMBER_ID_HEADER = "MemberId";

    private final EmitterService emitterService;
    private final NotificationService notificationService;

    @GetMapping(value = "/{agentId}/{sessionId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8")
    public SseEmitter subscribeToEvents(@PathVariable Long agentId, @PathVariable Long sessionId)
    {
        var emitter = emitterService.getEmitter(agentId, sessionId);
        if (emitter.isPresent()) {
            return emitter.get();
        }
        return null;
    }

    @GetMapping(value = "/create/{agentId}")
    public Long createEventSource(@PathVariable Long agentId)
    {
        return emitterService.createEmitter(agentId);
    }

    @GetMapping(value = "/clsoe/{agentId}/{sessionId}")
    public boolean close(@PathVariable Long agentId, @PathVariable Long sessionId)
    {
        emitterService.close(agentId, sessionId);
        return true;
    }
 /*   @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void publishEvent(@RequestHeader(name = MEMBER_ID_HEADER) String memberId, @RequestBody EventDto event) throws IOException {
        notificationService.sendNotification(memberId, event);
    }/*/
}
