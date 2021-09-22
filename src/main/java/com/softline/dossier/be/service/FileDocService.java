package com.softline.dossier.be.service;

import com.softline.dossier.be.domain.FileDoc;
import com.softline.dossier.be.graphql.types.input.FileDocInput;
import com.softline.dossier.be.repository.FileActivityRepository;
import com.softline.dossier.be.repository.FileDocRepository;
import com.softline.dossier.be.repository.FileRepository;
import com.softline.dossier.be.security.repository.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FileDocService extends IServiceBase<FileDoc, FileDocInput, FileDocRepository> {
    @Autowired
    FileActivityRepository fileActivityRepository;
    @Autowired
    AgentRepository agentRepository;
    @Autowired
    FileDocRepository fileDocRepository;
    @Autowired
    FileRepository fileRepository;

    @Override
    public List<FileDoc> getAll() {
        return null;
    }

    @Override

    public FileDoc create(FileDocInput fileDocInput) {
        var file = fileRepository.findById(fileDocInput.getFile().getId()).orElseThrow();
        var fileActivity = fileActivityRepository.findById(fileDocInput.getFileActivity().getId());
        var agent = agentRepository.findById(fileDocInput.getAgent().getId()).orElseThrow();
        return fileDocRepository.save(FileDoc.builder().description(fileDocInput.getDescription()).path(fileDocInput.getPath()).agent(agent).fileActivity(fileActivity.isPresent() ? fileActivity.get() : null).file(file).build());


    }

    @Override
    public FileDoc update(FileDocInput fileDocInput) {
        var fileDoc = fileDocRepository.findById(fileDocInput.getId()).orElseThrow();
        fileDoc.setPath(fileDocInput.getPath());
        fileDoc.setDescription(fileDocInput.getDescription());
        return fileDoc;
    }

    @Override
    public boolean delete(long id) {
        fileDocRepository.deleteById(id);
        return true;
    }

    @Override
    public FileDoc getById(long id) {
        return null;
    }

    public List<FileDoc> getAllByFileActivityIdOrFileId(Long fileActivityId, Long fileId) throws Exception {

        if (fileId != null) {
            return getRepository().findAllByFile_Id(fileId);
        }
        if (fileActivityId != null) {
            return getRepository().findAllByFileActivity_Id(fileActivityId);
        }

        throw new Exception("not found");
    }
}
