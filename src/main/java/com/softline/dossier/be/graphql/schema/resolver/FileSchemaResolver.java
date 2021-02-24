package com.softline.dossier.be.graphql.schema.resolver;

import com.softline.dossier.be.domain.File;
import com.softline.dossier.be.graphql.types.FileDTO;
import com.softline.dossier.be.graphql.types.FileFilterInput;
import com.softline.dossier.be.graphql.types.FileHistoryDTO;
import com.softline.dossier.be.graphql.types.PageList;
import com.softline.dossier.be.graphql.types.input.FileInput;
import com.softline.dossier.be.repository.FileRepository;
import com.softline.dossier.be.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FileSchemaResolver extends SchemaResolverBase<File, FileInput, FileRepository, FileService> {


    public File createFile(FileInput File){
        return create(File);
    }
    public File updateFile(FileInput File){
        return update(File);
    }
    public boolean deleteFile(Long id){
        return delete(id);
    }
    public List<File> getAllFile(){
        return getAll();
    }
    public File getFile(Long id){
        return get(id);
    }
    public PageList<FileDTO> getAllFilePageFilter(FileFilterInput input){
       return  service.getAllFilePageFilter(input);
     }
    public List<FileHistoryDTO> getFileHistory(Long id){
            return  service.getFileHistory(id);
    }

}
