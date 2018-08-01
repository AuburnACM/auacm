package com.auacm.api;

import com.auacm.api.model.*;
import com.auacm.api.model.response.ProfileResponse;
import com.auacm.database.model.User;
import com.auacm.exception.ForbiddenException;
import com.auacm.service.FileSystemService;
import com.auacm.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProfileController {
    @Autowired
    private FileSystemService fileSystemService;

    @Autowired
    private ProfileService profileService;

    @RequestMapping(value = "/api/profile/{username}", method = RequestMethod.GET)
    public @ResponseBody ProfileResponse getProfile(@PathVariable String username) {
        return profileService.getProfile(username);
    }

    @RequestMapping(value = "/api/profile/{username}/image", method = RequestMethod.GET)
    public ResponseEntity<Resource> getProfilePicture(@PathVariable String username) {
        return new ResponseEntity<>(fileSystemService.getProfilePicture(username), HttpStatus.OK);
    }

    @RequestMapping(value = "/api/profile/{username}/image", method = RequestMethod.POST)
    public void updateProfilePicture(@PathVariable String username, @RequestBody @Validated UpdateProfilePicture picture) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getAdmin() || user.getUsername().equals(username)) {
            fileSystemService.saveProfilePicture(user.getUsername(), picture.getData());
        } else {
            throw new ForbiddenException("You can only update your own picture!");
        }
    }

    @RequestMapping(value = "/api/profile/{username}/competitions", method = RequestMethod.GET)
    public RecentCompetitionList getRecentCompetitions(@PathVariable String username,
                                                         @RequestParam(value = "size", required = false) Integer size) {
        int amount = size == null ? 0 : size;
        return profileService.getRecentCompetitions(username, amount);
    }

    @RequestMapping(value = "/api/profile/{username}/blogs", method = RequestMethod.GET)
    public RecentBlogPostList getRecentBlogs(@PathVariable String username,
                                             @RequestParam(value = "size", required = false) Integer size) {
        int amount = size == null ? 0 : size;
        return profileService.getRecentBlogPosts(username, amount);
    }

    @RequestMapping(value = "/api/profile/{username}/submits", method = RequestMethod.GET)
    public RecentSubmissionList getRecentSubmissions(@PathVariable String username,
                                                       @RequestParam(value = "size", required = false) Integer size) {
        int amount = size == null ? 0 : size;
        return profileService.getRecentSubmissions(username, amount);
    }
}
