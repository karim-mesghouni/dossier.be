package com.softline.dossier.be.graphql.schema.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.softline.dossier.be.service.IServiceBase;
import com.softline.dossier.be.service.exceptions.ClientReadableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.IOException;
import java.util.List;

public abstract class SchemaResolverBase<IEntity, IEntityInput, IRepository extends JpaRepository<IEntity, Long>, IService extends IServiceBase<IEntity, IEntityInput, IRepository>> implements GraphQLMutationResolver, GraphQLQueryResolver {

    @Autowired
    protected IService service;

    protected IEntity create(IEntityInput entityInput) throws IOException, ClientReadableException {
        return service.create(entityInput);
    }

    protected IEntity update(IEntityInput entityInput) throws ClientReadableException
    {
        return service.update(entityInput);
    }

    protected boolean delete(Long id) throws ClientReadableException {
        return service.delete(id);
    }

    protected List<IEntity> getAll() {
        return service.getAll();
    }

    protected IEntity get(Long id) {
        return service.getById(id);
    }

    public IService getService() {
        return service;
    }
}
