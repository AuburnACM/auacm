package com.auacm.database.service;

import com.auacm.database.dao.ProblemDao;
import com.auacm.database.model.Problem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ProblemServiceImpl implements ProblemService {

    @Autowired
    private ProblemDao problemDao;

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
        List<Problem> problems = problemDao.findAll();
        return problems;
    }

    @Override
    public Problem getProblemForPid(long pid) {
        return problemDao.getOne(pid);
    }

    @Override
    public Problem getProblemForShortName(String shortName) {
        return problemDao.findByShortNameIgnoreCase(shortName);
    }
}
