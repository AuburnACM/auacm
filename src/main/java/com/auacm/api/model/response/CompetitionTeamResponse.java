package com.auacm.api.model.response;

import com.auacm.api.model.BasicUser;
import com.auacm.database.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CompetitionTeamResponse extends HashMap<String, List<BasicUser>> {
    public CompetitionTeamResponse(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public CompetitionTeamResponse(int initialCapacity) {
        super(initialCapacity);
    }

    public CompetitionTeamResponse() {
    }

    public CompetitionTeamResponse(Map<? extends String, ? extends List<BasicUser>> m) {
        super(m);
    }

    public CompetitionTeamResponse addTeams(Map<String, List<User>> users) {
        for (String s : users.keySet()) {
            this.put(s, users.get(s).stream().map(BasicUser::new).collect(Collectors.toList()));
        }
        return this;
    }
}
