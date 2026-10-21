package com.qlytro.repository;

import com.qlytro.entity.BangGiaDienNuoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BangGiaDienNuocRepository extends JpaRepository<BangGiaDienNuoc, Integer> {
    /** Luon lay dong duy nhat (id = 1 hoac dong dau tien). */
    Optional<BangGiaDienNuoc> findTopByOrderByIdAsc();
}
