package com.auacm.api.model;

import com.auacm.database.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BasicUser {
    private String displayName;
    private String username;

    public BasicUser(User user) {
        this.displayName = user.getDisplayName();
        this.username = user.getUsername();
    }
}
