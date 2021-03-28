package com.softline.dossier.be.service;

import com.softline.dossier.be.domain.Activity;
import com.softline.dossier.be.domain.ActivityDataField;
import com.softline.dossier.be.domain.File;
import com.softline.dossier.be.domain.FileActivity;
import com.softline.dossier.be.domain.enums.FieldType;
import com.softline.dossier.be.graphql.types.input.FileActivityInput;
import com.softline.dossier.be.repository.FileActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional

@Service
public class FileActivityService extends IServiceBase<FileActivity, FileActivityInput, FileActivityRepository> {

    @Override
    public List<FileActivity> getAll() {
        return repository.findAll();
    }

    @Override
    public FileActivity create(FileActivityInput entityInput) {
        var fileActivity = FileActivity.builder()
                .activity(Activity.builder().id(entityInput.getActivity().getId()).build())
                .file(File.builder().id(entityInput.getFile().getId()).build())
                .current(true)
                .build();
        if (entityInput.getDataFields() != null && !entityInput.getDataFields().isEmpty()) {
            var dataFields = entityInput.getDataFields().stream()
                    .map(x -> ActivityDataField.builder()
                            .data(x.getData())
                            .groupName(x.getGroup())
                            .fieldName(x.getFieldName())
                            .fieldType(FieldType.valueOf(x.getFieldType().toString()))
                            .fileActivity(fileActivity)

                            .build()
                    );
            fileActivity.setDataFields(dataFields.collect(Collectors.toList()));
        }
        return repository.save(fileActivity);
    }

    @Override
    public FileActivity update(FileActivityInput entityInput) {
        return null;
    }

    @Override
    public boolean delete(long id) {
        repository.deleteById(id);
        return true;
    }

    @Override
    public FileActivity getById(long id) {
        return repository.findById(id).orElseThrow();
    }

    public List<FileActivity> getAllFileActivityByFileId(Long fileId) {
        return getRepository().findAllByFile_Id(fileId);
    }

    public boolean changeValid(boolean valid, Long fileActivityId) {
        var fileActivity = getRepository().findById(fileActivityId).orElseThrow();
        fileActivity.setValid(valid);
        return true;
    }
}
