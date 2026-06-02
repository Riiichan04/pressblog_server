package vn.id.devblog.blog_server.dto.response.user;

import java.time.LocalDateTime;
import java.util.List;

public record AdminDashboardResponse(
        long totalUsers,
        long totalPosts,
        long pendingPosts,
        long totalViews,
        List<PendingPostDto> recentPendingPosts,
        List<ChartStatDto> chartData
) {
    public record PendingPostDto(
            Long id,
            String title,
            String author,
            LocalDateTime createdAt
    ) {
    }

    public record ChartStatDto(
            String date,
            long count
    ) {
    }
}