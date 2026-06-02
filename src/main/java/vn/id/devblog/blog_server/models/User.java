package vn.id.devblog.blog_server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Table(name = "users")
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(nullable = false, length = 64, unique = true)
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Invalid username format")
    private String username;

    @Column(length = 255)
    private String displayName;

    @Column(nullable = false)
    private String password;

    private byte gender;
    private String avatar;
    private String description;

    @Column(nullable = false)
    private boolean isActive;

    @Column(nullable = false)
    private boolean isVerified;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    @Override
    public void onCreate() {
        isActive = true;
        avatar = "";
        isVerified = false;
        displayName = username;
        posts = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    //Required methods from Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (this.role != null) {
            authorities.add(new SimpleGrantedAuthority(this.role.getName()));
            if (this.role.getPermissions() != null) {
                this.role.getPermissions().forEach(permission -> {
                    authorities.add(new SimpleGrantedAuthority(permission.getName()));
                });
            }
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isActive; //If admin ban user, account will be locked
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isActive;
    }
}
