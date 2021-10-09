package com.softline.dossier.be.Sse.service;


import com.softline.dossier.be.Sse.model.Event;

public interface NotificationService {

    void sendNotification(Long agentId, Event event);

    void sendNotificationForAll(Event event);
}
