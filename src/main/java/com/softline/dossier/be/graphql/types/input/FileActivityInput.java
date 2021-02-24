package com.softline.dossier.be.graphql.types.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.File;
import java.util.List;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileActivityInput  {

    private long id ;

    boolean current;


    FileStateTypeInput type;

    FileInput file;

    ActivityInput activity;
    List<ActivityDataFieldInput> dataFields;
  //  List<RepriseInput> reprises;
    List<FilePhaseInput> filePhases;
}
