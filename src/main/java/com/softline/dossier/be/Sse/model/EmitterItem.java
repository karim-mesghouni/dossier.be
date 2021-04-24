package com.softline.dossier.be.Sse.model;

import com.softline.dossier.be.security.domain.Agent;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.LinkedList;
import java.util.List;
@Data
@Builder
public class EmitterItem {
    SseEmitter emitter;
    Long sessionId;
}
