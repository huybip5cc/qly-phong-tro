package com.qlytro.service.strategy;

import com.qlytro.entity.HoaDonChiTiet;
import java.util.List;

/**
 * Strategy interface cho viec tinh toan tung dong chi tiet hoa don.
 * <p>
 * Cac class implement:
 *   - TinhTienPhongStrategy   -> TIEN_PHONG
 *   - TinhTienDienStrategy    -> TIEN_DIEN
 *   - TinhTienNuocStrategy    -> TIEN_NUOC
 *   - TinhTienDichVuStrategy  -> PHI_DICH_VU (co the tra nhieu dong)
 * <p>
 * HoaDonService goi toan bo List<TinhTienStrategy> va tong hop ket qua.
 */
public interface TinhTienStrategy {
    /**
     * Tinh cac dong chi tiet hoa don tu context.
     * Co the tra ra 1 hoac nhieu HoaDonChiTiet (VD: phi dich vu co nhieu loai).
     */
    List<HoaDonChiTiet> tinh(TinhTienContext context);
}
