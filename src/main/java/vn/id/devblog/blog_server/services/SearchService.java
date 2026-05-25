package vn.id.devblog.blog_server.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.id.devblog.blog_server.common.enums.PostStatus;
import vn.id.devblog.blog_server.dto.response.post.PublicPostResponse;
import vn.id.devblog.blog_server.models.Post;
import vn.id.devblog.blog_server.repositories.PostRepository;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final PostRepository postRepository;
    private final PostViewService postViewService;

    public Page<PublicPostResponse> searchPosts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        //FIXME: Change to PostStatus.PUBLISHED after complete review post feature in admin
        Page<Post> postsPage = postRepository.searchPublishedPosts(keyword, PostStatus.DRAFT, pageable);

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
