package com.softline.dossier.be.security.config;


import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.security.domain.Policy.PolicyEnforcement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.softline.dossier.be.Application.context;
import static com.softline.dossier.be.Halpers.Functions.safeRun;

@Component
@RequiredArgsConstructor
public class AbacPermissionEvaluator implements PermissionEvaluator {
    private final EntityManager entityManager;
    private final PolicyEnforcement policy;

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

    public static <T> Stream<T> filter(Stream<T> stream, String action) {
        var eval = context().getBean(AbacPermissionEvaluator.class);
        return stream.filter(e -> eval.hasPermission(Agent.auth(), e, action));
    }

    public static <T> List<T> filter(List<T> list, String action) {
        return filter(list.stream(), action).collect(Collectors.toList());
    }

    /**
     * return true if the current logged-in user can do the action on the object
     */
    public static boolean can(Object domain, String action) {
        var eval = context().getBean(AbacPermissionEvaluator.class);
        return eval.hasPermission(Agent.auth(), domain, action);
    }

    /**
     * return true if the current logged-in user cannot do the action on the object
     */
    public static boolean cannot(Object domain, String action) {
        return !can(domain, action);
    }
}
