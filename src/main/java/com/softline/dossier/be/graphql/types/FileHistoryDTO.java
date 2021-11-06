package com.softline.dossier.be.graphql.types;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
public class FileHistoryDTO {
    long id;
    String who;
    LocalDateTime date;
    String message;
    String data;
    List<FileHistoryDTO> children;
}
