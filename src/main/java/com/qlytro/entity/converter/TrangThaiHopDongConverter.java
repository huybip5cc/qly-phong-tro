package com.qlytro.entity.converter;

import com.qlytro.entity.enums.TrangThaiHopDong;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TrangThaiHopDongConverter implements AttributeConverter<TrangThaiHopDong, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TrangThaiHopDong attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public TrangThaiHopDong convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : TrangThaiHopDong.fromValue(dbData);
    }
}
