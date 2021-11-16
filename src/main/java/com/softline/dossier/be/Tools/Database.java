package com.softline.dossier.be.Tools;

import com.softline.dossier.be.security.config.AttributeBasedAccessControlEvaluator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.access.AccessDeniedException;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.metamodel.EntityType;
import java.io.Serializable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.softline.dossier.be.Application.context;
import static com.softline.dossier.be.Tools.Functions.throwIfEmpty;
import static com.softline.dossier.be.Tools.TextHelper.format;
import static com.softline.dossier.be.security.config.AttributeBasedAccessControlEvaluator.throwIfCannot;

/**
 * Data base helper methods
 */
public class Database {
    private Database() {
    }

    /**
     * @return the current entity manager session for this request (returns a new entity manager bean)
     */
    @NotNull
    public static EntityManager database() {
        return context().getBean(EntityManager.class);
    }

    /**
     * @return new entity manager instance, if the context is null then null is returned
     */
    @Nullable
    public static EntityManager unsafeEntityManager() {
        if (context() == null) return null;
        return context().getBean(EntityManager.class);
    }

    /**
     * find the entity T with the given id, throws RuntimeException if entity was not found
     *
     * @param clazz the entity class
     * @param id    entity id
     * @param <T>   entity type
     * @return T entity
     * @throws RuntimeException if entity was not found
     */
    public static <T> T findOrThrow(Class<T> clazz, Serializable id) throws RuntimeException {
        return throwIfEmpty(database().find(clazz, id), () -> new RuntimeException(new EntityNotFoundException(format("No entity of type {} with id {} was found", clazz, id))));
    }


    /**
     * apply the action on the given entity,
     * and then return the value returned by the action,
     * uses {@link Database#findOrThrow(Class, Serializable)} to get the entity
     *
     * @return the entity after modification
     * @throws RuntimeException if entity was not found
     */
    public static <T, R> R findOrThrow(Class<T> clazz, Serializable id, Function<T, R> action) throws RuntimeException {
        return action.apply(findOrThrow(clazz, id));
    }

    /**
     * apply the action on the given entity if the permission evaluation returns true,
     * and then return the value returned by the action,
     * uses {@link Database#findOrThrow(Class, Serializable)} to get the entity
     *
     * @return the entity after modification
     * @throws RuntimeException      if entity was not found
     * @throws AccessDeniedException if permission evaluation failed
     */
    public static <T, R> R findOrThrow(Class<T> clazz, Serializable id, String permission, Function<T, R> action) throws RuntimeException {
        T entity = findOrThrow(clazz, id);
        throwIfCannot(permission, entity);
        return action.apply(entity);
    }

    /**
     * apply the action on the given entity and then return it, uses {@link Database#findOrThrow(Class, Serializable)} to get the entity
     *
     * @throws RuntimeException if entity was not found
     */
    public static <T> T findOrThrow(Class<T> clazz, Serializable id, Consumer<T> action) throws RuntimeException {
        T entity = findOrThrow(clazz, id);
        action.accept(entity);
        return entity;
    }

    /**
     * remove the entity, uses {@link Database#findOrThrow(Class, Serializable)} to get the entity
     * and {@link EntityManager#remove(Object)} to delete the entity
     *
     * @throws RuntimeException         if entity was not found
     * @throws IllegalArgumentException if the instance is not an entity or is a detached entity
     */
    public static void remove(Object entity) throws RuntimeException, IllegalArgumentException {
        database().remove(entity);
    }

    /**
     * remove the entity after passing the entity to the consumer uses {@link Database#remove(Object)} to remove the entity
     *
     * @throws IllegalArgumentException if the instance is not an entity or is a detached entity
     */
    public static <T> boolean remove(T entity, Consumer<T> consumer) throws IllegalArgumentException {
        consumer.accept(entity);
        database().remove(entity);
        return true;
    }

    /**
     * remove the entity after passing the entity to the consumer
     * uses {@link Database#remove(Object)} to remove the entity
     * and {@link Database#findOrThrow(Class, Serializable)} to retrieve the entity
     *
     * @return always returns true
     * @throws RuntimeException if the entity was not found
     */
    public static <T> boolean remove(Class<T> clazz, Serializable id, Consumer<T> consumer) throws RuntimeException {
        T entity = findOrThrow(clazz, id);
        consumer.accept(entity);
        remove(entity);
        return true;
    }

    /**
     * remove the entity
     * uses {@link Database#remove(Object)} to remove the entity
     * and {@link Database#findOrThrow(Class, Serializable)} to retrieve the entity
     *
     * @return always returns true
     * @throws RuntimeException if the entity was not found
     */
    public static <T> boolean remove(Class<T> clazz, Serializable id) throws RuntimeException {
        remove(findOrThrow(clazz, id));
        return true;
    }


    /**
     * remove the entity if the permission evaluation succeeded
     * uses {@link Database#remove(Object)} to remove the entity
     * and {@link Database#findOrThrow(Class, Serializable)} to retrieve the entity
     * and {@link AttributeBasedAccessControlEvaluator#throwIfCannot(String, Object)} to evaluate the permission
     *
     * @throws RuntimeException      if the entity was not found
     * @throws AccessDeniedException if the permission evaluation failed
     */
    public static <T> boolean remove(Class<T> clazz, Serializable id, String permission) throws RuntimeException {
        T entity = findOrThrow(clazz, id);
        throwIfCannot(permission, entity);
        remove(entity);
        return true;
    }

    /**
     * persist the entity using entity manager
     */
    public static <T> T persist(T entity) {
        database().persist(entity);
        return entity;
    }

    /**
     * flush the entity manager of this request
     */
    public static void flush() {
        database().flush();
    }

    /**
     * @return entityType meta data
     */
    public static Optional<EntityType<?>> getEntityType(String name) {
        return database()
                .getMetamodel()
                .getEntities()
                .stream()
                .filter(t -> t.getName().equals(name))
                .findFirst();
    }

    /**
     * @return entityType meta data
     */
    public static <T> Optional<EntityType<?>> getEntityType(Class<T> clazz) {
        return database()
                .getMetamodel()
                .getEntities()
                .stream()
                .filter(t -> t.getJavaType().getName().equals(clazz.getName()))
                .findFirst();
    }
}
