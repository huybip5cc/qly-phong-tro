package com.qlytro.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "thiet_bi")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ThietBi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_thiet_bi", nullable = false, length = 100)
    private String tenThietBi;
}
