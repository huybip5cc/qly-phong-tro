package com.qlytro.entity.converter;

import com.qlytro.entity.enums.TrangThaiChiSo;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TrangThaiChiSoConverter implements AttributeConverter<TrangThaiChiSo, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TrangThaiChiSo attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public TrangThaiChiSo convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : TrangThaiChiSo.fromValue(dbData);
    }
}
