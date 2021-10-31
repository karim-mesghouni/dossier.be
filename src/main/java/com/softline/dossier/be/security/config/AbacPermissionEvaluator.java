package com.softline.dossier.be.security.config;


import com.softline.dossier.be.security.domain.Policy.PolicyEnforcement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.softline.dossier.be.Halpers.Functions.safeRun;

@Component
@RequiredArgsConstructor
public class AbacPermissionEvaluator implements PermissionEvaluator {
    private final EntityManager entityManager;
    @Autowired
    PolicyEnforcement policy;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        //Getting subject
        Object user = authentication.getPrincipal();
        //Getting environment
        Map<String, Object> environment = new HashMap<>();
        environment.put("time", new Date());

        return policy.check(user, targetDomainObject, permission, environment);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        var ref = new Object() {
            Object target = null;
        };
        this.entityManager.getMetamodel()
                .getEntities()
                .stream()
                .filter(t -> t.getName().equals(targetType))
                .findFirst()
                .ifPresent(entityTarget ->
                        safeRun(() ->
                                ref.target = entityManager.find(entityTarget.getJavaType(), targetId)
                        )
                );
        return hasPermission(authentication, ref.target, permission);
    }
}
