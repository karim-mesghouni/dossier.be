package com.softline.dossier.be.Sse.service;

import com.softline.dossier.be.Sse.model.Event;
import com.softline.dossier.be.Sse.repository.EmitterRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SseNotificationService implements NotificationService
{
    @Autowired
    EmitterRepository emitterRepository;

    public void sendNotification(Long agentId, Event event)
    {
        if (event == null) {
            return;
        }
        doSendNotification(agentId, event);
    }

    @Override
    public void sendNotificationForAll(Event event)
    {
        emitterRepository.getAll().forEach(x ->
                {
                    x.forEach(emitter ->
                            {
                                try {
                                    emitter.send(SseEmitter.event()
                                            .id(RandomStringUtils.randomAlphanumeric(12))
                                            .name(event.getName())
                                            .data(event.getPayloadJson()));
                                } catch (Exception Exception) {
                                    emitter.complete();
                                }
                            }
                    );
                }
        );
    }

    private void doSendNotification(Long agentId, Event event)
    {
        emitterRepository.get(agentId).ifPresent(x -> x.forEach(e ->
        {

            try {
                e.send(SseEmitter.event()
                        .id(RandomStringUtils.randomAlphanumeric(12))
                        .name(event.getName())
                        .data(event.getPayloadJson()));
            } catch (Exception Exception) {
                e.complete();
            }

        }));
    }
}
