package com.softline.dossier.be.graphql.schema.resolver;

import com.softline.dossier.be.Halpers.Functions;
import com.softline.dossier.be.domain.Job;
import com.softline.dossier.be.graphql.types.input.AgentInput;
import com.softline.dossier.be.repository.ActivityRepository;
import com.softline.dossier.be.repository.JobRepository;
import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.security.domain.Role;
import com.softline.dossier.be.security.repository.AgentRepository;
import com.softline.dossier.be.security.repository.RoleRepository;
import com.softline.dossier.be.service.AgentService;
import com.softline.dossier.be.service.exceptions.ClientReadableException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class AgentSchemaResolver extends SchemaResolverBase<Agent, AgentInput, AgentRepository, AgentService>
{
    private static final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final RoleRepository roleRepository;
    private final JobRepository jobRepository;
    private final ActivityRepository activityRepository;
    private final ModelMapper modelMapper;

    public boolean deleteAgent(Long id) throws ClientReadableException
    {
        return delete(id);
    }

    public List<Agent> getAllAgent()
    {
        return getAll();
    }

    public Agent getAgent(Long id)
    {
        return get(id);
    }

    public Agent getCurrentAgent()
    {
        return service.getCurrentAgent();
    }

    public List<Role> allRoles()
    {
        return roleRepository.findAll();
    }

    public List<Job> allJobs()
    {
        return jobRepository.findAll();
    }


    public Agent updateAgent(AgentInput input)
    {
        Agent agent = service.getRepository().getOne(input.getId());
        Functions.safeRun(() -> agent.setActivity(activityRepository.getOne(input.getActivity().getId())));
        Functions.safeRun(() -> agent.setJob(jobRepository.getOne(input.getJob().getId())));
        Functions.safeRun(() -> agent.setRole(roleRepository.getOne(input.getRole().getId())));
        Functions.safeRun(() -> input.getPassword().length() > 0 && !agent.getPassword().equals(input.getPassword()),
                () -> agent.setPassword(passwordEncoder.encode(input.getPassword())));
        return agent;
    }

    public Agent createAgent(AgentInput input)
    {
        input.setPassword(passwordEncoder.encode(input.getPassword()));
        var agent = modelMapper.map(input, Agent.class);
        return service.getRepository().save(agent);
    }

    public boolean deleteAgent(long id)
    {
        service.getRepository().deleteById(id);
        return true;
    }
}
