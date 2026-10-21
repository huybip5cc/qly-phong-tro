package com.qlytro.entity;

import com.qlytro.entity.enums.TrangThaiToaNha;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "toa_nha")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ToaNha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_toa_nha", nullable = false, length = 100)
    private String tenToaNha;

    @Column(name = "dia_chi", length = 255)
    private String diaChi;

    @Column(name = "trang_thai", nullable = false)
    @Builder.Default
    private TrangThaiToaNha trangThai = TrangThaiToaNha.HOAT_DONG;

    @Column(name = "ngay_tao", nullable = false, updatable = false)
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        if (ngayTao == null) ngayTao = LocalDateTime.now();
    }
}
