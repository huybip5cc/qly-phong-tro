package com.qlytro.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhongNoTienResponse {
    private Integer phongId;
    private String soPhong;
    private String tenToaNha;
    private String tenKhachDaiDien;
    private BigDecimal tongTienNo;
    private long soThangNo;
    private List<String> danhSachThangNo;
}
