package com.qlytro.entity;

import com.qlytro.entity.enums.LoaiTinhPhi;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "phi_dich_vu")
@Getter
@Setter
public class PhiDichVu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_phi", nullable = false)
    private String tenPhi;

    @Column(name = "so_tien", nullable = false)
    private BigDecimal soTien;

    // Ánh xạ tới cột loai_tinh trong database
    @Enumerated(EnumType.STRING)
    @Column(name = "loai_tinh", length = 20)
    private LoaiTinhPhi loaiTinh = LoaiTinhPhi.THEO_PHONG;

    @Column(name = "dang_ap_dung")
    private Boolean dangApDung = true;
}