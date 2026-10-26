package com.qlytro.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DoiMatKhauRequest {

    @NotBlank(message = "Vui lòng nhập mật khẩu hiện tại")
    private String currentPassword;

    @NotBlank(message = "Vui lòng nhập mật khẩu mới")
    @Size(min = 8, max = 72, message = "Mật khẩu mới phải từ 8 đến 72 ký tự")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[^\\p{L}\\p{N}\\s]).+$",
            message = "Mật khẩu mới phải có ít nhất một chữ số và một ký tự đặc biệt")
    private String newPassword;

    @NotBlank(message = "Vui lòng xác nhận mật khẩu mới")
    private String confirmPassword;
}
