package com.auacm.api.model;

import java.util.ArrayList;
import java.util.Collection;

public class RecentSubmissionList extends ArrayList<RecentSubmission> {
    public RecentSubmissionList(int initialCapacity) {
        super(initialCapacity);
    }

    public RecentSubmissionList() {
    }

    public RecentSubmissionList(Collection<? extends RecentSubmission> c) {
        super(c);
    }
}
