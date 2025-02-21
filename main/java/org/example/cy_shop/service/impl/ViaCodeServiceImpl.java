package org.example.cy_shop.service.impl;

import org.example.cy_shop.entity.ViaCode;
import org.example.cy_shop.repository.IViaCodeRepository;
import org.example.cy_shop.service.IViaCodeService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class ViaCodeServiceImpl implements IViaCodeService {
    private IViaCodeRepository _viaCodeRepository;
    public ViaCodeServiceImpl(IViaCodeRepository viaCodeRepository){
        _viaCodeRepository = viaCodeRepository;
    }
    @Override
    public boolean validateCode(String code, String email) {
        ViaCode viaCode = _viaCodeRepository.findByEmail(email);
        if (viaCode != null) {
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(viaCode.getCreatedAt(), now);
            if (duration.toMinutes() > 3) {
                return false;
            }
            String codeCheck = viaCode.getViaCode().toString();
            return code.equals(codeCheck);
        }
        return false;
    }
}
