package com.qlytro.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hop_dong_khach_thue",
       uniqueConstraints = @UniqueConstraint(name = "uq_hdkt",
                                              columnNames = {"hop_dong_id", "khach_thue_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HopDongKhachThue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hop_dong_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_hdkt_hop_dong"))
    private HopDong hopDong;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "khach_thue_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_hdkt_khach_thue"))
    private KhachThue khachThue;

    /**
     * Cot TINYINT 0/1 — dung Boolean thay vi enum (chi co 2 gia tri, khong co label phu).
     * DB: 1 = la dai dien, 0 = khong.
     * Rang buoc nghiep vu: moi hop dong chi co DUNG 1 khach co la_dai_dien=true.
     * Validate trong HopDongService.
     */
    @Column(name = "la_dai_dien", nullable = false)
    @Builder.Default
    private Boolean laDaiDien = false;

    /**
     * DB: 1 = dang o, 0 = da roi di.
     * Giu lai dong khi khach roi di (KHONG xoa), de lich su.
     */
    @Column(name = "dang_o", nullable = false)
    @Builder.Default
    private Boolean dangO = true;
}
