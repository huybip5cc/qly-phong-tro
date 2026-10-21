package com.qlytro.service.impl;

import com.qlytro.dto.request.ToaNhaRequest;
import com.qlytro.dto.response.ToaNhaResponse;
import com.qlytro.entity.ToaNha;
import com.qlytro.entity.enums.TrangThaiToaNha;
import com.qlytro.exception.BusinessException;
import com.qlytro.exception.ResourceNotFoundException;
import com.qlytro.repository.PhongRepository;
import com.qlytro.repository.ToaNhaRepository;
import com.qlytro.service.base.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class ToaNhaService implements BaseService<ToaNhaResponse, Integer> {

    private final ToaNhaRepository toaNhaRepository;
    private final PhongRepository phongRepository;

    public ToaNhaService(ToaNhaRepository toaNhaRepository, PhongRepository phongRepository) {
        this.toaNhaRepository = toaNhaRepository;
        this.phongRepository = phongRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ToaNhaResponse findById(Integer id) {
        ToaNha toaNha = toaNhaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tòa nhà", id));
        return toResponse(toaNha);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ToaNhaResponse> findAll() {
        return toaNhaRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ToaNhaResponse create(ToaNhaRequest request) {
        ToaNha toaNha = new ToaNha();
        mapFromRequest(request, toaNha);
        return toResponse(toaNhaRepository.save(toaNha));
    }

    public ToaNhaResponse update(Integer id, ToaNhaRequest request) {
        ToaNha toaNha = toaNhaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tòa nhà", id));
        mapFromRequest(request, toaNha);
        return toResponse(toaNhaRepository.save(toaNha));
    }

    @Override
    public void deleteById(Integer id) {
        ToaNha toaNha = toaNhaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tòa nhà", id));
        // Rang buoc: khong xoa toa nha con phong
        long soPhong = phongRepository.countByToaNhaId(id);
        if (soPhong > 0) {
            throw new BusinessException("Không thể xóa tòa nhà còn " + soPhong + " phòng. Hãy xóa phòng trước.");
        }
        toaNhaRepository.delete(toaNha);
    }

    private void mapFromRequest(ToaNhaRequest request, ToaNha toaNha) {
        toaNha.setTenToaNha(request.getTenToaNha());
        toaNha.setDiaChi(request.getDiaChi());
        toaNha.setTrangThai(request.getTrangThai() != null
                ? TrangThaiToaNha.fromValue(request.getTrangThai())
                : TrangThaiToaNha.HOAT_DONG);
    }

    private ToaNhaResponse toResponse(ToaNha entity) {
        ToaNhaResponse r = new ToaNhaResponse();
        r.setId(entity.getId());
        r.setTenToaNha(entity.getTenToaNha());
        r.setDiaChi(entity.getDiaChi());
        r.setTrangThai(entity.getTrangThai());
        r.setTrangThaiLabel(entity.getTrangThai().getLabel());
        r.setNgayTao(entity.getNgayTao());
        r.setSoPhong(phongRepository.countByToaNhaId(entity.getId()));
        return r;
    }
}
