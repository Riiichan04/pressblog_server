package vn.id.devblog.blog_server.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.id.devblog.blog_server.common.utilities.SlugUtils;
import vn.id.devblog.blog_server.models.Post;
import vn.id.devblog.blog_server.models.Tag;
import vn.id.devblog.blog_server.repositories.PostRepository;
import vn.id.devblog.blog_server.repositories.TagRepository;

@Service
@RequiredArgsConstructor
public class AdminTagService {
    private final TagRepository tagRepository;
    private final PostRepository postRepository;

    public Page<Tag> getAllTagsForAdmin(int page, int size) {
        return tagRepository.findAll(PageRequest.of(page, size, Sort.by("id").descending()));
    }

    @Transactional
    public Tag updateTag(int id, String newName) {
        Tag tag = tagRepository.findById((long) id).orElseThrow(() -> new RuntimeException("Tag not found"));
        tag.setName(newName);
        tag.setSlug(SlugUtils.toSlug(newName));
        return tagRepository.save(tag);
    }

    @Transactional
    public Tag toggleApproval(int id) {
        Tag tag = tagRepository.findById((long) id).orElseThrow(() -> new RuntimeException("Tag not found"));
        tag.setApproved(!tag.isApproved());
        return tagRepository.save(tag);
    }

    @Transactional
    public void forceDeleteTag(int id) {
        Tag tag = tagRepository.findById((long) id).orElseThrow(() -> new RuntimeException("Tag not found"));

        for (Post post : tag.getPosts()) {
            post.getTags().remove(tag);
            postRepository.save(post);
        }

        tagRepository.delete(tag);
    }

}
