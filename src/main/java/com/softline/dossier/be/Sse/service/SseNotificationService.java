package com.softline.dossier.be.Sse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import com.softline.dossier.be.Sse.model.EventDto;
import com.softline.dossier.be.Sse.repository.EmitterRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
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

    private void doSendNotification(Long agentId, EventDto event)  {
        emitterRepository.get(agentId).ifPresent(x->x.forEach(e-> {

            try {
                e.send( SseEmitter.event()
                        .id(RandomStringUtils.randomAlphanumeric(12))
                        .name(event.getType())
                        .data(new ObjectMapper().writeValueAsString(event.getBody())));
            } catch (IOException ioException) {
                    e.complete();
            }

        }));
    }
}
