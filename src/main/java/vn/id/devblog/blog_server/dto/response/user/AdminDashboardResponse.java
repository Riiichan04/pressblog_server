package vn.id.devblog.blog_server.dto.response.user;

public record AdminDashboardResponse(
        long totalUsers,
        long totalPosts,
        long pendingPosts,
        long totalViews
) {
}