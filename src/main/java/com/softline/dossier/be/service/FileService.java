package com.softline.dossier.be.service;

import com.softline.dossier.be.Sse.model.EventDto;
import com.softline.dossier.be.Sse.service.SseNotificationService;
import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.graphql.types.FileFilterInput;
import com.softline.dossier.be.graphql.types.FileHistoryDTO;
import com.softline.dossier.be.graphql.types.PageList;
import com.softline.dossier.be.graphql.types.input.FileInput;
import com.softline.dossier.be.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Transactional
@Service
public class FileService extends IServiceBase<File, FileInput, FileRepository> {
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    FileStateTypeRepository fileStateTypeRepository;
    @Autowired
    FileStateRepository fileStateRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    CommuneRepository communeRepository;
    @Autowired
    ActivityStateRepository activityStateRepository;
    @Autowired
    SseNotificationService sseNotificationService;

    @Override
    public List<File> getAll() {
        return repository.findAll();
    }

    @Override
    public File create(FileInput input) throws IOException {
        File reprise = null;
        if (input.isFileReprise()) {
            reprise = getRepository().findById(input.getReprise().getId()).orElseThrow();
        }

        var file = File.builder()
                .project(input.getProject())
                .provisionalDeliveryDate(input.getProvisionalDeliveryDate())
                .attributionDate(input.getAttributionDate())
                .deliveryDate(input.getDeliveryDate())
                .returnDeadline(input.getReturnDeadline())
                .fileStates(new ArrayList<>())
                .reprise(reprise)
                .fileReprise(input.isFileReprise())
                .fileActivities(new ArrayList<>())
                .client(Client.builder().id(input.getClient().getId()).build())
                .commune(Commune.builder().id(input.getCommune().getId()).build()).build();
        var activity = activityRepository.findById(input.getBaseActivity().getId()).orElseThrow();
     /*   var fileActivity = FileActivity.builder()
                .file(file)
                .current(true)
                .state(activityStateRepository.findFirstByInitialIsTrueAndActivity_Id(activity.getId()))
                .activity(activity)
                .build();*/

      /*  if (activity.getFields() != null && !activity.getFields().isEmpty()) {
            var dataFields = activity.getFields().stream()
                    .map(x -> ActivityDataField.builder()
                            .data("")
                            .fieldName(x.getFieldName())
                            .groupName(x.getGroup() != null ? x.getGroup().getName() : null)
                            .fieldType(FieldType.valueOf(x.getFieldType().toString()))
                            .fileActivity(fileActivity)
                            .build()
                    );
            fileActivity.setDataFields(dataFields.collect(Collectors.toList()));
        }*/
        file.setBaseActivity(activity);
        // file.getFileActivities().add(fileActivity);
        if (input.getCurrentFileState() != null && input.getCurrentFileState().getType() != null && input.getCurrentFileState().getType().getId() != null) {

            file.getFileStates().add(FileState.builder()
                    .file(file)
                    .current(true)
                    .type(FileStateType.builder().id(input.getCurrentFileState().getType().getId()).build())
                    .build()

            );
        } else {
            var currentType = fileStateTypeRepository.findFirstByInitialIsTrue();
            file.getFileStates().add(FileState.builder()
                    .file(file)
                    .current(true)
                    .type(currentType)
                    .build()
            );
        }

        sseNotificationService.sendNotificationForAll(EventDto.builder().type("changedInBackend").body(1).build());

        return repository.save(file);
    }

    @Override
    public File update(FileInput input) {
        File reprise = null;
        if (input.isFileReprise()) {
            reprise = getRepository().findById(input.getReprise().getId()).orElseThrow();
        }
        var fileExist = repository.findById(input.getId()).orElseThrow();
        var baseActivity = activityRepository.findById(input.getBaseActivity().getId()).orElseThrow();
        fileExist.setClient(clientRepository.findById(input.getClient().getId()).orElseThrow());
        fileExist.setAttributionDate(input.getAttributionDate());
        fileExist.setBaseActivity(baseActivity);
        fileExist.setReturnDeadline(input.getReturnDeadline());
        fileExist.setProvisionalDeliveryDate(input.getProvisionalDeliveryDate());
        fileExist.setProject(input.getProject());
        fileExist.setReprise(reprise);
        fileExist.setFileReprise(input.isFileReprise());
        fileExist.setCommune(communeRepository.findById(input.getCommune().getId()).orElseThrow());
        var oldfileState = fileStateRepository.findFirstByCurrentIsTrueAndFile_Id(fileExist.getId());
        if (oldfileState != null && input.getCurrentFileState() != null && input.getCurrentFileState().getType() != null) {
            if (oldfileState.getType().getId() != input.getCurrentFileState().getType().getId()) {
                oldfileState.setCurrent(false);
                fileExist.getFileStates().add(FileState.builder()
                        .file(fileExist)
                        .type(fileStateTypeRepository.findById(input.getCurrentFileState().getType().getId()).orElseThrow())
                        .current(true)
                        .build()
                );
            }
        }
        return repository.save(fileExist);
    }

    @Override
    public boolean delete(long id) {
        repository.deleteById(id);
        return true;
    }

    @Override
    public File getById(long id) {
        return repository.findById(id).orElseThrow();
    }

    public PageList<File> getAllFilePageFilter(FileFilterInput input) {
        if(input.getPageSize() <= 0){ // return everything
            var result = getRepository().findAll();
            return new PageList<>(result, result.size());
        }else
        {
            var result = getRepository().getByFilter(input);
            return new PageList<>(result.getValue1(), result.getValue0());
        }
    }

    public List<FileHistoryDTO> getFileHistory(long id) {
        Comparator<FileHistoryDTO> dateComparator = new Comparator<FileHistoryDTO>() {
            @Override
            public int compare(FileHistoryDTO o1, FileHistoryDTO o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        };
        AtomicInteger i = new AtomicInteger();
        var history = new ArrayList<FileHistoryDTO>();
        var file = repository.findById(id).orElseThrow();
        history.add(FileHistoryDTO.builder().id(i.incrementAndGet())
                .who(file.getAgent().getName())
                .date(file.getCreatedDate())
                .message("create.file").data(file.getProject()).build());

        file.getFileActivities().forEach(x ->
                {
                    var fileActivity = FileHistoryDTO.builder().id(i.incrementAndGet())
                            .who(x.getAgent().getName())
                            .date(x.getCreatedDate())
                            .message("create.file.activity")
                            .data(x.getActivity().getName())
                            .children(new ArrayList<>()).build();
                    x.getFileTasks().forEach(fileTask -> {
                        var taskHistory = FileHistoryDTO.builder().id(i.incrementAndGet())
                                .who(fileTask.getAgent().getName())
                                .date(fileTask.getCreatedDate())
                                .message("create.file.task")
                                .data(fileTask.getTask().getName())
                                .children(new ArrayList<>()).build();
                        fileTask.getFileTaskSituations()
                                .stream()
                                .skip(1)
                                .forEach(fileTaskSituation -> {
                                    taskHistory.getChildren().add(FileHistoryDTO.builder().id(i.incrementAndGet())
                                            .who(fileTaskSituation.getAgent().getName())
                                            .date(fileTaskSituation.getCreatedDate())
                                            .message("change.File.task.situation")
                                            .data(fileTaskSituation.getSituation().getName())
                                            .children(new ArrayList<>()).build());

                                });
                        fileActivity.getChildren().add(taskHistory);
                    });
                    history.add(fileActivity);
                }
        );


        file.getFileStates().forEach(x ->
                {
                    var state = FileHistoryDTO.builder().id(i.incrementAndGet())
                            .who(x.getAgent().getName())
                            .date(x.getCreatedDate())
                            .message("change.file.state")
                            .data(x.getType().getState()).build();
                    history.add(state);
                }
        );
        history.sort(dateComparator);
        return history;

    }

    public List<FileStateType> getAllFileStateType() {
        return fileStateTypeRepository.findAll();
    }

    public PageList<File> getAllFileInTrashPageFilter(FileFilterInput input) {

        var allFile = new ArrayList<File>();
        var fileOnly = getRepository().getInTrashByFilter(input);
        var fileWithTask = getRepository().getInTrashByFilterWithTask(input);
        var fileWithActivity = getRepository().getInTrashByFilterWithActivity(input);
        if (fileWithTask.getValue1().size() == 0 && fileOnly.getValue1().size() == 0 && fileWithActivity.getValue1().size() > 0) {
            return new PageList<>(fileWithActivity.getValue1(), fileWithActivity.getValue0());
        }
        if (fileWithTask.getValue1().size() > 0 && fileOnly.getValue1().size() == 0 && fileWithActivity.getValue1().size() == 0) {
            return new PageList<>(fileWithTask.getValue1(), fileWithTask.getValue0());
        }
        if (fileWithTask.getValue1().size() == 0 && fileOnly.getValue1().size() > 0 && fileWithActivity.getValue1().size() == 0) {
            return new PageList<>(fileOnly.getValue1(), fileOnly.getValue0());
        }
        if (fileWithTask.getValue1().size() == 0 && fileOnly.getValue1().size() == 0 && fileWithActivity.getValue1().size() == 0) {
            return new PageList<>(new ArrayList<>(), 0);
        }
        if (fileWithTask.getValue1().size() > 0 && fileOnly.getValue1().size() > 0 && fileWithActivity.getValue1().size() == 0) {
            fileOnly.getValue1().stream().filter(x -> fileWithTask.getValue1().stream().filter(f -> f.getId() == x.getId()).count() == 0).forEach(x -> allFile.add(x));
            fileWithTask.getValue1().forEach(x -> allFile.add(x));
        } else if (fileWithTask.getValue1().size() == 0 && fileOnly.getValue1().size() > 0 && fileWithActivity.getValue1().size() > 0) {
            fileOnly.getValue1().stream().filter(x -> fileWithActivity.getValue1().stream().filter(f -> f.getId() == x.getId()).count() == 0).forEach(x -> allFile.add(x));
            fileWithActivity.getValue1().forEach(x -> allFile.add(x));
        } else if (fileWithTask.getValue1().size() > 0 && fileOnly.getValue1().size() > 0 && fileWithActivity.getValue1().size() > 0) {
            fileOnly.getValue1().stream().filter(x -> fileWithActivity.getValue1().stream().filter(f -> f.getId() == x.getId()).count() == 0
                    && fileWithTask.getValue1().stream().filter(f -> f.getId() == x.getId()).count() == 0).forEach(x -> allFile.add(x));
            fileWithActivity.getValue1().forEach(x -> allFile.add(x));
            fileWithTask.getValue1().forEach(x -> allFile.add(x));
        }
        return new PageList<>(allFile, allFile.size());

    }

    public boolean sendFileToTrash(Long fileId) {
        var file = getRepository().findById(fileId).orElseThrow();
        file.setInTrash(true);
        return true;
    }

    public boolean recoverFileFromTrash(Long fileId) {
        var file = getRepository().getOne(fileId);
        file.setInTrash(false);
        return true;
    }


}