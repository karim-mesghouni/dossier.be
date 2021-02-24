package com.softline.dossier.be.service;

import com.softline.dossier.be.domain.Agent;
import com.softline.dossier.be.domain.Client;
import com.softline.dossier.be.domain.Commune;
import com.softline.dossier.be.domain.File;
import com.softline.dossier.be.graphql.types.FileDTO;
import com.softline.dossier.be.graphql.types.FileFilterInput;
import com.softline.dossier.be.graphql.types.PageList;
import com.softline.dossier.be.graphql.types.input.AgentInput;
import com.softline.dossier.be.graphql.types.input.FileInput;
import com.softline.dossier.be.repository.AgentRepository;
import com.softline.dossier.be.repository.FileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class AgentService extends IServiceBase<Agent, AgentInput, AgentRepository> {

    @Override
    public List<Agent> getAll() {
        return  repository.findAll();
    }
    @Override
    public Agent create(AgentInput input) {
      return  null;
    }
    @Override
    public Agent update(AgentInput input) {
        return null;
    }
    @Override
    public boolean delete(long id) {
        repository.deleteById(id);
        return true;
    }
    @Override
    public Agent getById(long id) {
        return repository.findById(id).orElseThrow();
    }


}