package com.qlytro.controller;

import com.qlytro.entity.KhachThue;
import com.qlytro.repository.HoaDonRepository;
import com.qlytro.repository.HopDongRepository;
import com.qlytro.repository.KhachThueRepository;
import com.qlytro.repository.SuCoRepository;
import com.qlytro.security.CustomUserDetails;
import com.qlytro.entity.enums.TrangThaiHoaDon;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tenant")
public class TenantController {

    private final KhachThueRepository khachThueRepository;
    private final HopDongRepository hopDongRepository;
    private final HoaDonRepository hoaDonRepository;
    private final SuCoRepository suCoRepository;

    public TenantController(KhachThueRepository khachThueRepository,
                            HopDongRepository hopDongRepository,
                            HoaDonRepository hoaDonRepository,
                            SuCoRepository suCoRepository) {
        this.khachThueRepository = khachThueRepository;
        this.hopDongRepository = hopDongRepository;
        this.hoaDonRepository = hoaDonRepository;
        this.suCoRepository = suCoRepository;
    }

    @GetMapping({"/dashboard", "/"})
    @Transactional(readOnly = true)
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("pageTitle", "Trang chủ");

        if (userDetails != null) {
            model.addAttribute("nguoiDung", userDetails.getNguoiDung());

            // Tim khach thue lien ket voi user dang dang nhap
            KhachThue khachThue = khachThueRepository
                    .findByNguoiDung_Id(userDetails.getId())
                    .orElse(null);

            if (khachThue != null) {
                // Tim hop dong active
                hopDongRepository.findActiveByKhachThueId(khachThue.getId())
                        .ifPresent(hopDong -> {
                            model.addAttribute("hopDong", hopDong);
                            model.addAttribute("soPhong", hopDong.getPhong().getSoPhong());
                            model.addAttribute("tenToaNha", hopDong.getPhong().getToaNha().getTenToaNha());

                            // Dem hoa don chua thanh toan
                            long hoaDonChuaTT = hoaDonRepository
                                    .findByHopDong_IdOrderByThangNamDesc(hopDong.getId())
                                    .stream()
                                    .filter(hd -> TrangThaiHoaDon.CHUA_THANH_TOAN.equals(hd.getTrangThai()))
                                    .count();
                            model.addAttribute("soHoaDonChuaTT", hoaDonChuaTT);
                        });

                // Dem su co dang xu ly cua user nay
                long suCoDangXuLy = suCoRepository.findByNguoiBao_Id(userDetails.getId())
                        .stream()
                        .filter(sc -> !com.qlytro.entity.enums.TrangThaiSuCo.HOAN_THANH.equals(sc.getTrangThai()))
                        .count();
                model.addAttribute("soSuCoDangXuLy", suCoDangXuLy);
            }
        }

        // Neu dashboard cua tenant dung layout/base chung:
        model.addAttribute("pageContent", "tenant/dashboard");
        return "layout/base";

        // (Luu y: Neu file tenant/dashboard.html la trang doc lap khong qua layout/base thi doi thanh: return "tenant/dashboard";)
    }
}