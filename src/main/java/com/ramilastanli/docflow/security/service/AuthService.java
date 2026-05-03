package com.ramilastanli.docflow.security.service;

import com.ramilastanli.docflow.entity.User;
import com.ramilastanli.docflow.entity.Role;
import com.ramilastanli.docflow.repository.UserRepository;
import com.ramilastanli.docflow.security.user.CustomUserDetails;
import com.ramilastanli.docflow.security.dto.request.LoginRequestDto;
import com.ramilastanli.docflow.security.dto.response.LoginResponseDto;
import com.ramilastanli.docflow.security.dto.request.RegisterRequestDto;
import com.ramilastanli.docflow.security.dto.response.UserDto;
import com.ramilastanli.docflow.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication resultAuthentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.email(),
                        loginRequestDto.password())
        );

        CustomUserDetails userDetails = (CustomUserDetails) resultAuthentication.getPrincipal();
        String jwtToken = jwtUtil.generateJwtToken(resultAuthentication);

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı"));

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        userDto.setUserId(user.getId());

        // Enum-dan String adını alırıq (UserDto-da role String-dirsə)
        userDto.setRole(user.getRole().name());

        return new LoginResponseDto("Giriş uğurludur", userDto, jwtToken);
    }

    @Transactional
    public void register(RegisterRequestDto dto) {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("Bu email artıq qeydiyyatdan keçib!");
        }

        User user = new User();
        // BeanUtils id-ni və rolu kopyalamamaq üçün bəzən diqqət tələb edir
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));

        // Enum olduğu üçün birbaşa Enum sabitini veririk
        user.setRole(Role.USER);

        userRepository.save(user);
    }

    @Transactional
    public void changeUserRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı!"));

        user.setRole(newRole);
        userRepository.save(user); // Transactional olduğu üçün əslində save-ə ehtiyac yoxdur, amma vizual olaraq qalsın
    }
}