package com.auacm.api.model.response;

import com.auacm.database.model.Problem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProblemListResponse extends ArrayList<CreateProblemResponse> {
    public ProblemListResponse(int initialCapacity) {
        super(initialCapacity);
    }

    public ProblemListResponse() {
    }

    public ProblemListResponse(Collection<? extends CreateProblemResponse> c) {
        super(c);
    }

    public ProblemListResponse(List<? extends Problem> c) {
        super();
        c.forEach(problem -> this.add(new CreateProblemResponse(problem)));
    }
}
