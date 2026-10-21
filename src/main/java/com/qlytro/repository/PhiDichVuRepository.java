package com.qlytro.repository;

import com.qlytro.entity.PhiDichVu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PhiDichVuRepository extends JpaRepository<PhiDichVu, Integer> {
    List<PhiDichVu> findByDangApDung(Boolean dangApDung);
}
