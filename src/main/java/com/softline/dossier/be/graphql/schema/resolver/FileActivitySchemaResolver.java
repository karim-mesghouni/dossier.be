package com.softline.dossier.be.graphql.schema.resolver;

import com.softline.dossier.be.domain.FileActivity;
import com.softline.dossier.be.graphql.types.input.FileActivityInput;
import com.softline.dossier.be.repository.FileActivityRepository;
import com.softline.dossier.be.service.FileActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FileActivitySchemaResolver extends SchemaResolverBase<FileActivity,FileActivityInput, FileActivityRepository, FileActivityService> {


    public FileActivity createFileActivity(FileActivityInput FileActivity){
        return create(FileActivity);
    }
    public FileActivity updateFileActivity(FileActivityInput FileActivity){
        return update(FileActivity);
    }
    public boolean deleteFileActivity(Long id){
        return delete(id);
    }
    protected List<FileActivity> getAllFileActivity(){
        return getAll();
    }
    protected FileActivity getFileActivity(Long id){
        return get(id);
    }
    public List<FileActivity>  getAllFileActivityByFileId(Long fileId){
        return  service.getAllFileActivityByFileId(fileId);
    }
  public boolean changeValid( boolean valid,Long fileActivityId){
        return  service.changeValid(valid,fileActivityId);
  }
}
