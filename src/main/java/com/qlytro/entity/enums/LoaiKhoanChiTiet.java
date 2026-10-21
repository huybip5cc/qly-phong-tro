package com.qlytro.entity.enums;

/**
 * Loai khoan trong hoa don chi tiet.
 * Map voi cot loai_khoan (TINYINT) trong bang hoa_don_chi_tiet.
 * 0=TIEN_PHONG, 1=TIEN_DIEN, 2=TIEN_NUOC, 3=PHI_DICH_VU
 */
public enum LoaiKhoanChiTiet {
    TIEN_PHONG(0, "Tiền phòng"),
    TIEN_DIEN(1, "Tiền điện"),
    TIEN_NUOC(2, "Tiền nước"),
    PHI_DICH_VU(3, "Phí dịch vụ");

    private final int value;
    private final String label;

    LoaiKhoanChiTiet(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() { return value; }
    public String getLabel() { return label; }

    public static LoaiKhoanChiTiet fromValue(int value) {
        for (LoaiKhoanChiTiet l : values()) {
            if (l.value == value) return l;
        }
        throw new IllegalArgumentException("LoaiKhoanChiTiet không hợp lệ: " + value);
    }
}
