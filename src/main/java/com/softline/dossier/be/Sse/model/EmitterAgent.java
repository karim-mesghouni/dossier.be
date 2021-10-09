package com.softline.dossier.be.Sse.model;

import lombok.Data;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class EmitterAgent {
    List<EmitterItem> emitterSessions;
    Long agentId;

    public EmitterAgent(Long agentId) {
        this.agentId = agentId;
        emitterSessions = Collections.synchronizedList(new ArrayList<>());
    }

    private Long getNextSessionId() {
        return emitterSessions.stream().count() + 1;
    }

    public Long addEmitterSession(SseEmitter emitter) {
        var nextSessionId = getNextSessionId();
        emitterSessions.add(EmitterItem.builder().sessionId(nextSessionId).emitter(emitter).build());
        return nextSessionId;
    }

    public void removeEmitterSession(Long sessionId) {
        emitterSessions.removeIf(x -> x.getSessionId() == sessionId);
    }
}