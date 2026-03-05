package vn.id.devblog.blog_server.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    private User author;

    @Column(length = 2048)
    private String content;

    private int upvote;
    private int downvote;

    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parent;

    @Override
    public void onCreate() {
        this.upvote = 0;
        this.downvote = 0;
        this.isDeleted = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
