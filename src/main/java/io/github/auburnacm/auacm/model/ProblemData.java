package io.github.auburnacm.auacm.model;

import javax.persistence.*;

@Entity
@Table(name = "problem_data")
public class ProblemData {
    @Id
    private long pid;

    @Column(name = "time_limit")
    private int timeLimit;

    private String description;

    @Column(name = "input_desc")
    private String inputDescription;

    @Column(name = "output_desc")
    private String outputDescription;
}
