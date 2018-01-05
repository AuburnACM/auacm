package com.auacm.api.validator;

import com.auacm.api.model.CreateCompetition;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CreateCompetitionValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(CreateCompetition.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CreateCompetition competition = (CreateCompetition) target;
        if (competition.isClosed() == null) {
            errors.reject("Closed cannot be null!");
        }
        if (competition.getLength() == null) {
            errors.reject("Length cannot be null!");
        }
        if (competition.getStartTime() == null) {
            errors.reject("Start time cannot be null!");
        }
        if (competition.getProblems() == null) {
            errors.reject("Problems cannot be null!");
        } else {
            if (competition.getProblems().size() > 26) {
                errors.reject("Max size for problems is 26!");
            }
        }
        if (competition.getName() == null) {
            errors.reject("Name cannot be null!");
        }
    }
}