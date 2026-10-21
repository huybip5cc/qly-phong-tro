package com.qlytro.entity;

import com.qlytro.entity.enums.NguyenNhanSuCo;
import com.qlytro.entity.enums.TrangThaiSuCo;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "su_co")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SuCo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Service tu xac dinh phong_id tu hop dong ACTIVE cua tai khoan dang nhap. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "phong_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_su_co_phong"))
    private Phong phong;

    /** NULLABLE: null neu su co khong thuoc thiet bi cu the. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thiet_bi_id",
                foreignKey = @ForeignKey(name = "fk_su_co_thiet_bi"))
    private ThietBi thietBi;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nguoi_bao_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_su_co_nguoi_bao"))
    private NguoiDung nguoiBao;

    @Column(name = "mo_ta", nullable = false, length = 255)
    private String moTa;

    @Column(name = "trang_thai", nullable = false)
    @Builder.Default
    private TrangThaiSuCo trangThai = TrangThaiSuCo.MOI;

    /**
     * NULLABLE: chi duoc dien SAU khi chu tro xu ly xong (trang_thai=HOAN_THANH).
     * Validate trong SuCoService.
     */
    @Column(name = "nguyen_nhan")
    private NguyenNhanSuCo nguyenNhan;

    @Column(name = "ngay_bao", nullable = false, updatable = false)
    private LocalDateTime ngayBao;

    @PrePersist
    protected void onCreate() {
        if (ngayBao == null) ngayBao = LocalDateTime.now();
    }
}
