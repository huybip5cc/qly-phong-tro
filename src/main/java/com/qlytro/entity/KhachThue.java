package com.qlytro.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "khach_thue")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KhachThue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ho_ten", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "cccd", nullable = false, unique = true, length = 20)
    private String cccd;

    /** NULLABLE trong DB -> LocalDate (reference type, co the null). */
    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "sdt", length = 15)
    private String sdt;

    /**
     * NULLABLE: chi khach_thue la dai dien (co tai khoan) moi co gia tri.
     * Nguoi o cung khac de NULL.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id",
                foreignKey = @ForeignKey(name = "fk_khach_thue_nguoi_dung"))
    private NguoiDung nguoiDung;
}
