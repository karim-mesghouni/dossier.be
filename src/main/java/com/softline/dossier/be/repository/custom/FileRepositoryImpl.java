package com.softline.dossier.be.repository.custom;

import com.softline.dossier.be.domain.*;
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

        CriteriaQuery<Long> CountQury = cb.createQuery(Long.class);
        var countRoot= CountQury.from(File.class);

        List<Predicate> whereConditions = new ArrayList<>();
        List<Predicate> whereCountConditions = new ArrayList<>();
        if(input.getAttributionDateFrom()!=null){
            whereConditions.add(cb.greaterThanOrEqualTo(fileRoot.get(File_.ATTRIBUTION_DATE),input.getAttributionDateFrom()));
            whereCountConditions.add(cb.greaterThanOrEqualTo(countRoot.get(File_.ATTRIBUTION_DATE),input.getAttributionDateFrom()));
        }
        if(input.getAttributionDateTo()!=null){
            whereConditions.add(cb.lessThanOrEqualTo(fileRoot.get(File_.ATTRIBUTION_DATE),input.getAttributionDateTo()));
            whereCountConditions.add(cb.lessThanOrEqualTo(countRoot.get(File_.ATTRIBUTION_DATE),input.getAttributionDateTo()));
        }
        if(input.getProvisionalDeliveryDateFrom()!=null){
            whereConditions.add(cb.greaterThanOrEqualTo(fileRoot.get(File_.PROVISIONAL_DELIVERY_DATE),input.getProvisionalDeliveryDateFrom()));
            whereCountConditions.add(cb.greaterThanOrEqualTo(countRoot.get(File_.PROVISIONAL_DELIVERY_DATE),input.getProvisionalDeliveryDateFrom()));
        }
        if(input.getProvisionalDeliveryDateTo()!=null){
            whereConditions.add(cb.lessThanOrEqualTo(fileRoot.get(File_.PROVISIONAL_DELIVERY_DATE),input.getProvisionalDeliveryDateTo()));
            whereCountConditions.add(cb.lessThanOrEqualTo(countRoot.get(File_.PROVISIONAL_DELIVERY_DATE),input.getProvisionalDeliveryDateTo()));
        }
        if(input.getDeliveryDateFrom()!=null){
            whereConditions.add(cb.greaterThanOrEqualTo(fileRoot.get(File_.DELIVERY_DATE),input.getDeliveryDateFrom()));
            whereCountConditions.add(cb.greaterThanOrEqualTo(countRoot.get(File_.DELIVERY_DATE),input.getDeliveryDateFrom()));
        }
        if(input.getDeliveryDateTo()!=null){
            whereConditions.add(cb.lessThanOrEqualTo(fileRoot.get(File_.DELIVERY_DATE),input.getDeliveryDateTo()));
            whereCountConditions.add(cb.lessThanOrEqualTo(countRoot.get(File_.DELIVERY_DATE),input.getDeliveryDateTo()));
        }
        if(input.getClientId()!=null){
            whereConditions.add(cb.equal(fileRoot.get(File_.CLIENT).get(Client_.ID),input.getClientId()));
            whereCountConditions.add(cb.equal(countRoot.get(File_.CLIENT).get(Client_.ID),input.getClientId()));
        }
        if(input.getActivityId()!=null){
            var fJoinFileActivities=fileRoot.join(File_.FILE_ACTIVITIES);
            var cJoinFileActivities=countRoot.join(File_.FILE_ACTIVITIES);
            whereConditions.add(cb.equal(fJoinFileActivities.get(FileActivity_.ACTIVITY).get(Activity_.ID),input.getActivityId()));
            whereCountConditions.add(cb.equal(cJoinFileActivities.get(FileActivity_.ACTIVITY).get(Activity_.ID),input.getActivityId()));
        }
        if(input.getStateId()!=null){
            var fJoinFileStates=fileRoot.join(File_.FILE_STATES);
            var cJoinFileStates=countRoot.join(File_.FILE_STATES);
            whereConditions.add(cb.equal(fJoinFileStates.get(FileState_.ID),input.getStateId()));
            whereCountConditions.add(cb.equal(cJoinFileStates.get(FileState_.ID),input.getStateId()));
        }
        if(input.getProject()!=null){
            whereConditions.add(cb.like(fileRoot.get(File_.PROJECT),"%"+input.getProject()+"%"));
            whereCountConditions.add(cb.equal(countRoot.get(File_.PROJECT),"%"+input.getProject()+"%"));
        }
        cq.where(cb.and(whereConditions.toArray(new Predicate[]{})));
        TypedQuery<File> query = entityManager.createQuery(cq);
        CountQury= CountQury.select(cb.count(countRoot));
        CountQury.where(cb.and(whereCountConditions.toArray(new Predicate[]{})));

        return  new Pair<>(entityManager.createQuery(CountQury).getSingleResult(),query.setFirstResult(input.getPageNumber()* input.getPageSize()).setMaxResults(input.getPageSize()).getResultList());
    }

}
