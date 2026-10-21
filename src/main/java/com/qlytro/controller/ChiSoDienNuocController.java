package com.qlytro.controller;

import com.qlytro.dto.request.ChiSoDienNuocRequest;
import com.qlytro.service.impl.ChiSoDienNuocService;
import com.qlytro.service.impl.PhongService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/landlord/chi-so")
public class ChiSoDienNuocController {

    private final ChiSoDienNuocService chiSoService;
    private final PhongService phongService;

    public ChiSoDienNuocController(ChiSoDienNuocService chiSoService,
                                   PhongService phongService) {
        this.chiSoService = chiSoService;
        this.phongService = phongService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Integer phongId, Model model) {
        if (phongId != null) {
            model.addAttribute("danhSach", chiSoService.findByPhong(phongId));
            model.addAttribute("phongFilter", phongService.findById(phongId));
        } else {
            model.addAttribute("danhSach", chiSoService.findAll());
        }
        model.addAttribute("danhSachPhong", phongService.findAll());
        model.addAttribute("pageTitle", "Chỉ số Điện / Nước");

        // SỬA 1: Trả về layout/base
        model.addAttribute("pageContent", "landlord/chi-so/list");
        return "layout/base";
    }

    @GetMapping("/nhap")
    public String nhapForm(@RequestParam(required = false) Integer phongId, Model model) {
        ChiSoDienNuocRequest req = new ChiSoDienNuocRequest();
        req.setPhongId(phongId);
        // Goi y thang hien tai
        req.setThangNam(java.time.YearMonth.now().toString());

        boolean daCoChiSoCu = false;
        if (phongId != null) {
            var latestOpt = chiSoService.findLatestByPhong(phongId);
            if (latestOpt.isPresent()) {
                var latest = latestOpt.get();
                req.setChiSoDienCu(latest.getChiSoDienMoi());
                req.setChiSoNuocCu(latest.getChiSoNuocMoi());
                daCoChiSoCu = true;
            } else {
                req.setChiSoDienCu(java.math.BigDecimal.ZERO);
                req.setChiSoNuocCu(java.math.BigDecimal.ZERO);
            }
        }

        model.addAttribute("request", req);
        model.addAttribute("daCoChiSoCu", daCoChiSoCu);
        model.addAttribute("danhSachPhong", phongService.findAll());
        model.addAttribute("pageTitle", "Nhập chỉ số điện/nước");

        // Trả về layout/base cho form nhập
        model.addAttribute("pageContent", "landlord/chi-so/form");
        return "layout/base";
    }

    @PostMapping("/nhap")
    public String nhap(@Valid @ModelAttribute("request") ChiSoDienNuocRequest request,
                       BindingResult result, Model model,
                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("danhSachPhong", phongService.findAll());
            model.addAttribute("pageTitle", "Nhập chỉ số điện/nước");

            // SỬA 3: Khi lỗi validate thì trả lại form trong base
            model.addAttribute("pageContent", "landlord/chi-so/form");
            return "layout/base";
        }
        chiSoService.create(request);
        redirectAttributes.addFlashAttribute("successMsg", "Nhập chỉ số thành công!");
        return "redirect:/landlord/chi-so";
    }

    @PostMapping("/{id}/chot")
    public String chotChiSo(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        chiSoService.chotChiSo(id);
        redirectAttributes.addFlashAttribute("successMsg", "Chốt chỉ số thành công!");
        return "redirect:/landlord/chi-so";
    }

    @PostMapping("/{id}/xoa")
    public String xoa(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        chiSoService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMsg", "Xóa chỉ số thành công!");
        return "redirect:/landlord/chi-so";
    }
}