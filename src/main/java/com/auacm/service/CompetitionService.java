package com.auacm.service;

import com.auacm.database.model.Competition;
import com.auacm.database.model.Problem;

public interface CompetitionService {
    boolean isInUpcomingCompetition(Problem problem);

    Competition getCompeitionByName(String name);
}
