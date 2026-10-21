package com.qlytro.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
public class KhachThueRequest {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100)
    private String hoTen;

    @NotBlank(message = "Số CCCD không được để trống")
    @Size(max = 20)
    private String cccd;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate ngaySinh;

    @Size(max = 15)
    private String sdt;

    /** Optional: ID nguoiDung neu muon tao kem tai khoan cho khach nay. */
    private Integer nguoiDungId;
}
