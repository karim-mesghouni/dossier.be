package com.softline.dossier.be.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.IOException;
import java.util.List;

public abstract class IServiceBase<IEntity, IEntityInput, IRepository extends JpaRepository<IEntity, Long>> {

    @Autowired
    IRepository repository;

    public abstract List<IEntity> getAll();

    public abstract IEntity create(IEntityInput entityInput) throws IOException;

    public abstract IEntity update(IEntityInput entityInput);

    public abstract boolean delete(long id);

    public abstract IEntity getById(long id);

    public IRepository getRepository() {
        return repository;
    }

}
