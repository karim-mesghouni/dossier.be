package com.softline.dossier.be.taskmanger.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class IntegerListToStringConverter implements AttributeConverter<List<Integer>, String> {
    @Override
    public String convertToDatabaseColumn(List<Integer> attribute) {
        return attribute.stream().map(Object::toString).collect(Collectors.joining(","));
    }

    @Override
    public List<Integer> convertToEntityAttribute(String dbData) {
        return Arrays.stream(dbData.split(",")).filter(s -> s.length() > 0).map(Integer::parseInt).collect(Collectors.toList());
    }
}
