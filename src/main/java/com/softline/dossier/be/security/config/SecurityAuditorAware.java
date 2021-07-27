package com.softline.dossier.be.security.config;

import com.softline.dossier.be.security.domain.Agent;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import  java.util.*;
@Component
public class SecurityAuditorAware implements AuditorAware<Agent> {

    @Override
    public Optional<Agent> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        return Optional.of(((Agent) authentication.getPrincipal()));
    }
}