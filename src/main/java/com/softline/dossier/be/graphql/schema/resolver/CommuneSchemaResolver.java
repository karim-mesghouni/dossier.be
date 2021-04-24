package com.softline.dossier.be.graphql.schema.resolver;

import com.softline.dossier.be.domain.Commune;
import com.softline.dossier.be.graphql.types.input.CommuneInput;
import com.softline.dossier.be.repository.CommuneRepository;
import com.softline.dossier.be.service.CommuneService;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.core.ApplicationPart;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Component
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")

public class CommuneSchemaResolver extends SchemaResolverBase<Commune, CommuneInput, CommuneRepository, CommuneService> {


    public Commune createCommune(CommuneInput communeInput) throws IOException {
        return create(communeInput);
    }
    public Commune updateCommune(CommuneInput communeInput){
        return update(communeInput);
    }
    public boolean deleteCommune(Long id){
        return delete(id);
    }
    protected List<Commune> getAllCommune(){
        return getAll();
    }
    protected Commune getCommune(Long id){
        return get(id);
    }


}
