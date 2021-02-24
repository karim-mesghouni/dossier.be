package com.softline.dossier.be.graphql.types;

import lombok.Data;

@Data
public class FileFilterInput {
   int pageNumber;
   int pageSize;
}
