package com.softline.dossier.be.service;

import com.softline.dossier.be.domain.Commune;
import com.softline.dossier.be.graphql.types.input.CommuneInput;
import com.softline.dossier.be.repository.CommuneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional

@Service
public class CommuneService extends IServiceBase<Commune, CommuneInput, CommuneRepository>
{
    @Override
    public List<Commune> getAll()
    {
        return repository.findAll();
    }

    @Override
    public Commune create(CommuneInput communeInput)
    {
        return null;
    }

    @Override
    public Commune update(CommuneInput communeInput)
    {
        return null;
    }

    @Override
    public boolean delete(long id)
    {
        return false;
    }

    @Override
    public Commune getById(long id)
    {
        return null;
    }
}
