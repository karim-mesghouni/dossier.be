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
        return null;
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

