package com.qlytro.entity.enums;

/**
 * Trang thai tai khoan nguoi dung.
 * Map voi cot trang_thai (TINYINT) trong bang nguoi_dung.
 * 1 = active, 0 = khoa tai khoan
 */
public enum TrangThaiNguoiDung {
    KHOA(0, "Đã khóa"),
    ACTIVE(1, "Đang hoạt động");

    private final int value;
    private final String label;

    TrangThaiNguoiDung(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() { return value; }
    public String getLabel() { return label; }

    public static TrangThaiNguoiDung fromValue(int value) {
        for (TrangThaiNguoiDung t : values()) {
            if (t.value == value) return t;
        }
        throw new IllegalArgumentException("TrangThaiNguoiDung không hợp lệ: " + value);
    }
}
