package com.softline.dossier.be.repository.custom;

import com.softline.dossier.be.domain.File;
import com.softline.dossier.be.domain.File_;
import com.softline.dossier.be.graphql.types.FileFilterInput;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
@Transactional
public class FileRepositoryImpl implements  FileRepositoryCustom{
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Pair<Long,List<File>> getByFilter(FileFilterInput input) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<File> cq = cb.createQuery(File.class);
        var fileRoot=cq.from(File.class);
        cq.select(fileRoot);
        List<Predicate> whereConditions = new ArrayList<>();

        TypedQuery<File> query = entityManager.createQuery(cq);
        CriteriaQuery<Long> CountQury = cb.createQuery(Long.class);
        CountQury= CountQury.select(cb.count(CountQury.from(File.class)));
     //   CountQury.where(cb.and(whereConditions.toArray(new Predicate[]{})));

        return  new Pair<>(entityManager.createQuery(CountQury).getSingleResult(),query.setFirstResult(input.getPageNumber()* input.getPageSize()).setMaxResults(input.getPageSize()).getResultList());
    }

}
