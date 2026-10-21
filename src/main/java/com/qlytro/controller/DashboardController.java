package com.qlytro.controller;

import com.qlytro.dto.response.PhongNoTienResponse;
import com.qlytro.entity.HoaDon;
import com.qlytro.entity.Phong;
import com.qlytro.entity.enums.TrangThaiHoaDon;
import com.qlytro.entity.enums.TrangThaiHopDong;
import com.qlytro.entity.enums.TrangThaiPhong;
import com.qlytro.entity.enums.TrangThaiSuCo;
import com.qlytro.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@org.springframework.transaction.annotation.Transactional(readOnly = true)
public class DashboardController {

    private final PhongRepository phongRepository;
    private final HopDongRepository hopDongRepository;
    private final HoaDonRepository hoaDonRepository;
    private final SuCoRepository suCoRepository;
    private final HopDongKhachThueRepository hdktRepository;

    public DashboardController(PhongRepository phongRepository,
                               HopDongRepository hopDongRepository,
                               HoaDonRepository hoaDonRepository,
                               SuCoRepository suCoRepository,
                               HopDongKhachThueRepository hdktRepository) {
        this.phongRepository = phongRepository;
        this.hopDongRepository = hopDongRepository;
        this.hoaDonRepository = hoaDonRepository;
        this.suCoRepository = suCoRepository;
        this.hdktRepository = hdktRepository;
    }

    @GetMapping("/landlord/dashboard")
    public String landlordDashboard(Model model) {
        // Thong ke tong quan
        model.addAttribute("tongPhong", phongRepository.count());
        model.addAttribute("phongTrong", phongRepository.findByTrangThai(TrangThaiPhong.TRONG).size());
        model.addAttribute("phongDangThue", phongRepository.findByTrangThai(TrangThaiPhong.DANG_THUE).size());
        model.addAttribute("hopDongActive",
                hopDongRepository.findByTrangThai(TrangThaiHopDong.ACTIVE).size());
        model.addAttribute("hoaDonChoDuyet",
                hoaDonRepository.findByTrangThai(TrangThaiHoaDon.CHO_DUYET).size());
        model.addAttribute("suCoMoi",
                suCoRepository.findByTrangThai(TrangThaiSuCo.MOI).size());

        // 1. Tong doanh thu thang hien tai
        String thangHienTai = YearMonth.now().toString();
        BigDecimal doanhThuThang = hoaDonRepository.sumTongTienByTrangThaiAndThangNam(TrangThaiHoaDon.DA_THANH_TOAN, thangHienTai);
        model.addAttribute("doanhThuThang", doanhThuThang != null ? doanhThuThang : BigDecimal.ZERO);
        model.addAttribute("thangHienTai", thangHienTai);

        // 2. Danh sach phong dang no tien
        List<HoaDon> dsHoaDonChuaThanhToan = hoaDonRepository.findByTrangThaiNotFetchHopDongAndPhong(TrangThaiHoaDon.DA_THANH_TOAN);
        Map<Phong, List<HoaDon>> mapPhongNo = dsHoaDonChuaThanhToan.stream()
                .filter(hd -> hd.getHopDong() != null && hd.getHopDong().getPhong() != null)
                .collect(Collectors.groupingBy(hd -> hd.getHopDong().getPhong()));

        List<PhongNoTienResponse> danhSachPhongNo = mapPhongNo.entrySet().stream()
                .map(entry -> {
                    Phong phong = entry.getKey();
                    List<HoaDon> hds = entry.getValue();

                    BigDecimal tongTienNo = hds.stream()
                            .map(HoaDon::getTongTien)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    long soThangNo = hds.size();
                    List<String> dsThangNo = hds.stream()
                            .map(HoaDon::getThangNam)
                            .sorted()
                            .distinct()
                            .toList();

                    Integer hopDongId = hds.get(0).getHopDong().getId();
                    String tenKhach = hdktRepository.findByHopDong_Id(hopDongId).stream()
                            .filter(hdkt -> Boolean.TRUE.equals(hdkt.getLaDaiDien()))
                            .map(hdkt -> hdkt.getKhachThue().getHoTen())
                            .findFirst()
                            .orElse("—");

                    return PhongNoTienResponse.builder()
                            .phongId(phong.getId())
                            .soPhong(phong.getSoPhong())
                            .tenToaNha(phong.getToaNha().getTenToaNha())
                            .tenKhachDaiDien(tenKhach)
                            .tongTienNo(tongTienNo)
                            .soThangNo(soThangNo)
                            .danhSachThangNo(dsThangNo)
                            .build();
                })
                .sorted(Comparator.comparing(PhongNoTienResponse::getTongTienNo).reversed())
                .toList();

        model.addAttribute("danhSachPhongNo", danhSachPhongNo);
        BigDecimal tongCongNoToanBo = danhSachPhongNo.stream()
                .map(PhongNoTienResponse::getTongTienNo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("tongCongNoToanBo", tongCongNoToanBo);

        model.addAttribute("pageTitle", "Tổng quan");
        model.addAttribute("pageContent", "landlord/dashboard");
        return "layout/base";
    }
}