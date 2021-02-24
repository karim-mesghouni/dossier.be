package com.softline.dossier.be.graphql.schema.resolver;

import com.softline.dossier.be.domain.ActivityField;
import com.softline.dossier.be.domain.Commune;
import com.softline.dossier.be.graphql.types.input.ActivityFieldInput;
import com.softline.dossier.be.graphql.types.input.CommuneInput;
import com.softline.dossier.be.repository.ActivityFieldRepository;
import com.softline.dossier.be.repository.CommuneRepository;
import com.softline.dossier.be.service.ActivityFieldService;
import com.softline.dossier.be.service.CommuneService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommuneSchemaResolver extends SchemaResolverBase<Commune, CommuneInput, CommuneRepository, CommuneService> {


    public Commune createCommune(CommuneInput communeInput){
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
