package com.softline.dossier.be.graphql.schema.resolver;

import com.softline.dossier.be.domain.File;
import com.softline.dossier.be.domain.FilePhase;
import com.softline.dossier.be.domain.FilePhaseAgent;
import com.softline.dossier.be.domain.FilePhaseState;
import com.softline.dossier.be.graphql.types.FileDTO;
import com.softline.dossier.be.graphql.types.FileFilterInput;
import com.softline.dossier.be.graphql.types.PageList;
import com.softline.dossier.be.graphql.types.input.FileInput;
import com.softline.dossier.be.graphql.types.input.FilePhaseAgentInput;
import com.softline.dossier.be.graphql.types.input.FilePhaseInput;
import com.softline.dossier.be.repository.FilePhaseRepository;
import com.softline.dossier.be.repository.FileRepository;
import com.softline.dossier.be.service.FilePhaseService;
import com.softline.dossier.be.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FilePhaseSchemaResolver extends SchemaResolverBase<FilePhase, FilePhaseInput, FilePhaseRepository, FilePhaseService> {


    public FilePhase createFilePhase(FilePhaseInput filePhase){
        return create(filePhase);
    }
    public FilePhase updateFilePhase(FilePhaseInput filePhase){
        return update(filePhase);
    }
    public boolean deleteFilePhase(Long id){
        return delete(id);
    }
    public List<FilePhase> getAllFilePhase(){
        return getAll();
    }
    public FilePhase getFilePhase(Long id){
        return get(id);
    }
    public FilePhase getCurrentFilePhase(Long fileActivityId){
        return service.getCurrentFilePhase(fileActivityId);
    }
    public FilePhaseState getCurrentFilePhaseState(Long fileAgentId){
        return service.getCurrentFilePhaseState(fileAgentId);
    }
    public FilePhaseAgent getCurrentFilePhaseAgent(Long filePhaseId){
        return service.getCurrentFilePhaseAgent(filePhaseId);
    }
    public FilePhase changeFilePhase(Long phaseId,Long fileActivityId){
        return service.ChangeFilePhase(phaseId,fileActivityId);
    }
    public FilePhaseAgent changeAgent(Long agentId,Long phaseFileAgentId){
        return service.ChangeAgent(agentId,phaseFileAgentId);
    }
    public FilePhaseState changePhaseState(Long stateId,Long phaseFileAgentId){
        return service.changePhaseState(stateId,phaseFileAgentId);
    }
   public FilePhaseAgent updateFilePhaseAgent(FilePhaseAgentInput filePhaseAgentInput){
        return  service.updateFilePhaseAgent(filePhaseAgentInput);
   }

}
