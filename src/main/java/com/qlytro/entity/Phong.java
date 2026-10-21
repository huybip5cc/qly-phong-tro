package com.qlytro.entity;

import com.qlytro.entity.enums.TrangThaiPhong;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "phong",
       uniqueConstraints = @UniqueConstraint(name = "uq_phong_so_phong",
                                              columnNames = {"toa_nha_id", "so_phong"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Phong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "toa_nha_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_phong_toa_nha"))
    private ToaNha toaNha;

    @Column(name = "so_phong", nullable = false, length = 10)
    private String soPhong;

    /** NULLABLE trong DB -> dung BigDecimal (wrapper-like, co the null). */
    @Column(name = "dien_tich", precision = 6, scale = 2)
    private BigDecimal dienTich;

    /** Gia niem yet. Service COPY sang hop_dong.tien_thue khi tao hop dong moi. */
    @Column(name = "gia_thue_mac_dinh", nullable = false, precision = 12, scale = 2)
    private BigDecimal giaThueTheoMacDinh;

    @Column(name = "trang_thai", nullable = false)
    @Builder.Default
    private TrangThaiPhong trangThai = TrangThaiPhong.TRONG;

    @Column(name = "mo_ta", length = 255)
    private String moTa;
}
