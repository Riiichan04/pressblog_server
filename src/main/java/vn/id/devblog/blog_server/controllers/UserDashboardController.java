package vn.id.devblog.blog_server.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.id.devblog.blog_server.dto.response.statistic.DashboardStatResponse;
import vn.id.devblog.blog_server.dto.response.statistic.DashboardStatResponse.*;
import vn.id.devblog.blog_server.repositories.PostRepository;
import vn.id.devblog.blog_server.services.PostViewService;
import vn.id.devblog.blog_server.services.UserDashboardService;

import java.util.List;

@RequestMapping("/user/dashboard")
@Slf4j
public class UserDashboardController {
    PostRepository postRepository;
    UserDashboardService userDashboardService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatResponse> getDashboardStats(@RequestAttribute String userId) {
        try {
            long extractedUserId = Long.parseLong(userId);
            long posts = postRepository.countByAuthorId(extractedUserId);
            long views = postRepository.sumViewsByAuthorId(extractedUserId);
            long comments = 0; //TODO: Add count comment
            List<DailyViewStat> weekTrending = userDashboardService.getWeeklyTrends(extractedUserId);
            List<TrendingPostDto> trendingPost = userDashboardService.getTopTrendingPosts(extractedUserId);

            return ResponseEntity.ok(new DashboardStatResponse(posts, views, 0, weekTrending, trendingPost));
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(500).build();
        }

    }
}
