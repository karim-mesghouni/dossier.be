package com.softline.dossier.be.service;

import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.events.EntityEvent;
import com.softline.dossier.be.events.entities.FileTaskEvent;
import com.softline.dossier.be.graphql.types.input.BlockingInput;
import com.softline.dossier.be.repository.BlockingRepository;
import com.softline.dossier.be.repository.FileTaskSituationRepository;
import com.softline.dossier.be.repository.TaskSituationRepository;
import graphql.GraphQLException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.softline.dossier.be.Tools.Functions.safeRun;
import static com.softline.dossier.be.security.config.AttributeBasedAccessControlEvaluator.DenyOrProceed;

@Service
@RequiredArgsConstructor
public class BlockingService {
    private final FileTaskSituationRepository fileTaskSituationRepository;
    private final TaskSituationRepository taskSituationRepository;
    private final BlockingRepository repository;

    public List<Blocking> getAll() {
        return Database.findAll(Blocking.class);
    }

    public Blocking getById(long id) {
        return Database.findOrThrow(Blocking.class, id);
    }

    public List<BlockingQualification> getAllQualification() {
        return Database.findAll(BlockingQualification.class);
    }

    public List<BlockingLabel> getAllLabels() {
        return Database.findAll(BlockingLabel.class);
    }

    public List<BlockingLockingAddress> getAllLockingAddress() {
        return Database.findAll(BlockingLockingAddress.class);
    }

    public List<Blocking> getBlockingByFileTaskId(Long fileTaskId) {
        return repository.findAllByState_FileTask_Id(fileTaskId);
    }

    public Blocking create(Blocking input) {
        var currentFileTask = Database.findOrThrow(input.getState().getFileTask());
        DenyOrProceed("WORK_IN_FILE_TASK", currentFileTask);
        Database.startTransaction();
        var oldSituation = Database.findOrThrow(fileTaskSituationRepository.findFirstByFileTaskAndCurrentIsTrue(currentFileTask));
        var blockState = Database.findOrThrow(taskSituationRepository.findAllByTask_IdAndBlockIsTrue(currentFileTask.getTask().getId()));
        var fileSituation = Database.persist(FileTaskSituation.builder()
                .fileTask(FileTask.builder().id(input.getState().getFileTask().getId()).build())
                .situation(blockState)
                .current(true)
                .build());
        oldSituation.setCurrent(false);
        var block = Database.persist(Blocking.builder()
                .id(input.getId())
                .label(BlockingLabel.builder().id(input.getLabel().getId()).build())
                .lockingAddress(BlockingLockingAddress.builder().id(input.getLockingAddress().getId()).build())
                .qualification(BlockingQualification.builder().id(input.getQualification().getId()).build())
                .explication(input.getExplication())
                .dateUnBlocked(input.getDateUnBlocked())
                .state(fileSituation)
                .date(input.getDate())
                .build());
        Database.commit();
        new FileTaskEvent(EntityEvent.Type.UPDATED, currentFileTask).fireToAll();
        return block;
    }

    public Blocking update(BlockingInput input) {
        var blocking = Database.findOrThrow(Blocking.class, input);
        boolean wasBlocked = blocking.getBlock();
        Database.startTransaction();
        var fileTask = blocking.getState().getFileTask();
        DenyOrProceed("WORK_IN_FILE_TASK", fileTask);

        safeRun(() -> blocking.setLabel(Database.findOrThrow(BlockingLabel.class, input.getLabel())));
        safeRun(() -> blocking.setQualification(Database.findOrThrow(BlockingQualification.class, input.getQualification())));
        safeRun(() -> blocking.setLockingAddress(Database.findOrThrow(BlockingLockingAddress.class, input.getLockingAddress())));
        FileTaskSituation oldState = fileTask.getFileTaskSituations().stream().filter(e -> !e.getSituation().isBlock()).min((a, b) -> (int) (a.getId() - b.getId())).get();

        blocking.setExplication(input.getExplication());
        blocking.setDateUnBlocked(input.getDateUnBlocked());
        blocking.setDate(input.getDate());

        if (wasBlocked && !blocking.getBlock()) {
            var currentState = fileTask.getCurrentState();
            currentState.setCurrent(false);
            fileTaskSituationRepository.save(FileTaskSituation.builder().blocking(oldState.getBlocking()).fileTask(oldState.getFileTask()).current(true).situation(oldState.getSituation()).build());
        } else {
            if (!wasBlocked && blocking.getBlock()) {
                var oldSituation = Database.findOrThrow(fileTaskSituationRepository.findFirstByFileTaskAndCurrentIsTrue(fileTask));
                TaskSituation blockState = Database.findOrThrow(taskSituationRepository.findAllByTask_IdAndBlockIsTrue(fileTask.getTask().getId()));
                var fileSituation = Database.persist(FileTaskSituation.builder()
                        .fileTask(FileTask.builder().id(fileTask.getId()).build())
                        .situation(blockState)
                        .current(true)
                        .build());
                blocking.setState(fileSituation);
                oldSituation.setCurrent(false);
            }
        }
        Database.commit();
        new FileTaskEvent(EntityEvent.Type.UPDATED, fileTask).fireToAll();
        return blocking;
    }

    public boolean delete(long id) {
        var block = Database.findOrThrow(Blocking.class, id);
        if (block.getBlock()) {
            throw new GraphQLException("veuillez débloquer le blocage avant de le supprimer");
        }
        DenyOrProceed("WORK_IN_FILE_TASK", block.getState().getFileTask());
        Database.removeNow(Blocking.class, block.getId());
        new FileTaskEvent(EntityEvent.Type.UPDATED, block.getState().getFileTask()).fireToAll();
        return true;
    }
}

