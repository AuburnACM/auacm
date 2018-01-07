package com.auacm.api;

import com.auacm.api.model.UpdateProfilePicture;
import com.auacm.database.model.User;
import com.auacm.database.model.UserPrincipal;
import com.auacm.exception.ForbiddenException;
import com.auacm.service.FileSystemService;
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

    @RequestMapping(value = "/api/profile/image/{username}", method = RequestMethod.GET)
    public ResponseEntity<Resource> getProfilePicture(@PathVariable String username) {
        return new ResponseEntity<>(fileSystemService.getProfilePicture(username), HttpStatus.OK);
    }

    @RequestMapping(value = "/api/profile/image/{username}", method = RequestMethod.POST)
    public void updateProfilePicture(@PathVariable String username, @RequestBody @Validated UpdateProfilePicture picture) {
        User user = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        if (user.isAdmin() || user.getUsername().equals(username)) {
            fileSystemService.saveProfilePicture(user.getUsername(), picture.getData());
        } else {
            throw new ForbiddenException("You can only update your own picture!");
        }
    }
}
