package vn.id.devblog.blog_server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.id.devblog.blog_server.dto.request.user.UpdateProfileRequest;
import vn.id.devblog.blog_server.dto.response.user.UserProfileResponse;
import vn.id.devblog.blog_server.models.User;
import vn.id.devblog.blog_server.repositories.UserRepository;

@Service
public class UserService {
    UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return null;
        return mapToResponse(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return null;

        if (request.displayName() != null) {
            user.setDisplayName(request.displayName());
        }
        if (request.bio() != null) {
            user.setDescription(request.bio());
        }
        if (request.avatar() != null) {
            user.setAvatar(request.avatar());
        }

        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    private UserProfileResponse mapToResponse(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName(),
                user.getDescription(),
                user.getAvatar(),
                user.getCreatedAt()
        );
    }
}
