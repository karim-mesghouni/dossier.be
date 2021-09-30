package com.softline.dossier.be.db;

import com.github.javafaker.Faker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;
import java.util.Random;

@Configuration
public class FakerConfiguration
{

    @Bean
    public Faker faker() {
        return new Faker(new Locale("fr"), new Random(0));// fixed seed for persistent results
    }
}
