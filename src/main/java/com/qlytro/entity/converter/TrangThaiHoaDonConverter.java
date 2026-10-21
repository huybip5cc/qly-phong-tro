package com.qlytro.entity.converter;

import com.qlytro.entity.enums.TrangThaiHoaDon;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TrangThaiHoaDonConverter implements AttributeConverter<TrangThaiHoaDon, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TrangThaiHoaDon attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public TrangThaiHoaDon convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : TrangThaiHoaDon.fromValue(dbData);
    }
}
