package com.qlytro.repository;

import com.qlytro.entity.HoaDon;
import com.qlytro.entity.enums.TrangThaiHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {
    List<HoaDon> findByHopDong_Id(Integer hopDongId);
    Optional<HoaDon> findByHopDong_IdAndThangNam(Integer hopDongId, String thangNam);
    boolean existsByHopDong_IdAndThangNam(Integer hopDongId, String thangNam);
    List<HoaDon> findByTrangThai(TrangThaiHoaDon trangThai);
    List<HoaDon> findByHopDong_IdOrderByThangNamDesc(Integer hopDongId);

    @Query("SELECT COALESCE(SUM(h.tongTien), 0) FROM HoaDon h WHERE h.trangThai = :trangThai AND h.thangNam = :thangNam")
    BigDecimal sumTongTienByTrangThaiAndThangNam(@Param("trangThai") TrangThaiHoaDon trangThai, @Param("thangNam") String thangNam);

    @Query("SELECT h FROM HoaDon h JOIN FETCH h.hopDong hd JOIN FETCH hd.phong p JOIN FETCH p.toaNha WHERE h.trangThai <> :trangThai")
    List<HoaDon> findByTrangThaiNotFetchHopDongAndPhong(@Param("trangThai") TrangThaiHoaDon trangThai);

    List<HoaDon> findByTrangThaiNot(TrangThaiHoaDon trangThai);
}

