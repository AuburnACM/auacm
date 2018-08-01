package com.auacm.api.model.request;

import com.auacm.database.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateUserRequest extends User {
    private String newPassword;
}
