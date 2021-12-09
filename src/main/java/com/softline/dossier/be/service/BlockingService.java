package com.softline.dossier.be.service;

import com.softline.dossier.be.Tools.ListUtils;
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
import java.util.Objects;
import java.util.stream.Collectors;

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
        var blockState = ListUtils.filterFirst(fileTask.getTask().getSituations(), TaskSituation::isBlock)
                .orElseThrow(() -> new EntityNotFoundException("Task does not have a block situation"));
        var fileSituation = Database.persist(FileTaskSituation.builder()
                .fileTask(block.getState().getFileTask())
                .situation(blockState)
                .current(true)
                .build());
        block.setState(fileSituation);
        Database.persist(block);
        fileSituation.setBlocking(block);
        fileTask.getFileTaskSituations().add(fileSituation);
        Database.commit();
        new FileTaskEvent(EntityEvent.Type.UPDATED, fileTask).fireToAll();
        return block;
    }

    public Blocking update(Blocking input) {
        var blocking = Database.findOrThrow(input);
        var blockingShadow = Blocking.builder()
                .date(blocking.getDate())
                .state(blocking.getState())
                .dateUnBlocked(blocking.getDateUnBlocked())
                .build();
        boolean wasBlocked = blockingShadow.getBlock();
        Database.startTransaction();
        var fileTask = blockingShadow.getState().getFileTask();
        DenyOrProceed("WORK_IN_FILE_TASK", fileTask);

        safeRun(() -> blocking.setLabel(Database.findOrThrow(input.getLabel())));
        safeRun(() -> blocking.setQualification(Database.findOrThrow(input.getQualification())));
        safeRun(() -> blocking.setLockingAddress(Database.findOrThrow(input.getLockingAddress())));

        blocking.setExplication(input.getExplication());
        blocking.setDate(input.getDate());
        blockingShadow.setDateUnBlocked(input.getDateUnBlocked());

        if (wasBlocked && !blockingShadow.getBlock()) {// it was blocked, and now he wants to "deBlock" the blocking
            // get all active blocks
            @SuppressWarnings("Convert2MethodRef")
            var blocks = fileTask.getFileTaskSituations().stream().map(fts -> fts.getBlocking()).filter(Objects::nonNull).filter(Blocking::getBlock).collect(Collectors.toList());
            if (blocks.size() == 1) {// if there is only one active block (which is the current block)
                // get the last non-blocking fileState
                FileTaskSituation oldNonBlockingSituation = fileTask.getFileTaskSituations().stream().filter(e -> !e.getSituation().isBlock()).min((b, a) -> (int) (a.getId() - b.getId())).get();
                blocking.getState().setCurrent(false);
                Database.persist(FileTaskSituation.builder()
                        .fileTask(oldNonBlockingSituation.getFileTask())
                        .current(true)
                        .situation(oldNonBlockingSituation.getSituation())
                        .build());
            } else {// there are more than one block, so we will keep the current "block fileTaskState"
                // just do nothing
            }
        } else {
            if (!wasBlocked && blockingShadow.getBlock()) {// the block is "deBlocked", and now he wants to activate it again
                // situation that is not blocked
                var oldSituation = Database.findOrThrow(fileTaskSituationRepository.findFirstByFileTaskAndCurrentIsTrue(fileTask));
                oldSituation.setCurrent(false);
                var newBlockingFileTaskSituation = Database.persist(FileTaskSituation.builder()
                        .fileTask(fileTask)
                        .blocking(blocking)
                        .situation(blocking.getState().getSituation())
                        .current(true)
                        .build());
                fileTask.getFileTaskSituations().add(newBlockingFileTaskSituation);
                blocking.setState(newBlockingFileTaskSituation);
            }
        }
        blocking.setDateUnBlocked(input.getDateUnBlocked());
        Database.commit();
        new FileTaskEvent(EntityEvent.Type.UPDATED, fileTask).fireToAll();
        return blockingShadow;
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

