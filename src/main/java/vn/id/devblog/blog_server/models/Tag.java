package vn.id.devblog.blog_server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tag extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    @Column(unique = true)
    private String slug;

    //Prevent StackOverflow when repeat Tag and Post
    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    private Set<Post> posts = new HashSet<>();

    @Column(name = "is_approved", nullable = false, columnDefinition = "boolean default false")
    private boolean isApproved = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag tag)) return false;

        return name != null && name.equalsIgnoreCase(tag.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.toLowerCase().hashCode() : getClass().hashCode();
    }
}
