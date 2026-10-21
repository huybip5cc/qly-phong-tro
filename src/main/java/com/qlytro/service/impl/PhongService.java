package com.qlytro.service.impl;

import com.qlytro.dto.request.PhongRequest;
import com.qlytro.dto.response.PhongResponse;
import com.qlytro.entity.Phong;
import com.qlytro.entity.ToaNha;
import com.qlytro.entity.enums.TrangThaiPhong;
import com.qlytro.exception.BusinessException;
import com.qlytro.exception.ResourceNotFoundException;
import com.qlytro.repository.HopDongRepository;
import com.qlytro.repository.PhongRepository;
import com.qlytro.repository.PhongThietBiRepository;
import com.qlytro.repository.ToaNhaRepository;
import com.qlytro.service.base.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class PhongService implements BaseService<PhongResponse, Integer> {

    private final PhongRepository phongRepository;
    private final ToaNhaRepository toaNhaRepository;
    private final HopDongRepository hopDongRepository;
    private final PhongThietBiRepository phongThietBiRepository;

    public PhongService(PhongRepository phongRepository,
                        ToaNhaRepository toaNhaRepository,
                        HopDongRepository hopDongRepository,
                        PhongThietBiRepository phongThietBiRepository) {
        this.phongRepository = phongRepository;
        this.toaNhaRepository = toaNhaRepository;
        this.hopDongRepository = hopDongRepository;
        this.phongThietBiRepository = phongThietBiRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PhongResponse findById(Integer id) {
        return toResponse(phongRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phòng", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhongResponse> findAll() {
        return phongRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PhongResponse> findByToaNha(Integer toaNhaId) {
        return phongRepository.findByToaNha_Id(toaNhaId).stream().map(this::toResponse).toList();
    }

    public PhongResponse create(PhongRequest request) {
        // Validate: so phong phai duy nhat trong toa nha
        if (phongRepository.existsByToaNha_IdAndSoPhong(request.getToaNhaId(), request.getSoPhong())) {
            throw new BusinessException("Số phòng '" + request.getSoPhong()
                    + "' đã tồn tại trong tòa nhà này.");
        }
        ToaNha toaNha = toaNhaRepository.findById(request.getToaNhaId())
                .orElseThrow(() -> new ResourceNotFoundException("Tòa nhà", request.getToaNhaId()));
        Phong phong = new Phong();
        mapFromRequestForCreate(request, phong, toaNha);
        return toResponse(phongRepository.save(phong));
    }

    public PhongResponse update(Integer id, PhongRequest request) {
        Phong phong = phongRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phòng", id));
        // Validate: so phong phai duy nhat (ngoai tru ban than phong nay)
        if (phongRepository.existsByToaNha_IdAndSoPhongAndIdNot(request.getToaNhaId(), request.getSoPhong(), id)) {
            throw new BusinessException("Số phòng '" + request.getSoPhong()
                    + "' đã tồn tại trong tòa nhà này.");
        }
        ToaNha toaNha = toaNhaRepository.findById(request.getToaNhaId())
                .orElseThrow(() -> new ResourceNotFoundException("Tòa nhà", request.getToaNhaId()));
        mapFromRequestForUpdate(request, phong, toaNha);
        return toResponse(phongRepository.save(phong));
    }

    @Override
    public void deleteById(Integer id) {
        Phong phong = phongRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phòng", id));
        // Rang buoc: khong xoa phong dang co hop dong ACTIVE
        if (hopDongRepository.existsByPhong_IdAndTrangThai(id,
                com.qlytro.entity.enums.TrangThaiHopDong.ACTIVE)) {
            throw new BusinessException("Không thể xóa phòng đang có hợp đồng hiệu lực.");
        }
        phongRepository.delete(phong);
    }

    /**
     * Dung cho CREATE: map tat ca cac truong tu request, bao gom trang_thai.
     * Phong moi co the bat dau o TRONG hoac NGUNG_SU_DUNG.
     */
    private void mapFromRequestForCreate(PhongRequest request, Phong phong, ToaNha toaNha) {
        phong.setToaNha(toaNha);
        phong.setSoPhong(request.getSoPhong());
        phong.setDienTich(request.getDienTich());
        phong.setGiaThueTheoMacDinh(request.getGiaThueTheoMacDinh());
        phong.setTrangThai(request.getTrangThai() != null
                ? TrangThaiPhong.fromValue(request.getTrangThai())
                : TrangThaiPhong.TRONG);
        phong.setMoTa(request.getMoTa());
    }

    /**
     * Dung cho UPDATE: KHONG bao gio ghi de trang_thai tu request.
     * Trang thai phong chi duoc thay doi boi nghiep vu hop dong
     * (HopDongService.create() -> DANG_THUE, HopDongService.ketThuc() -> TRONG).
     * Giu nguyen gia tri trang_thai hien tai cua phong trong DB.
     */
    private void mapFromRequestForUpdate(PhongRequest request, Phong phong, ToaNha toaNha) {
        phong.setToaNha(toaNha);
        phong.setSoPhong(request.getSoPhong());
        phong.setDienTich(request.getDienTich());
        phong.setGiaThueTheoMacDinh(request.getGiaThueTheoMacDinh());
        // trangThai: KHONG set - giu nguyen gia tri hien tai
        phong.setMoTa(request.getMoTa());
    }

    private PhongResponse toResponse(Phong entity) {
        PhongResponse r = new PhongResponse();
        r.setId(entity.getId());
        r.setToaNhaId(entity.getToaNha().getId());
        r.setTenToaNha(entity.getToaNha().getTenToaNha());
        r.setSoPhong(entity.getSoPhong());
        r.setDienTich(entity.getDienTich());
        r.setGiaThueTheoMacDinh(entity.getGiaThueTheoMacDinh());
        r.setTrangThai(entity.getTrangThai());
        r.setTrangThaiLabel(entity.getTrangThai().getLabel());
        r.setMoTa(entity.getMoTa());
        // Load danh sach thiet bi
        r.setDanhSachThietBi(
            phongThietBiRepository.findByPhong_Id(entity.getId())
                .stream()
                .map(ptb -> ptb.getThietBi())
                .toList()
        );
        return r;
    }
}
