package com.qlytro.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ChiSoDienNuocRequest {

    @NotNull(message = "Vui lòng chọn phòng")
    private Integer phongId;

    @NotBlank(message = "Tháng năm không được để trống")
    @Pattern(regexp = "\\d{4}-\\d{2}", message = "Định dạng tháng-năm phải là YYYY-MM")
    private String thangNam;

    @NotNull(message = "Chỉ số điện cũ không được để trống")
    @DecimalMin(value = "0.0", message = "Chỉ số không được âm")
    private BigDecimal chiSoDienCu;

    @NotNull(message = "Chỉ số điện mới không được để trống")
    @DecimalMin(value = "0.0", message = "Chỉ số không được âm")
    private BigDecimal chiSoDienMoi;

    @NotNull(message = "Chỉ số nước cũ không được để trống")
    @DecimalMin(value = "0.0", message = "Chỉ số không được âm")
    private BigDecimal chiSoNuocCu;

    @NotNull(message = "Chỉ số nước mới không được để trống")
    @DecimalMin(value = "0.0", message = "Chỉ số không được âm")
    private BigDecimal chiSoNuocMoi;

    private String anhChiSo;

    @Size(max = 255)
    private String ghiChu;
}
