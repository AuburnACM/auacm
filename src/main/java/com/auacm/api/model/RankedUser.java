package com.auacm.api.model;

import com.auacm.database.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RankedUser extends User implements Comparable<RankedUser> {
    private int rank;
    private int solved;

    public RankedUser(User user) {
        this.admin = user.getAdmin();
        this.username = user.getUsername();
        this.displayName = user.getDisplayName();
        this.password = user.getPassword();
        this.rank = 0;
        this.solved = 0;
    }

    public RankedUser(User user, int rank, int solved) {
        this.admin = user.getAdmin();
        this.username = user.getUsername();
        this.displayName = user.getDisplayName();
        this.password = user.getPassword();
        this.rank = rank;
        this.solved = solved;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getSolved() {
        return solved;
    }

    public void setSolved(int solved) {
        this.solved = solved;
    }

    @Override
    public int compareTo(RankedUser o) {
        if (solved - o.solved == 0) {
            return displayName.compareTo(o.displayName);
        } else {
            return solved - o.solved;
        }
    }
}
