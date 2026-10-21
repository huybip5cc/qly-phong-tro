package com.qlytro.entity.converter;

import com.qlytro.entity.enums.NguonPhatHienBoiThuong;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class NguonPhatHienBoiThuongConverter implements AttributeConverter<NguonPhatHienBoiThuong, Integer> {

    @Override
    public Integer convertToDatabaseColumn(NguonPhatHienBoiThuong attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public NguonPhatHienBoiThuong convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : NguonPhatHienBoiThuong.fromValue(dbData);
    }
}
