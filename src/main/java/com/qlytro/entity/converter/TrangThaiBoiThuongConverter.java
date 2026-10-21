package com.qlytro.entity.converter;

import com.qlytro.entity.enums.TrangThaiBoiThuong;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TrangThaiBoiThuongConverter implements AttributeConverter<TrangThaiBoiThuong, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TrangThaiBoiThuong attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public TrangThaiBoiThuong convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : TrangThaiBoiThuong.fromValue(dbData);
    }
}
