package com.qlytro.entity;

import com.qlytro.entity.enums.TrangThaiChiSo;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "chi_so_dien_nuoc",
       uniqueConstraints = @UniqueConstraint(name = "uq_csdn",
                                              columnNames = {"phong_id", "thang_nam"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChiSoDienNuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Gan voi phong (khong phai hop dong) vi dong ho la tai san vat ly co dinh. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "phong_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_csdn_phong"))
    private Phong phong;

    /** Format: 'YYYY-MM', VD: 2026-09 */
    @Column(name = "thang_nam", nullable = false, length = 7)
    private String thangNam;

    @Column(name = "chi_so_dien_cu", nullable = false, precision = 10, scale = 2)
    private BigDecimal chiSoDienCu;

    @Column(name = "chi_so_dien_moi", nullable = false, precision = 10, scale = 2)
    private BigDecimal chiSoDienMoi;

    @Column(name = "chi_so_nuoc_cu", nullable = false, precision = 10, scale = 2)
    private BigDecimal chiSoNuocCu;

    @Column(name = "chi_so_nuoc_moi", nullable = false, precision = 10, scale = 2)
    private BigDecimal chiSoNuocMoi;

    /** NULLABLE: Anh khach chup dong ho gui kem. */
    @Column(name = "anh_chi_so", length = 255)
    private String anhChiSo;

    @Column(name = "trang_thai", nullable = false)
    @Builder.Default
    private TrangThaiChiSo trangThai = TrangThaiChiSo.TAM_TINH;

    @Column(name = "ghi_chu", length = 255)
    private String ghiChu;

    @Column(name = "ngay_bao", nullable = false, updatable = false)
    private LocalDateTime ngayBao;

    /** NULLABLE: Chi co gia tri khi trang_thai = DA_CHOT. */
    @Column(name = "ngay_chot")
    private LocalDateTime ngayChot;

    @PrePersist
    protected void onCreate() {
        if (ngayBao == null) ngayBao = LocalDateTime.now();
    }
}
