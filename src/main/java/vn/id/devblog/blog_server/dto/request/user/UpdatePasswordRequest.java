package vn.id.devblog.blog_server.dto.request.user;

public record UpdatePasswordRequest(Long userId, String oldPassword, String newPassword) {
}
