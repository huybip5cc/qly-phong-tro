package com.qlytro.entity.converter;

import com.qlytro.entity.enums.TrangThaiPhong;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TrangThaiPhongConverter implements AttributeConverter<TrangThaiPhong, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TrangThaiPhong attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public TrangThaiPhong convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : TrangThaiPhong.fromValue(dbData);
    }
}
