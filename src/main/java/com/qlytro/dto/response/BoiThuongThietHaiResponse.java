package com.qlytro.dto.response;

import com.qlytro.entity.enums.NguonPhatHienBoiThuong;
import com.qlytro.entity.enums.TrangThaiBoiThuong;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoiThuongThietHaiResponse {
    private Integer id;
    private Integer hopDongId;
    private String soPhong;
    private String tenToaNha;
    private String tenKhachDaiDien;
    private Integer suCoId;
    private String moTaSuCo;
    private String moTa;
    private BigDecimal soTien;
    private NguonPhatHienBoiThuong nguonPhatHien;
    private String nguonPhatHienLabel;
    private TrangThaiBoiThuong trangThai;
    private String trangThaiLabel;
    private String anhChuyenKhoan;
    private LocalDate ngayGhiNhan;
}
