package com.softline.dossier.be.service;

import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.graphql.types.input.BlockingInput;
import com.softline.dossier.be.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockingService extends IServiceBase<Blocking, BlockingInput, BlockingRepository> {
    private final BlockingLockingAddressRepository blockingLockingAddressRepository;
    private final BlockingLabelRepository blockingLabelRepository;
    private final BlockingQualificationRepository blockingQualificationRepository;
    private final FileTaskSituationRepository fileTaskSituationRepository;
    private final FileTaskRepository fileTaskRepository;
    private final TaskSituationRepository taskSituationRepository;
    @Override
    public List<Blocking> getAll() {
        return repository.findAll();
    }

    @Override
    public Blocking create(BlockingInput blockingInput) {
        var currentFileTask = fileTaskRepository.findById(blockingInput.getState().getFileTask().getId()).orElseThrow();

        FileTaskSituation oldSituation = fileTaskSituationRepository.findFirstByFileTaskAndCurrentIsTrue(currentFileTask);
        TaskSituation blockState = taskSituationRepository.findAllByTask_IdAndBlockIsTrue(currentFileTask.getTask().getId());

        var fileSituation = fileTaskSituationRepository.save(FileTaskSituation.builder()
                .fileTask(FileTask.builder().id(blockingInput.getState().getFileTask().getId()).build())
                .situation(blockState)
                .current(true)
                .build());
        oldSituation.setCurrent(false);
        return repository.save(Blocking.buildFromInput(blockingInput, fileSituation));
    }
    @Override
    public Blocking update(BlockingInput blockingInput) {
        Blocking blocking = repository.getOne(blockingInput.getId());
        boolean wasBlocked = blocking.getBlock();
        blocking = repository.save(Blocking.buildFromInput(blockingInput, blocking.getState()));
        FileTask fileTask = blocking.getState().getFileTask();
        FileTaskSituation oldState = fileTask.getFileTaskSituations().stream().filter(e -> !e.getSituation().isBlock()).min((a, b) -> (int) (a.getId() - b.getId())).get();
        FileTaskSituation currentState = fileTask.getCurrentState();
        if(wasBlocked && !blocking.getBlock())
        {
            currentState.setCurrent(false);
            fileTaskSituationRepository.save(currentState);
            fileTaskSituationRepository.save(FileTaskSituation.builder().blocking(oldState.getBlocking()).fileTask(oldState.getFileTask()).current(true).situation(oldState.getSituation()).build());
        }else if(!wasBlocked && blocking.getBlock()){
            FileTaskSituation oldSituation = fileTaskSituationRepository.findFirstByFileTaskAndCurrentIsTrue(fileTask);
            TaskSituation blockState = taskSituationRepository.findAllByTask_IdAndBlockIsTrue(fileTask.getTask().getId());
            var fileSituation = fileTaskSituationRepository.save(FileTaskSituation.builder()
                    .fileTask(FileTask.builder().id(fileTask.getId()).build())
                    .situation(blockState)
                    .current(true)
                    .build());
            blocking.setState(fileSituation);
            oldSituation.setCurrent(false);
            return repository.save(blocking);
        }
        return blocking;
    }

    @Override
    public boolean delete(long id) {
        repository.deleteById(id);
        return true;
    }

    @Override
    public Blocking getById(long id) {
        return repository.getOne(id);
    }

    public List<BlockingQualification> getAllQualification() {
        return blockingQualificationRepository.findAll();
    }

    public List<BlockingLabel> getAllLables() {
        return blockingLabelRepository.findAll();
    }

    public List<BlockingLockingAddress> getAllLockingAddress() {
        return blockingLockingAddressRepository.findAll();

    }

    public List<Blocking> getBlockingByFileTaskId(Long fileTaskId) {
        return repository.findAllByState_FileTask_Id(fileTaskId);
    }
}

