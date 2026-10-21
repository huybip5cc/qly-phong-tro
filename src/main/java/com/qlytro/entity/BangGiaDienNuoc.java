package com.qlytro.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Luon chi co DUNG 1 dong trong bang nay.
 * Doi gia la UPDATE truc tiep, khong luu lich su.
 * Lich su gia duoc bao ton trong hoa_don_chi_tiet.don_gia (da copy tai thoi diem tinh).
 */
@Entity
@Table(name = "bang_gia_dien_nuoc")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BangGiaDienNuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Don vi: dong/so */
    @Column(name = "don_gia_dien", nullable = false, precision = 10, scale = 2)
    private BigDecimal donGiaDien;

    /** Don vi: dong/khoi */
    @Column(name = "don_gia_nuoc", nullable = false, precision = 10, scale = 2)
    private BigDecimal donGiaNuoc;

    @Column(name = "ngay_cap_nhat", nullable = false)
    private LocalDateTime ngayCapNhat;

    @PrePersist
    @PreUpdate
    protected void onSave() {
        ngayCapNhat = LocalDateTime.now();
    }
}
