package com.auacm.api.model.response;

import com.auacm.api.model.RankedUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RankedUserListResponse extends ArrayList<RankedUserResponse> {
    public RankedUserListResponse(int initialCapacity) {
        super(initialCapacity);
    }

    public RankedUserListResponse() {
    }

    public RankedUserListResponse(Collection<? extends RankedUserResponse> c) {
        super(c);
    }

    public RankedUserListResponse(List<RankedUser> c) {
        super();
        c.forEach(rankedUser -> this.add(new RankedUserResponse(rankedUser)));
    }
}
