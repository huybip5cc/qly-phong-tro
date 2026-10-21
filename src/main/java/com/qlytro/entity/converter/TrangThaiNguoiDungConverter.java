package com.qlytro.entity.converter;

import com.qlytro.entity.enums.TrangThaiNguoiDung;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TrangThaiNguoiDungConverter implements AttributeConverter<TrangThaiNguoiDung, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TrangThaiNguoiDung attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public TrangThaiNguoiDung convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : TrangThaiNguoiDung.fromValue(dbData);
    }
}
