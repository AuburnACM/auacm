package com.auacm.api.model;

import com.auacm.database.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Competition {
    private Long cid;
    private Boolean closed;
    private Long length;
    private String name;
    private Boolean registered;
    private Long startTime;

    public Competition(com.auacm.database.model.Competition competition) {
        this.cid = competition.getCid();
        this.closed = competition.getClosed();
        this.length = competition.getStop() - competition.getStart();
        this.name = competition.getName();
        this.registered = competition.getCompetitionUsers().stream().anyMatch(
                predicate -> predicate.getUser().getUsername().equals(
                        ((User)SecurityContextHolder.getContext()
                                .getAuthentication().getPrincipal()).getUsername()));
        this.startTime = competition.getStart();
    }
}
