package com.softline.dossier.be.database;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@FunctionalInterface
public interface QueryPredicate<T> {
    Predicate predicate(CriteriaBuilder cb, Root<T> r);
}
