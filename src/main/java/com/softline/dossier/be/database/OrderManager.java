package com.softline.dossier.be.database;

import com.softline.dossier.be.domain.traits.HasOrder;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class OrderManager {
    private OrderManager() {
    }

    /**
     * change the order of an entity,
     * will be called when the user changes the order of an entity in the FilesView
     * in the case when entityBefore is not existent the entity will be moved to be the first item in the list
     *
     * @param entity       the entity that we want to change its order
     * @param entityBefore the entity which should be before the new position of the entity, may be non-existent
     */
    synchronized public static <T extends HasOrder> void changeOrder(@NotNull T entity, @Nullable T entityBefore, @Nullable Predicate<T> filter) {
        if (filter == null) {
            filter = f -> true;
        }
        Predicate<T> finalFilter = filter;
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>) entity.getClass();

        var entityType = Database.getEntityType(type);
        @Language("HQL")
        var select = "SELECT e from " + entityType.getName() + " e ";
        Database.startTransaction();
        if (entityBefore != null) {
            // how many entities will be updated (increment or decrement their order)
            var levelsChange = Database.query(select + "where ((:a < :b and e.order > :a and e.order < :b) or (:a >= :b and e.order > :b and e.order < :a))", type)
                    .setParameter("a", entity.getOrder())
                    .setParameter("b", entityBefore.getOrder())
                    .getResultStream()
                    .filter(finalFilter)
                    .count();
            if (entity.getOrder() < entityBefore.getOrder()) {// entity is moving down the list
                Database.query(select + "where e.order > :value order by e.order", type)
                        .setParameter("value", entity.getOrder())
                        .getResultStream()
                        .filter(finalFilter)
                        .limit(levelsChange + 1)
                        .forEach(T::decrementOrder);
                entity.setOrder(entityBefore.getOrder() + 1);
            } else {// entity is moving up the list
                var allAfter = Database.query(select + "where e.order > :value order by e.order", type)
                        .setParameter("value", entityBefore.getOrder())
                        .getResultStream()
                        .filter(finalFilter)
                        .collect(Collectors.toList());
                allAfter.stream()
                        .limit(levelsChange)
                        .forEach(T::incrementOrder);
                if (allAfter.isEmpty() && entity.getOrder() == entityBefore.getOrder()) {
                    entity.incrementOrder();
                } else {
                    entity.setOrder(allAfter.stream().findFirst().orElseThrow().getOrder() - 1);
                }
            }
        } else {// entity should be the last item
            Database.query(select + "where e.order < :value order by e.order", type)
                    .setParameter("value", entity.getOrder())
                    .getResultStream()
                    .filter(finalFilter)
                    .forEach(T::incrementOrder);
            entity.setOrder(Database.query(select, type).getResultList().stream().filter(finalFilter).mapToLong(HasOrder::getOrder).min().orElse(1) - 1);
        }
        Database.commit();
    }
}
