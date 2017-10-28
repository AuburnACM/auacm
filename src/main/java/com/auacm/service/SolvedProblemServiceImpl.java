package com.auacm.service;

import com.auacm.database.dao.SolvedProblemDao;
import com.auacm.database.model.Problem;
import com.auacm.database.model.SolvedProblem;
import com.auacm.database.model.User;
import com.auacm.database.model.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Override
    public boolean hasSolved(Problem problem) {
        try {
            User user = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
            SolvedProblem solvedProblem = solvedProblemDao.findOneByUsernameAndPid(user.getUsername(), problem.getPid());
            if (solvedProblem != null) {
                return true;
            } else {
                return false;
            }
        } catch (ClassCastException | JpaObjectRetrievalFailureException e) {
            return false;
        }
    }

    @Override
    public boolean hasSolved(User user, Problem problem) {
        try {
            SolvedProblem solvedProblem = solvedProblemDao.findOneByUsernameAndPid(user.getUsername(), problem.getPid());
            if (solvedProblem != null) {
                return true;
            } else {
                return false;
            }
        } catch (ClassCastException | JpaObjectRetrievalFailureException e) {
            return false;
        }
    }
}
