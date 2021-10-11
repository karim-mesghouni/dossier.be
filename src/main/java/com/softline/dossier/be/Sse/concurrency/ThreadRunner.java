package com.softline.dossier.be.Sse.concurrency;

import com.softline.dossier.be.Sse.service.SseNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

@Configuration
@Slf4j
public class ThreadRunner
{
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    SseNotificationService notifier;
    @Bean
    public CommandLineRunner schedulingRunner(TaskExecutor executor)
    {
        return args ->
        {
            executor.execute(new PingerThread(notifier));
            log.warn("###### Startup ok");
        };
    }
}
