package com.softline.dossier.be.service;

import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.events.EntityEvent;
import com.softline.dossier.be.events.entities.FileTaskEvent;
import com.softline.dossier.be.repository.BlockingRepository;
import com.softline.dossier.be.repository.FileTaskSituationRepository;
import com.softline.dossier.be.repository.TaskSituationRepository;
import graphql.GraphQLException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
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

    public Blocking create(Blocking block) {
        var fileTask = Database.findOrThrow(block.getState().getFileTask());
        DenyOrProceed("WORK_IN_FILE_TASK", fileTask);
        Database.startTransaction();
        Database.findOrThrow(fileTaskSituationRepository.findFirstByFileTaskAndCurrentIsTrue(fileTask))
                .setCurrent(false);
        var blockState = fileTask.getTask().getSituations()
                .stream().filter(TaskSituation::isBlock).findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Task does not have a block situation"));
        var fileSituation = Database.persist(FileTaskSituation.builder()
                .fileTask(block.getState().getFileTask())
                .situation(blockState)
                .current(true)
                .build());
        block.setState(fileSituation);
        Database.persist(block);
        fileSituation.setBlocking(block);
        Database.commit();
        new FileTaskEvent(EntityEvent.Type.UPDATED, fileTask).fireToAll();
        return block;
    }

    public Blocking update(Blocking input) {
        var blocking = Database.findOrThrow(input);
        boolean wasBlocked = blocking.getBlock();
        Database.startTransaction();
        var fileTask = blocking.getState().getFileTask();
        DenyOrProceed("WORK_IN_FILE_TASK", fileTask);

        safeRun(() -> blocking.setLabel(Database.findOrThrow(input.getLabel())));
        safeRun(() -> blocking.setQualification(Database.findOrThrow(input.getQualification())));
        safeRun(() -> blocking.setLockingAddress(Database.findOrThrow(input.getLockingAddress())));

        blocking.setExplication(input.getExplication());
        blocking.setDateUnBlocked(input.getDateUnBlocked());
        blocking.setDate(input.getDate());

        if (wasBlocked && !blocking.getBlock()) {// it was blocked, and now he wants to "deBlock" the blocking
            // get the last non-blocking fileState
            FileTaskSituation oldNonBlockingSituation = fileTask.getFileTaskSituations().stream().filter(e -> !e.getSituation().isBlock()).min((b, a) -> (int) (a.getId() - b.getId())).get();
            blocking.getState().setCurrent(false);
            Database.persist(FileTaskSituation.builder()
                    .fileTask(oldNonBlockingSituation.getFileTask())
                    .current(true)
                    .situation(oldNonBlockingSituation.getSituation())
                    .build());
        } else {
            if (!wasBlocked && blocking.getBlock()) {// the block is "deBlocked", and now he wants to activate it again
                // situation that is not blocked
                var oldSituation = Database.findOrThrow(fileTaskSituationRepository.findFirstByFileTaskAndCurrentIsTrue(fileTask));
                oldSituation.setCurrent(false);
                var newBlockingFileTaskSituation = Database.persist(FileTaskSituation.builder()
                        .fileTask(fileTask)
                        .blocking(blocking)
                        .situation(blocking.getState().getSituation())
                        .current(true)
                        .build());
                blocking.setState(newBlockingFileTaskSituation);
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

