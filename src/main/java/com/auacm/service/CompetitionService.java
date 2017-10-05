package com.auacm.service;

import com.auacm.database.model.Problem;

public interface CompetitionService {
    boolean isInUpcomingCompetition(Problem problem);
}
