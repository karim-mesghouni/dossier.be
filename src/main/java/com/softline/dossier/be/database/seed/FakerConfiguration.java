package com.softline.dossier.be.database.seed;

import com.github.javafaker.Faker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;
import java.util.Random;

@Configuration
public class FakerConfiguration {
    private static Faker faker;

    @Bean
    public static Faker faker() {
        if (faker == null) {
            faker = new Faker(new Locale("fr"), new Random(0));// fixed seed for persistent results
        }
        return faker;
    }
}
