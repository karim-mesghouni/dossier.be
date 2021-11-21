package com.softline.dossier.be.database;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@FunctionalInterface
public interface QueryFilter<T> {
    void filtering(CriteriaQuery<T> cq, CriteriaBuilder cb, Root<T> r);
}
