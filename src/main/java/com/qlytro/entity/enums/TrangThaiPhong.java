package com.qlytro.entity.enums;

/**
 * Trang thai cua phong.
 * Map voi cot trang_thai (TINYINT) trong bang phong.
 * 0=TRONG, 1=DANG_THUE, 2=NGUNG_SU_DUNG
 */
public enum TrangThaiPhong {
    TRONG(0, "Trống"),
    DANG_THUE(1, "Đang thuê"),
    NGUNG_SU_DUNG(2, "Ngừng sử dụng");

    private final int value;
    private final String label;

    TrangThaiPhong(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() { return value; }
    public String getLabel() { return label; }

    public static TrangThaiPhong fromValue(int value) {
        for (TrangThaiPhong t : values()) {
            if (t.value == value) return t;
        }
        throw new IllegalArgumentException("TrangThaiPhong không hợp lệ: " + value);
    }
}
