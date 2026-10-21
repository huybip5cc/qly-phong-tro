package com.qlytro.dto.response;

import com.qlytro.entity.enums.TrangThaiChiSo;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ChiSoDienNuocResponse {
    private Integer id;
    private Integer phongId;
    private String soPhong;
    private String tenToaNha;
    private String thangNam;
    private BigDecimal chiSoDienCu;
    private BigDecimal chiSoDienMoi;
    private BigDecimal chiSoNuocCu;
    private BigDecimal chiSoNuocMoi;
    private String anhChiSo;
    private TrangThaiChiSo trangThai;
    private String trangThaiLabel;
    private String ghiChu;
    private LocalDateTime ngayBao;
    private LocalDateTime ngayChot;
}
