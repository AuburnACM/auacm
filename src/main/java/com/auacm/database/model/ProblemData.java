package com.auacm.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "problem_data")
@Getter
@Setter
@NoArgsConstructor
public class ProblemData implements Serializable {
    @JsonIgnore
    @Id
    private Long pid;

    @Column(name = "time_limit")
    private Long timeLimit;

    private String description;

    @Column(name = "input_desc")
    private String inputDescription;

    @Column(name = "output_desc")
    private String outputDescription;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pid")
    @MapsId
    private Problem problem;
}
