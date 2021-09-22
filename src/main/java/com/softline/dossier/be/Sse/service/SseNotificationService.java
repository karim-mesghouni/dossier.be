package com.softline.dossier.be.Sse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softline.dossier.be.Sse.model.EventDto;
import com.softline.dossier.be.Sse.repository.EmitterRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
public class SseNotificationService implements NotificationService {
    @Autowired
    EmitterRepository emitterRepository;

    public void sendNotification(Long agentId, EventDto event) throws IOException {
        if (event == null) {
            return;
        }
        doSendNotification(agentId, event);
    }

    @Override
    public void sendNotificationForAll(EventDto event) throws IOException {
        emitterRepository.getAll().forEach(x -> {

                    x.forEach(emitter -> {
                                try {
                                    emitter.send(SseEmitter.event()
                                            .id(RandomStringUtils.randomAlphanumeric(12))
                                            .name(event.getType())
                                            .data(new ObjectMapper().writeValueAsString(event.getBody())));
                                } catch (Exception Exception) {
                                    emitter.complete();
                                }
                            }
                    );
                }
        );
    }

    private void doSendNotification(Long agentId, EventDto event) {
        emitterRepository.get(agentId).ifPresent(x -> x.forEach(e -> {

            try {
                e.send(SseEmitter.event()
                        .id(RandomStringUtils.randomAlphanumeric(12))
                        .name(event.getType())
                        .data(new ObjectMapper().writeValueAsString(event.getBody())));
            } catch (Exception Exception) {
                e.complete();
            }

        }));
    }
}
