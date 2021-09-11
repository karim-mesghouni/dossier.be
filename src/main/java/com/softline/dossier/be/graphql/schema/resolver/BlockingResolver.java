package com.softline.dossier.be.graphql.schema.resolver;

import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.graphql.types.input.BlockingInput;

import com.softline.dossier.be.graphql.types.input.FileInput;
import com.softline.dossier.be.repository.BlockingRepository;

import com.softline.dossier.be.service.BlockingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;


@Component
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class BlockingResolver extends SchemaResolverBase<Blocking, BlockingInput, BlockingRepository, BlockingService> {
    public Blocking createBlocking(BlockingInput blockingInput) throws IOException {
        return create(blockingInput);
    }
    public Blocking updateBlocking(BlockingInput blockingInput){
        return update(blockingInput);
    }
    public boolean deleteBlocking(Long id){
        return delete(id);
    }
    public List<Blocking> getAllBlocking(){
        return getAll();
    }
    public Blocking getBlocking(Long id){
        return get(id);
    }
    public List<BlockingQualification> getAllQualification(){
       return service.getAllQualification();
    }
    public List<BlockingLabel> getAllLables(){
        return service.getAllLables();
    }
    public  List<BlockingLockingAddress> getAllLockingAddress(){
        return  service.getAllLockingAddress();
    }
    public  List<Blocking> getBlockingByFileTaskId(Long fileTaskId){
        return service.getBlockingByFileTaskId(fileTaskId);
    }

}
