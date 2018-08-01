package com.auacm.api.model.response;

import com.auacm.api.model.RankedUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RankedUserResponse extends MeResponse {
    private int rank;
    private int solved;

    public RankedUserResponse(RankedUser rankedUser) {
        super(rankedUser);
        this.rank = rankedUser.getRank();
        this.solved = rankedUser.getSolved();
    }
}
