package com.auacm.service;

import com.auacm.database.dao.CompetitionDao;
import com.auacm.database.dao.CompetitionProblemDao;
import com.auacm.database.model.Competition;
import com.auacm.database.model.Problem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompetitionServiceImpl implements CompetitionService {
    @Autowired
    private CompetitionDao competitionDao;

    @Autowired
    private CompetitionProblemDao problemDao;

    @Override
    public boolean isInUpcomingCompetition(Problem problem) {
        if (problem.getCompetitionId() > 0) {
            Competition competition = competitionDao.findOne(problem.getCompetitionId());
            long currentTime = System.currentTimeMillis() / 1000;
            if (competition.getStart() > currentTime) {
                return true;
            }
        }
        return false;
    }
}
