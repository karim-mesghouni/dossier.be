package com.softline.dossier.be.service;

import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.database.OrderManager;
import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.events.EntityEvent;
import com.softline.dossier.be.events.entities.FileEvent;
import com.softline.dossier.be.graphql.types.FileFilterInput;
import com.softline.dossier.be.graphql.types.FileHistoryDTO;
import com.softline.dossier.be.graphql.types.PageList;
import com.softline.dossier.be.graphql.types.input.FileInput;
import com.softline.dossier.be.repository.FileRepository;
import com.softline.dossier.be.security.domain.Agent;
import lombok.RequiredArgsConstructor;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.softline.dossier.be.Tools.Functions.*;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService {
    private final FileRepository repository;

    @PostFilter("hasPermission(filterObject, 'READ_FILE')")
    public List<File> getAll() {
        return Database.findAll(File.class);
    }

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

        safeRunWithFallback(() -> file.setReprise(Database.findOrThrow(File.class, input.getReprise())),
                () -> file.setReprise(null));
        file.setBaseActivity(Database.findOrThrow(Activity.class, input.getBaseActivity()));
        var stateBuilder = FileState.builder()
                .file(file)
                .current(true);
        safeRunWithFallback(() -> file.getFileStates().add(stateBuilder.type(Database.findOrThrow(FileStateType.class, input.getCurrentFileState().getType())).build()),
                () -> file.getFileStates().add(stateBuilder.type(Database.getSingle("SELECT t FROM FileStateType t where t.initial = true", FileStateType.class)).build()));

        file.setOrder(repository.minOrder());
        repository.incrementAllOrder();
        Database.persist(file);
        new FileEvent(EntityEvent.Type.ADDED, file).fireToAll();
        return file;
    }

    public File update(FileInput input) {
        return Database.findOrThrow(File.class, input, "UPDATE_FILE", file -> {
            if (isEmpty(() -> file.getReprise().getId()) != isEmpty(() -> input.getReprise().getId())) {
                file.setReprise(safeValue(() -> Database.findOrThrow(File.class, input.getReprise())));
            }
            var baseActivity = Database.findOrThrow(Activity.class, input.getBaseActivity());
            file.setClient(Database.findOrThrow(Client.class, input.getClient()));
            file.setAttributionDate(input.getAttributionDate());
            file.setBaseActivity(baseActivity);
            file.setReturnDeadline(input.getReturnDeadline());
            file.setProvisionalDeliveryDate(input.getProvisionalDeliveryDate());
            file.setProject(input.getProject());
            file.setCommune(Database.findOrThrow(Commune.class, input.getCommune()));
            var oldFileState = Database.querySingle("SELECT fs FROM FileState fs where fs.current = true and fs.file.id = :fileId", FileState.class).setParameter("fileId", file.getId()).getSingleResult();
            if (oldFileState != null && input.getCurrentFileState() != null && input.getCurrentFileState().getType() != null) {
                if (oldFileState.getType().getId() != input.getCurrentFileState().getType().getId()) {
                    oldFileState.setCurrent(false);
                    file.getFileStates().add(FileState.builder()
                            .file(file)
                            .agent((Agent) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                            .type(Database.findOrThrow(FileStateType.class, input.getCurrentFileState().getType()))
                            .current(true)
                            .build()
                    );
                }
            }
            Database.flush();
            new FileEvent(EntityEvent.Type.UPDATED, file).fireToAll();
            return file;
        });
    }

    public boolean delete(long id) {
        // files should not be removed (only trashed for now)
        return false;
    }

    public File getById(long id) {
        return Database.findOrThrow(File.class, id);
    }

    @PreAuthorize("hasPermission(#id, 'File', 'READ_FILE')")
    public List<FileHistoryDTO> getFileHistory(long id) {
        AtomicInteger i = new AtomicInteger();
        var history = new ArrayList<FileHistoryDTO>();
        var file = Database.findOrThrow(File.class, id);
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
        return Database.findAll(FileStateType.class);
    }

    @PreAuthorize("hasPermission(#fileId, 'File', 'DELETE_FILE')")
    public boolean sendFileToTrash(Long fileId) {
        var file = Database.findOrThrow(File.class, fileId);
        file.setInTrash(true);
        Database.flush();
        new FileEvent(EntityEvent.Type.TRASHED, file).fireToAll();
        return true;
    }

    @PreAuthorize("hasPermission(#fileId, 'File', 'DELETE_FILE')")
    public boolean recoverFileFromTrash(Long fileId) {
        var file = Database.findOrThrow(File.class, fileId);
        file.setInTrash(false);
        Database.flush();
        new FileEvent(EntityEvent.Type.RECOVERED, file).fireToAll();
        return true;
    }

    public boolean changeOrder(Long fileId, Long fileBeforeId) {
        OrderManager.changeOrder(Database.findOrThrow(File.class, fileId), Database.findOrNull(File.class, fileBeforeId), null);
        return true;
    }

    public PageList<File> getAllFilesByFilter(FileFilterInput filter) {
        var q = buildQuery("f", filter, File.class);
        if (filter.pageSize > 1 && filter.pageNumber > 0) {
            q.setMaxResults(filter.pageSize).setFirstResult((filter.pageNumber - 1) * filter.pageSize);
        }
        return new PageList<>(
                q.getResultList(),
                buildQuery("f.id", filter, Long.class).getResultStream().count()
        );
    }


    @NotNull
    private <T> TypedQuery<T> buildQuery(String select, FileFilterInput filter, Class<T> type) {
        @Language("HQL")
        String query = "SELECT distinct " + select + " FROM File f left join f.fileStates fs " +
                "left join f.fileActivities fa left join fa.fileTasks ft " +
                "where (f.project like CONCAT('%', :project, '%') or :project='') " +
                "and :activityId in(0, f.baseActivity.id) " +
                "and :clientId in(0, f.client.id) " +
                "and :isAdmin = true " +
                "and (fs.current = true and :stateId in(0, fs.type.id) or size(f.fileStates) = 0) ";
        var dates = new HashMap<String, LocalDate>();
        if (filter.attributionDate.from != null || filter.attributionDate.to != null) {
            query += "and f.attributionDate between :adf and :adt ";
            dates.put("adf", filter.attributionDate.getFrom());
            dates.put("adt", filter.attributionDate.getTo());
        }
        if (filter.deliveryDate.from != null || filter.deliveryDate.to != null) {
            query += "and f.deliveryDate between :ddf and :ddt ";
            dates.put("ddf", filter.deliveryDate.getFrom());
            dates.put("ddt", filter.deliveryDate.getTo());
        }
        if (filter.provisionalDeliveryDate.from != null || filter.provisionalDeliveryDate.to != null) {
            query += "and f.provisionalDeliveryDate between :pddf and :pddt ";
            dates.put("pddf", filter.provisionalDeliveryDate.getFrom());
            dates.put("pddt", filter.provisionalDeliveryDate.getTo());
        }
        if (filter.returnDeadline.from != null || filter.returnDeadline.to != null) {
            query += "and f.returnDeadline between :rdf and :rdt ";
            dates.put("rdf", filter.returnDeadline.getFrom());
            dates.put("rdt", filter.returnDeadline.getTo());
        }
        query += "and ((:isReprise = true  and :isNotReprise = false and f.reprise is not null) " +
                "  or (:isReprise = false and :isNotReprise = true  and f.reprise is null) " +
                "  or (:isReprise = false and :isNotReprise = false)) ";

        if (filter.onlyTrashed) {
            query += "and (f.inTrash = true or fa.inTrash = true or ft.inTrash = true) ";
        } else {
            query += "and f.inTrash = false and (fa.inTrash = false or size(f.fileActivities) = 0) and (ft.inTrash = false or size(fa.fileTasks) = 0 or size(f.fileActivities) = 0) ";
        }
        if (type == File.class) {
            query += "order by f.order";
        }
        var q = Database.query(query, type)
                .setParameter("project", firstNonNull(filter.project, ""))
                .setParameter("clientId", firstNonNull(filter.client.getId(), 0))
                .setParameter("activityId", firstNonNull(filter.activity.getId(), 0))
                .setParameter("stateId", firstNonNull(filter.state.getType().getId(), 0))
                .setParameter("isReprise", filter.reprise)
                .setParameter("isNotReprise", filter.notReprise)
                .setParameter("isAdmin", Agent.thisAgent().isAdmin() || Agent.thisAgent().getRole().getName().equals("REFERENT"));
        dates.forEach(q::setParameter);
        return q;
    }
}
