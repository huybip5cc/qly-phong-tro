package com.qlytro.service.impl;

import com.qlytro.dto.response.HoaDonChiTietResponse;
import com.qlytro.dto.response.HoaDonResponse;
import com.qlytro.entity.*;
import com.qlytro.entity.enums.TrangThaiChiSo;
import com.qlytro.entity.enums.TrangThaiHoaDon;
import com.qlytro.exception.BusinessException;
import com.qlytro.exception.ResourceNotFoundException;
import com.qlytro.repository.*;
import com.qlytro.service.base.BaseService;
import com.qlytro.service.strategy.TinhTienContext;
import com.qlytro.service.strategy.TinhTienDichVuStrategy;
import com.qlytro.service.strategy.TinhTienDienStrategy;
import com.qlytro.service.strategy.TinhTienNuocStrategy;
import com.qlytro.service.strategy.TinhTienPhongStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class HoaDonService implements BaseService<HoaDonResponse, Integer> {

    private final HoaDonRepository hoaDonRepository;
    private final HoaDonChiTietRepository chiTietRepository;
    private final HopDongRepository hopDongRepository;
    private final HopDongKhachThueRepository hdktRepository;
    private final ChiSoDienNuocRepository chiSoRepository;
    private final BangGiaDienNuocRepository bangGiaRepository;
    private final PhiDichVuRepository phiDichVuRepository;
    private final KhachThueRepository khachThueRepository;

    // Strategy Pattern: cac chien luoc tinh tien
    private final TinhTienPhongStrategy tinhTienPhong;
    private final TinhTienDienStrategy tinhTienDien;
    private final TinhTienNuocStrategy tinhTienNuoc;
    private final TinhTienDichVuStrategy tinhTienDichVu;

    public HoaDonService(HoaDonRepository hoaDonRepository,
                         HoaDonChiTietRepository chiTietRepository,
                         HopDongRepository hopDongRepository,
                         HopDongKhachThueRepository hdktRepository,
                         ChiSoDienNuocRepository chiSoRepository,
                         BangGiaDienNuocRepository bangGiaRepository,
                         PhiDichVuRepository phiDichVuRepository,
                         KhachThueRepository khachThueRepository,
                         TinhTienPhongStrategy tinhTienPhong,
                         TinhTienDienStrategy tinhTienDien,
                         TinhTienNuocStrategy tinhTienNuoc,
                         TinhTienDichVuStrategy tinhTienDichVu) {
        this.hoaDonRepository = hoaDonRepository;
        this.chiTietRepository = chiTietRepository;
        this.hopDongRepository = hopDongRepository;
        this.hdktRepository = hdktRepository;
        this.chiSoRepository = chiSoRepository;
        this.bangGiaRepository = bangGiaRepository;
        this.phiDichVuRepository = phiDichVuRepository;
        this.khachThueRepository = khachThueRepository;
        this.tinhTienPhong = tinhTienPhong;
        this.tinhTienDien = tinhTienDien;
        this.tinhTienNuoc = tinhTienNuoc;
        this.tinhTienDichVu = tinhTienDichVu;
    }

    @Override
    @Transactional(readOnly = true)
    public HoaDonResponse findById(Integer id) {
        return toResponse(hoaDonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hóa đơn", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoaDonResponse> findAll() {
        return hoaDonRepository.findAll().stream().map(this::toResponse).toList();
    }

    /**
     * Lay danh sach hoa don cua khach thue dang dang nhap.
     * Truy van: nguoi_dung_id -> khach_thue -> hop_dong -> hoa_don
     */
    @Transactional(readOnly = true)
    public List<HoaDonResponse> findByNguoiDungId(Integer nguoiDungId) {
        // Tim khach_thue tu nguoi_dung_id
        com.qlytro.entity.KhachThue khachThue = khachThueRepository
                .findByNguoiDung_Id(nguoiDungId)
                .orElse(null);
        if (khachThue == null) return java.util.List.of();

        // Tim tat ca hop_dong cua khach nay (qua bang trung gian)
        List<Integer> hopDongIds = hdktRepository.findByKhachThue_Id(khachThue.getId())
                .stream()
                .map(hdkt -> hdkt.getHopDong().getId())
                .toList();

        if (hopDongIds.isEmpty()) return java.util.List.of();

        // Lay hoa don cua tat ca hop dong do
        return hopDongIds.stream()
                .flatMap(hopDongId -> hoaDonRepository
                        .findByHopDong_IdOrderByThangNamDesc(hopDongId).stream())
                .map(this::toResponse)
                .toList();
    }

    /** Khach thue nop tien (bao da chuyen khoan): POST form don gian. */
    public HoaDonResponse khachNopTienForm(Integer id) {
        HoaDon hd = hoaDonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hóa đơn", id));
        if (!TrangThaiHoaDon.CHUA_THANH_TOAN.equals(hd.getTrangThai())) {
            throw new BusinessException("Hóa đơn này không ở trạng thái Chưa thanh toán.");
        }
        hd.setTrangThai(TrangThaiHoaDon.CHO_DUYET);
        return toResponse(hoaDonRepository.save(hd));
    }

    /**
     * Tao hoa don cho 1 hop dong trong 1 thang cu the.
     * Quy trinh:
     * 1. Kiem tra hop dong con ACTIVE.
     * 2. Kiem tra chua co hoa don cho thang nay.
     * 3. Lay chi so dien nuoc DA_CHOT cua phong trong thang nay.
     * 4. Dung Strategy Pattern de tinh tung dong chi tiet.
     * 5. Tinh tong_tien = sum(thanh_tien).
     * 6. Luu hoa_don va tat ca hoa_don_chi_tiet.
     */
    public HoaDonResponse taoHoaDon(Integer hopDongId, String thangNam) {
        HopDong hopDong = hopDongRepository.findById(hopDongId)
                .orElseThrow(() -> new ResourceNotFoundException("Hợp đồng", hopDongId));

        if (!com.qlytro.entity.enums.TrangThaiHopDong.ACTIVE.equals(hopDong.getTrangThai())) {
            throw new BusinessException("Hợp đồng đã kết thúc, không thể tạo hóa đơn mới.");
        }
        if (hoaDonRepository.existsByHopDong_IdAndThangNam(hopDongId, thangNam)) {
            throw new BusinessException("Hóa đơn tháng " + thangNam + " đã tồn tại.");
        }

        // Tinh thang truoc: hoa don thang N lay chi so dien nuoc thang N-1 (dong tre 1 ky)
        String thangTruoc = java.time.YearMonth.parse(thangNam).minusMonths(1).toString();

        // Lay chi so dien nuoc thang truoc (neu co)
        ChiSoDienNuoc chiSo = chiSoRepository
                .findByPhong_IdAndThangNam(hopDong.getPhong().getId(), thangTruoc)
                .orElse(null);

        // Neu da co chi so dien nuoc thang truoc ma chua chot -> bao loi yeu cau chot
        if (chiSo != null && !TrangThaiChiSo.DA_CHOT.equals(chiSo.getTrangThai())) {
            throw new BusinessException(
                    "Chỉ số điện nước tháng " + thangTruoc + " chưa được chốt. "
                    + "Vui lòng chốt chỉ số trước khi tạo hóa đơn.");
        }

        BangGiaDienNuoc bangGia = bangGiaRepository.findTopByOrderByIdAsc()
                .orElseThrow(() -> new BusinessException("Chưa có bảng giá điện nước. Vui lòng thiết lập trước."));

        List<PhiDichVu> danhSachPhi = phiDichVuRepository.findByDangApDung(true);

        // Xay dung context cho Strategy Pattern
        TinhTienContext context = TinhTienContext.builder()
                .hopDong(hopDong)
                .chiSoDienNuoc(chiSo)
                .bangGiaDienNuoc(bangGia)
                .danhSachPhiDichVu(danhSachPhi)
                .thangNam(thangNam)
                .build();

        // Tao hoa don truoc (can id de lien ket chi tiet)
        HoaDon hoaDon = new HoaDon();
        hoaDon.setHopDong(hopDong);
        hoaDon.setThangNam(thangNam);
        hoaDon.setTrangThai(TrangThaiHoaDon.CHUA_THANH_TOAN);
        hoaDon.setTongTien(BigDecimal.ZERO); // se cap nhat sau
        hoaDon = hoaDonRepository.save(hoaDon);

        // Goi tat ca Strategy -> thu thap chi tiet
        List<HoaDonChiTiet> danhSachChiTiet = new ArrayList<>();
        danhSachChiTiet.addAll(tinhTienPhong.tinh(context));
        danhSachChiTiet.addAll(tinhTienDien.tinh(context));
        danhSachChiTiet.addAll(tinhTienNuoc.tinh(context));
        danhSachChiTiet.addAll(tinhTienDichVu.tinh(context));

        // Gan hoa_don_id va luu
        final HoaDon finalHoaDon = hoaDon;
        danhSachChiTiet.forEach(ct -> ct.setHoaDon(finalHoaDon));
        chiTietRepository.saveAll(danhSachChiTiet);

        // Tinh tong tien va cap nhat
        BigDecimal tongTien = danhSachChiTiet.stream()
                .map(HoaDonChiTiet::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        hoaDon.setTongTien(tongTien);
        hoaDon = hoaDonRepository.save(hoaDon);

        return toResponse(hoaDon);
    }

    /** Khach nop tien: upload anh chuyen khoan, chuyen sang CHO_DUYET. */
    public HoaDonResponse khachNopTien(Integer id, String anhChuyenKhoan) {
        HoaDon hd = hoaDonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hóa đơn", id));
        if (!TrangThaiHoaDon.CHUA_THANH_TOAN.equals(hd.getTrangThai())) {
            throw new BusinessException("Hóa đơn này không ở trạng thái Chưa thanh toán.");
        }
        hd.setAnhChuyenKhoan(anhChuyenKhoan);
        hd.setTrangThai(TrangThaiHoaDon.CHO_DUYET);
        return toResponse(hoaDonRepository.save(hd));
    }

    /** Chu tro xac nhan da nhan tien. */
    public HoaDonResponse xacNhanThanhToan(Integer id) {
        HoaDon hd = hoaDonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hóa đơn", id));
        if (!TrangThaiHoaDon.CHO_DUYET.equals(hd.getTrangThai())) {
            throw new BusinessException("Hóa đơn này không ở trạng thái Chờ duyệt.");
        }
        hd.setTrangThai(TrangThaiHoaDon.DA_THANH_TOAN);
        hd.setNgayThanhToan(LocalDateTime.now());
        return toResponse(hoaDonRepository.save(hd));
    }

    @Override
    public void deleteById(Integer id) {
        HoaDon hd = hoaDonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hóa đơn", id));
        if (!TrangThaiHoaDon.CHUA_THANH_TOAN.equals(hd.getTrangThai())) {
            throw new BusinessException("Chỉ có thể xóa hóa đơn chưa thanh toán.");
        }
        chiTietRepository.deleteByHoaDon_Id(id);
        hoaDonRepository.delete(hd);
    }

    private HoaDonResponse toResponse(HoaDon entity) {
        HoaDonResponse r = new HoaDonResponse();
        r.setId(entity.getId());
        r.setHopDongId(entity.getHopDong().getId());
        r.setSoPhong(entity.getHopDong().getPhong().getSoPhong());
        r.setTenToaNha(entity.getHopDong().getPhong().getToaNha().getTenToaNha());
        r.setThangNam(entity.getThangNam());
        r.setTongTien(entity.getTongTien());
        r.setTrangThai(entity.getTrangThai());
        r.setTrangThaiLabel(entity.getTrangThai().getLabel());
        r.setAnhChuyenKhoan(entity.getAnhChuyenKhoan());
        r.setNgayTao(entity.getNgayTao());
        r.setNgayThanhToan(entity.getNgayThanhToan());

        // Load chi tiet
        List<HoaDonChiTietResponse> danhSachChiTiet = chiTietRepository
                .findByHoaDon_Id(entity.getId()).stream()
                .map(ct -> {
                    HoaDonChiTietResponse ctr = new HoaDonChiTietResponse();
                    ctr.setId(ct.getId());
                    ctr.setLoaiKhoan(ct.getLoaiKhoan());
                    ctr.setLoaiKhoanLabel(ct.getLoaiKhoan().getLabel());
                    ctr.setNoiDung(ct.getNoiDung());
                    ctr.setSoLuong(ct.getSoLuong());
                    ctr.setDonGia(ct.getDonGia());
                    ctr.setThanhTien(ct.getThanhTien());
                    return ctr;
                }).toList();
        r.setDanhSachChiTiet(danhSachChiTiet);

        // Lay ten khach dai dien qua repository (HopDong entity khong co collection nay)
        hdktRepository.findByHopDong_Id(entity.getHopDong().getId()).stream()
                .filter(hdkt -> Boolean.TRUE.equals(hdkt.getLaDaiDien()) && Boolean.TRUE.equals(hdkt.getDangO()))
                .findFirst()
                .ifPresent(hdkt -> r.setTenKhachDaiDien(hdkt.getKhachThue().getHoTen()));
        return r;
    }
}
