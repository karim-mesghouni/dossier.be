package com.softline.dossier.be.Sse.repository;

import com.softline.dossier.be.Sse.model.EmitterAgent;
import com.softline.dossier.be.Sse.model.EmitterItem;
import com.softline.dossier.be.Sse.service.EmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class InMemoryEmitterRepository implements EmitterRepository {

    private List<EmitterAgent> emitters = new ArrayList<>();

    @Override
    public Long addEmitter(Long agentId) {

        var ifexistsEmitter = emitters.stream().filter(x->x.getAgentId()==agentId).findFirst();
        EmitterAgent existsEmitter=null;
        if (!ifexistsEmitter.isPresent()) {
            existsEmitter=new EmitterAgent(agentId);
            emitters.add(existsEmitter);
        }else
            existsEmitter=ifexistsEmitter.get();

        EmitterService.Utf8SseEmitter emitter = new EmitterService.Utf8SseEmitter(1000*60*5);
        var sessionId= existsEmitter.addEmitterSession(emitter);

        emitter.onCompletion(() ->
        {
            emitters.stream().filter(x->x.getAgentId()==agentId).findFirst().ifPresent(x->x.removeEmitterSession(sessionId));
        });
        emitter.onTimeout(() ->
        {
            emitters.stream().filter(x->x.getAgentId()==agentId).findFirst().ifPresent(x->x.removeEmitterSession(sessionId));
        }
        );
        emitter.onError(e -> {
            log.error("Create SseEmitter exception", e);
            emitters.stream().filter(x->x.getAgentId()==agentId).findFirst().ifPresent(x->x.removeEmitterSession(sessionId));
        });
        return  sessionId;
    }

    @Override
    public void remove(Long agentId,Long sessionId) {
        var existsEmitter=    get(agentId,sessionId);
            existsEmitter.ifPresent(x->x.complete());
    }

    @Override
    public void remove(Long agentId, SseEmitter emitter) {
        emitters.stream().filter(x->x.getAgentId()==agentId).findFirst().ifPresent(x->{
            x.getEmitterSessions().removeIf(ei->ei.getEmitter().equals(emitter));
        });
    }

    @Override
    public Optional<SseEmitter> get(Long agentId,Long sessionId) {
        var existsEmitter = emitters.stream().filter(x->x.getAgentId()==agentId).findFirst();
        if(existsEmitter.isPresent()){
          return   existsEmitter.get().getEmitterSessions().stream().filter(x->x.getSessionId()==sessionId).findFirst().map(x->x.getEmitter());
        }
        return  Optional.empty();
    }

    @Override
    public Optional<List<SseEmitter>> get(Long agentId) {
        var existsEmitter = emitters.stream().filter(x->x.getAgentId()==agentId).findFirst();

        if(existsEmitter.isPresent()){
            return  Optional.ofNullable(existsEmitter.get().getEmitterSessions().stream().map(x->x.getEmitter()).collect(Collectors.toList()));
        }
        return  Optional.empty();
    }
}
