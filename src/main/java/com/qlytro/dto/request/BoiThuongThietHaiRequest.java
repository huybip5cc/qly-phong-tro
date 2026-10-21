package com.qlytro.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoiThuongThietHaiRequest {

    @NotNull(message = "Vui lòng chọn hợp đồng")
    private Integer hopDongId;

    private Integer suCoId;

    @NotBlank(message = "Mô tả thiệt hại không được để trống")
    private String moTa;

    @NotNull(message = "Số tiền bồi thường không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Số tiền bồi thường phải lớn hơn 0")
    private BigDecimal soTien;

    @NotNull(message = "Vui lòng chọn nguồn phát hiện")
    private Integer nguonPhatHien; // 0: TRONG_LUC_O, 1: LUC_TRA_PHONG

    private LocalDate ngayGhiNhan;
}
