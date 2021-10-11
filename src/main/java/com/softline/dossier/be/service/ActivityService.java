package com.softline.dossier.be.service;

import com.softline.dossier.be.domain.Activity;
import com.softline.dossier.be.graphql.types.input.ActivityInput;
import com.softline.dossier.be.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional

public class ActivityService extends IServiceBase<Activity, ActivityInput, ActivityRepository>
{


    @Override
    public List<Activity> getAll()
    {
        return repository.findAll();
    }

    @Override
    public Activity create(ActivityInput entityInput)
    {
        return null;
    }

    @Override
    public Activity update(ActivityInput entityInput)
    {
        return null;
    }

    @Override
    public boolean delete(long id)
    {
        repository.deleteById(id);
        return true;
    }

    @Override
    public Activity getById(long id)
    {
        return repository.findById(id).orElseThrow();
    }
}
