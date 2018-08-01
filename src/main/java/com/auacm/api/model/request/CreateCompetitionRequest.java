package com.auacm.api.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateCompetitionRequest {
    private Boolean closed;
    private Long length;
    private String name;
    private List<Long> problems;
    private Long startTime;
    private List<String> userNames;
}
