package com.running.runapp.domain.ghostRun.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class GhostCategoryConverter implements AttributeConverter<GhostCategory, String> {

    @Override
    public String convertToDatabaseColumn(GhostCategory attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public GhostCategory convertToEntityAttribute(String dbData) {
        return dbData == null ? null : GhostCategory.from(dbData);
    }
}
