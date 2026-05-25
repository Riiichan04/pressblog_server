package vn.id.devblog.blog_server.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.id.devblog.blog_server.common.enums.PostStatus;
import vn.id.devblog.blog_server.dto.response.post.PublicPostResponse;
import vn.id.devblog.blog_server.dto.response.user.PublicUserProfileResponse;
import vn.id.devblog.blog_server.models.Post;
import vn.id.devblog.blog_server.models.User;
import vn.id.devblog.blog_server.repositories.PostRepository;
import vn.id.devblog.blog_server.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class UserPublicService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostViewService postViewService;

    public PublicUserProfileResponse getPublicProfile(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {return null;}

        return new PublicUserProfileResponse(
                user.getUsername(),
                user.getDisplayName(),
                user.getDescription(),
                user.getAvatar(),
                user.getCreatedAt()
        );
    }

    public Page<PublicPostResponse> getPublicPostsByAuthor(String username, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        //FIXME: Change to PostStatus.PUBLISHED after complete review post feature in admin
        Page<Post> postsPage = postRepository.findByAuthorUsernameAndStatus(username, PostStatus.DRAFT, pageable);

        return postsPage.map(post -> new PublicPostResponse(
                post.getId(),
                post.getName(),
                post.getSlug(),
                post.getExcerpt(),
                post.getThumbnail(),
                postViewService.getViewCount(post.getSlug()),
                post.getCreatedAt(),
                post.getUpdatedAt()
        ));
    }
}
