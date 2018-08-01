package com.auacm.service;

import com.auacm.api.model.RankedUser;
import com.auacm.database.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserService {
    boolean validatePassword(String username, String password);

    User createUser(User user);

    User createUser(String displayName, String username, String password, boolean isAdmin);

    User getUser(String username);

    void deleteUser(User user);

    void updateUsername(User user, String newUsername);

    void updatePassword(User user, String newPassword);

    void updateDisplayName(User user, String newDisplayName);

    void updateUser(User user);

    User updateUser(String username, User user);

    User updateSelf(User user);

    boolean userExists(String username);

    List<RankedUser> getRanks(String timeFrame);
}
