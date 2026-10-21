package com.qlytro.dto.response;

import com.qlytro.entity.enums.LoaiKhoanChiTiet;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class HoaDonChiTietResponse {
    private Integer id;
    private LoaiKhoanChiTiet loaiKhoan;
    private String loaiKhoanLabel;
    private String noiDung;
    private BigDecimal soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
}
