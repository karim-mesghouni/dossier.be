package com.softline.dossier.be.Sse.service;


import com.softline.dossier.be.Sse.model.EventDto;

public interface NotificationService {

    void sendNotification(Long agentId, EventDto event);

    void sendNotificationForAll(EventDto event);
}
