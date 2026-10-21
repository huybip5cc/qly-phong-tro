package com.qlytro.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HopDongRequest {

    @NotNull(message = "Vui lòng chọn phòng")
    private Integer phongId;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate ngayBatDau;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate ngayKetThuc;

    @NotNull(message = "Tiền thuê không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Tiền thuê phải lớn hơn 0")
    private BigDecimal tienThue;

    @NotNull(message = "Tiền cọc không được để trống")
    @DecimalMin(value = "0.0", message = "Tiền cọc không được âm")
    private BigDecimal tienCoc;

    /** ID khach thue dai dien (bat buoc). */
    @NotNull(message = "Vui lòng chọn khách thuê đại diện")
    private Integer khachThueId;
}
