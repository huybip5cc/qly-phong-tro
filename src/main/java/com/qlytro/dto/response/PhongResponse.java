package com.qlytro.dto.response;

import com.qlytro.entity.ThietBi;
import com.qlytro.entity.enums.TrangThaiPhong;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PhongResponse {
    private Integer id;
    private Integer toaNhaId;
    private String tenToaNha;
    private String soPhong;
    private BigDecimal dienTich;
    private BigDecimal giaThueTheoMacDinh;
    private TrangThaiPhong trangThai;
    private String trangThaiLabel;
    private String moTa;
    // Danh sach thiet bi dang co trong phong (load khi can chi tiet)
    private List<ThietBi> danhSachThietBi;
}
