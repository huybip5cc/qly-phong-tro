package com.qlytro.entity.enums;

/**
 * Trang thai su co.
 * Map voi cot trang_thai (TINYINT) trong bang su_co.
 * 0=MOI, 1=DANG_XU_LY, 2=HOAN_THANH
 */
public enum TrangThaiSuCo {
    MOI(0, "Mới"),
    DANG_XU_LY(1, "Đang xử lý"),
    HOAN_THANH(2, "Hoàn thành");

    private final int value;
    private final String label;

    TrangThaiSuCo(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() { return value; }
    public String getLabel() { return label; }

    public static TrangThaiSuCo fromValue(int value) {
        for (TrangThaiSuCo t : values()) {
            if (t.value == value) return t;
        }
        throw new IllegalArgumentException("TrangThaiSuCo không hợp lệ: " + value);
    }
}
