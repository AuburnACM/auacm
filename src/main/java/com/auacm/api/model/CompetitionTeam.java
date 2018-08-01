package com.auacm.api.model;

import com.auacm.database.model.CompetitionProblem;
import com.auacm.database.model.Submission;
import com.auacm.database.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
public class CompetitionTeam {
    private List<BasicUser> users;
    private Map<String, CompetitionProblemData> problemData;

    public CompetitionTeam() {
        this.users = new ArrayList<>();
        this.problemData = new HashMap<>();
    }

    public void addUser(User user) {
        this.users.add(new BasicUser(user));
    }

    public void addSubmission(String label, Submission submission) {
        if (!problemData.containsKey(label)) {
            problemData.put(submission.getPid() + "", new CompetitionProblemData(label));
        }
        if (!problemData.get(submission.getPid() + "").getStatus().equals("good")) {
            problemData.get(submission.getPid() + "").setStatus(submission.getResult());
            problemData.get(submission.getPid() + "").incrementSubmitCount();
            problemData.get(submission.getPid() + "").setSubmitTime(submission.getSubmitTime());
        }
    }

    public void initializeProblems(List<CompetitionProblem> competitionProblems) {
        for (CompetitionProblem competitionProblem : competitionProblems) {
            problemData.put(competitionProblem.getProblem().getPid() + "",
                    new CompetitionProblemData(competitionProblem.getLabel(), "unattempted", 0L, 0L));
        }
    }
}
