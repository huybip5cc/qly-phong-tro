package com.qlytro.repository;

import com.qlytro.entity.Phong;
import com.qlytro.entity.enums.TrangThaiPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhongRepository extends JpaRepository<Phong, Integer> {

    List<Phong> findByToaNha_Id(Integer toaNhaId);

    List<Phong> findByToaNha_IdAndTrangThai(Integer toaNhaId, TrangThaiPhong trangThai);

    List<Phong> findByTrangThai(TrangThaiPhong trangThai);

    boolean existsByToaNha_IdAndSoPhong(Integer toaNhaId, String soPhong);

    boolean existsByToaNha_IdAndSoPhongAndIdNot(Integer toaNhaId, String soPhong, Integer id);

    Optional<Phong> findByToaNha_IdAndSoPhong(Integer toaNhaId, String soPhong);

    @Query("SELECT COUNT(p) FROM Phong p WHERE p.toaNha.id = :toaNhaId")
    long countByToaNhaId(@Param("toaNhaId") Integer toaNhaId);
}
