package vn.id.devblog.blog_server.dto.request.verify;

public record VerifyRequest(
        String email,
        String code,
        String newPassword
) {
}
