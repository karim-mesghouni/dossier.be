package com.softline.dossier.be.Sse.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Data
@Builder
public class EmitterItem {
    SseEmitter emitter;
    Long sessionId;
}
