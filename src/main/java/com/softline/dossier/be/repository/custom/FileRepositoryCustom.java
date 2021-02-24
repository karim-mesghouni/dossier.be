package com.softline.dossier.be.repository.custom;

import com.softline.dossier.be.domain.File;
import com.softline.dossier.be.graphql.types.FileFilterInput;
import org.javatuples.Pair;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FileRepositoryCustom {

    Pair<Long,List<File>> getByFilter(FileFilterInput input);


}
