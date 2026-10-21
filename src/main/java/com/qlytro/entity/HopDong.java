package com.qlytro.entity;

import com.qlytro.entity.enums.TrangThaiCoc;
import com.qlytro.entity.enums.TrangThaiHopDong;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "hop_dong")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HopDong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "phong_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_hop_dong_phong"))
    private Phong phong;

    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDate ngayBatDau;

    /** NULLABLE trong DB -> LocalDate (reference type). */
    @Column(name = "ngay_ket_thuc")
    private LocalDate ngayKetThuc;

    /**
     * COPY tu phong.gia_thue_mac_dinh tai thoi diem tao.
     * Sau do doc lap hoan toan — doi gia niem yet khong anh huong hop dong da ky.
     */
    @Column(name = "tien_thue", nullable = false, precision = 12, scale = 2)
    private BigDecimal tienThue;

    @Column(name = "tien_coc", nullable = false, precision = 12, scale = 2)
    private BigDecimal tienCoc;

    /**
     * NULLABLE: Chi co gia tri khi trang_thai_coc = DA_KHAU_TRU.
     * Co the am neu cong no > coc.
     */
    @Column(name = "tien_coc_con_lai", precision = 12, scale = 2)
    private BigDecimal tienCocConLai;

    @Column(name = "trang_thai", nullable = false)
    @Builder.Default
    private TrangThaiHopDong trangThai = TrangThaiHopDong.ACTIVE;

    @Column(name = "trang_thai_coc", nullable = false)
    @Builder.Default
    private TrangThaiCoc trangThaiCoc = TrangThaiCoc.DANG_GIU;

    @Column(name = "ngay_tao", nullable = false, updatable = false)
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        if (ngayTao == null) ngayTao = LocalDateTime.now();
    }
}
