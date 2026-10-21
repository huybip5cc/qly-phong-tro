package com.qlytro.dto.response;

import com.qlytro.entity.enums.NguyenNhanSuCo;
import com.qlytro.entity.enums.TrangThaiSuCo;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SuCoResponse {
    private Integer id;
    private Integer phongId;
    private String soPhong;
    private String tenToaNha;
    private Integer thietBiId;
    private String tenThietBi;
    private Integer nguoiBaoId;
    private String tenNguoiBao;
    private String moTa;
    private TrangThaiSuCo trangThai;
    private String trangThaiLabel;
    private NguyenNhanSuCo nguyenNhan;
    private String nguyenNhanLabel;
    private LocalDateTime ngayBao;
}
