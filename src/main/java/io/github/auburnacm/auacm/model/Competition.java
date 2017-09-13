package io.github.auburnacm.auacm.model;

import javax.persistence.*;

/**
 * Created by Mac on 9/13/17.
 */
@Entity
@Table(name = "comp_names")
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long cid;

    private String name;

    private int start;
}
