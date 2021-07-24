package com.softline.dossier.be.graphql.types;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
public class FileHistoryDTO {
    long id;
    String who;
    Date date;
    String message;
    List<FileHistoryDTO> children;
}
