package vn.id.devblog.blog_server.security.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import vn.id.devblog.blog_server.common.constants.RoleConstants;
import vn.id.devblog.blog_server.models.Role;
import vn.id.devblog.blog_server.models.User;
import vn.id.devblog.blog_server.repositories.RoleRepository;
import vn.id.devblog.blog_server.repositories.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        User existingUser = userRepository.findByEmail(email);

        if (existingUser != null) {
            if (existingUser.getAvatar() == null) {
                existingUser.setAvatar(picture);
            }

            if (!existingUser.isVerified()) {
                existingUser.setVerified(true);
            }

            userRepository.save(existingUser);

            return new CustomOAuth2User(existingUser, oAuth2User.getAttributes());

        } else {
            User newUser = new User();
            newUser.setEmail(email);

            newUser.setUsername(email.split("@")[0] + "_" + UUID.randomUUID().toString().substring(0, 5));
            newUser.setDisplayName(name);
            newUser.setAvatar(picture);

            newUser.setVerified(true);
            newUser.setActive(true);

            newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

            Role defaultRole = roleRepository.findByName(RoleConstants.ROLE_USER);
            newUser.setRole(defaultRole);

            newUser = userRepository.save(newUser);

            return new CustomOAuth2User(newUser, oAuth2User.getAttributes());
        }
    }
}