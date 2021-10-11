package com.softline.dossier.be.Sse.concurrency;

import com.softline.dossier.be.Sse.model.Event;
import com.softline.dossier.be.Sse.service.NotificationService;
import com.softline.dossier.be.Sse.service.SseNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Pings all SSE listener
 * if we don't ping the channel after 45 seconds the connection will be
 * closed automatically by the browser
 */
@Slf4j
public class PingerThread implements Runnable
{
    private final SseNotificationService notifier;

    public PingerThread(SseNotificationService notifier)
    {
        this.notifier = notifier;
    }

    @Override
    public void run()
    {
        while (!Thread.currentThread().isInterrupted()){
            try {
                Thread.sleep(30000);
                log.info("Sending ping event for all");
                notifier.sendNotificationForAll(new Event("ping", System.currentTimeMillis()));
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}
