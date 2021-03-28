package com.softline.dossier.be.graphql.types;

import lombok.Data;

import java.sql.Date;

@Data
public class FileFilterInput {
   int pageNumber;
   int pageSize;
   String  project;
   Long clientId;
   Long  activityId;
   Long  stateId;
   Date   attributionDateFrom;
   Date attributionDateTo;
   Date   returnDeadlineFrom;
   Date  returnDeadlineTo;
   Date   provisionalDeliveryDateFrom;
   Date  provisionalDeliveryDateTo;
   Date  deliveryDateFrom;
   Date  deliveryDateTo;
}
