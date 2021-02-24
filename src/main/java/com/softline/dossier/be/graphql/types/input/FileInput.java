package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.FileState;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.File;
import java.util.Date;
import java.util.List;

@SuperBuilder

@Data
@NoArgsConstructor
public class FileInput {

    private Long id;

    String project;

    Date attributionDate;

    Date returnDeadline;

    Date provisionalDeliveryDate;

    Date deliveryDate;

    String cem;

    ClientInput client;
    CommuneInput commune;

    List<FileDocInput> fileDocs;
    List<FileActivityInput> fileStates;

}
