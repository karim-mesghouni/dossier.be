package com.softline.dossier.be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class Application {
    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        context = SpringApplication.run(Application.class, args);
    }

    /**
     * @return the current spring boot application context (useful for getting beans),
     * until the application boot phase completes this will return null
     */
    //!! @Nullable (before the application boots-up)
    public static ConfigurableApplicationContext context() {
        return context;
    }

    /**
     * @return the bean instance that uniquely matches the given object
     */
    public static <T> T getBean(Class<T> requiredType) {
        return context().getBean(requiredType);
    }

}
