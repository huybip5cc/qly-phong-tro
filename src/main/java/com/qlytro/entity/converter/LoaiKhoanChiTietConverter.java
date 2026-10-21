package com.qlytro.entity.converter;

import com.qlytro.entity.enums.LoaiKhoanChiTiet;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LoaiKhoanChiTietConverter implements AttributeConverter<LoaiKhoanChiTiet, Integer> {

    @Override
    public Integer convertToDatabaseColumn(LoaiKhoanChiTiet attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public LoaiKhoanChiTiet convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : LoaiKhoanChiTiet.fromValue(dbData);
    }
}
