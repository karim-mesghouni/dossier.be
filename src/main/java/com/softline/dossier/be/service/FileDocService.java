package com.softline.dossier.be.service;

import com.softline.dossier.be.domain.Agent;
import com.softline.dossier.be.domain.File;
import com.softline.dossier.be.domain.FileDoc;
import com.softline.dossier.be.graphql.types.input.FileDocInput;
import com.softline.dossier.be.graphql.types.input.FileInput;
import com.softline.dossier.be.repository.AgentRepository;
import com.softline.dossier.be.repository.FileActivityRepository;
import com.softline.dossier.be.repository.FileDocRepository;
import com.softline.dossier.be.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class FileDocService  extends IServiceBase<FileDoc, FileDocInput, FileDocRepository> {
   @Autowired
    FileActivityRepository fileActivityRepository;
   @Autowired
   AgentRepository agentRepository;
   @Autowired
   FileDocRepository fileDocRepository;
   @Override
    public List<FileDoc> getAll() {
        return null;
    }

    @Override
    public FileDoc create(FileDocInput fileDocInput) {

        var fileActivity=fileActivityRepository.findById(fileDocInput.getFileActivity().getId()).orElseThrow();
        var agent= agentRepository.findById(fileDocInput.getAgent().getId()).orElseThrow();
       // return  fileDocRepository.save(FileDoc.builder().agent(agent).fileActivity(fileActivity).build());

   return  null;
   }

    @Override
    public FileDoc update(FileDocInput fileDocInput) {
        return null;
    }

    @Override
    public boolean delete(long id) {
        return false;
    }

    @Override
    public FileDoc getById(long id) {
        return null;
    }
}
