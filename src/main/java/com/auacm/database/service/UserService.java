package com.auacm.database.service;

import com.auacm.api.model.RankedUser;
import com.auacm.api.model.UpdateUser;
import com.auacm.database.model.User;
import com.auacm.database.model.UserPrincipal;
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

    User updateUser(String username, UpdateUser user);

    User updateSelf(UpdateUser user);

    boolean userExists(String username);

    List<RankedUser> getRanks(String timeFrame);

    com.auacm.api.proto.User.MeResponseWrapper getMeResponse(UserPrincipal user);

    com.auacm.api.proto.User.RankResponseWrapper getRankedResponse(List<RankedUser> ranks);
}
