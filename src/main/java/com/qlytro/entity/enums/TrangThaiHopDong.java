package com.qlytro.entity.enums;

/**
 * Trang thai hop dong.
 * Map voi cot trang_thai (TINYINT) trong bang hop_dong.
 * 0=ACTIVE, 1=KET_THUC_BINH_THUONG, 2=KET_THUC_BAT_THUONG
 */
public enum TrangThaiHopDong {
    ACTIVE(0, "Đang hiệu lực"),
    KET_THUC_BINH_THUONG(1, "Kết thúc bình thường"),
    KET_THUC_BAT_THUONG(2, "Kết thúc bất thường");

    private final int value;
    private final String label;

    TrangThaiHopDong(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() { return value; }
    public String getLabel() { return label; }

    public static TrangThaiHopDong fromValue(int value) {
        for (TrangThaiHopDong t : values()) {
            if (t.value == value) return t;
        }
        throw new IllegalArgumentException("TrangThaiHopDong không hợp lệ: " + value);
    }
}
