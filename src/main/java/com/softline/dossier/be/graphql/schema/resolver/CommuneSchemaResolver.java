package com.softline.dossier.be.graphql.schema.resolver;

import com.softline.dossier.be.domain.Commune;
import com.softline.dossier.be.graphql.types.input.CommuneInput;
import com.softline.dossier.be.repository.CommuneRepository;
import com.softline.dossier.be.service.CommuneService;
import com.softline.dossier.be.service.exceptions.ClientReadableException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")

public class CommuneSchemaResolver extends SchemaResolverBase<Commune, CommuneInput, CommuneRepository, CommuneService>
{


    public Commune createCommune(CommuneInput communeInput) throws IOException, ClientReadableException
    {
        return create(communeInput);
    }

    public Commune updateCommune(CommuneInput communeInput) throws ClientReadableException
    {
        return update(communeInput);
    }

    public boolean deleteCommune(Long id) throws ClientReadableException
    {
        return delete(id);
    }

    protected List<Commune> getAllCommune()
    {
        return getAll();
    }

    protected Commune getCommune(Long id)
    {
        return get(id);
    }


}
