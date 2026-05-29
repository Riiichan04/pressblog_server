package vn.id.devblog.blog_server.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.id.devblog.blog_server.dto.request.auth.LoginRequest;
import vn.id.devblog.blog_server.dto.request.auth.RegisterRequest;
import vn.id.devblog.blog_server.dto.response.auth.AuthDto;
import vn.id.devblog.blog_server.dto.response.auth.AuthResponse;
import vn.id.devblog.blog_server.models.Permission;
import vn.id.devblog.blog_server.models.Role;
import vn.id.devblog.blog_server.models.User;
import vn.id.devblog.blog_server.repositories.RoleRepository;
import vn.id.devblog.blog_server.repositories.UserRepository;
import vn.id.devblog.blog_server.security.JwtConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtConfig jwtConfig;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest input) {
        User targetUser = userRepository.findByEmail(input.email());
        if (targetUser == null) {
            return new AuthResponse(false, "Wrong password or email", null);
        }

        boolean isPasswordMatch = passwordEncoder.matches(input.password(), targetUser.getPassword());
        if (!isPasswordMatch) {
            return new AuthResponse(false, "Wrong password or email", null);
        }

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", targetUser.getId());
        if (targetUser.getRole() != null) {
            extraClaims.put("userRole", targetUser.getRole().getName());
        }

        String jwtToken = jwtConfig.generateToken(extraClaims, targetUser);

        AuthDto dto = getAuthDto(targetUser, jwtToken);
        return new AuthResponse(true, "Login success!", dto);
    }

    @Transactional
    public AuthResponse register(RegisterRequest input) {
        User existEmail = this.userRepository.findByEmail(input.email());
        if (existEmail != null) {
            return new AuthResponse(false, "email_exists", null);
        }

        String normalizedUsername = input.username().toLowerCase();
        User existUsername = this.userRepository.findByUsername(normalizedUsername);
        if (existUsername != null) {
            return new AuthResponse(false, "username_exists", null);
        }

        User newUser = new User();
        newUser.setEmail(input.email());
        newUser.setUsername(normalizedUsername);
        newUser.setPassword(passwordEncoder.encode(input.password()));

        Role defaultRole = roleRepository.findByName("USER");
        if (defaultRole != null) {
            newUser.setRole(defaultRole);
        } else {
            log.warn("USER role not found");
        }

        this.userRepository.save(newUser);

        return new AuthResponse(true, "register_success", null);
    }

    private static AuthDto getAuthDto(User targetUser, String jwtToken) {
        AuthDto dto = new AuthDto();
        dto.setId(targetUser.getId());
        dto.setActive(targetUser.isActive());
        dto.setAvatar(targetUser.getAvatar());
        dto.setDescription(targetUser.getDescription());
        dto.setDisplayName(targetUser.getDisplayName());
        dto.setEmail(targetUser.getEmail());
        dto.setGender(targetUser.getGender());
        dto.setUsername(targetUser.getUsername());
        dto.setVerified(targetUser.isVerified());
        dto.setJwtToken(jwtToken);

        //Handle role and permission
        if (targetUser.getRole() != null) {
            dto.setRole(targetUser.getRole().getName());
            if (targetUser.getRole().getPermissions() != null) {
                List<String> permissionNames = targetUser.getRole().getPermissions()
                        .stream()
                        .map(Permission::getName)
                        .toList();
                dto.setPermissions(permissionNames);
            }
        }

        return dto;
    }
}