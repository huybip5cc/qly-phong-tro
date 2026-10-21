package com.qlytro.repository;

import com.qlytro.entity.HopDong;
import com.qlytro.entity.enums.TrangThaiHopDong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface HopDongRepository extends JpaRepository<HopDong, Integer> {

    List<HopDong> findByPhong_Id(Integer phongId);

    /** Tim hop dong dang ACTIVE cua 1 phong (dung khi tao hoa don / kiem tra). */
    Optional<HopDong> findByPhong_IdAndTrangThai(Integer phongId, TrangThaiHopDong trangThai);

    List<HopDong> findByTrangThai(TrangThaiHopDong trangThai);

    /**
     * Tim hop dong ACTIVE cua 1 khach thue (qua bang trung gian hdkt).
     * Dung trong SuCoService de lay phong_id tu tai khoan dang dang nhap.
     */
    @Query("""
            SELECT hd FROM HopDong hd
            JOIN FETCH hd.phong p
            JOIN FETCH p.toaNha tn
            JOIN HopDongKhachThue hdkt ON hdkt.hopDong = hd
            WHERE hdkt.khachThue.id = :khachThueId
              AND hd.trangThai = com.qlytro.entity.enums.TrangThaiHopDong.ACTIVE
              AND hdkt.dangO = true
            """)
    Optional<HopDong> findActiveByKhachThueId(@Param("khachThueId") Integer khachThueId);

    boolean existsByPhong_IdAndTrangThai(Integer phongId, TrangThaiHopDong trangThai);
}
