package vn.id.devblog.blog_server.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.id.devblog.blog_server.dto.response.user.AdminUserResponse;
import vn.id.devblog.blog_server.models.Role;
import vn.id.devblog.blog_server.models.User;
import vn.id.devblog.blog_server.repositories.RoleRepository;
import vn.id.devblog.blog_server.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public Page<AdminUserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAll(pageable);

        return users.map(user -> new AdminUserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName(),
                user.getRole() != null ? user.getRole().getName() : "N/A",
                user.isActive(),
                user.isVerified(),
                user.getCreatedAt(),
                user.getAvatar()
        ));
    }

    @Transactional
    public boolean toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return false;

        user.setActive(!user.isActive());
        userRepository.save(user);
        return true;
    }

    @Transactional
    public boolean changeUserRole(Long userId, String roleName) {
        User user = userRepository.findById(userId).orElse(null);
        Role role = roleRepository.findByName(roleName.toUpperCase());

        if (user == null || role == null) return false;

        user.setRole(role);
        userRepository.save(user);
        return true;
    }
}