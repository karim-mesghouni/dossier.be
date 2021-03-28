package com.softline.dossier.be.service.resolver;

import com.coxautodev.graphql.tools.GraphQLResolver;
import com.softline.dossier.be.domain.Activity;
import com.softline.dossier.be.domain.DescriptionComment;
import com.softline.dossier.be.domain.FileTask;
import com.softline.dossier.be.domain.FileTaskSituation;
import com.softline.dossier.be.repository.FileTaskSituationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileTaskResolver implements GraphQLResolver<FileTask> {
    @Autowired
    FileTaskSituationRepository fileTaskSituationRepository;
   public FileTaskSituation getCurrentFileTaskSituation(FileTask fileTask){
         return  fileTaskSituationRepository.findFirstByFileTaskAndCurrentIsTrue(fileTask);
   }

}
