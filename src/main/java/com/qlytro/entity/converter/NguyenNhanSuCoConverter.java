package com.qlytro.entity.converter;

import com.qlytro.entity.enums.NguyenNhanSuCo;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Cot nguyen_nhan la NULLABLE — xu ly null ca 2 chieu. */
@Converter(autoApply = true)
public class NguyenNhanSuCoConverter implements AttributeConverter<NguyenNhanSuCo, Integer> {

    @Override
    public Integer convertToDatabaseColumn(NguyenNhanSuCo attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public NguyenNhanSuCo convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : NguyenNhanSuCo.fromValue(dbData);
    }
}
