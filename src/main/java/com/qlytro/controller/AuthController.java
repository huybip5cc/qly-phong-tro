package com.qlytro.controller;

import com.qlytro.security.CustomUserDetails;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            if (authentication.getPrincipal() instanceof CustomUserDetails user
                    && user.isMustChangePassword()) {
                return "redirect:/tenant/change-password";
            }
            boolean isLandlord = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_LANDLORD"));
            return isLandlord ? "redirect:/landlord/dashboard" : "redirect:/tenant/dashboard";
        }
        return "auth/login";
    }

    @GetMapping("/")
    public String root(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            if (authentication.getPrincipal() instanceof CustomUserDetails user
                    && user.isMustChangePassword()) {
                return "redirect:/tenant/change-password";
            }
            boolean isLandlord = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_LANDLORD"));
            return isLandlord ? "redirect:/landlord/dashboard" : "redirect:/tenant/dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/error/403")
    public String accessDenied() {
        return "error/403";
    }
}
