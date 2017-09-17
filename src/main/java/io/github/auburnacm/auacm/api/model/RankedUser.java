package io.github.auburnacm.auacm.api.model;

public class RankedUser implements Comparable<RankedUser> {
    private String displayName;

    private String username;

    private int rank;

    private int solved;

    public RankedUser(String displayName, String username, int rank, int solved) {
        this.displayName = displayName;
        this.username = username;
        this.rank = rank;
        this.solved = solved;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
        if (solved - o.solved== 0) {
            return displayName.compareTo(o.displayName);
        } else {
            return solved - o.solved;
        }
    }
}
