package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.graphql.types.input.enums.FieldTypeInput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityDataFieldInput
{


    long id;
    String fieldName;

    FieldTypeInput fieldType;
    String data;
    String groupName;
    FileActivityInput fileActivity;
}
