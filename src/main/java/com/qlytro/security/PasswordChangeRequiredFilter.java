package com.qlytro.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class PasswordChangeRequiredFilter extends OncePerRequestFilter {

    private static final String CHANGE_PASSWORD_PATH = "/tenant/change-password";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUserDetails user
                && user.isMustChangePassword()
                && !isAllowedDuringPasswordChange(request.getServletPath())) {
            response.sendRedirect(request.getContextPath() + CHANGE_PASSWORD_PATH);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAllowedDuringPasswordChange(String path) {
        return path.equals(CHANGE_PASSWORD_PATH)
                || path.equals("/logout")
                || path.equals("/login")
                || path.equals("/error")
                || path.equals("/favicon.ico")
                || path.startsWith("/error/")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/uploads/");
    }
}
