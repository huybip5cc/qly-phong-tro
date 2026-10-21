package com.qlytro.entity.enums;

/**
 * Trang thai hoa don.
 * Map voi cot trang_thai (TINYINT) trong bang hoa_don.
 * 0=CHUA_THANH_TOAN, 1=CHO_DUYET, 2=DA_THANH_TOAN
 */
public enum TrangThaiHoaDon {
    CHUA_THANH_TOAN(0, "Chưa thanh toán"),
    CHO_DUYET(1, "Chờ duyệt"),
    DA_THANH_TOAN(2, "Đã thanh toán");

    private final int value;
    private final String label;

    TrangThaiHoaDon(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() { return value; }
    public String getLabel() { return label; }

    public static TrangThaiHoaDon fromValue(int value) {
        for (TrangThaiHoaDon t : values()) {
            if (t.value == value) return t;
        }
        throw new IllegalArgumentException("TrangThaiHoaDon không hợp lệ: " + value);
    }
}
