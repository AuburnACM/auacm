package io.github.auburnacm.auacm.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Mac on 9/13/17.
 */
@Entity
@Table(name = "comp_names")
public class CompetitionProblem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int cid;

    private int pid;

    private String label;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
