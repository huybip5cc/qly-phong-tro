package com.qlytro.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SuCoRequest {

    /** phong_id duoc Service tu xac dinh tu hop dong ACTIVE neu la TENANT. */
    private Integer phongId;

    /** Nullable: null neu su co khong thuoc thiet bi cu the. */
    private Integer thietBiId;

    @NotBlank(message = "Mô tả sự cố không được để trống")
    @Size(max = 255)
    private String moTa;
}
