package com.softline.dossier.be.service.resolver;

import com.coxautodev.graphql.tools.GraphQLResolver;
import com.softline.dossier.be.domain.File;
import com.softline.dossier.be.domain.FileState;
import com.softline.dossier.be.repository.FileActivityRepository;
import com.softline.dossier.be.repository.FileStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileResolver implements GraphQLResolver<File>
{

    @Autowired
    FileStateRepository fileStateRepository;
    @Autowired
    FileActivityRepository fileActivityRepository;

    public FileState getCurrentFileState(File file)
    {
        var res = fileStateRepository.findFirstByCurrentIsTrueAndFile_Id(file.getId());
        return res;
    }

}
