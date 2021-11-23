package com.softline.dossier.be.graphql.schema.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.softline.dossier.be.domain.Blocking;
import com.softline.dossier.be.domain.BlockingLabel;
import com.softline.dossier.be.domain.BlockingLockingAddress;
import com.softline.dossier.be.domain.BlockingQualification;
import com.softline.dossier.be.graphql.types.input.BlockingInput;
import com.softline.dossier.be.service.BlockingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class BlockingResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    private final BlockingService service;

    public Blocking createBlocking(BlockingInput blockingInput) {
        return service.create(blockingInput.map());
    }

    public Blocking updateBlocking(BlockingInput blockingInput) {
        return service.update(blockingInput);
    }

    public boolean deleteBlocking(Long id) {
        return service.delete(id);
    }

    public List<Blocking> getAllBlocking() {
        return service.getAll();
    }

    public Blocking getBlocking(Long id) {
        return service.getById(id);
    }

    public List<BlockingQualification> getAllQualification() {
        return service.getAllQualification();
    }

    public List<BlockingLabel> getAllLabels() {
        return service.getAllLabels();
    }

    public List<BlockingLockingAddress> getAllLockingAddress() {
        return service.getAllLockingAddress();
    }

    public List<Blocking> getBlockingByFileTaskId(Long fileTaskId) {
        return service.getBlockingByFileTaskId(fileTaskId);
    }

}
