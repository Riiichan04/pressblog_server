package vn.id.devblog.blog_server.dto.response.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
    private boolean active;
    private boolean verified;
    private String role;
    private List<String> permissions;
    private String jwtToken;
    private String refreshToken;
}
