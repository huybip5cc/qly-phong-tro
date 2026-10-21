package com.qlytro.entity.enums;

/**
 * Trang thai chi so dien nuoc.
 * Map voi cot trang_thai (TINYINT) trong bang chi_so_dien_nuoc.
 * 0=TAM_TINH, 1=DA_CHOT, 2=CAN_XAC_MINH
 */
public enum TrangThaiChiSo {
    TAM_TINH(0, "Tạm tính"),
    DA_CHOT(1, "Đã chốt"),
    CAN_XAC_MINH(2, "Cần xác minh");

    private final int value;
    private final String label;

    TrangThaiChiSo(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() { return value; }
    public String getLabel() { return label; }

    public static TrangThaiChiSo fromValue(int value) {
        for (TrangThaiChiSo t : values()) {
            if (t.value == value) return t;
        }
        throw new IllegalArgumentException("TrangThaiChiSo không hợp lệ: " + value);
    }
}
