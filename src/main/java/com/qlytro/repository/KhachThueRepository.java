package com.qlytro.repository;

import com.qlytro.entity.KhachThue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface KhachThueRepository extends JpaRepository<KhachThue, Integer> {
    boolean existsByCccd(String cccd);
    boolean existsByCccdAndIdNot(String cccd, Integer id);
    Optional<KhachThue> findByCccd(String cccd);
    Optional<KhachThue> findByNguoiDung_Id(Integer nguoiDungId);
}
