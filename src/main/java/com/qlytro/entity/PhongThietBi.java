package com.qlytro.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "phong_thiet_bi",
        uniqueConstraints = @UniqueConstraint(name = "uq_ptb",
                columnNames = {"phong_id", "thiet_bi_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PhongThietBi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "phong_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_ptb_phong"))
    private Phong phong;

    // ĐÃ SỬA: Đổi sang FetchType.EAGER để tránh LazyInitializationException khi Thymeleaf render
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "thiet_bi_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_ptb_thiet_bi"))
    private ThietBi thietBi;
}