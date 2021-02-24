package com.softline.dossier.be.service;

import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.graphql.GQLExpetion;
import com.softline.dossier.be.graphql.types.FileDTO;
import com.softline.dossier.be.graphql.types.FileFilterInput;
import com.softline.dossier.be.graphql.types.PageList;
import com.softline.dossier.be.graphql.types.input.FileInput;
import com.softline.dossier.be.graphql.types.input.FilePhaseAgentInput;
import com.softline.dossier.be.graphql.types.input.FilePhaseInput;
import com.softline.dossier.be.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class FilePhaseService extends IServiceBase<FilePhase, FilePhaseInput, FilePhaseRepository> {
    @Autowired
    PhaseRepository phaseRepository;
    @Autowired
    FilePhaseStateRepository filePhaseStateRepository;
    @Autowired
    FilePhaseAgentRepository filePhaseAgentRepository;
    @Autowired
    AgentRepository agentRepository;
    @Autowired
    PhaseStateRepository phaseStateRepository;

    @Override
    public List<FilePhase> getAll() {
        return repository.findAll();
    }

    @Override
    public FilePhase create(FilePhaseInput input) {
        return null;
    }

    @Override
    public FilePhase update(FilePhaseInput input) {
        return null;
    }

    @Override
    public boolean delete(long id) {
        repository.deleteById(id);
        return true;
    }

    @Override
    public FilePhase getById(long id) {
        return repository.findById(id).orElseThrow();
    }

    public FilePhase getCurrentFilePhase(Long fileActivityId) {
        return getRepository().getFilePhaseByFileActivity_IdAndCurrentIsTrue(fileActivityId);
    }

    public FilePhaseState getCurrentFilePhaseState(Long fileAgentId) {
        return filePhaseStateRepository.getFilePhaseStateByPhaseAgent_IdAndCurrentIsTrue(fileAgentId);
    }

    public FilePhaseAgent getCurrentFilePhaseAgent(Long filePhaseId) {
        return filePhaseAgentRepository.getFilePhaseAgentByFilePhase_IdAndCurrentIsTrue(filePhaseId);
    }

    public FilePhase ChangeFilePhase(Long phaseId, Long fileActivityId) {
        var phase = phaseRepository.findById(phaseId);
        if (!phase.isPresent())
            throw new GQLExpetion("phase not present ");
        var oldFilePhase = getRepository().getFilePhaseByFileActivity_IdAndCurrentIsTrue(fileActivityId);

        if (oldFilePhase != null) {
            if (oldFilePhase.getPhase().getId() == phaseId) return oldFilePhase;
            oldFilePhase.setCurrent(false);

        }
        var oldFilePhaseNoTCurrent = getRepository().getFilePhaseByFileActivity_IdAndCurrentIsFalseAndPhase_Id(fileActivityId, phaseId);
        if (oldFilePhaseNoTCurrent != null) {
            oldFilePhaseNoTCurrent.setCurrent(true);
            //TODO return code
            return oldFilePhaseNoTCurrent;
        }


        var filePhase = FilePhase.builder()
                .fileActivity(FileActivity.builder().id(fileActivityId).build())
                .current(true)
                .phase(phase.get()).build();

        var filePhaseAgentStates = new ArrayList<FilePhaseAgent>();
        var filePhaseAgent = FilePhaseAgent.builder().
                filePhase(filePhase).current(true).build();
        var filePhaseState = FilePhaseState.builder()
                .state(phase.get().getStates().stream().filter(x -> x.isInitial()).findFirst().orElse(phase.get().getStates().stream().findFirst().orElse(null)))
                .current(true)
                .phaseAgent(filePhaseAgent)
                .build();
        if (filePhaseState.getState() != null) {
            var filePhaseStates = new ArrayList<FilePhaseState>();
            filePhaseStates.add(filePhaseState);
            filePhaseAgent.setFilePhaseStates(filePhaseStates);
            filePhaseAgentStates.add(filePhaseAgent);
            filePhase.setFilePhaseAgents(filePhaseAgentStates);
        }
        return getRepository().save(
                filePhase
        );
    }

    public FilePhaseAgent ChangeAgent(Long agentId, Long phaseFileAgentId) {
        var oldFilePhaseAgent = filePhaseAgentRepository.findById(phaseFileAgentId);
        var agent = agentRepository.findById(agentId);
        if (!oldFilePhaseAgent.isPresent())
            throw new GQLExpetion("filePhaseAgent not present ");
        if (!agent.isPresent())
            throw new GQLExpetion("agent not present ");
        if (oldFilePhaseAgent.get().getAssignedTo() == null) {
            oldFilePhaseAgent.get().setAssignedTo(agent.get());
            return oldFilePhaseAgent.get();
        }
        if (oldFilePhaseAgent.get().getFilePhase() != null) {
            if (oldFilePhaseAgent.get().getAssignedTo() != null && oldFilePhaseAgent.get().getAssignedTo().getId() == agent.get().getId())
                return oldFilePhaseAgent.get();
            oldFilePhaseAgent.get().setCurrent(false);
        }
        var oldFilePhaseAgentNoTCurrent = filePhaseAgentRepository.getFilePhaseAgentByFilePhase_IdAndCurrentIsFalseAndAndAssignedTo_Id(oldFilePhaseAgent.get().getFilePhase().getId(), agentId);
        if (oldFilePhaseAgentNoTCurrent != null) {
            oldFilePhaseAgentNoTCurrent.setCurrent(true);
            //TODO return code
            return oldFilePhaseAgentNoTCurrent;

        }

        var filePhaseAgent = FilePhaseAgent.builder().
                filePhase(oldFilePhaseAgent.get().getFilePhase())
                .assignedTo(agent.get())
                .current(true).build();
        var filePhaseState = FilePhaseState.builder()
                .state(oldFilePhaseAgent.get().getFilePhase().getPhase().getStates().stream().filter(x -> x.isInitial()).findFirst().orElse(oldFilePhaseAgent.get().getFilePhase().getPhase().getStates().stream().findFirst().orElse(null)))
                .current(true)
                .phaseAgent(filePhaseAgent)
                .build();
        if (filePhaseState.getState() != null) {
            var filePhaseStates = new ArrayList<FilePhaseState>();
            filePhaseStates.add(filePhaseState);
            filePhaseAgent.setFilePhaseStates(filePhaseStates);
        }
        return filePhaseAgentRepository.save(filePhaseAgent);
    }

    public FilePhaseState changePhaseState(Long stateId, Long phaseFileAgentId) {
        var oldState = filePhaseStateRepository.getFilePhaseStateByPhaseAgent_IdAndCurrentIsTrue(phaseFileAgentId);
        var statePhase = phaseStateRepository.findById(stateId);
        if (!statePhase.isPresent())
            throw new GQLExpetion("state Phase not present ");
        if (oldState != null) {
            if (oldState.getState().getId() == statePhase.get().getId()) return oldState;
            oldState.setCurrent(false);
        }
        var newState = FilePhaseState.builder()
                .state(statePhase.get())
                .current(true)
                .phaseAgent(FilePhaseAgent.builder().id(phaseFileAgentId).build())
                .build();
        return filePhaseStateRepository.save(newState);
    }

    public FilePhaseAgent updateFilePhaseAgent(FilePhaseAgentInput agent) {
        var filePhaseAgent = filePhaseAgentRepository.findById(agent.getId()).orElseThrow();
        filePhaseAgent.setDueDate(agent.getDueDate());
        filePhaseAgent.setToStartDate(agent.getToStartDate());
        if (agent.getReporter().getId()!=null)
             filePhaseAgent.setReporter(Agent.builder().id(agent.getReporter().getId()).build());
        return filePhaseAgent;
    }
}