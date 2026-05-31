package vn.id.devblog.blog_server.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.id.devblog.blog_server.common.enums.PostStatus;
import vn.id.devblog.blog_server.dto.response.user.AdminDashboardResponse;
import vn.id.devblog.blog_server.repositories.PostRepository;
import vn.id.devblog.blog_server.repositories.UserRepository;

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

        return new AdminDashboardResponse(totalUsers, totalPosts, pendingPosts, totalViews);
    }
}