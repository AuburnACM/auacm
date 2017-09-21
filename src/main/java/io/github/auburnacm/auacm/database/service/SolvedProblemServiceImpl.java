package io.github.auburnacm.auacm.database.service;

import io.github.auburnacm.auacm.database.dao.SolvedProblemDao;
import io.github.auburnacm.auacm.database.model.SolvedProblem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolvedProblemServiceImpl implements SolvedProblemService {
    @Autowired
    private SolvedProblemDao solvedProblemDao;

    @Override
    public List<SolvedProblem> getProblemsForUser(String username) {
        return solvedProblemDao.findByUsernameIgnoreCase(username);
    }

    @Override
    public void addSolvedProblem(SolvedProblem problem) {
        solvedProblemDao.save(problem);
    }

    @Override
    public void addSolvedProblem(long problemId, String username) {
        addSolvedProblem(new SolvedProblem(problemId, username));
    }
}
