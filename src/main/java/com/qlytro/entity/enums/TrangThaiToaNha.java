package com.qlytro.entity.enums;

/**
 * Trang thai cua toa nha.
 * Map voi cot trang_thai (TINYINT) trong bang toa_nha.
 */
public enum TrangThaiToaNha {
    NGUNG_HOAT_DONG(0, "Ngừng hoạt động"),
    HOAT_DONG(1, "Hoạt động");

    private final int value;
    private final String label;

    TrangThaiToaNha(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() { return value; }
    public String getLabel() { return label; }

    public static TrangThaiToaNha fromValue(int value) {
        for (TrangThaiToaNha t : values()) {
            if (t.value == value) return t;
        }
        throw new IllegalArgumentException("TrangThaiToaNha không hợp lệ: " + value);
    }
}
