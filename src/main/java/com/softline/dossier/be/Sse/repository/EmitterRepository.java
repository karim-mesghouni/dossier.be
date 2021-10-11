package com.softline.dossier.be.Sse.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface EmitterRepository
{

    Long addEmitter(Long agentId);

    void remove(Long agentId, Long sessionId);

    void remove(Long agentId, SseEmitter emitter);

    Stream<Stream<SseEmitter>> getAll();

    Optional<SseEmitter> get(Long agentId, Long sessionId);

    Optional<List<SseEmitter>> get(Long agentId);

}
