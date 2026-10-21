package com.qlytro.service.impl;

import com.qlytro.entity.PhiDichVu;
import com.qlytro.entity.enums.LoaiTinhPhi;
import com.qlytro.exception.ResourceNotFoundException;
import com.qlytro.repository.PhiDichVuRepository;
import com.qlytro.service.base.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class PhiDichVuService implements BaseService<PhiDichVu, Integer> {

    private final PhiDichVuRepository phiDichVuRepository;

    public PhiDichVuService(PhiDichVuRepository phiDichVuRepository) {
        this.phiDichVuRepository = phiDichVuRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PhiDichVu findById(Integer id) {
        return phiDichVuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phí dịch vụ", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhiDichVu> findAll() {
        return phiDichVuRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<PhiDichVu> findDangApDung() {
        return phiDichVuRepository.findByDangApDung(true);
    }

    // Nhận thêm tham số loaiTinh (mặc định THEO_PHONG nếu truyền vào null)
    public PhiDichVu create(String tenPhi, BigDecimal soTien, LoaiTinhPhi loaiTinh) {
        PhiDichVu phi = new PhiDichVu();
        phi.setTenPhi(tenPhi);
        phi.setSoTien(soTien);
        phi.setLoaiTinh(loaiTinh != null ? loaiTinh : LoaiTinhPhi.THEO_PHONG);
        phi.setDangApDung(true);
        return phiDichVuRepository.save(phi);
    }

    public PhiDichVu update(Integer id, String tenPhi, BigDecimal soTien, LoaiTinhPhi loaiTinh, Boolean dangApDung) {
        PhiDichVu phi = findById(id);
        phi.setTenPhi(tenPhi);
        phi.setSoTien(soTien);
        if (loaiTinh != null) {
            phi.setLoaiTinh(loaiTinh);
        }
        phi.setDangApDung(dangApDung);
        return phiDichVuRepository.save(phi);
    }

    public PhiDichVu toggleTrangThai(Integer id) {
        PhiDichVu phi = findById(id);
        phi.setDangApDung(!Boolean.TRUE.equals(phi.getDangApDung()));
        return phiDichVuRepository.save(phi);
    }

    @Override
    public void deleteById(Integer id) {
        phiDichVuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phí dịch vụ", id));
        phiDichVuRepository.deleteById(id);
    }
}