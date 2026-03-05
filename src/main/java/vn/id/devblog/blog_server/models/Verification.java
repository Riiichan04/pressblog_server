package vn.id.devblog.blog_server.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.id.devblog.blog_server.common.enums.VerificationType;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Verification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private int attempts;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationType type;

    private LocalDateTime expiredAt;

    @PrePersist
    public void onCreate() {
        attempts = 0;
        expiredAt = LocalDateTime.now().plusMinutes(10);
    }
}
