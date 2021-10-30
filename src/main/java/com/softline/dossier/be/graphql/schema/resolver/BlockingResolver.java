package com.softline.dossier.be.graphql.schema.resolver;

import com.softline.dossier.be.domain.Blocking;
import com.softline.dossier.be.domain.BlockingLabel;
import com.softline.dossier.be.domain.BlockingLockingAddress;
import com.softline.dossier.be.domain.BlockingQualification;
import com.softline.dossier.be.graphql.types.input.BlockingInput;
import com.softline.dossier.be.repository.BlockingRepository;
import com.softline.dossier.be.service.BlockingService;
import com.softline.dossier.be.service.exceptions.ClientReadableException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;


@Component
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class BlockingResolver extends SchemaResolverBase<Blocking, BlockingInput, BlockingRepository, BlockingService> {
    public Blocking createBlocking(BlockingInput blockingInput) throws IOException, ClientReadableException {
        return create(blockingInput);
    }

    public Blocking updateBlocking(BlockingInput blockingInput) throws ClientReadableException {
        return update(blockingInput);
    }

    public boolean deleteBlocking(Long id) throws ClientReadableException {
        return delete(id);
    }

    public List<Blocking> getAllBlocking() {
        return getAll();
    }

    public Blocking getBlocking(Long id) {
        return get(id);
    }

    public List<BlockingQualification> getAllQualification() {
        return service.getAllQualification();
    }

    public List<BlockingLabel> getAllLables() {
        return service.getAllLables();
    }

    public List<BlockingLockingAddress> getAllLockingAddress() {
        return service.getAllLockingAddress();
    }

    public List<Blocking> getBlockingByFileTaskId(Long fileTaskId) {
        return service.getBlockingByFileTaskId(fileTaskId);
    }

}
