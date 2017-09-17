package io.github.auburnacm.auacm.database.service;

import io.github.auburnacm.auacm.api.model.RankedUser;
import io.github.auburnacm.auacm.database.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

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

    void updateUser(User user);

    boolean userExists(String username);

    List<RankedUser> getRanks(String timeFrame);
}
