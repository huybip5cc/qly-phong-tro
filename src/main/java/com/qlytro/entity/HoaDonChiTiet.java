package com.qlytro.entity;

import com.qlytro.entity.enums.LoaiKhoanChiTiet;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Chi tiet tung dong trong hoa don.
 * Tach rieng de tuan 3NF va cung la noi Strategy Pattern the hien:
 * moi loai_khoan tuong ung voi 1 chien luoc tinh tien khac nhau.
 */
@Entity
@Table(name = "hoa_don_chi_tiet")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HoaDonChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hoa_don_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_hdct_hoa_don"))
    private HoaDon hoaDon;

    /**
     * NULLABLE: Chi co gia tri voi dong TIEN_DIEN / TIEN_NUOC.
     * Chi de TRUY VET nguon goc so lieu, KHONG dung de tinh toan lai.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chi_so_dien_nuoc_id",
                foreignKey = @ForeignKey(name = "fk_hdct_chi_so"))
    private ChiSoDienNuoc chiSoDienNuoc;

    @Column(name = "loai_khoan", nullable = false)
    private LoaiKhoanChiTiet loaiKhoan;

    @Column(name = "noi_dung", nullable = false, length = 255)
    private String noiDung;

    /** NULLABLE: Chi dung cho dong dien/nuoc. */
    @Column(name = "so_luong", precision = 10, scale = 2)
    private BigDecimal soLuong;

    /** NULLABLE: Chi dung cho dong dien/nuoc (COPY tu bang_gia tai thoi diem tinh). */
    @Column(name = "don_gia", precision = 12, scale = 2)
    private BigDecimal donGia;

    @Column(name = "thanh_tien", nullable = false, precision = 12, scale = 2)
    private BigDecimal thanhTien;
}
