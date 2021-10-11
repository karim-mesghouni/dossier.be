package com.softline.dossier.be.service;

import com.softline.dossier.be.Sse.model.Event;
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
public class FileService extends IServiceBase<File, FileInput, FileRepository>
{
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
    public List<File> getAll()
    {
        return repository.findAll();
    }

    @Override
    public File create(FileInput input) throws IOException
    {
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
        file.setBaseActivity(activity);
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

        repository.save(file);
        sseNotificationService.sendNotificationForAll(new Event("fileAdded", file.getId()));
        return file;
    }

    @Override
    public File update(FileInput input)
    {
        File reprise = null;
        if (input.isFileReprise()) {
            reprise = getRepository().findById(input.getReprise().getId()).orElseThrow();
        }
        var file = repository.findById(input.getId()).orElseThrow();
        var baseActivity = activityRepository.findById(input.getBaseActivity().getId()).orElseThrow();
        file.setClient(clientRepository.findById(input.getClient().getId()).orElseThrow());
        file.setAttributionDate(input.getAttributionDate());
        file.setBaseActivity(baseActivity);
        file.setReturnDeadline(input.getReturnDeadline());
        file.setProvisionalDeliveryDate(input.getProvisionalDeliveryDate());
        file.setProject(input.getProject());
        file.setReprise(reprise);
        file.setFileReprise(input.isFileReprise());
        file.setCommune(communeRepository.findById(input.getCommune().getId()).orElseThrow());
        var oldfileState = fileStateRepository.findFirstByCurrentIsTrueAndFile_Id(file.getId());
        if (oldfileState != null && input.getCurrentFileState() != null && input.getCurrentFileState().getType() != null) {
            if (oldfileState.getType().getId() != input.getCurrentFileState().getType().getId()) {
                oldfileState.setCurrent(false);
                file.getFileStates().add(FileState.builder()
                        .file(file)
                        .type(fileStateTypeRepository.findById(input.getCurrentFileState().getType().getId()).orElseThrow())
                        .current(true)
                        .build()
                );
            }
        }
        repository.save(file);
        sseNotificationService.sendNotificationForAll(new Event("fileUpdated", file.getId()));
        return file;
    }

    @Override
    public boolean delete(long id)
    {
        // TODO: fix this issue
        // files should not be removed (only trashed for now)
        // we need their order when changing the order of other files
        return false;
    }

    @Override
    public File getById(long id)
    {
        repository.flush();
        return repository.findById(id).orElseThrow();
    }

    public PageList<File> getAllFilePageFilter(FileFilterInput input)
    {
        if (input.getPageSize() <= 0) { // return everything
            var result = getRepository().findAll();
            return new PageList<>(result, result.size());
        } else {
            var result = getRepository().getByFilter(input);
            return new PageList<>(result.getValue1(), result.getValue0());
        }
    }

    public List<FileHistoryDTO> getFileHistory(long id)
    {
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
                    x.getFileTasks().forEach(fileTask ->
                    {
                        var taskHistory = FileHistoryDTO.builder().id(i.incrementAndGet())
                                .who(fileTask.getAgent().getName())
                                .date(fileTask.getCreatedDate())
                                .message("create.file.task")
                                .data(fileTask.getTask().getName())
                                .children(new ArrayList<>()).build();
                        fileTask.getFileTaskSituations()
                                .stream()
                                .skip(1)
                                .forEach(fileTaskSituation ->
                                {
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
        history.sort(Comparator.comparing(FileHistoryDTO::getDate));
        return history;

    }

    public List<FileStateType> getAllFileStateType()
    {
        return fileStateTypeRepository.findAll();
    }

    public PageList<File> getAllFileInTrashPageFilter(FileFilterInput input)
    {

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
        } else {
            if (fileWithTask.getValue1().size() == 0 && fileOnly.getValue1().size() > 0 && fileWithActivity.getValue1().size() > 0) {
                fileOnly.getValue1().stream().filter(x -> fileWithActivity.getValue1().stream().filter(f -> f.getId() == x.getId()).count() == 0).forEach(x -> allFile.add(x));
                fileWithActivity.getValue1().forEach(x -> allFile.add(x));
            } else {
                if (fileWithTask.getValue1().size() > 0 && fileOnly.getValue1().size() > 0 && fileWithActivity.getValue1().size() > 0) {
                    fileOnly.getValue1().stream().filter(x -> fileWithActivity.getValue1().stream().filter(f -> f.getId() == x.getId()).count() == 0
                            && fileWithTask.getValue1().stream().filter(f -> f.getId() == x.getId()).count() == 0).forEach(x -> allFile.add(x));
                    fileWithActivity.getValue1().forEach(x -> allFile.add(x));
                    fileWithTask.getValue1().forEach(x -> allFile.add(x));
                }
            }
        }
        return new PageList<>(allFile, allFile.size());

    }

    public boolean sendFileToTrash(Long fileId)
    {
        var file = getRepository().findById(fileId).orElseThrow();
        file.setInTrash(true);
        sseNotificationService.sendNotificationForAll(new Event("fileTrashed", file.getId()));
        return true;
    }

    public boolean recoverFileFromTrash(Long fileId)
    {
        var file = getRepository().getOne(fileId);
        file.setInTrash(false);
        return true;
    }

    /**
     * change the order of a file,
     * will be called when the user changes the order of a file in the FilesView
     * in the case when fileBeforeId is not existent the file will be moved to be the first item in the list
     *
     * @param fileId       the file(id) that we want to change its order
     * @param fileBeforeId the file(id) which should be before the new position of the file, may be non-existent
     * @return boolean
     */
    public boolean changeOrder(Long fileId, Long fileBeforeId)
    {
        if (repository.count() < 2) {
            return true;// this should not happen
        }
        var file = repository.getOne(fileId);
        var res = repository.findById(fileBeforeId);
        // TODO: convert this logic into Mutating queries in JPA
        if (res.isPresent()) {
            var fileBefore = res.get();
            // how many files will be updated (increment or decrement their order)
            var levelsChange = repository.countAllByOrderBetween(file.getOrder(), fileBefore.getOrder());
            if (file.getOrder() < fileBefore.getOrder()) {// file is moving down the list
                repository.findAllByOrderAfter(file.getOrder())
                        .stream()
                        .limit(levelsChange + 1)
                        .forEach(File::decrementOrder);
                file.setOrder(fileBefore.getOrder() + 1);
            } else {// file is moving up the list
                var allAfter = repository.findAllByOrderAfter(fileBefore.getOrder());
                allAfter.stream()
                        .limit(levelsChange)
                        .forEach(File::incrementOrder);
                file.setOrder(repository.findAllByOrderAfter(fileBefore.getOrder()).stream().findFirst().get().getOrder() - 1);
            }
        } else {// file should be the first item in the list
            var allBefore = repository.findAllByOrderBefore(file.getOrder());
            file.setOrder(allBefore.stream().findFirst().get().getOrder());// gets the order of the old first file
            allBefore.forEach(File::incrementOrder);
        }
        return true;
    }
}