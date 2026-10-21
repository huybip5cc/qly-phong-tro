package com.qlytro.controller;

import com.qlytro.service.impl.HoaDonService;
import com.qlytro.service.impl.HopDongService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HoaDonController {

    private final HoaDonService hoaDonService;
    private final HopDongService hopDongService;

    public HoaDonController(HoaDonService hoaDonService, HopDongService hopDongService) {
        this.hoaDonService = hoaDonService;
        this.hopDongService = hopDongService;
    }

    // ===== LANDLORD =====
    @GetMapping("/landlord/hoa-don")
    public String landlordList(Model model) {
        model.addAttribute("danhSach", hoaDonService.findAll());
        model.addAttribute("pageTitle", "Quản lý Hóa đơn");

        // SỬA 1: Trả về layout/base
        model.addAttribute("pageContent", "landlord/hoa-don/list");
        return "layout/base";
    }

    @GetMapping("/landlord/hoa-don/{id}")
    public String landlordDetail(@PathVariable Integer id, Model model) {
        model.addAttribute("hoaDon", hoaDonService.findById(id));
        model.addAttribute("pageTitle", "Chi tiết Hóa đơn");

        // SỬA 2: Trả về layout/base
        model.addAttribute("pageContent", "landlord/hoa-don/detail");
        return "layout/base";
    }

    @PostMapping("/landlord/hoa-don/tao")
    public String taoHoaDon(@RequestParam(required = false) Integer hopDongId,
                            @RequestParam(required = false) Integer phongId,
                            @RequestParam String thangNam,
                            RedirectAttributes redirectAttributes) {
        try {
            // Nếu không có hopDongId nhưng có phongId (từ trang chỉ số sang) -> tự tìm hợp đồng đang hiệu lực
            if (hopDongId == null && phongId != null) {
                var dsHopDong = hopDongService.findAll();
                for (var hdItem : dsHopDong) {
                    if (hdItem.getPhongId() != null && hdItem.getPhongId().equals(phongId)
                            && hdItem.getTrangThai() != null
                            && "ACTIVE".equalsIgnoreCase(hdItem.getTrangThai().name())) {
                        hopDongId = hdItem.getId();
                        break;
                    }
                }
            }

            if (hopDongId == null) {
                redirectAttributes.addFlashAttribute("errorMsg", "Không tìm thấy hợp đồng đang hiệu lực cho phòng này!");
                return "redirect:/landlord/chi-so";
            }

            var hd = hoaDonService.taoHoaDon(hopDongId, thangNam);
            redirectAttributes.addFlashAttribute("successMsg",
                    "Tạo hóa đơn tháng " + thangNam + " thành công!");
            return "redirect:/landlord/hoa-don/" + hd.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi tạo hóa đơn: " + e.getMessage());
            return "redirect:/landlord/chi-so";
        }
    }

    @PostMapping("/landlord/hoa-don/{id}/xac-nhan")
    public String xacNhanThanhToan(@PathVariable Integer id,
                                   RedirectAttributes redirectAttributes) {
        hoaDonService.xacNhanThanhToan(id);
        redirectAttributes.addFlashAttribute("successMsg", "Đã xác nhận thanh toán!");
        return "redirect:/landlord/hoa-don/" + id;
    }

    @PostMapping("/landlord/hoa-don/{id}/xoa")
    public String xoa(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        hoaDonService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMsg", "Đã xóa hóa đơn.");
        return "redirect:/landlord/hoa-don";
    }
}