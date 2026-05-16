package com.omkarsathe.outvoice.auth;

import com.omkarsathe.outvoice.auth.dto.AuthResponse;
import com.omkarsathe.outvoice.auth.dto.SignupRequest;
import com.omkarsathe.outvoice.organization.Organization;
import com.omkarsathe.outvoice.organization.OrganizationRepository;
import com.omkarsathe.outvoice.security.JwtService;
import com.omkarsathe.outvoice.user.User;
import com.omkarsathe.outvoice.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.allow-duplicate-signup:false}")
    private boolean allowDuplicateSignup;

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        if (allowDuplicateSignup) {
            String emailParam = StringUtils.hasText(request.getEmail()) ? request.getEmail() : "";
            String mobileParam = StringUtils.hasText(request.getMobileNumber()) ? request.getMobileNumber() : "";
            var existing = userRepository.findByEmailOrMobileNumber(emailParam, mobileParam);
            if (existing.isPresent()) {
                return new AuthResponse(jwtService.generateToken(existing.get().getPrincipal()));
            }
        }

        String taxComplianceName = StringUtils.hasText(request.getTaxComplianceName())
                ? request.getTaxComplianceName()
                : request.getFullName();

        Organization org = organizationRepository.save(Organization.builder()
                .name(request.getOrganizationName())
                .taxComplianceName(taxComplianceName)
                .panNumber(request.getPanNumber())
                .gstNumber(request.getGstNumber())
                .tanNumber(request.getTanNumber())
                .build());

        User user = userRepository.save(User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .mobileNumber(request.getMobileNumber())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .organization(org)
                .build());

        return new AuthResponse(jwtService.generateToken(user.getPrincipal()));
    }
}
