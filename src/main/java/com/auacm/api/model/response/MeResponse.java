package com.auacm.api.model.response;

import com.auacm.api.model.BasicUser;
import com.auacm.database.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MeResponse extends BasicUser {
    private boolean isAdmin;
    private List<String> permissions;

    public MeResponse(User user) {
        super(user);
        this.isAdmin = user.getAdmin();
        this.permissions = new ArrayList<>();
        user.getAuthorities().forEach(authority -> permissions.add(authority.getAuthority()));
    }
}
