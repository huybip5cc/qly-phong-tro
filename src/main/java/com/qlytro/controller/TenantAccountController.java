package com.qlytro.controller;

import com.qlytro.dto.request.DoiMatKhauRequest;
import com.qlytro.exception.BusinessException;
import com.qlytro.security.CustomUserDetails;
import com.qlytro.service.impl.NguoiDungService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tenant/change-password")
public class TenantAccountController {

    private final NguoiDungService nguoiDungService;

    public TenantAccountController(NguoiDungService nguoiDungService) {
        this.nguoiDungService = nguoiDungService;
    }

    @GetMapping
    public String form(Model model) {
        model.addAttribute("request", new DoiMatKhauRequest());
        return renderForm(model);
    }

    @PostMapping
    public String changePassword(@Valid @ModelAttribute("request") DoiMatKhauRequest request,
                                 BindingResult result,
                                 @AuthenticationPrincipal CustomUserDetails user,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (request.getNewPassword() != null
                && !request.getNewPassword().equals(request.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "password.mismatch",
                    "Mật khẩu xác nhận không khớp.");
        }
        if (result.hasErrors()) {
            return renderForm(model);
        }

        try {
            nguoiDungService.changePassword(user.getId(),
                    request.getCurrentPassword(), request.getNewPassword());
        } catch (BusinessException ex) {
            model.addAttribute("errorMsg", ex.getMessage());
            return renderForm(model);
        }

        user.markPasswordChangeComplete();
        redirectAttributes.addFlashAttribute("successMsg", "Đổi mật khẩu thành công.");
        return "redirect:/tenant/dashboard";
    }

    private String renderForm(Model model) {
        model.addAttribute("pageTitle", "Đổi mật khẩu");
        model.addAttribute("pageContent", "tenant/change-password");
        return "layout/base";
    }
}
