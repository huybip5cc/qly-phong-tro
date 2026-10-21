package com.qlytro.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PhongRequest {

    @NotNull(message = "Vui lòng chọn tòa nhà")
    private Integer toaNhaId;

    @NotBlank(message = "Số phòng không được để trống")
    @Size(max = 10, message = "Số phòng tối đa 10 ký tự")
    private String soPhong;

    @DecimalMin(value = "0.0", inclusive = false, message = "Diện tích phải lớn hơn 0")
    private BigDecimal dienTich;

    @NotNull(message = "Giá thuê không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá thuê phải lớn hơn 0")
    private BigDecimal giaThueTheoMacDinh;

    /** 0=TRONG, 1=DANG_THUE, 2=NGUNG_SU_DUNG */
    private Integer trangThai = 0;

    @Size(max = 255)
    private String moTa;
}
