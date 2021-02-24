package com.softline.dossier.be.service;

import com.softline.dossier.be.domain.ActivityField;
import com.softline.dossier.be.graphql.types.FileDTO;
import com.softline.dossier.be.graphql.types.FileFilterInput;
import com.softline.dossier.be.graphql.types.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public  abstract class IServiceBase<IEntity,IEntityInput,IRepository extends JpaRepository<IEntity,Long>> {

    @Autowired
    IRepository repository;

    public   abstract  List<IEntity> getAll();
    public   abstract  IEntity create(IEntityInput entityInput );
    public   abstract  IEntity update(IEntityInput entityInput);
    public   abstract  boolean delete(long id) ;
    public   abstract IEntity getById(long id);

    public IRepository getRepository() {
        return repository;
    }

}
