package com.softline.dossier.be.repository.custom;

import com.softline.dossier.be.domain.File;
import com.softline.dossier.be.graphql.types.FileFilterInput;
import org.javatuples.Pair;

import java.util.List;

public interface FileRepositoryCustom {

    Pair<Long, List<File>> getByFilter(FileFilterInput input);


    Pair<Long, List<File>> getInTrashByFilter(FileFilterInput input);

    Pair<Long, List<File>> getInTrashByFilterWithActivity(FileFilterInput input);

    Pair<Long, List<File>> getInTrashByFilterWithTask(FileFilterInput input);
}
