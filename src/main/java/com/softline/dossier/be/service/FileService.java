package com.softline.dossier.be.service;

import com.google.common.base.MoreObjects;
import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.events.FileEvent;
import com.softline.dossier.be.events.types.EntityEvent;
import com.softline.dossier.be.graphql.types.FileFilterInput;
import com.softline.dossier.be.graphql.types.FileHistoryDTO;
import com.softline.dossier.be.graphql.types.PageList;
import com.softline.dossier.be.graphql.types.input.FileInput;
import com.softline.dossier.be.repository.*;
import com.softline.dossier.be.security.config.AbacPermissionEvaluator;
import com.softline.dossier.be.security.domain.Agent;
import lombok.RequiredArgsConstructor;
import org.javatuples.Pair;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.softline.dossier.be.Halpers.Functions.*;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService extends IServiceBase<File, FileInput, FileRepository> {
    private final ActivityRepository activityRepository;
    private final FileStateTypeRepository fileStateTypeRepository;
    private final FileStateRepository fileStateRepository;
    private final ClientRepository clientRepository;
    private final CommuneRepository communeRepository;
    private final ActivityStateRepository activityStateRepository;
    private final AbacPermissionEvaluator permissionEvaluator;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<File> getAll() {
        return null;
    }

    @Override
    @PreAuthorize("hasPermission(#input, 'CREATE_FILE')")
    @Transactional
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

        safeRunWithFallback(() -> file.setReprise(repository.findById(input.getReprise().getId()).orElseThrow()),
                () -> file.setReprise(null));
        file.setBaseActivity(activityRepository.findById(input.getBaseActivity().getId()).orElseThrow());
        var stateBuilder = FileState.builder()
                .file(file)
                .current(true);
        safeRunWithFallback(() -> file.getFileStates().add(stateBuilder.type(fileStateTypeRepository.findById(input.getCurrentFileState().getType().getId()).orElseThrow()).build()),
                () -> file.getFileStates().add(stateBuilder.type(fileStateTypeRepository.findFirstByInitialIsTrue()).build()));

        file.setOrder(repository.minOrder());
        repository.incrementAllOrder();
        repository.saveAndFlush(file);
        new FileEvent(EntityEvent.Event.ADDED, file).fireToAll();
        return file;
    }

    @Override
    @PreAuthorize("hasPermission(#input.id, 'File', 'UPDATE_FILE')")
    public File update(FileInput input) {
        var file = repository.findById(input.getId()).orElseThrow();
        if (safeRun(() -> throwIfEmpty(input.getReprise().getId()))) {
            file.setReprise(repository.findById(input.getReprise().getId()).orElseThrow());
        } else {
            file.setReprise(null);
        }
        var baseActivity = activityRepository.findById(input.getBaseActivity().getId()).orElseThrow();
        file.setClient(clientRepository.findById(input.getClient().getId()).orElseThrow());
        file.setAttributionDate(input.getAttributionDate());
        file.setBaseActivity(baseActivity);
        file.setReturnDeadline(input.getReturnDeadline());
        file.setProvisionalDeliveryDate(input.getProvisionalDeliveryDate());
        file.setProject(input.getProject());
        file.setCommune(communeRepository.findById(input.getCommune().getId()).orElseThrow());
        var oldFileState = fileStateRepository.findFirstByCurrentIsTrueAndFile_Id(file.getId());
        if (oldFileState != null && input.getCurrentFileState() != null && input.getCurrentFileState().getType() != null) {
            if (oldFileState.getType().getId() != input.getCurrentFileState().getType().getId()) {
                oldFileState.setCurrent(false);
                file.getFileStates().add(FileState.builder()
                        .file(file)
                        .agent((Agent) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                        .type(fileStateTypeRepository.findById(input.getCurrentFileState().getType().getId()).orElseThrow())
                        .current(true)
                        .build()
                );
            }
        }
        repository.saveAndFlush(file);
        new FileEvent(EntityEvent.Event.UPDATED, file).fireToAll();
        return file;
    }

    @Override
    public boolean delete(long id) {
        // files should not be removed (only trashed for now)
        return false;
    }

    @Override
    public File getById(long id) {
        repository.flush();
        return repository.findById(id).orElseThrow();
    }

    public PageList<File> getAllFilePageFilter(FileFilterInput input) {
        var result = getByFilter(input);
        var filtered = result.getValue1().stream().filter(f -> permissionEvaluator.hasPermission(SecurityContextHolder.getContext().getAuthentication(), f, "READ_FILE")).collect(Collectors.toList());
        return new PageList<>(filtered, result.getValue0() - (result.getValue1().size() - filtered.size()));
    }

    @PreAuthorize("hasPermission(#id, 'File', 'READ_FILE')")
    public List<FileHistoryDTO> getFileHistory(long id) {
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

    public List<FileStateType> getAllFileStateType() {
        return fileStateTypeRepository.findAll();
    }

    @PreAuthorize("hasPermission(#fileId, 'File', 'DELETE_FILE')")
    public boolean sendFileToTrash(Long fileId) {
        var file = getRepository().findById(fileId).orElseThrow();
        file.setInTrash(true);
        new FileEvent(EntityEvent.Event.TRASHED, file).fireToAll();
        return true;
    }

    @PreAuthorize("hasPermission(#fileId, 'File', 'DELETE_FILE')")
    public boolean recoverFileFromTrash(Long fileId) {
        var file = getRepository().getOne(fileId);
        file.setInTrash(false);
        new FileEvent(EntityEvent.Event.RECOVERED, file).fireToAll();
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
    @Transactional
    public synchronized boolean changeOrder(Long fileId, Long fileBeforeId) {
        if (repository.count() < 2) {
            return true;// this should not happen
        }
        var file = repository.findById(fileId).orElseThrow();
        var res = repository.findById(fileBeforeId);
        // TODO: convert this logic into Mutating queries in JPA
        res.ifPresentOrElse(fileBefore -> {
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
        }, () -> {// else if fileBefore does not exist
            var allBefore = repository.findAllByOrderBefore(file.getOrder());
            file.setOrder(allBefore.stream().findFirst().get().getOrder());// gets the order of the old first file
            allBefore.forEach(File::incrementOrder);
        });
        return true;
    }


    private <T> TypedQuery<T> withParameters(FileFilterInput input, TypedQuery<T> query) {
        return query.setParameter("project", MoreObjects.firstNonNull(input.project, ""))
                .setParameter("clientId", MoreObjects.firstNonNull(input.client.getId(), 0))
                .setParameter("activityId", MoreObjects.firstNonNull(input.activity.getId(), 0))
                .setParameter("stateId", MoreObjects.firstNonNull(input.state.getType().getId(), 0))
                .setParameter("adf", input.attributionDate.getFrom())
                .setParameter("adt", input.attributionDate.getTo())
                .setParameter("ddf", input.deliveryDate.getFrom())
                .setParameter("ddt", input.deliveryDate.getTo())
                .setParameter("pddt", input.provisionalDeliveryDate.getTo())
                .setParameter("pddf", input.provisionalDeliveryDate.getFrom())
                .setParameter("isReprise", input.reprise)
                .setParameter("isNotReprise", input.notReprise)
                ;
    }

    private <T> Function<String, TypedQuery<T>> buildSelector(FileFilterInput input, Class<T> clazz) {
        return (String sel) ->
        {
            String query = "SELECT distinct " + sel + " FROM File f inner join f.fileStates fs on fs.file.id = f.id ";
            if (input.onlyTrashed) {
                query += "inner join f.fileActivities fa inner join fa.fileTasks ft ";
            }
            query += "where f.project like CONCAT(CONCAT('%', :project), '%') " +
                    "and :activityId in(0, f.baseActivity.id) " +
                    "and :clientId in(0, f.client.id) " +
                    "and (fs.current=true " +
                    "and :stateId in(0, fs.type.id)) " +
                    "and f.provisionalDeliveryDate between :pddf and :pddt " +
                    "and f.attributionDate between :adf and :adt " +
                    "and f.deliveryDate between :ddf and :ddt " +
                    "and ((:isReprise = true  and :isNotReprise = false and f.reprise is not null) " +
                    "  or (:isReprise = false and :isNotReprise = true  and f.reprise is null) " +
                    "  or (:isReprise = false and :isNotReprise=false)) ";
            if (input.onlyTrashed) {
                query += "and (f.inTrash=true or fa.inTrash=true or ft.inTrash=true) ";
            } else {
                query += "and f.inTrash=false ";
            }
            if (!sel.equals("f.id")) {
                query += "order by f.order";
            }
            return withParameters(input, entityManager.createQuery(query, clazz));
        };
    }

    private Pair<Long, List<File>> getByFilter(FileFilterInput input) {
        var qList = buildSelector(input, File.class).apply("f");
        if (input.pageSize > 0) {
            qList
                    .setFirstResult((input.pageNumber - 1) * input.pageSize)
                    .setMaxResults(input.pageSize);
        }
        return new Pair<>(
                buildSelector(input, Long.class).apply("f.id").getResultStream().count(),
                qList.getResultList()
        );
    }
}