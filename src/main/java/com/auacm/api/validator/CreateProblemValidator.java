package com.auacm.api.validator;

import com.auacm.api.model.CreateProblem;
import com.auacm.database.model.Competition;
import com.auacm.service.CompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CreateProblemValidator implements Validator {

    @Autowired
    private CompetitionService competitionService;

    @Override
    public boolean supports(Class<?> aClass) {
        return CreateProblem.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        CreateProblem problem = (CreateProblem) o;
        if (problem.getImportZip() == null || problem.getImportZip().isEmpty()) {
            if (problem.getName() != null) {
                if (problem.getName().length() < 2) {
                    errors.reject("Name must be greater than 2 characters long!");
                }
            } else {
                errors.reject("Name cannot be null!");
            }

            if (problem.getDescription() != null) {
                if (problem.getDescription().length() < 2) {
                    errors.reject("The description must be greater than 2 characters long!");
                }
            } else {
                errors.reject("Description cannot be null!");
            }

            if (problem.getInputDesc() != null) {
                if (problem.getInputDesc().length() < 1) {
                    errors.reject("Input description must be greater than 1 characters long!");
                }
            } else {
                errors.reject("Input description cannot be null!");
            }

            if (problem.getOutputDesc() != null) {
                if (problem.getOutputDesc().length() < 1) {
                    errors.reject("Output description must be greater than 1 characters long!");
                }
            } else {
                errors.reject("Output description cannot be null!");
            }

            if (problem.getAppearedIn() != null) {
                Competition competition = competitionService.getCompetitionByName(problem.getAppearedIn());
                if (competition == null) {
                    errors.reject(String.format("The competition %s does not exist.", problem.getAppearedIn()));
                }
            }

            if (problem.getInputZip() == null && problem.getInputFiles() == null) {
                errors.reject("You must have at least 1 input file!");
            }

            if (problem.getOutputZip() == null && problem.getOutputFiles() == null) {
                errors.reject("You must have at least 1 output file!");
            }

            if (problem.getInputFiles() != null) {
                if (problem.getOutputFiles() != null) {
                    if (problem.getInputFiles().size() != problem.getOutputFiles().size()) {
                        errors.reject("The number of input files does not match the number of output files!");
                    }
                } else {
                    errors.reject("The number of input files does not match the number of output files!");
                }
            } else {
                if (problem.getOutputFiles() != null) {
                    errors.reject("The number of input files does not match the number of output files!");
                }
            }

            if (problem.getSampleCases() == null) {
                errors.reject("SampleCases cannot be null!");
            }

            if (problem.getSolutionFile() == null) {
                errors.reject("Solution file cannot be null!");
            }
        }
    }
}
