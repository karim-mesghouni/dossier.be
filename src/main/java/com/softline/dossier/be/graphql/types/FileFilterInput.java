package com.softline.dossier.be.graphql.types;

import lombok.Data;

import java.sql.Date;
import java.time.LocalDate;

@Data
public class FileFilterInput {
   int pageNumber;
   int pageSize;
   String  project;
   Long clientId;
   Long  activityId;
   Long  stateId;
   LocalDate   attributionDateFrom;
   LocalDate attributionDateTo;
   LocalDate   returnDeadlineFrom;
   LocalDate  returnDeadlineTo;
   LocalDate   provisionalDeliveryDateFrom;
   LocalDate  provisionalDeliveryDateTo;
   LocalDate deliveryDateFrom;
   LocalDate  deliveryDateTo;
   FileType fileType  ;
}
