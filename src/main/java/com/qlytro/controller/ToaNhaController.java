package com.qlytro.controller;

import com.qlytro.dto.request.ToaNhaRequest;
import com.qlytro.service.impl.ToaNhaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.qlytro.entity.enums.TrangThaiToaNha;

@Controller
@RequestMapping("/landlord/toa-nha")
public class ToaNhaController {

    private final ToaNhaService toaNhaService;

    public ToaNhaController(ToaNhaService toaNhaService) {
        this.toaNhaService = toaNhaService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("danhSach", toaNhaService.findAll());
        model.addAttribute("pageTitle", "Quản lý Tòa nhà");
        model.addAttribute("pageContent", "landlord/toa-nha/list");
        return "layout/base";
    }

    @GetMapping("/them")
    public String themForm(Model model) {
        model.addAttribute("request", new ToaNhaRequest());
        model.addAttribute("trangThaiList", TrangThaiToaNha.values());
        model.addAttribute("pageTitle", "Thêm Tòa nhà");
        model.addAttribute("pageContent", "landlord/toa-nha/form");
        return "layout/base";
    }

    @PostMapping("/them")
    public String them(@Valid @ModelAttribute("request") ToaNhaRequest request,
                       BindingResult result, Model model,
                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("trangThaiList", TrangThaiToaNha.values());
            model.addAttribute("pageTitle", "Thêm Tòa nhà");
            model.addAttribute("pageContent", "landlord/toa-nha/form");
            return "layout/base";
        }
        toaNhaService.create(request);
        redirectAttributes.addFlashAttribute("successMsg", "Thêm tòa nhà thành công!");
        return "redirect:/landlord/toa-nha";
    }

    @GetMapping("/sua/{id}")
    public String suaForm(@PathVariable Integer id, Model model) {
        var existing = toaNhaService.findById(id);
        ToaNhaRequest request = new ToaNhaRequest();
        request.setTenToaNha(existing.getTenToaNha());
        request.setDiaChi(existing.getDiaChi());
        request.setTrangThai(existing.getTrangThai().getValue());
        model.addAttribute("request", request);
        model.addAttribute("id", id);
        model.addAttribute("trangThaiList", TrangThaiToaNha.values());
        model.addAttribute("pageTitle", "Sửa Tòa nhà");
        model.addAttribute("pageContent", "landlord/toa-nha/form");
        return "layout/base";
    }

    @PostMapping("/sua/{id}")
    public String sua(@PathVariable Integer id,
                      @Valid @ModelAttribute("request") ToaNhaRequest request,
                      BindingResult result, Model model,
                      RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("id", id);
            model.addAttribute("trangThaiList", TrangThaiToaNha.values());
            model.addAttribute("pageTitle", "Sửa Tòa nhà");
            model.addAttribute("pageContent", "landlord/toa-nha/form");
            return "layout/base";
        }
        toaNhaService.update(id, request);
        redirectAttributes.addFlashAttribute("successMsg", "Cập nhật tòa nhà thành công!");
        return "redirect:/landlord/toa-nha";
    }

    @PostMapping("/xoa/{id}")
    public String xoa(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        toaNhaService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMsg", "Xóa tòa nhà thành công!");
        return "redirect:/landlord/toa-nha";
    }
}