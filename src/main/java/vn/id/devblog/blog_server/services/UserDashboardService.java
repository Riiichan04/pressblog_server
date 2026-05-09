package vn.id.devblog.blog_server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import vn.id.devblog.blog_server.models.Post;
import vn.id.devblog.blog_server.repositories.PostRepository;

import vn.id.devblog.blog_server.dto.response.statistic.DashboardStatResponse.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDashboardService {
    PostRepository postRepository;
    PostViewService postViewService;
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public UserDashboardService(RedisTemplate<String, String> redisTemplate, PostViewService postViewService, PostRepository postRepository) {
        this.redisTemplate = redisTemplate;
        this.postViewService = postViewService;
        this.postRepository = postRepository;
    }

    public List<DailyViewStat> getWeeklyTrends(Long userId) {
        List<DailyViewStat> stats = new ArrayList<>();
        LocalDateTime today = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // Get last 1 week
        for (int i = 6; i >= 0; i--) {
            LocalDateTime date = today.minusDays(i);
            String dateStr = date.format(formatter); // format: yyyy-MM-dd
            String dailyKey = "user:" + userId + ":views:" + dateStr;
            String views = redisTemplate.opsForValue().get(dailyKey);
            long count = (views != null) ? Long.parseLong(views) : 0L;
            stats.add(new DailyViewStat(count, date));
        }
        return stats;
    }

    public List<TrendingPostDto> getTopTrendingPosts(Long userId) {
        List<Post> trendingPosts = postRepository.findTop5ByViewCountOrderByIdDesc(userId);
        return trendingPosts.stream().map(post ->
                new TrendingPostDto(
                        post.getId(),
                        post.getName(),
                        post.getStatus(),
                        postViewService.getViewCount(post.getSlug()),
                        0L, //FIXME: Add actual comment count
                        post.getCreatedAt()
                )
        ).collect(Collectors.toList());
    }
}
