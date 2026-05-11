package vn.id.devblog.blog_server.dto.response.statistic;

import vn.id.devblog.blog_server.common.enums.PostStatus;

import java.time.LocalDateTime;
import java.util.List;

public record DashboardStatResponse(
        long totalPosts,
        long totalViews,
        long totalComments,
        List<DailyViewStat> viewTrends,
        List<TrendingPostDto> trendingPosts
) {
    public record DailyViewStat(long view, LocalDateTime date) {
        @Override
        public String toString() {
            return "DailyViewStat{" +
                    "view=" + view +
                    ", date=" + date +
                    '}';
        }
    }
    public record TrendingPostDto(
            Long id,
            String name,
            PostStatus status,
            long views,
            long comments,
            LocalDateTime createdAt
    ) {
        @Override
        public String toString() {
            return "TrendingPostDto{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", status=" + status +
                    ", views=" + views +
                    ", comments=" + comments +
                    ", createdAt=" + createdAt +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "DashboardStatResponse{" +
                "totalPosts=" + totalPosts +
                ", totalViews=" + totalViews +
                ", totalComments=" + totalComments +
                ", viewTrends=" + viewTrends +
                ", trendingPosts=" + trendingPosts +
                '}';
    }
}
