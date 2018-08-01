package com.auacm.database.model;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "sample_cases")
@Getter
@Setter
@NoArgsConstructor
public class SampleCase implements Serializable {

    @EmbeddedId
    private SampleCasePK sampleCasePK;

    private String input;

    private String output;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("pid")
    @JoinColumn(name = "pid", referencedColumnName = "pid", updatable = false, insertable = false)
    private Problem problem;

    public SampleCase(com.auacm.api.model.SampleCase sampleCase) {
        this.sampleCasePK = new SampleCasePK(null, sampleCase.getCaseNum());
        this.input = sampleCase.getInput();
        this.output = sampleCase.getOutput();
    }

    public SampleCase(JsonObject object) {
        this.sampleCasePK = new SampleCasePK();
        this.sampleCasePK.setCaseNum(object.get("caseNum").getAsLong());
        this.input = object.get("input").getAsString();
        this.output = object.get("output").getAsString();
    }
}
