package com.qlytro.entity.enums;

/**
 * Trang thai boi thuong thiet hai.
 * Map voi cot trang_thai (TINYINT) trong bang boi_thuong_thiet_hai.
 *
 * Y nghia phu thuoc vao nguon_phat_hien:
 *   TRONG_LUC_O (0):  0=CHUA_THANH_TOAN, 1=CHO_DUYET, 2=DA_THANH_TOAN
 *   LUC_TRA_PHONG (1): 0=CHUA_XU_LY, 1=DA_KHAU_TRU_VAO_COC
 *
 * Enum nay map theo gia tri so nguyen, logic phan biet nguon_phat_hien
 * duoc xu ly tai tang Service (khong tai Entity).
 */
public enum TrangThaiBoiThuong {
    TRANG_THAI_0(0, "Chưa thanh toán / Chưa xử lý"),
    TRANG_THAI_1(1, "Chờ duyệt / Đã khấu trừ vào cọc"),
    TRANG_THAI_2(2, "Đã thanh toán");

    private final int value;
    private final String label;

    TrangThaiBoiThuong(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() { return value; }
    public String getLabel() { return label; }

    /**
     * Lay nhan hien thi phu hop voi nguon_phat_hien.
     */
    public String getLabelByNguon(NguonPhatHienBoiThuong nguon) {
        if (nguon == NguonPhatHienBoiThuong.TRONG_LUC_O) {
            return switch (this) {
                case TRANG_THAI_0 -> "Chưa thanh toán";
                case TRANG_THAI_1 -> "Chờ duyệt";
                case TRANG_THAI_2 -> "Đã thanh toán";
            };
        } else { // LUC_TRA_PHONG
            return switch (this) {
                case TRANG_THAI_0 -> "Chưa xử lý";
                case TRANG_THAI_1 -> "Đã khấu trừ vào cọc";
                case TRANG_THAI_2 -> "Đã thanh toán";
            };
        }
    }

    public static TrangThaiBoiThuong fromValue(int value) {
        for (TrangThaiBoiThuong t : values()) {
            if (t.value == value) return t;
        }
        throw new IllegalArgumentException("TrangThaiBoiThuong không hợp lệ: " + value);
    }
}
