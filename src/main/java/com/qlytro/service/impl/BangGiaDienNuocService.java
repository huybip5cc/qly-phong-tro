package com.qlytro.service.impl;

import com.qlytro.entity.BangGiaDienNuoc;
import com.qlytro.exception.BusinessException;
import com.qlytro.exception.ResourceNotFoundException;
import com.qlytro.repository.BangGiaDienNuocRepository;
import com.qlytro.service.base.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class BangGiaDienNuocService implements BaseService<BangGiaDienNuoc, Integer> {

    private final BangGiaDienNuocRepository bangGiaRepository;

    public BangGiaDienNuocService(BangGiaDienNuocRepository bangGiaRepository) {
        this.bangGiaRepository = bangGiaRepository;
    }

    /** Luon chi co 1 dong gia — lay dong duy nhat. */
    @Transactional(readOnly = true)
    public BangGiaDienNuoc layBangGia() {
        return bangGiaRepository.findTopByOrderByIdAsc()
                .orElseThrow(() -> new BusinessException("Chưa có bảng giá điện nước. Vui lòng thiết lập."));
    }

    @Override
    @Transactional(readOnly = true)
    public BangGiaDienNuoc findById(Integer id) {
        return bangGiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bảng giá", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BangGiaDienNuoc> findAll() {
        return bangGiaRepository.findAll();
    }

    /** Cập nhật đơn giá điện và nước. */
    public BangGiaDienNuoc capNhat(BigDecimal donGiaDien, BigDecimal donGiaNuoc) {
        if (donGiaDien.compareTo(BigDecimal.ZERO) <= 0 || donGiaNuoc.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Đơn giá phải lớn hơn 0.");
        }
        BangGiaDienNuoc bangGia = bangGiaRepository.findTopByOrderByIdAsc()
                .orElseGet(BangGiaDienNuoc::new); // Tao moi neu chua co
        bangGia.setDonGiaDien(donGiaDien);
        bangGia.setDonGiaNuoc(donGiaNuoc);
        return bangGiaRepository.save(bangGia);
    }

    @Override
    public void deleteById(Integer id) {
        throw new BusinessException("Không thể xóa bảng giá. Hãy cập nhật giá thay thế.");
    }
}
