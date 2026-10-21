package com.qlytro.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class NguoiDungRequest {

    @NotBlank(message = "Username không được để trống")
    @Size(min = 4, max = 50, message = "Username từ 4-50 ký tự")
    private String username;

    /** Mat khau plain text — Service se ma hoa BCrypt truoc khi luu. */
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu tối thiểu 6 ký tự")
    private String password;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100)
    private String hoTen;

    @Size(max = 15)
    private String sdt;

    @Email(message = "Email không hợp lệ")
    @Size(max = 100)
    private String email;

    /** 1 = CHU_TRO, 0 = KHACH_THUE */
    @NotNull(message = "Vai trò không được để trống")
    private Integer vaiTro;
}
