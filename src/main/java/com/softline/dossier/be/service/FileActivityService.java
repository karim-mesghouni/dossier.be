package com.softline.dossier.be.service;

import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.domain.enums.FieldType;
import com.softline.dossier.be.graphql.types.input.FileActivityInput;
import com.softline.dossier.be.repository.ActivityStateRepository;
import com.softline.dossier.be.repository.FileActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional

@Service
public class FileActivityService extends IServiceBase<FileActivity, FileActivityInput, FileActivityRepository>
{
    @Autowired
    ActivityStateRepository activityStateRepository;

    @Override
    public List<FileActivity> getAll()
    {
        return repository.findAll();
    }

    @Override
    public FileActivity create(FileActivityInput entityInput)
    {
        var fileActivityOrder = getRepository().getMaxOrder();
        if (fileActivityOrder == null) {
            fileActivityOrder = 0;
        }
        fileActivityOrder++;
        var fileActivity = FileActivity.builder()
                .activity(Activity.builder().id(entityInput.getActivity().getId()).build())
                .file(File.builder().id(entityInput.getFile().getId()).build())
                .state(activityStateRepository.findFirstByInitialIsTrueAndActivity_Id(entityInput.getActivity().getId()))
                .current(true)
                .fileActivityOrder(fileActivityOrder)
                .build();
        if (entityInput.getDataFields() != null && !entityInput.getDataFields().isEmpty()) {
            var dataFields = entityInput.getDataFields().stream()
                    .map(x -> ActivityDataField.builder()
                            .data(x.getData())
                            .groupName(x.getGroupName())
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
    public FileActivity update(FileActivityInput entityInput)
    {
        return null;
    }

    @Override
    public boolean delete(long id)
    {
        repository.deleteById(id);
        return true;
    }

    @Override
    public FileActivity getById(long id)
    {
        return repository.findById(id).orElseThrow();
    }

    public List<FileActivity> getAllFileActivityByFileId(Long fileId)
    {
        return getRepository().findAllByFile_Id(fileId);
    }

    public ActivityState changeActivityState(Long activityStateId, Long fileActivityId)
    {
        var activityState = activityStateRepository.findById(activityStateId).orElseThrow();
        var fileActivity = getRepository().findById(fileActivityId).orElseThrow();
        fileActivity.setState(activityState);
        return activityState;
    }

    public List<FileActivity> getAllFileActivityByFileIdInTrash(Long fileId)
    {
        var fileActivityInTrash = getRepository().findAllByFile_Id_In_Trash(fileId);
        var fileActivityInTrashWithTask = getRepository().findAllByFile_Id_In_TrashWithTask(fileId);
        if (fileActivityInTrash.size() == 0) {
            return fileActivityInTrashWithTask;
        } else {
            if (fileActivityInTrashWithTask.size() == 0) {
                return fileActivityInTrash;
            } else {
                var allFileActivity = new ArrayList<FileActivity>();
                fileActivityInTrash.stream().filter(x -> fileActivityInTrashWithTask.stream().filter(f -> f.getId() == x.getId()).count() == 0).forEach(x -> allFileActivity.add(x));
                fileActivityInTrashWithTask.forEach(x -> allFileActivity.add(x));
                return allFileActivity;
            }
        }
    }

    public boolean sendFileActivityToTrash(Long fileActivityId)
    {
        var fileActivity = getRepository().getOne(fileActivityId);
        fileActivity.setInTrash(true);
        return true;
    }

    public boolean recoverFileActivityFromTrash(Long fileActivityId)
    {
        var fileActivity = getRepository().getOne(fileActivityId);
        fileActivity.setInTrash(false);
        return true;
    }

    public boolean fileActivityOrderUp(Long fileActivityId)
    {
        var fileActivity = getRepository().findById(fileActivityId).orElseThrow();
        var sourceOrder = fileActivity.getFileActivityOrder();
        if (sourceOrder <= 1) {
            return false;
        }
        int targetOrder = sourceOrder;
        while (targetOrder > 0) {
            targetOrder = targetOrder - 1;
            var previousFiles = getRepository().getFileByFileActivityOrder(targetOrder);
            if (!previousFiles.isEmpty()) {
                previousFiles.forEach(fileActivity1 -> fileActivity1.setFileActivityOrder(fileActivity1.getFileActivityOrder() + 1));
                fileActivity.setFileActivityOrder(targetOrder);
                return true;
            }
        }
        return false;
    }

    public boolean fileActivityOrderDown(Long fileActivityId)
    {
        var fileActivity = getRepository().findById(fileActivityId).orElseThrow();
        var sourceOrder = fileActivity.getFileActivityOrder();
        var maxOrder = getRepository().getMaxOrder();

        if (sourceOrder == maxOrder) {
            return false;
        }
        int targetOrder = sourceOrder;
        while (targetOrder <= maxOrder) {
            targetOrder = targetOrder + 1;
            var previousFiles = getRepository().getFileByFileActivityOrder(targetOrder);
            if (!previousFiles.isEmpty()) {
                previousFiles.forEach(fileActivity1 -> fileActivity1.setFileActivityOrder(fileActivity1.getFileActivityOrder() - 1));
                fileActivity.setFileActivityOrder(targetOrder);
                return true;
            }
        }
        return false;
    }
}
