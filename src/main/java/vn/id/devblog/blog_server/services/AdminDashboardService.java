package vn.id.devblog.blog_server.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.id.devblog.blog_server.common.enums.PostStatus;
import vn.id.devblog.blog_server.dto.response.user.AdminDashboardResponse;
import vn.id.devblog.blog_server.dto.response.user.AdminDashboardResponse.*;
import vn.id.devblog.blog_server.models.Post;
import vn.id.devblog.blog_server.repositories.PostRepository;
import vn.id.devblog.blog_server.repositories.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public AdminDashboardResponse getOverviewStats() {
        long totalUsers = userRepository.count();
        long totalPosts = postRepository.countByIsDeletedFalse();
        long pendingPosts = postRepository.countByIsDeletedFalseAndStatus(PostStatus.PENDING);
        long totalViews = postRepository.sumAllViews();

        List<Post> recentPending = postRepository.findTop5ByIsDeletedFalseAndStatusOrderByCreatedAtDesc(PostStatus.PENDING);
        List<PendingPostDto> recentPendingDtos = recentPending.stream()
                .map(post -> new PendingPostDto(
                        post.getId(),
                        post.getName(),
                        post.getAuthor() != null ? post.getAuthor().getUsername() : "Unknown",
                        post.getCreatedAt()
                ))
                .collect(Collectors.toList());

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(6).withHour(0).withMinute(0).withSecond(0);
        List<Post> recentPostsForChart = postRepository.findByCreatedAtAfterAndIsDeletedFalse(sevenDaysAgo);

        Map<LocalDate, Long> postCountsByDate = recentPostsForChart.stream()
                .collect(Collectors.groupingBy(
                        post -> post.getCreatedAt().toLocalDate(),
                        Collectors.counting()
                ));

        List<ChartStatDto> chartData = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            long count = postCountsByDate.getOrDefault(date, 0L);
            chartData.add(new ChartStatDto(date.format(formatter), count));
        }

        return new AdminDashboardResponse(
                totalUsers, totalPosts, pendingPosts, totalViews, recentPendingDtos, chartData
        );
    }
}