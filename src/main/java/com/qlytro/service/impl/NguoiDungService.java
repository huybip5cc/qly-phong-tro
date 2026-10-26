package com.qlytro.service.impl;

import com.qlytro.dto.request.NguoiDungRequest;
import com.qlytro.dto.response.NguoiDungResponse;
import com.qlytro.entity.NguoiDung;
import com.qlytro.entity.enums.TrangThaiNguoiDung;
import com.qlytro.entity.enums.VaiTroNguoiDung;
import com.qlytro.exception.BusinessException;
import com.qlytro.exception.ResourceNotFoundException;
import com.qlytro.repository.NguoiDungRepository;
import com.qlytro.security.CustomUserDetails;
import com.qlytro.service.base.BaseService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Transactional
public class NguoiDungService implements BaseService<NguoiDungResponse, Integer> {

    private static final String MAT_KHAU_KHOI_TAO_KHACH = "123";

    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionRegistry sessionRegistry;

    public NguoiDungService(NguoiDungRepository nguoiDungRepository,
                             PasswordEncoder passwordEncoder,
                             SessionRegistry sessionRegistry) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    @Transactional(readOnly = true)
    public NguoiDungResponse findById(Integer id) {
        return toResponse(nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NguoiDungResponse> findAll() {
        return nguoiDungRepository.findAll().stream().map(this::toResponse).toList();
    }

    public NguoiDungResponse create(NguoiDungRequest request) {
        if (nguoiDungRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username '" + request.getUsername() + "' đã tồn tại.");
        }
        if (Integer.valueOf(VaiTroNguoiDung.KHACH_THUE.getValue()).equals(request.getVaiTro())
                && (request.getSdt() == null || !request.getSdt().matches("[0-9]{10}"))) {
            throw new BusinessException("Số điện thoại khách thuê phải gồm đúng 10 chữ số.");
        }
        NguoiDung nd = new NguoiDung();
        mapFromRequest(request, nd, true);
        nd.setMustChangePassword(nd.getVaiTro() == VaiTroNguoiDung.KHACH_THUE);
        return toResponse(nguoiDungRepository.save(nd));
    }

    public void changePassword(Integer id, String currentPassword, String newPassword) {
        NguoiDung nd = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", id));
        if (!passwordEncoder.matches(currentPassword, nd.getPassword())) {
            throw new BusinessException("Mật khẩu hiện tại không chính xác.");
        }
        if (newPassword.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new BusinessException("Mật khẩu mới không được vượt quá 72 byte UTF-8.");
        }
        if (passwordEncoder.matches(newPassword, nd.getPassword())) {
            throw new BusinessException("Mật khẩu mới phải khác mật khẩu hiện tại.");
        }

        nd.setPassword(passwordEncoder.encode(newPassword));
        nd.setMustChangePassword(false);
        nguoiDungRepository.save(nd);
    }

    public void resetTenantPassword(Integer id) {
        NguoiDung nd = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", id));
        if (nd.getVaiTro() != VaiTroNguoiDung.KHACH_THUE) {
            throw new BusinessException("Chỉ có thể đặt lại mật khẩu cho tài khoản khách thuê.");
        }

        nd.setPassword(passwordEncoder.encode(MAT_KHAU_KHOI_TAO_KHACH));
        nd.setMustChangePassword(true);
        nguoiDungRepository.save(nd);

        sessionRegistry.getAllPrincipals().stream()
                .filter(CustomUserDetails.class::isInstance)
                .map(CustomUserDetails.class::cast)
                .filter(user -> user.getId().equals(id))
                .flatMap(user -> sessionRegistry.getAllSessions(user, false).stream())
                .forEach(SessionInformation::expireNow);
    }

    public NguoiDungResponse update(Integer id, NguoiDungRequest request) {
        NguoiDung nd = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", id));
        mapFromRequest(request, nd, !request.getPassword().isBlank());
        return toResponse(nguoiDungRepository.save(nd));
    }

    public void khoaTaiKhoan(Integer id) {
        NguoiDung nd = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", id));
        nd.setTrangThai(TrangThaiNguoiDung.KHOA);
        nguoiDungRepository.save(nd);
    }

    public void moKhoa(Integer id) {
        NguoiDung nd = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", id));
        nd.setTrangThai(TrangThaiNguoiDung.ACTIVE);
        nguoiDungRepository.save(nd);
    }

    @Override
    public void deleteById(Integer id) {
        nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", id));
        nguoiDungRepository.deleteById(id);
    }

    private void mapFromRequest(NguoiDungRequest request, NguoiDung nd, boolean encodePassword) {
        nd.setUsername(request.getUsername());
        if (encodePassword && request.getPassword() != null && !request.getPassword().isBlank()) {
            // Service dam bao mat khau luon duoc ma hoa BCrypt, KHONG luu plain text
            nd.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        nd.setHoTen(request.getHoTen());
        nd.setSdt(request.getSdt());
        nd.setEmail(request.getEmail());
        nd.setVaiTro(VaiTroNguoiDung.fromValue(request.getVaiTro()));
        if (nd.getTrangThai() == null) {
            nd.setTrangThai(TrangThaiNguoiDung.ACTIVE);
        }
    }

    private NguoiDungResponse toResponse(NguoiDung entity) {
        NguoiDungResponse r = new NguoiDungResponse();
        r.setId(entity.getId());
        r.setUsername(entity.getUsername());
        r.setHoTen(entity.getHoTen());
        r.setSdt(entity.getSdt());
        r.setEmail(entity.getEmail());
        r.setVaiTro(entity.getVaiTro());
        r.setVaiTroLabel(entity.getVaiTro().getLabel());
        r.setTrangThai(entity.getTrangThai());
        r.setTrangThaiLabel(entity.getTrangThai().getLabel());
        r.setNgayTao(entity.getNgayTao());
        return r;
    }
}
