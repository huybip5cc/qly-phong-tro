package com.qlytro.entity.enums;

/**
 * Vai tro cua nguoi dung trong he thong.
 * Map voi cot vai_tro (TINYINT) trong bang nguoi_dung.
 * 1 = Chủ trọ (LANDLORD), 0 = Người thuê (TENANT)
 */
public enum VaiTroNguoiDung {
    KHACH_THUE(0, "Khách thuê", "ROLE_TENANT"),
    CHU_TRO(1, "Chủ trọ", "ROLE_LANDLORD");

    private final int value;
    private final String label;
    private final String springRole;

    VaiTroNguoiDung(int value, String label, String springRole) {
        this.value = value;
        this.label = label;
        this.springRole = springRole;
    }

    public int getValue() { return value; }
    public String getLabel() { return label; }
    public String getSpringRole() { return springRole; }

    public static VaiTroNguoiDung fromValue(int value) {
        for (VaiTroNguoiDung v : values()) {
            if (v.value == value) return v;
        }
        throw new IllegalArgumentException("VaiTroNguoiDung không hợp lệ: " + value);
    }
}
