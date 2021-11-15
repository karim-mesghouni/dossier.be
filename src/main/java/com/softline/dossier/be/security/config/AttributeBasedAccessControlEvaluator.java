package com.softline.dossier.be.security.config;


import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.security.policy.PolicyMatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.softline.dossier.be.Application.context;
import static com.softline.dossier.be.Tools.Functions.safeRun;

/**
 * Extension for Expression-Based Access Control
 * this class allows to pass an entity to be handled along with the permission evaluation
 *
 * @see <a href="https://docs.spring.io/spring-security/site/docs/3.0.x/reference/el-access.html">Expression-Based-Access-Control Documentation</a>
 * @see <a href="https://en.wikipedia.org/wiki/Attribute-based_access_control#Attributes">Attribute-Based-Access-Control Documentation</a>
 */
@Component
@RequiredArgsConstructor
@SuppressWarnings("CodeBlock2Expr")
public class AttributeBasedAccessControlEvaluator implements PermissionEvaluator {
    private final EntityManager entityManager;
    private final PolicyMatcher policy;

    /**
     * return true if the current logged-in user cannot do the action on the object
     */
    public static boolean cannot(String action, Object domain) {
        return !can(action, domain);
    }

    /**
     * throws {@link AccessDeniedException} if the current logged-in user cannot do the action on the object
     */
    public static void throwIfCannot(String action, Object domain) throws AccessDeniedException {
        if (cannot(action, domain)) {
            throw new AccessDeniedException("erreur de privilege");
        }
    }

    /**
     * throws {@link AccessDeniedException} if the current logged-in user cannot do the action on the object
     */
    public static void throwIfCannot(String action, Object domain, String exceptionMessage) throws AccessDeniedException {
        if (cannot(action, domain)) {
            throw new AccessDeniedException(exceptionMessage);
        }
    }

    public static <T> Stream<T> filter(Stream<T> stream, String action) {
        var eval = context().getBean(AttributeBasedAccessControlEvaluator.class);
        return stream.filter(e -> eval.hasPermission(Agent.auth(), e, action));
    }

    /**
     * return true if the current logged-in user can do the action on the object
     */
    public static boolean can(String action, Object domain) {
        var eval = context().getBean(AttributeBasedAccessControlEvaluator.class);
        return eval.hasPermission(Agent.auth(), domain, action);
    }

    /**
     * @param authentication user
     * @param entity         entity object
     * @param action         action name in string
     * @return true if the user can do the action on the entity
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object entity, Object action) {
        return policy.check(authentication.getPrincipal(),
                entity,
                action,
                null);
    }

    /**
     * @param authentication user
     * @param entityId       primary key
     * @param entityType     entity name in string
     * @param action         action name in string
     * @return true if the user can do the action on the referenced entity
     */
    @Override
    public boolean hasPermission(Authentication authentication, Serializable entityId, String entityType, Object action) {
        var ref = new Object() {
            Object target = null;
        };
        entityManager
                .getMetamodel()
                .getEntities()
                .stream()
                .filter(t -> t.getName().equals(entityType))
                .findFirst()
                .ifPresent(entityTarget -> {
                    safeRun(() -> ref.target = entityManager.find(entityTarget.getJavaType(), entityId));
                });
        return hasPermission(authentication, ref.target, action);
    }


    public static <T> List<T> filter(List<T> list, String action) {
        return filter(list.stream(), action).collect(Collectors.toList());
    }
}
