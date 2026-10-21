package com.qlytro.repository;

import com.qlytro.entity.NguoiDung;
import com.qlytro.entity.enums.VaiTroNguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Integer> {
    Optional<NguoiDung> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<NguoiDung> findByVaiTro(VaiTroNguoiDung vaiTro);
}
