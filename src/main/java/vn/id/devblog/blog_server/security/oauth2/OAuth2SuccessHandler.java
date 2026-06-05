package vn.id.devblog.blog_server.security.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import vn.id.devblog.blog_server.security.JwtConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtConfig jwtConfig;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", oAuth2User.getUser().getId());
        extraClaims.put("userRole", oAuth2User.getUser().getRole().getName());

        String token = jwtConfig.generateToken(extraClaims, oAuth2User.getUser());

        //TODO: Put URL into application.properties
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3001/oauth2/redirect")
                .queryParam("token", token)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}