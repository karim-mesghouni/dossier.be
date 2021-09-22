package com.softline.dossier.be.security.domain.Policy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.file.AccessDeniedException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ContextAwarePolicyEnforcement {
    @Autowired
    protected PolicyEnforcement policy;

    public void checkPermission(Object resource, String permission) throws AccessDeniedException {
        //Getting the subject
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        //Getting the environment
        Map<String, Object> environment = new HashMap<>();
        environment.put("time", new Date());

        if (!policy.check(auth.getPrincipal(), resource, permission, environment))
            throw new AccessDeniedException("Access is denied");
    }
}
