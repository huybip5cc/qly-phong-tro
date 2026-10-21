package com.qlytro.controller;

import com.qlytro.security.CustomUserDetails;
import com.qlytro.service.impl.HoaDonService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tenant/hoa-don")
public class TenantHoaDonController {

    private final HoaDonService hoaDonService;

    public TenantHoaDonController(HoaDonService hoaDonService) {
        this.hoaDonService = hoaDonService;
    }

    @GetMapping
    public String list(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        // Filter hoa don theo nguoi dung dang dang nhap
        model.addAttribute("danhSach", hoaDonService.findByNguoiDungId(userDetails.getId()));
        model.addAttribute("pageTitle", "Hóa đơn của tôi");
        model.addAttribute("pageContent", "tenant/hoa-don/list");
        return "layout/base";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id,
                         @AuthenticationPrincipal CustomUserDetails userDetails,
                         Model model) {
        model.addAttribute("hoaDon", hoaDonService.findById(id));
        model.addAttribute("pageTitle", "Chi tiết Hóa đơn");
        model.addAttribute("pageContent", "tenant/hoa-don/detail");
        return "layout/base";
    }

    /**
     * Khach thue bao da chuyen khoan -> chuyen trang thai sang CHO_DUYET.
     * Chu tro se xac nhan o trang landlord/hoa-don/{id}/xac-nhan.
     */
    @PostMapping("/{id}/nop-tien")
    public String nopTien(@PathVariable Integer id,
                          RedirectAttributes redirectAttributes) {
        hoaDonService.khachNopTienForm(id);
        redirectAttributes.addFlashAttribute("successMsg",
                "Đã gửi xác nhận thanh toán. Chủ trọ sẽ kiểm tra và xác nhận sớm nhất!");
        return "redirect:/tenant/hoa-don/" + id;
    }
}
