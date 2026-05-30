package vn.id.devblog.blog_server.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.id.devblog.blog_server.dto.response.statistic.DashboardStatResponse;
import vn.id.devblog.blog_server.dto.response.statistic.DashboardStatResponse.*;
import vn.id.devblog.blog_server.models.User;
import vn.id.devblog.blog_server.repositories.PostRepository;
import vn.id.devblog.blog_server.services.UserDashboardService;

import java.util.List;
@RestController
@RequestMapping("/user/dashboard")
@Slf4j
@RequiredArgsConstructor
public class UserDashboardController {
    private final PostRepository postRepository;
    private final UserDashboardService userDashboardService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatResponse> getDashboardStats(
            @AuthenticationPrincipal User currentUser   // Get current user from Spring Security
    ) {
        try {
            long extractedUserId = currentUser.getId();
            long posts = postRepository.countByAuthorId(extractedUserId);
            long views = postRepository.sumViewsByAuthorId(extractedUserId);
            long comments = 0;
            List<DailyViewStat> weekTrending = userDashboardService.getWeeklyTrends(extractedUserId);
            List<TrendingPostDto> trendingPost = userDashboardService.getTopTrendingPosts(extractedUserId);

            return ResponseEntity.ok(new DashboardStatResponse(posts, views, comments, weekTrending, trendingPost));
        } catch (Exception e) {
            log.error("Fetch dashboard stat error: ", e);
            return ResponseEntity.status(500).build();
        }
    }
}
