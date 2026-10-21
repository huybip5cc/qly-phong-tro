package com.qlytro.entity.enums;

/**
 * Trang thai tien coc trong hop dong.
 * Map voi cot trang_thai_coc (TINYINT) trong bang hop_dong.
 * 0=DANG_GIU, 1=DA_KHAU_TRU
 */
public enum TrangThaiCoc {
    DANG_GIU(0, "Đang giữ cọc"),
    DA_KHAU_TRU(1, "Đã khấu trừ");

    private final int value;
    private final String label;

    TrangThaiCoc(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() { return value; }
    public String getLabel() { return label; }

    public static TrangThaiCoc fromValue(int value) {
        for (TrangThaiCoc t : values()) {
            if (t.value == value) return t;
        }
        throw new IllegalArgumentException("TrangThaiCoc không hợp lệ: " + value);
    }
}
