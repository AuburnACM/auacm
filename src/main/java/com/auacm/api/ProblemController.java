package com.auacm.api;

import com.auacm.api.model.DataWrapper;
import com.auacm.api.model.ProblemResponse;
import com.auacm.api.model.ProblemFullResponse;
import com.auacm.database.model.Problem;
import com.auacm.database.service.ProblemService;
import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ProblemController {
    @Autowired
    private ProblemService problemService;

    //@Autowired
    //TODO private UpdateProblemValidator updateProblemValidator;

    //@InitBinder(value = "updateProblem")
    //TODO protected void initBinder(final WebDataBinder binder) { binder.addValidators(updateProblemValidator); }

    @RequestMapping(path = "/api/problems", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody DataWrapper<List<ProblemResponse>> getProblems(HttpServletResponse response) {
        List<Problem> problems = problemService.getAllProblems();
        List<ProblemResponse> responseList = new ArrayList<>();
        for (Problem problem : problems){
            responseList.add(new ProblemResponse(problem));
        }
        return new DataWrapper<>(responseList, response.getStatus());
    }

    @RequestMapping(path = "/api/problems/{identifier}", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody DataWrapper<ProblemFullResponse> getProblem(@PathVariable String identifier, HttpServletResponse response) {
        try {
            Long pid = Long.parseLong(identifier);
            Problem problem = problemService.getProblemForPid(pid);
            ProblemFullResponse ProblemFullResponse = new ProblemFullResponse(problem);
            return new DataWrapper<>(ProblemFullResponse, response.getStatus());
        } catch (NumberFormatException e){
            Problem problem = problemService.getProblemForShortName(identifier);
            ProblemFullResponse ProblemFullResponse = new ProblemFullResponse(problem);
            return new DataWrapper<>(ProblemFullResponse, response.getStatus());
        }
    }
    //TODO GetProblem and UpdateProblem and CreateProblem

}
