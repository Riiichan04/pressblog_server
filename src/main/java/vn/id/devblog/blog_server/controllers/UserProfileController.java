package vn.id.devblog.blog_server.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.id.devblog.blog_server.dto.request.user.UpdateProfileRequest;
import vn.id.devblog.blog_server.dto.response.user.UserProfileResponse;
import vn.id.devblog.blog_server.services.UserService;

@RestController
@RequestMapping("/user/profile")
@Slf4j
public class UserProfileController {

    private final UserService userProfileService;

    public UserProfileController(UserService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/get")
    public ResponseEntity<UserProfileResponse> getProfile(@RequestAttribute String userId) {
        Long id = Long.parseLong(userId);
        UserProfileResponse response = userProfileService.getProfile(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @RequestAttribute String userId,
            @RequestBody UpdateProfileRequest request
    ) {
        Long id = Long.parseLong(userId);
        UserProfileResponse response = userProfileService.updateProfile(id, request);
        return ResponseEntity.ok(response);
    }
}