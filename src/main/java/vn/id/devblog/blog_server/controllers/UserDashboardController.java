package vn.id.devblog.blog_server.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.id.devblog.blog_server.dto.response.statistic.DashboardStatResponse;
import vn.id.devblog.blog_server.dto.response.statistic.DashboardStatResponse.*;
import vn.id.devblog.blog_server.repositories.PostRepository;
import vn.id.devblog.blog_server.services.UserDashboardService;

import java.util.List;
@RestController
@RequestMapping("/user/dashboard")
@Slf4j
public class UserDashboardController {
    PostRepository postRepository;
    UserDashboardService userDashboardService;

    @Autowired
    public UserDashboardController(PostRepository postRepository, UserDashboardService userDashboardService) {
        this.postRepository = postRepository;
        this.userDashboardService = userDashboardService;
    }

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
