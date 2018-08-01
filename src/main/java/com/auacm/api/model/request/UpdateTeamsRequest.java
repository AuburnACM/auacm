package com.auacm.api.model.request;

import com.auacm.api.model.BasicUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
public class UpdateTeamsRequest {
    private Map<String, List<BasicUser>> teams;

    public UpdateTeamsRequest() {
        this.teams = new HashMap<>();
    }
}
