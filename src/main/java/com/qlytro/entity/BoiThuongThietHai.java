package com.qlytro.entity;

import com.qlytro.entity.enums.NguonPhatHienBoiThuong;
import com.qlytro.entity.enums.TrangThaiBoiThuong;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "boi_thuong_thiet_hai")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BoiThuongThietHai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hop_dong_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_bttv_hop_dong"))
    private HopDong hopDong;

    /**
     * NULLABLE: co gia tri neu phat sinh tu ticket bao hong (su_co).
     * NULL neu chu tro ghi nhan truc tiep khi kiem tra phong.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "su_co_id",
                foreignKey = @ForeignKey(name = "fk_bttv_su_co"))
    private SuCo suCo;

    @Column(name = "mo_ta", nullable = false, length = 255)
    private String moTa;

    @Column(name = "so_tien", nullable = false, precision = 12, scale = 2)
    private BigDecimal soTien;

    /**
     * TRONG_LUC_O: trang_thai 0=CHUA_THANH_TOAN, 1=CHO_DUYET, 2=DA_THANH_TOAN.
     * LUC_TRA_PHONG: trang_thai 0=CHUA_XU_LY, 1=DA_KHAU_TRU_VAO_COC.
     * Y nghia day du duoc xu ly tai tang Service (khong tai Entity).
     */
    @Column(name = "nguon_phat_hien", nullable = false)
    private NguonPhatHienBoiThuong nguonPhatHien;

    @Column(name = "trang_thai", nullable = false)
    @Builder.Default
    private TrangThaiBoiThuong trangThai = TrangThaiBoiThuong.TRANG_THAI_0;

    /** Chi co gia tri khi nguon_phat_hien = TRONG_LUC_O. */
    @Column(name = "anh_chuyen_khoan", length = 255)
    private String anhChuyenKhoan;

    @Column(name = "ngay_ghi_nhan", nullable = false)
    private LocalDate ngayGhiNhan;
}
