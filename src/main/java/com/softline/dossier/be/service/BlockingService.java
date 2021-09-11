package com.softline.dossier.be.service;

import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.graphql.types.input.BlockingInput;
import com.softline.dossier.be.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class BlockingService extends IServiceBase<Blocking, BlockingInput, BlockingRepository> {
    @Autowired
    BlockingLockingAddressRepository blockingLockingAddressRepository;
    @Autowired
    BlockingLabelRepository blockingLabelRepository;
    @Autowired
    BlockingQualificationRepository blockingQualificationRepository;
    @Autowired
    FileTaskSituationRepository fileTaskSituationRepository;
    @Autowired
    FileTaskRepository fileTaskRepository;
    @Autowired
    TaskSituationRepository taskSituationRepository;

    @Override
    public List<Blocking> getAll() {
        return repository.findAll();
    }

    @Override
    public Blocking create(BlockingInput blockingInput) throws IOException {
        var currentFileTask = fileTaskRepository.findById(blockingInput.getState().getFileTask().getId()).orElseThrow();

        var situation = taskSituationRepository.findAllByTask_IdAndBlockIsTrue(currentFileTask.getTask().getId());
        var oldSituation = fileTaskSituationRepository.findFirstByFileTaskAndCurrentIsTrue(currentFileTask);

        var fileSituation = fileTaskSituationRepository.save(FileTaskSituation.builder()
                .fileTask(FileTask.builder().id(blockingInput.getState().getFileTask().getId()).build())
                .situation(situation)
                .current(true)
                .build());
        oldSituation.setCurrent(false);
        return repository.save(Blocking.builder()
                .label(BlockingLabel.builder().id(blockingInput.getLabel().getId()).build())
                .lockingAddress(BlockingLockingAddress.builder().id(blockingInput.getLockingAddress().getId()).build())
                .qualification(BlockingQualification.builder().id(blockingInput.getQualification().getId()).build())
                .explication(blockingInput.getExplication())
                .state(fileSituation)
                .date(blockingInput.getDate())
                .build());
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

