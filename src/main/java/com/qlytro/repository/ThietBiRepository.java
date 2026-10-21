package com.qlytro.repository;

import com.qlytro.entity.ThietBi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThietBiRepository extends JpaRepository<ThietBi, Integer> {
    boolean existsByTenThietBi(String tenThietBi);
}
