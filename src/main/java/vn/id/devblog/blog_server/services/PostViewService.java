package vn.id.devblog.blog_server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vn.id.devblog.blog_server.common.enums.PostStatus;
import vn.id.devblog.blog_server.models.Post;
import vn.id.devblog.blog_server.repositories.PostRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

@Service
public class PostViewService {
    private static final String VIEW_KEY_PREFIX = "post:views:";
    private static final String LOCK_KEY_PREFIX = "view_lock:";

    private final PostRepository postRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public PostViewService(PostRepository postRepository, RedisTemplate<String, String> redisTemplate) {
        this.postRepository = postRepository;
        this.redisTemplate = redisTemplate;
    }

    public void incrementView(String slug, String userIp) {
        Post post = postRepository.findValidPublicPostBySlug(slug, PostStatus.PUBLISHED).orElse(null);
        if (post == null) return;

        String viewKey = VIEW_KEY_PREFIX + slug;
        String lockKey = LOCK_KEY_PREFIX + slug + ":" + userIp;

        //Load current view if the post don't have current view
        if (Boolean.FALSE.equals(redisTemplate.hasKey(viewKey))) {
            long viewsInDb = postRepository.findViewCountBySlug(slug);
            redisTemplate.opsForValue().setIfAbsent(viewKey, String.valueOf(viewsInDb));
        }

        Boolean isFirstView = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", Duration.ofHours(1));

        if (Boolean.TRUE.equals(isFirstView)) {
            redisTemplate.opsForValue().increment(VIEW_KEY_PREFIX + slug);

            //Add view to calculate trending
            Long authorId = post.getAuthor().getId();
            String today = LocalDateTime.now().toString();
            String userDailyKey = "user:" + authorId + ":views:" + today;
            redisTemplate.opsForValue().increment(userDailyKey);
        }
    }

    public long getViewCount(String slug) {
        String viewKey = VIEW_KEY_PREFIX + slug;
        String val = redisTemplate.opsForValue().get(viewKey);

        if (val != null) {
            return Integer.parseInt(val);
        }
        //If redis dont have data, get from db and update to redis
        long finalViews = postRepository.findViewCountBySlug(slug);
        redisTemplate.opsForValue().setIfAbsent(viewKey, String.valueOf(finalViews));

        return finalViews;
    }

    @Scheduled(fixedRate = 1800000)
    public void syncViewsToDb() {
        // Get all key
        Set<String> keys = redisTemplate.keys(VIEW_KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            String slug = key.replace(VIEW_KEY_PREFIX, "");
            String viewsStr = redisTemplate.opsForValue().get(key);

            if (viewsStr != null) {
                long views = Long.parseLong(viewsStr);
                //Update query
                postRepository.updateViewCount(slug, views);
            }
        }
    }
}
