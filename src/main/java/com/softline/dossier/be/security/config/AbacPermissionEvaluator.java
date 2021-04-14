package com.softline.dossier.be.security.config;


import com.softline.dossier.be.security.domain.Policy.PolicyEnforcement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

import java.util.*;
@Component
public class AbacPermissionEvaluator implements PermissionEvaluator {
    @Autowired
    PolicyEnforcement policy;

    @Override
    public boolean hasPermission(Authentication authentication , Object targetDomainObject, Object permission) {
        //Getting subject
        Object user = authentication.getPrincipal();
        //Getting environment
        Map<String, Object> environment = new HashMap<>();
        environment.put("time", new Date());

        return policy.check(user, targetDomainObject, permission, environment);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
