package com.qlytro.entity.enums;

/**
 * Nguon phat hien boi thuong thiet hai.
 * Map voi cot nguon_phat_hien (TINYINT) trong bang boi_thuong_thiet_hai.
 * 0=TRONG_LUC_O: thanh toan rieng, doc lap hoa don thang.
 * 1=LUC_TRA_PHONG: gop vao khau tru coc cuoi hop dong.
 */
public enum NguonPhatHienBoiThuong {
    TRONG_LUC_O(0, "Trong lúc ở"),
    LUC_TRA_PHONG(1, "Lúc trả phòng");

    private final int value;
    private final String label;

    NguonPhatHienBoiThuong(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() { return value; }
    public String getLabel() { return label; }

    public static NguonPhatHienBoiThuong fromValue(int value) {
        for (NguonPhatHienBoiThuong n : values()) {
            if (n.value == value) return n;
        }
        throw new IllegalArgumentException("NguonPhatHienBoiThuong không hợp lệ: " + value);
    }
}
