package com.qlytro.repository;

import com.qlytro.entity.ToaNha;
import com.qlytro.entity.enums.TrangThaiToaNha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ToaNhaRepository extends JpaRepository<ToaNha, Integer> {
    List<ToaNha> findByTrangThai(TrangThaiToaNha trangThai);
    boolean existsByTenToaNha(String tenToaNha);
}
