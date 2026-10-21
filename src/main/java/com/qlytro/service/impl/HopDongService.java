package com.qlytro.service.impl;

import com.qlytro.dto.request.HopDongRequest;
import com.qlytro.dto.response.HopDongResponse;
import com.qlytro.dto.response.KhachThueResponse;
import com.qlytro.entity.*;
import com.qlytro.entity.enums.*;
import com.qlytro.exception.BusinessException;
import com.qlytro.exception.ResourceNotFoundException;
import com.qlytro.repository.*;
import com.qlytro.service.base.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class HopDongService implements BaseService<HopDongResponse, Integer> {

    private final HopDongRepository hopDongRepository;
    private final HopDongKhachThueRepository hdktRepository;
    private final PhongRepository phongRepository;
    private final KhachThueRepository khachThueRepository;
    private final ChiSoDienNuocRepository chiSoRepository;
    private final BangGiaDienNuocRepository bangGiaRepository;
    private final HoaDonRepository hoaDonRepository;
    private final BoiThuongThietHaiRepository boiThuongRepository;

    public HopDongService(HopDongRepository hopDongRepository,
                          HopDongKhachThueRepository hdktRepository,
                          PhongRepository phongRepository,
                          KhachThueRepository khachThueRepository,
                          ChiSoDienNuocRepository chiSoRepository,
                          BangGiaDienNuocRepository bangGiaRepository,
                          HoaDonRepository hoaDonRepository,
                          BoiThuongThietHaiRepository boiThuongRepository) {
        this.hopDongRepository = hopDongRepository;
        this.hdktRepository = hdktRepository;
        this.phongRepository = phongRepository;
        this.khachThueRepository = khachThueRepository;
        this.chiSoRepository = chiSoRepository;
        this.bangGiaRepository = bangGiaRepository;
        this.hoaDonRepository = hoaDonRepository;
        this.boiThuongRepository = boiThuongRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public HopDongResponse findById(Integer id) {
        HopDong hd = hopDongRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hợp đồng", id));
        return toResponse(hd);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HopDongResponse> findAll() {
        return hopDongRepository.findAll().stream().map(this::toResponse).toList();
    }

    /**
     * Tao hop dong moi:
     * 1. Phong phai dang o trang thai TRONG.
     * 2. Tao 1 dong HopDongKhachThue cho khach dai dien voi la_dai_dien=true.
     * 3. Chuyen trang thai phong sang DANG_THUE.
     * 4. COPY gia_thue_mac_dinh tu phong sang hop_dong.tien_thue (khong tham chieu ngoc).
     */
    public HopDongResponse create(HopDongRequest request) {
        Phong phong = phongRepository.findById(request.getPhongId())
                .orElseThrow(() -> new ResourceNotFoundException("Phòng", request.getPhongId()));

        // Validate: phong phai TRONG
        if (!TrangThaiPhong.TRONG.equals(phong.getTrangThai())) {
            throw new BusinessException("Phòng " + phong.getSoPhong()
                    + " hiện không ở trạng thái Trống, không thể ký hợp đồng mới.");
        }

        KhachThue khachThue = khachThueRepository.findById(request.getKhachThueId())
                .orElseThrow(() -> new ResourceNotFoundException("Khách thuê", request.getKhachThueId()));

        // Tao hop dong - COPY gia thue tai thoi diem nay
        HopDong hopDong = new HopDong();
        hopDong.setPhong(phong);
        hopDong.setNgayBatDau(request.getNgayBatDau());
        hopDong.setNgayKetThuc(request.getNgayKetThuc());
        hopDong.setTienThue(request.getTienThue() != null
                ? request.getTienThue()
                : phong.getGiaThueTheoMacDinh()); // fallback: lay gia niem yet
        hopDong.setTienCoc(request.getTienCoc());
        hopDong.setTrangThai(TrangThaiHopDong.ACTIVE);
        hopDong = hopDongRepository.save(hopDong);

        // Tao dong HopDongKhachThue cho khach dai dien
        HopDongKhachThue hdkt = new HopDongKhachThue();
        hdkt.setHopDong(hopDong);
        hdkt.setKhachThue(khachThue);
        hdkt.setLaDaiDien(true); // nguoi ky hop dong chinh
        hdkt.setDangO(true);
        hdktRepository.save(hdkt);

        // Validate rang buoc nghiep vu sau khi tao:
        // Moi hop dong chi co DUNG 1 khach co la_dai_dien=true.
        // (Trong create chi tao 1 dong, nen dieu nay luon dung - validate day du hon trong addKhach)
        validateMotDaiDien(hopDong.getId());

        // Chuyen trang thai phong sang DANG_THUE
        phong.setTrangThai(TrangThaiPhong.DANG_THUE);
        phongRepository.save(phong);

        return toResponse(hopDong);
    }

    public void ketThuc(Integer id, TrangThaiHopDong trangThai) {
        HopDong hd = hopDongRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hợp đồng", id));
        if (!TrangThaiHopDong.ACTIVE.equals(hd.getTrangThai())) {
            throw new BusinessException("Hợp đồng này đã kết thúc rồi.");
        }

        // 1. Tinh tien dien/nuoc thang cuoi chua len hoa don (neu co ChiSoDienNuoc DA_CHOT nhung chua co HoaDon)
        BigDecimal tienDienNuocChuaHoaDon = BigDecimal.ZERO;
        List<ChiSoDienNuoc> dsChiSo = chiSoRepository.findByPhong_IdOrderByThangNamDesc(hd.getPhong().getId());
        BangGiaDienNuoc bangGia = bangGiaRepository.findTopByOrderByIdAsc().orElse(null);

        for (ChiSoDienNuoc chiSo : dsChiSo) {
            if (TrangThaiChiSo.DA_CHOT.equals(chiSo.getTrangThai())) {
                boolean daCoHoaDon = hoaDonRepository.existsByHopDong_IdAndThangNam(hd.getId(), chiSo.getThangNam());
                if (!daCoHoaDon && bangGia != null) {
                    BigDecimal soDien = (chiSo.getChiSoDienMoi() != null && chiSo.getChiSoDienCu() != null)
                            ? chiSo.getChiSoDienMoi().subtract(chiSo.getChiSoDienCu())
                            : BigDecimal.ZERO;
                    if (soDien.compareTo(BigDecimal.ZERO) > 0 && bangGia.getDonGiaDien() != null) {
                        tienDienNuocChuaHoaDon = tienDienNuocChuaHoaDon.add(soDien.multiply(bangGia.getDonGiaDien()));
                    }

                    BigDecimal soNuoc = (chiSo.getChiSoNuocMoi() != null && chiSo.getChiSoNuocCu() != null)
                            ? chiSo.getChiSoNuocMoi().subtract(chiSo.getChiSoNuocCu())
                            : BigDecimal.ZERO;
                    if (soNuoc.compareTo(BigDecimal.ZERO) > 0 && bangGia.getDonGiaNuoc() != null) {
                        tienDienNuocChuaHoaDon = tienDienNuocChuaHoaDon.add(soNuoc.multiply(bangGia.getDonGiaNuoc()));
                    }
                }
            }
        }

        // 2. Tong cac dong BoiThuongThietHai co nguon_phat_hien = LUC_TRA_PHONG va trang_thai chua xu ly
        List<BoiThuongThietHai> dsBoiThuong = boiThuongRepository.findByHopDong_Id(hd.getId());
        BigDecimal tongBoiThuong = BigDecimal.ZERO;
        List<BoiThuongThietHai> listCanCapNhat = new ArrayList<>();

        for (BoiThuongThietHai bt : dsBoiThuong) {
            if (NguonPhatHienBoiThuong.LUC_TRA_PHONG.equals(bt.getNguonPhatHien())
                    && TrangThaiBoiThuong.TRANG_THAI_0.equals(bt.getTrangThai())) {
                if (bt.getSoTien() != null) {
                    tongBoiThuong = tongBoiThuong.add(bt.getSoTien());
                }
                bt.setTrangThai(TrangThaiBoiThuong.TRANG_THAI_1);
                listCanCapNhat.add(bt);
            }
        }
        if (!listCanCapNhat.isEmpty()) {
            boiThuongRepository.saveAll(listCanCapNhat);
        }

        // 3. Tinh tien coc con lai (cho phep am)
        BigDecimal tongCongNo = tienDienNuocChuaHoaDon.add(tongBoiThuong);
        BigDecimal tienCocGoc = hd.getTienCoc() != null ? hd.getTienCoc() : BigDecimal.ZERO;
        BigDecimal tienCocConLai = tienCocGoc.subtract(tongCongNo);

        hd.setTienCocConLai(tienCocConLai);
        hd.setTrangThaiCoc(TrangThaiCoc.DA_KHAU_TRU);
        hd.setTrangThai(trangThai);
        hopDongRepository.save(hd);

        // Chuyen trang thai phong ve TRONG
        hd.getPhong().setTrangThai(TrangThaiPhong.TRONG);
        phongRepository.save(hd.getPhong());
    }

    public HopDongResponse themNguoiOCung(Integer hopDongId, com.qlytro.dto.request.KhachThueRequest khachRequest) {
        HopDong hopDong = hopDongRepository.findById(hopDongId)
                .orElseThrow(() -> new ResourceNotFoundException("Hợp đồng", hopDongId));

        if (!TrangThaiHopDong.ACTIVE.equals(hopDong.getTrangThai())) {
            throw new BusinessException("Hợp đồng đã kết thúc, không thể thêm người ở cùng.");
        }

        // Tim hoac tao khach thue theo CCCD
        KhachThue khachThue = khachThueRepository.findByCccd(khachRequest.getCccd())
                .orElseGet(() -> {
                    KhachThue kt = new KhachThue();
                    kt.setHoTen(khachRequest.getHoTen());
                    kt.setCccd(khachRequest.getCccd());
                    kt.setNgaySinh(khachRequest.getNgaySinh());
                    kt.setSdt(khachRequest.getSdt());
                    return khachThueRepository.save(kt);
                });

        // Kiem tra trong hop dong
        var existingHdkt = hdktRepository.findByHopDong_IdAndKhachThue_Id(hopDongId, khachThue.getId());
        if (existingHdkt.isPresent()) {
            HopDongKhachThue hdkt = existingHdkt.get();
            if (Boolean.TRUE.equals(hdkt.getDangO())) {
                throw new BusinessException("Khách thuê này hiện đã có tên và đang ở trong phòng.");
            }
            hdkt.setDangO(true);
            hdktRepository.save(hdkt);
        } else {
            HopDongKhachThue hdkt = new HopDongKhachThue();
            hdkt.setHopDong(hopDong);
            hdkt.setKhachThue(khachThue);
            hdkt.setLaDaiDien(false);
            hdkt.setDangO(true);
            hdktRepository.save(hdkt);
        }

        validateMotDaiDien(hopDongId);
        return toResponse(hopDong);
    }

    public HopDongResponse doiNguoiDaiDien(Integer hopDongId, Integer khachThueIdMoi) {
        HopDong hopDong = hopDongRepository.findById(hopDongId)
                .orElseThrow(() -> new ResourceNotFoundException("Hợp đồng", hopDongId));

        if (!TrangThaiHopDong.ACTIVE.equals(hopDong.getTrangThai())) {
            throw new BusinessException("Hợp đồng đã kết thúc, không thể đổi người đại diện.");
        }

        HopDongKhachThue hdktMoi = hdktRepository.findByHopDong_IdAndKhachThue_Id(hopDongId, khachThueIdMoi)
                .orElseThrow(() -> new BusinessException("Khách thuê không thuộc hợp đồng này."));

        if (!Boolean.TRUE.equals(hdktMoi.getDangO())) {
            throw new BusinessException("Khách thuê phải đang ở trong phòng mới có thể làm người đại diện.");
        }

        if (Boolean.TRUE.equals(hdktMoi.getLaDaiDien())) {
            throw new BusinessException("Khách thuê này hiện đã là người đại diện.");
        }

        // Tat quyen dai dien cua nguoi cu
        List<HopDongKhachThue> dsKhach = hdktRepository.findByHopDong_Id(hopDongId);
        for (HopDongKhachThue hdkt : dsKhach) {
            if (Boolean.TRUE.equals(hdkt.getLaDaiDien())) {
                hdkt.setLaDaiDien(false);
                hdktRepository.save(hdkt);
            }
        }

        // Bat quyen dai dien cho nguoi moi
        hdktMoi.setLaDaiDien(true);
        hdktRepository.save(hdktMoi);

        // Kiem tra rang buoc dung 1 dai dien
        validateMotDaiDien(hopDongId);

        return toResponse(hopDong);
    }

    public HopDongResponse nguoiRoiDi(Integer hopDongId, Integer khachThueId) {
        HopDong hopDong = hopDongRepository.findById(hopDongId)
                .orElseThrow(() -> new ResourceNotFoundException("Hợp đồng", hopDongId));

        if (!TrangThaiHopDong.ACTIVE.equals(hopDong.getTrangThai())) {
            throw new BusinessException("Hợp đồng đã kết thúc, không thể thay đổi thông tin.");
        }

        HopDongKhachThue hdkt = hdktRepository.findByHopDong_IdAndKhachThue_Id(hopDongId, khachThueId)
                .orElseThrow(() -> new BusinessException("Khách thuê không thuộc hợp đồng này."));

        if (Boolean.TRUE.equals(hdkt.getLaDaiDien())) {
            throw new BusinessException("Người đại diện không thể rời đi. Vui lòng chuyển quyền đại diện cho người khác trước.");
        }

        if (!Boolean.TRUE.equals(hdkt.getDangO())) {
            throw new BusinessException("Khách thuê này đã rời phòng trước đó.");
        }

        hdkt.setDangO(false);
        hdktRepository.save(hdkt);

        return toResponse(hopDong);
    }

    @Override
    public void deleteById(Integer id) {
        throw new BusinessException("Không thể xóa hợp đồng. Hãy chuyển trạng thái kết thúc.");
    }

    /**
     * Rang buoc nghiep vu: moi hop dong chi co DUNG 1 khach thuê co la_dai_dien=true.
     * DB khong tu ep duoc rang buoc nay nen phai validate trong Service.
     */
    private void validateMotDaiDien(Integer hopDongId) {
        long count = hdktRepository.countDaiDienByHopDongId(hopDongId);
        if (count != 1) {
            throw new BusinessException(
                    "Mỗi hợp đồng phải có đúng 1 khách thuê đại diện. Hiện có: " + count);
        }
    }

    public HopDongResponse toResponse(HopDong entity) {
        HopDongResponse r = new HopDongResponse();
        r.setId(entity.getId());
        r.setPhongId(entity.getPhong().getId());
        r.setSoPhong(entity.getPhong().getSoPhong());
        r.setTenToaNha(entity.getPhong().getToaNha().getTenToaNha());
        r.setNgayBatDau(entity.getNgayBatDau());
        r.setNgayKetThuc(entity.getNgayKetThuc());
        r.setTienThue(entity.getTienThue());
        r.setTienCoc(entity.getTienCoc());
        r.setTienCocConLai(entity.getTienCocConLai());
        r.setTrangThai(entity.getTrangThai());
        r.setTrangThaiLabel(entity.getTrangThai().getLabel());
        r.setTrangThaiCoc(entity.getTrangThaiCoc());
        r.setTrangThaiCocLabel(entity.getTrangThaiCoc().getLabel());
        r.setNgayTao(entity.getNgayTao());

        // Load danh sach khach thue
        List<HopDongKhachThue> danhSachHdkt = hdktRepository.findByHopDong_Id(entity.getId());
        List<KhachThueResponse> danhSachKhach = danhSachHdkt.stream()
                .map(hdkt -> {
                    KhachThueResponse kt = new KhachThueResponse();
                    kt.setId(hdkt.getKhachThue().getId());
                    kt.setHoTen(hdkt.getKhachThue().getHoTen());
                    kt.setCccd(hdkt.getKhachThue().getCccd());
                    kt.setSdt(hdkt.getKhachThue().getSdt());
                    kt.setLaDaiDien(hdkt.getLaDaiDien());
                    kt.setDangO(hdkt.getDangO());
                    return kt;
                }).toList();
        r.setDanhSachKhach(danhSachKhach);
        r.setDaiDien(danhSachKhach.stream()
                .filter(kt -> Boolean.TRUE.equals(kt.getLaDaiDien()))
                .findFirst().orElse(null));
        return r;
    }
}
