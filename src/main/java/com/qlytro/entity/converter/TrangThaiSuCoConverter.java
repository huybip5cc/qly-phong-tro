package com.qlytro.entity.converter;

import com.qlytro.entity.enums.TrangThaiSuCo;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TrangThaiSuCoConverter implements AttributeConverter<TrangThaiSuCo, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TrangThaiSuCo attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public TrangThaiSuCo convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : TrangThaiSuCo.fromValue(dbData);
    }
}
