package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.ActivityDataField;
import com.softline.dossier.be.domain.Concerns.HasId;
import com.softline.dossier.be.graphql.types.input.enums.FieldTypeInput;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Getter
public class ActivityDataFieldInput extends Input<ActivityDataField> implements HasId {
    Class<ActivityDataField> mappingTarget = ActivityDataField.class;

    Long id;
    String fieldName;
    @Setter
    FieldTypeInput fieldType;
    String data;
    String groupName;
    FileActivityInput fileActivity;

    /**
     * try to cast the data, if the dataCasting failed it means that the input string is not of the correct type
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void tryCastData() throws NumberFormatException, DateTimeParseException {
        switch (fieldType) {
            case String:
                return;
            case Number:
                Double.valueOf(data);
                return;
            case Date:
                LocalDate.parse(data);
        }
    }
}
