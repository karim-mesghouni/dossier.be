package com.softline.dossier.be.Sse.service;

import com.softline.dossier.be.Sse.repository.EmitterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.charset.Charset;
import java.util.Optional;

@Service
@Slf4j
public class EmitterService {

    @Autowired
     EmitterRepository repository;


    public Long createEmitter(Long agentId) {

        return repository.addEmitter(agentId);

    }
    public  Optional<SseEmitter> getEmitter(Long agentId,Long sessionId){
      return   repository.get(agentId, sessionId);
    }

    public void close(Long agentId, Long sessionId) {
        repository.remove(agentId,sessionId);
    }

    public static final class Utf8SseEmitter extends SseEmitter {

        private static final MediaType UTF8_TEXT_STREAM = new MediaType("text", "event-stream", Charset.forName("UTF-8"));
        public Utf8SseEmitter() {}

        public Utf8SseEmitter(long maxValue) {
            super(maxValue);
        }

        @Override
        protected void extendResponse(ServerHttpResponse outputMessage) {
            HttpHeaders headers = outputMessage.getHeaders();
            if (headers.getContentType() == null) {
                headers.setContentType(UTF8_TEXT_STREAM);
            }
        }

    }
}
