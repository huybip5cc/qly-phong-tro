package com.qlytro.service.strategy;

import com.qlytro.entity.BangGiaDienNuoc;
import com.qlytro.entity.ChiSoDienNuoc;
import com.qlytro.entity.HopDong;
import com.qlytro.entity.PhiDichVu;
import java.util.List;

/**
 * Context (du lieu dau vao) cho cac chien luoc tinh tien.
 * Chua tat ca thong tin can thiet de bat ky chien luoc nao co the tinh duoc.
 * Moi chien luoc chi doc cac truong no can, bo qua cac truong con lai.
 */
public class TinhTienContext {
    private HopDong hopDong;
    private ChiSoDienNuoc chiSoDienNuoc;
    private BangGiaDienNuoc bangGiaDienNuoc;
    private List<PhiDichVu> danhSachPhiDichVu;
    private String thangNam;

    private TinhTienContext() {}

    public HopDong getHopDong() { return hopDong; }
    public ChiSoDienNuoc getChiSoDienNuoc() { return chiSoDienNuoc; }
    public BangGiaDienNuoc getBangGiaDienNuoc() { return bangGiaDienNuoc; }
    public List<PhiDichVu> getDanhSachPhiDichVu() { return danhSachPhiDichVu; }
    public String getThangNam() { return thangNam; }

    /** Builder de tao TinhTienContext mot cach ro rang. */
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final TinhTienContext ctx = new TinhTienContext();

        public Builder hopDong(HopDong hopDong) {
            ctx.hopDong = hopDong;
            return this;
        }

        public Builder chiSoDienNuoc(ChiSoDienNuoc chiSoDienNuoc) {
            ctx.chiSoDienNuoc = chiSoDienNuoc;
            return this;
        }

        public Builder bangGiaDienNuoc(BangGiaDienNuoc bangGiaDienNuoc) {
            ctx.bangGiaDienNuoc = bangGiaDienNuoc;
            return this;
        }

        public Builder danhSachPhiDichVu(List<PhiDichVu> danhSachPhiDichVu) {
            ctx.danhSachPhiDichVu = danhSachPhiDichVu;
            return this;
        }

        public Builder thangNam(String thangNam) {
            ctx.thangNam = thangNam;
            return this;
        }

        public TinhTienContext build() { return ctx; }
    }
}
