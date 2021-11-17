package com.softline.dossier.be.Tools;

import com.softline.dossier.be.domain.Concerns.HasId;
import com.softline.dossier.be.security.config.AttributeBasedAccessControlEvaluator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.access.AccessDeniedException;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.metamodel.EntityType;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
     * find the entity T with the given id, throws EntityNotFoundException if entity was not found
     *
     * @param clazz the entity class
     * @param id    entity id
     * @param <T>   entity type
     * @return T entity
     * @throws EntityNotFoundException if entity was not found
     */
    public static <T> T findOrThrow(Class<T> clazz, Serializable id) throws EntityNotFoundException {
        return throwIfEmpty(database().find(clazz, id), () -> new EntityNotFoundException(format("No entity of type {} with id {} was found", clazz, id)));
    }

    /**
     * find the entity T with the given entity, throws EntityNotFoundException if entity was not found
     *
     * @param clazz the entity class
     * @param id    entity which has an id field
     * @param <T>   entity type
     * @return T entity
     * @throws EntityNotFoundException if entity was not found
     */
    public static <T> T findOrThrow(Class<T> clazz, HasId id) throws EntityNotFoundException {
        return throwIfEmpty(database().find(clazz, id.getId()), () -> new EntityNotFoundException(format("No entity of type {} with id {} was found", clazz, id.getId())));
    }


    /**
     * apply the action on the given entity,
     * and then return the value returned by the action,
     * uses {@link Database#findOrThrow(Class, Serializable)} to get the entity
     *
     * @return the entity after modification
     * @throws EntityNotFoundException if entity was not found
     */
    public static <T, R> R findOrThrow(Class<T> clazz, Serializable id, Function<T, R> action) throws EntityNotFoundException {
        return action.apply(findOrThrow(clazz, id));
    }

    /**
     * apply the action on the given entity,
     * and then return the value returned by the action,
     * uses {@link Database#findOrThrow(Class, HasId)} to get the entity
     *
     * @return the entity after modification
     * @throws EntityNotFoundException if entity was not found
     */
    public static <T, R> R findOrThrow(Class<T> clazz, HasId id, Function<T, R> action) throws EntityNotFoundException {
        return action.apply(findOrThrow(clazz, id));
    }

    /**
     * apply the action on the given entity if the permission evaluation returns true,
     * and then return the value returned by the action,
     * uses {@link Database#findOrThrow(Class, Serializable)} to get the entity
     *
     * @return the entity after modification
     * @throws EntityNotFoundException      if entity was not found
     * @throws AccessDeniedException if permission evaluation failed
     */
    public static <T, R> R findOrThrow(Class<T> clazz, Serializable id, String permission, Function<T, R> action) throws EntityNotFoundException {
        T entity = findOrThrow(clazz, id);
        throwIfCannot(permission, entity);
        return action.apply(entity);
    }

    /**
     * apply the action on the given entity if the permission evaluation returns true,
     * and then return the value returned by the action,
     * uses {@link Database#findOrThrow(Class, Serializable)} to get the entity
     * uses {@link AttributeBasedAccessControlEvaluator#throwIfCannot(String, Object)} to evaluate the permission
     * @return the entity after modification
     * @throws EntityNotFoundException      if entity was not found
     * @throws AccessDeniedException if permission evaluation failed
     */
    public static <T, R> R findOrThrow(Class<T> clazz, HasId id, String permission, Function<T, R> action) throws EntityNotFoundException {
        T entity = findOrThrow(clazz, id);
        throwIfCannot(permission, entity);
        return action.apply(entity);
    }


    /**
     * apply the action on the given entity and then return it, uses {@link Database#findOrThrow(Class, Serializable)} to get the entity
     *
     * @throws EntityNotFoundException if entity was not found
     */
    public static <T> T findOrThrow(Class<T> clazz, Serializable id, Consumer<T> action) throws EntityNotFoundException {
        T entity = findOrThrow(clazz, id);
        action.accept(entity);
        return entity;
    }

    /**
     * find all results with entity T
     */
    public static <T> List<T> findAll(Class<T> clazz) {
        return database().createQuery("SELECT e FROM " + getEntityType(clazz).orElseThrow().getName() + " e", clazz).getResultList();
    }
    /**
     * find all results with entity T after applying the filter
     */
    public static <T> List<T> findAll(Class<T> clazz, Predicate<T> filter) {
        return database().createQuery("SELECT e FROM " + getEntityType(clazz).orElseThrow().getName() + " e", clazz).getResultStream().filter(filter).collect(Collectors.toList());
    }


    /**
     * remove the entity, uses {@link Database#findOrThrow(Class, Serializable)} to get the entity
     * and {@link EntityManager#remove(Object)} to delete the entity
     *
     * @throws EntityNotFoundException         if entity was not found
     * @throws IllegalArgumentException if the instance is not an entity or is a detached entity
     */
    public static void remove(Object entity) throws EntityNotFoundException, IllegalArgumentException {
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
     * @throws EntityNotFoundException if the entity was not found
     */
    public static <T> boolean remove(Class<T> clazz, Serializable id, Consumer<T> consumer) throws EntityNotFoundException {
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
     * @throws EntityNotFoundException if the entity was not found
     */
    public static <T> boolean remove(Class<T> clazz, Serializable id) throws EntityNotFoundException {
        remove(findOrThrow(clazz, id));
        return true;
    }


    /**
     * remove the entity if the permission evaluation succeeded
     * uses {@link Database#remove(Object)} to remove the entity
     * and {@link Database#findOrThrow(Class, Serializable)} to retrieve the entity
     * and {@link AttributeBasedAccessControlEvaluator#throwIfCannot(String, Object)} to evaluate the permission
     *
     * @throws EntityNotFoundException      if the entity was not found
     * @throws AccessDeniedException if the permission evaluation failed
     */
    public static <T> boolean remove(Class<T> clazz, Serializable id, String permission) throws EntityNotFoundException {
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
