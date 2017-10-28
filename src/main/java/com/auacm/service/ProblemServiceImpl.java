package com.auacm.service;

import com.auacm.database.dao.ProblemDao;
import com.auacm.database.model.Problem;
import com.auacm.database.model.SampleCase;
import com.auacm.database.model.SolvedProblem;
import com.auacm.exception.ProblemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProblemServiceImpl implements ProblemService {

    @Autowired
    private ProblemDao problemDao;

    @Autowired
    private SolvedProblemService solvedProblemService;

    @Autowired
    private CompetitionService competitionService;

    @Override
    @Transactional
    public Problem addProblem(Problem problem) {
        return problemDao.save(problem);
    }

    @Override
    @Transactional
    public Problem updateProblem(Problem problem) {
        return problemDao.save(problem);
    }

    @Override
    @Transactional
    public void deleteProblem(Problem problem) {
        problemDao.delete(problem);
    }

    @Override
    public List<Problem> getAllProblems() {
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return problemDao.findAll();
        } else {
            List<Problem> allProblems = problemDao.findAll();
            List<Problem> notInUpcoming = new ArrayList<>();
            for (Problem problem : allProblems) {
                if (!competitionService.isInUpcomingCompetition(problem)) {
                    notInUpcoming.add(problem);
                }
            }
            return notInUpcoming;
        }
    }

    @Override
    public Problem getProblem(String identifier) {
        try {
            long pid = Integer.parseInt(identifier);
            return getProblemForPid(pid);
        } catch (NumberFormatException e) {
            return getProblemForShortName(identifier);
        }
    }

    @Override
    public Problem getProblemForPid(long pid) {
        try {
            Problem problem = problemDao.findOne(pid);
            if (competitionService.isInUpcomingCompetition(problem)) {
                if (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                        .contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                    return problem;
                } else {
                    throw new ProblemNotFoundException("Failed to find a problem for pid " + pid + ".");
                }
            } else {
                return problem;
            }
        } catch (JpaObjectRetrievalFailureException e) {
            throw new ProblemNotFoundException("Failed to find a problem for pid " + pid + ".");
        }
    }

    @Override
    public Problem getProblemForShortName(String shortName) {
        try {
            Problem problem = problemDao.findByShortNameIgnoreCase(shortName);
            if (competitionService.isInUpcomingCompetition(problem)) {
                if (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                        .contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                    return problem;
                } else {
                    throw new ProblemNotFoundException("Failed to find a problem for shortname " + shortName + ".");
                }
            } else {
                return problem;
            }
        } catch (JpaObjectRetrievalFailureException e) {
            throw new ProblemNotFoundException("Failed to find a problem for shortname " + shortName + ".");
        }
    }

    @Override
    public com.auacm.api.proto.Problem.ProblemListWrapper getProblemListResponse(List<Problem> problems) {
        com.auacm.api.proto.Problem.ProblemListWrapper.Builder builder = com.auacm.api.proto.Problem.ProblemListWrapper.newBuilder();
        for (Problem problem : problems) {
            builder.addData(com.auacm.api.proto.Problem.SimpleProblemResponse.newBuilder()
                    .setAdded(problem.getAdded())
                    .setAppeared(problem.getAppeared())
                    .setCompRelease(problem.getCompetitionId())
                    .setDifficuty(problem.getDifficulty())
                    .setName(problem.getName())
                    .setPid(problem.getPid())
                    .setShortName(problem.getShortName())
                    .setSolved(solvedProblemService.hasSolved(problem))
                    .setUrl(String.format("/problems/%s/info.pdf", problem.getShortName())));
        }
        return builder.build();
    }

    @Override
    public com.auacm.api.proto.Problem.ProblemWrapper getProblemResponse(Problem problem) {
        com.auacm.api.proto.Problem.ProblemResponse.Builder builder = com.auacm.api.proto.Problem.ProblemResponse.newBuilder();
        builder.setAdded(problem.getAdded())
                .setAppeared(problem.getAppeared())
                .setCompRelease(problem.getCompetitionId())
                .setDescription(problem.getProblemData().getDescription())
                .setDifficuty(problem.getDifficulty())
                .setInputDesc(problem.getProblemData().getInputDescription())
                .setName(problem.getName())
                .setOutputDesc(problem.getProblemData().getOutputDescription())
                .setPid(problem.getPid())
                .setShortName(problem.getShortName());
        for (SampleCase sampleCase : problem.getSampleCases()) {
            builder.addSampleCases(com.auacm.api.proto.Problem.SampleCase.newBuilder()
                    .setCaseNum(sampleCase.getCaseNum())
                    .setInput(sampleCase.getInput())
                    .setOutput(sampleCase.getOutput()));
        }
        return com.auacm.api.proto.Problem.ProblemWrapper.newBuilder().setData(builder).build();
    }
}
