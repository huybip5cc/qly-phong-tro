package com.qlytro.repository;

import com.qlytro.entity.BoiThuongThietHai;
import com.qlytro.entity.enums.NguonPhatHienBoiThuong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BoiThuongThietHaiRepository extends JpaRepository<BoiThuongThietHai, Integer> {
    List<BoiThuongThietHai> findByHopDong_Id(Integer hopDongId);
    List<BoiThuongThietHai> findByNguonPhatHien(NguonPhatHienBoiThuong nguon);
    List<BoiThuongThietHai> findBySuCo_Id(Integer suCoId);
}
