package com.qlytro.entity.enums;

/**
 * Nguyen nhan su co (chi dien sau khi HOAN_THANH).
 * Map voi cot nguyen_nhan (TINYINT NULL) trong bang su_co.
 * 0=HAO_MON_TU_NHIEN, 1=DO_NGUOI_O_LAM_HONG
 * NULLABLE: converter phai xu ly null dung.
 */
public enum NguyenNhanSuCo {
    HAO_MON_TU_NHIEN(0, "Hao mòn tự nhiên"),
    DO_NGUOI_O_LAM_HONG(1, "Do người ở làm hỏng");

    private final int value;
    private final String label;

    NguyenNhanSuCo(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() { return value; }
    public String getLabel() { return label; }

    public static NguyenNhanSuCo fromValue(int value) {
        for (NguyenNhanSuCo n : values()) {
            if (n.value == value) return n;
        }
        throw new IllegalArgumentException("NguyenNhanSuCo không hợp lệ: " + value);
    }
}
