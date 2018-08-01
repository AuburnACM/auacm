package com.auacm.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RecentCompetition {
    private long cid;
    private String compName;
    private String teamName;
    private int teamSize;
}
