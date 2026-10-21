package com.qlytro.entity.enums;

public enum LoaiTinhPhi {
    THEO_PHONG("Theo phòng"),
    THEO_NGUOI("Theo người");

    private final String label;

    LoaiTinhPhi(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}