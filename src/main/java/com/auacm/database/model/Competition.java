package com.auacm.database.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "comp_names")
public class Competition implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long cid;

    private String name;

    private int start;

    private int stop;

    private boolean closed;

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getStop() {
        return stop;
    }

    public void setStop(int stop) {
        this.stop = stop;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}
