package com.softline.dossier.be.service;

import com.softline.dossier.be.Sse.model.EventDto;
import com.softline.dossier.be.Sse.service.SseNotificationService;
import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.domain.enums.FieldType;
import com.softline.dossier.be.graphql.types.FileDTO;
import com.softline.dossier.be.graphql.types.FileFilterInput;
import com.softline.dossier.be.graphql.types.FileHistoryDTO;
import com.softline.dossier.be.graphql.types.PageList;
import com.softline.dossier.be.graphql.types.input.FileInput;
import com.softline.dossier.be.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
        return (List<File>) repository.findAll();
    }

    @Override
    public File create(FileInput input) throws IOException {
        var file = File.builder()
                .project(input.getProject())
                .provisionalDeliveryDate(input.getProvisionalDeliveryDate())
                .attributionDate(input.getAttributionDate())
                .deliveryDate(input.getDeliveryDate())
                .returnDeadline(input.getReturnDeadline())
                .fileStates(new ArrayList<>())
                .fileActivities(new ArrayList<>())
                .client(Client.builder().id(input.getClient().getId()).build())
                .commune(Commune.builder().id(input.getCommune().getId()).build()).build();
        var activity = activityRepository.findById(input.getCurrentFileActivity().getActivity().getId()).orElseThrow();
        var fileActivity = FileActivity.builder()
                .file(file)
                .current(true)
                .state(activityStateRepository.findFirstByInitialIsTrueAndActivity_Id(activity.getId()))
                .activity(activity)
                .build();

        if (activity.getFields() != null && !activity.getFields().isEmpty()) {
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
        }
        file.setBaseActivity(activity);
        file.getFileActivities().add(fileActivity);
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

        var fileExist = repository.findById(input.getId()).orElseThrow();
        fileExist.setClient(clientRepository.findById(input.getClient().getId()).orElseThrow());
        fileExist.setAttributionDate(input.getAttributionDate());
        fileExist.setReturnDeadline(input.getReturnDeadline());
        fileExist.setDeliveryDate(input.getDeliveryDate());
        fileExist.setProject(input.getProject());
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
        return (File) repository.findById(id).orElseThrow();
    }

    public PageList<File> getAllFilePageFilter(FileFilterInput input) {
        var result = getRepository().getByFilter(input);
        return new PageList<>(result.getValue1(), result.getValue0());

    }

    public List<FileHistoryDTO> getFileHistory(long id) {
     /*   Comparator<FileHistoryDTO> dateComparator = new Comparator<FileHistoryDTO>() {
            @Override
            public int compare(FileHistoryDTO o1, FileHistoryDTO o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        };
        AtomicInteger i= new AtomicInteger();
        var history=new ArrayList<FileHistoryDTO>();
         var file=   repository.findById(id).orElseThrow();
        history.add(FileHistoryDTO.builder().id(i.incrementAndGet())
                .who("Agent 1")
                .date(file.getCreatedDate())
                .message("create File").build());
        file.getFileStates().forEach(x->
        {
            var state=FileHistoryDTO.builder().id(i.incrementAndGet())
                    .who("Agent 1")
                    .date(x.getCreatedDate())
                    .message("change file to " + x.getActivity().getName()).build();
            state.setChildren(new ArrayList());
            x.getFilePhases().forEach(filePhase->{
                state.getChildren().add(FileHistoryDTO.builder().id(i.incrementAndGet())
                        .who("Agent 1")
                        .date(filePhase.getCreatedDate())
                        .message("change  filePhase "+x.getActivity().getName()+" to " + filePhase.getPhase().getName()).build());
                filePhase.getFilePhaseAgents().forEach(phaseAgent->{
                    phaseAgent.getFilePhaseStates().forEach(filePhaseState -> {
                        state.getChildren().add(FileHistoryDTO.builder().id(i.incrementAndGet())
                                .who("Agent 1")
                                .date(filePhaseState.getCreatedDate())
                                .message("change  file Phase "+filePhase.getPhase().getName()+" to " + filePhaseState.getState().getState()).build());
                        state.getChildren().sort(dateComparator);
                    });

                });
            });
            history.add(state);
         }
        );
        history.sort(dateComparator);
        return  history;*/
        return null;

    }

    public List<FileStateType> getAllFileStateType() {
        return fileStateTypeRepository.findAll();
    }

}