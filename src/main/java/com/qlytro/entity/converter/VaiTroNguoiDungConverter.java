package com.qlytro.entity.converter;

import com.qlytro.entity.enums.VaiTroNguoiDung;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class VaiTroNguoiDungConverter implements AttributeConverter<VaiTroNguoiDung, Integer> {

    @Override
    public Integer convertToDatabaseColumn(VaiTroNguoiDung attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public VaiTroNguoiDung convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : VaiTroNguoiDung.fromValue(dbData);
    }
}
