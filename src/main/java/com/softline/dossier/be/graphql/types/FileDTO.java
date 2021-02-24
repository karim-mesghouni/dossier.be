package com.softline.dossier.be.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileDTO {
    Long idFile;

    String project;

    Date attributionDate;

    Date returnDeadline;

    Date provisionalDeliveryDate;

    Date deliveryDate;

    String cem;
    String activityName;
    Long fileActivityId;
    Long fileStateId;

}
