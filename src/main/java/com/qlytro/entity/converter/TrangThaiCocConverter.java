package com.qlytro.entity.converter;

import com.qlytro.entity.enums.TrangThaiCoc;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TrangThaiCocConverter implements AttributeConverter<TrangThaiCoc, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TrangThaiCoc attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public TrangThaiCoc convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : TrangThaiCoc.fromValue(dbData);
    }
}
