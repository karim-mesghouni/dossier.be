package com.softline.dossier.be.Sse.service;


import com.softline.dossier.be.Sse.model.EventDto;

import java.io.IOException;

public interface NotificationService {

    void sendNotification(Long agentId, EventDto event) throws IOException;
}
