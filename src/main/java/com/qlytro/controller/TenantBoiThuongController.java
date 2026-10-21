package com.qlytro.controller;

import com.qlytro.security.CustomUserDetails;
import com.qlytro.service.impl.BoiThuongThietHaiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
@RequestMapping("/tenant/boi-thuong")
public class TenantBoiThuongController {

    private final BoiThuongThietHaiService boiThuongService;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public TenantBoiThuongController(BoiThuongThietHaiService boiThuongService) {
        this.boiThuongService = boiThuongService;
    }

    @GetMapping
    public String list(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("danhSach", boiThuongService.findByNguoiDungId(userDetails.getId()));
        model.addAttribute("pageTitle", "Bồi thường của tôi");
        model.addAttribute("pageContent", "tenant/boi-thuong/list");
        return "layout/base";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id,
                         @AuthenticationPrincipal CustomUserDetails userDetails,
                         Model model) {
        model.addAttribute("boiThuong", boiThuongService.findById(id));
        model.addAttribute("pageTitle", "Chi tiết Bồi thường");
        model.addAttribute("pageContent", "tenant/boi-thuong/detail");
        return "layout/base";
    }

    @PostMapping("/{id}/nop-tien")
    public String nopTien(@PathVariable Integer id,
                          @RequestParam(value = "file", required = false) MultipartFile file,
                          RedirectAttributes redirectAttributes) {
        String anhChuyenKhoan = null;
        if (file != null && !file.isEmpty()) {
            try {
                String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload.jpg");
                String filename = UUID.randomUUID() + "_" + originalFilename;
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Files.copy(file.getInputStream(), uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
                anhChuyenKhoan = "/uploads/" + filename;
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("errorMsg", "Không thể tải lên ảnh biên lai: " + e.getMessage());
                return "redirect:/tenant/boi-thuong/" + id;
            }
        }

        boiThuongService.khachNopTien(id, anhChuyenKhoan);
        redirectAttributes.addFlashAttribute("successMsg",
                "Đã gửi báo cáo thanh toán bồi thường. Chủ trọ sẽ kiểm tra và xác nhận sớm nhất!");
        return "redirect:/tenant/boi-thuong/" + id;
    }
}
