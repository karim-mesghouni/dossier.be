package com.softline.dossier.be.security.config;


import com.softline.dossier.be.Application;
import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.security.policy.PolicyMatcher;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.softline.dossier.be.Tools.Functions.safeRun;
import static com.softline.dossier.be.Tools.Functions.safeSupplied;
import static com.softline.dossier.be.Tools.TextHelper.format;

/**
 * Attribute-Based-Access-Control Documentation
 * <p>
 * Extension for Expression-Based Access Control
 * this class allows to pass an entity to be handled along with the permission evaluation
 *
 * @see <a href="https://docs.spring.io/spring-security/site/docs/3.0.x/reference/el-access.html">Expression-Based-Access-Control Documentation</a>
 * @see <a href="https://en.wikipedia.org/wiki/Attribute-based_access_control#Attributes">Attribute-Based-Access-Control Documentation</a>
 */
@Component
@RequiredArgsConstructor
public class Gate implements PermissionEvaluator {
    private final PolicyMatcher policy;

    /**
     * return true if the current logged-in user cannot do the action on the object
     */
    public static boolean cannot(@NotNull String action, @Nullable Object domain) {
        return !can(action, domain);
    }

    /**
     * throws {@link AccessDeniedException} if the current logged-in user cannot do the action on the given object
     */
    @SuppressWarnings({"Convert2MethodRef", "ConstantConditions"})
    public static void check(@NotNull String action, @Nullable Object domain) throws AccessDeniedException {
        if (cannot(action, domain)) {
            throw new AccessDeniedException(format("Denied operation {} on {} of type {} with authentication {}", action, safeSupplied(() -> domain.toString(), () -> "<failed to call toString>"), domain != null ? domain.getClass().getName() : "NULL", safeSupplied(() -> Agent.authentication().getPrincipal(), () -> "None")));
        }
    }

    /**
     * throws {@link AccessDeniedException} if the current logged-in user cannot do the action on the object
     */
    public static void check(@NotNull String action, @Nullable Object domain, @Nullable String exceptionMessage) throws AccessDeniedException {
        if (cannot(action, domain)) {
            throw new AccessDeniedException(exceptionMessage);
        }
    }

    public static <T> Stream<T> filter(Stream<T> stream, String action) {
        var eval = accessControl();
        Authentication auth = Agent.authentication();
        return stream.filter(e -> eval.hasPermission(auth, e, action));
    }

    /**
     * return true if the current logged-in user can do the action on the object
     */
    public static boolean can(@NotNull String action, @Nullable Object domain) {
        return accessControl().hasPermission(Agent.authentication(), domain, action);
    }

    /**
     * @param authentication user
     * @param entity         entity object
     * @param action         action name in string
     * @return true if the user can do the action on the entity
     */
    @Override
    public boolean hasPermission(@NotNull Authentication authentication, @Nullable Object entity, @NotNull Object action) {
        return policy.check(authentication.getPrincipal(),
                entity,
                action,
                null);
    }

    /**
     * @return an instance of {@link Gate}
     */
    public static Gate accessControl() {
        return Application.getBean(Gate.class);
    }


    public static <T> List<T> filter(List<T> list, String action) {
        return filter(list.stream(), action).collect(Collectors.toList());
    }

    /**
     * @param authentication user
     * @param entityId       primary key
     * @param entityType     entity name in string
     * @param action         action name in string
     * @return true if the user can do the action on the referenced entity
     */
    @Override
    public boolean hasPermission(@NotNull Authentication authentication, @NotNull Serializable entityId, @NotNull String entityType, @NotNull Object action) {
        var ref = new Object() {
            Object target = null;
        };
        safeRun(() -> ref.target = Database.findOrThrow(Database.getEntityType(entityType).getJavaType(), entityId));
        return hasPermission(authentication, ref.target, action);
    }
}
