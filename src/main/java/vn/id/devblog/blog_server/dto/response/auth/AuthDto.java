package vn.id.devblog.blog_server.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthDto {
    private Long id;
    private String email;
    private String username;
    private String displayName;
    private byte gender;
    private String avatar;
    private String description;
    private boolean isActive;
    private boolean isVerified;
    private int role;
    private String jwtToken;
}
