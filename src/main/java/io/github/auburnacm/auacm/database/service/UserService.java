package io.github.auburnacm.auacm.database.service;

import io.github.auburnacm.auacm.model.User;
import org.springframework.stereotype.Component;

@Component
public interface UserService {
    boolean validatePassword(String username, String password);

    void createUser(User user);

    void createUser(String displayName, String username, String password, boolean isAdmin);

    User getUser(String username);

    void deleteUser(User user);

    void updateUsername(User user, String newUsername);

    void updatePassword(User user, String newPassword);

    void updateDisplayName(User user, String newDisplayName);

    boolean userExists(String username);
}
