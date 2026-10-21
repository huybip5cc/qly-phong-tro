package com.qlytro.dto.response;

import com.qlytro.entity.enums.TrangThaiToaNha;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ToaNhaResponse {
    private Integer id;
    private String tenToaNha;
    private String diaChi;
    private TrangThaiToaNha trangThai;
    private String trangThaiLabel;
    private LocalDateTime ngayTao;
    private long soPhong;
}
