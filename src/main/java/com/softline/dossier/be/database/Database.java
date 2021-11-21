package com.softline.dossier.be.database;

import com.softline.dossier.be.domain.Concerns.HasId;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.access.AccessDeniedException;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.metamodel.EntityType;
import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.softline.dossier.be.Application.context;
import static com.softline.dossier.be.Tools.Functions.*;
import static com.softline.dossier.be.Tools.TextHelper.format;
import static com.softline.dossier.be.security.config.AttributeBasedAccessControlEvaluator.throwIfCannot;


public class Database {
    private Database() {
    }


    @NotNull
    public static EntityManager em() {
        return context().getBean(EntityManager.class);
    }


    @NotNull
    public static <T> TypedQuery<T> query(@Language("HQL") String query, Class<T> clazz) {
        return em().createQuery(query, clazz);
    }


    @NotNull
    public static <T> TypedQuery<T> querySingle(@Language("HQL") String query, Class<T> clazz) {
        return em().createQuery(query, clazz).setMaxResults(1);
    }


    @NotNull
    public static <T> javax.persistence.criteria.Predicate predicate(Class<T> clazz, QueryPredicate<T> predicate) {
        var cb = em().getCriteriaBuilder();
        var cq = cb.createQuery(clazz);
        var r = cq.from(clazz);
        return predicate.predicate(cb, r);
    }


    @NotNull
    public static <T> T getSingle(@Language("HQL") String query, Class<T> clazz) {
        return em().createQuery(query, clazz).setMaxResults(1).getSingleResult();
    }

    private static <T> TypedQuery<T> criteria(Class<T> clazz, QueryFilter<T> filter) {
        var cb = em().getCriteriaBuilder();
        var cq = cb.createQuery(clazz);
        var r = cq.from(clazz);
        filter.filtering(cq, cb, r);
        return em().createQuery(cq);
    }

    public static <T> int count(Class<T> clazz, QueryFilter<T> filter) {
        return criteria(clazz, filter).getResultList().size();
    }

    public static <T> List<T> findAll(Class<T> clazz, int maxResults, QueryFilter<T> filter) {
        return criteria(clazz, filter).setMaxResults(maxResults).getResultList();
    }

    public static <T> List<T> findAll(Class<T> clazz, QueryFilter<T> filter) {
        return criteria(clazz, filter).getResultList();
    }

    public static <T> T findOne(Class<T> clazz, QueryFilter<T> filter) throws NoResultException {
        return criteria(clazz, filter).setMaxResults(1).getSingleResult();
    }

    @Nullable
    public static EntityManager unsafeEntityManager() {
        if (context() == null) return null;
        return context().getBean(EntityManager.class);
    }

    public static <T> T findOrDefault(Class<T> clazz, Serializable id, Supplier<T> _default) {
        return safeSupplied(() -> Database.findOrThrow(clazz, id), _default);
    }

    public static <T> T findOrNull(Class<T> clazz, Serializable id) {
        return safeValue(() -> Database.findOrThrow(clazz, id));
    }

    public static <T> T findOrNull(Class<T> clazz, HasId id) {
        return safeValue(() -> Database.findOrThrow(clazz, id));
    }

    public static <T> T findOrThrow(Class<T> clazz, Serializable id) throws EntityNotFoundException {
        return throwIfEmpty(em().find(clazz, id), () -> new EntityNotFoundException(format("No entity of type {} with id {} was found", clazz, id)));
    }


    public static <T> T findOrThrow(Class<T> clazz, Serializable id, String action) throws EntityNotFoundException {
        return findOrThrow(clazz, id, (Consumer<T>) entity -> throwIfCannot(action, entity));
    }


    public static <T> T findOrThrow(Class<T> clazz, HasId id) throws EntityNotFoundException {
        return throwIfEmpty(em().find(clazz, id.getId()), () -> new EntityNotFoundException(format("No entity of type {} with id {} was found", clazz, id.getId())));
    }


    public static <T extends HasId> T findOrThrow(T input) throws EntityNotFoundException {
        //noinspection unchecked
        return findOrThrow((Class<T>) input.getClass(), input.getId());
    }


    public static <T, R> R findOrThrow(Class<T> clazz, Serializable id, Function<T, R> action) throws EntityNotFoundException {
        return action.apply(findOrThrow(clazz, id));
    }


    public static <T, R> R findOrThrow(Class<T> clazz, HasId id, Function<T, R> action) throws EntityNotFoundException {
        return action.apply(findOrThrow(clazz, id));
    }


    public static <T, R> R findOrThrow(Class<T> clazz, Serializable id, String permission, Function<T, R> action) throws EntityNotFoundException {
        T entity = findOrThrow(clazz, id);
        throwIfCannot(permission, entity);
        return action.apply(entity);
    }


    public static <T, R> R findOrThrow(Class<T> clazz, HasId id, String permission, Function<T, R> action) throws EntityNotFoundException, AccessDeniedException {
        T entity = findOrThrow(clazz, id);
        throwIfCannot(permission, entity);
        return action.apply(entity);
    }


    public static <T> T findOrThrow(Class<T> clazz, Serializable id, Consumer<T> action) throws EntityNotFoundException {
        T entity = findOrThrow(clazz, id);
        action.accept(entity);
        return entity;
    }


    public static <T> List<T> findAll(Class<T> clazz) {
        return em().createQuery("SELECT e FROM " + getEntityType(clazz).getName() + " e", clazz).getResultList();
    }


    public static <T> List<T> findAll(Class<T> clazz, Predicate<T> filter) {
        return em().createQuery("SELECT e FROM " + getEntityType(clazz).getName() + " e", clazz).getResultStream().filter(filter).collect(Collectors.toList());
    }



    public static void remove(Object entity) throws EntityNotFoundException, IllegalArgumentException {
        em().remove(entity);
    }


    public static <T> boolean remove(T entity, Consumer<T> consumer) throws IllegalArgumentException {
        consumer.accept(entity);
        em().remove(entity);
        return true;
    }


    public static <T> boolean remove(Class<T> clazz, Serializable id, Consumer<T> consumer) throws EntityNotFoundException {
        T entity = findOrThrow(clazz, id);
        consumer.accept(entity);
        remove(entity);
        return true;
    }


    public static <T> boolean afterRemoving(Class<T> clazz, Serializable id, String action, Consumer<T> consumer) throws EntityNotFoundException {
        T entity = findOrThrow(clazz, id, action);
        remove(entity, consumer);
        consumer.accept(entity);
        return true;
    }


    public static <T> boolean remove(Class<T> clazz, Serializable id) throws EntityNotFoundException {
        remove(findOrThrow(clazz, id));
        return true;
    }


    public static <T> boolean remove(Class<T> clazz, Serializable id, String permission) throws EntityNotFoundException {
        T entity = findOrThrow(clazz, id);
        throwIfCannot(permission, entity);
        remove(entity);
        return true;
    }


    public static <T> T persist(T entity) {
        em().persist(entity);
        return entity;
    }


    public static void flush() {
        em().flush();
    }


    public static EntityType<?> getEntityType(String name) {
        return em()
                .getMetamodel()
                .getEntities()
                .stream()
                .filter(t -> t.getName().equals(name))
                .findFirst()
                .orElseThrow();
    }


    public static <T> EntityType<?> getEntityType(Class<T> clazz) {
        return em()
                .getMetamodel()
                .getEntities()
                .stream()
                .filter(t -> t.getJavaType().getName().equals(clazz.getName()))
                .findFirst()
                .orElseThrow();
    }
}
