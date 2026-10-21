package com.qlytro.entity.converter;

import com.qlytro.entity.enums.TrangThaiToaNha;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TrangThaiToaNhaConverter implements AttributeConverter<TrangThaiToaNha, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TrangThaiToaNha attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public TrangThaiToaNha convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : TrangThaiToaNha.fromValue(dbData);
    }
}
