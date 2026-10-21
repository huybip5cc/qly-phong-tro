package com.qlytro.dto.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class KhachThueResponse {
    private Integer id;
    private String hoTen;
    private String cccd;
    private LocalDate ngaySinh;
    private String sdt;
    private Integer nguoiDungId;
    private String username;
    private Boolean laDaiDien;
    private Boolean dangO;
}
