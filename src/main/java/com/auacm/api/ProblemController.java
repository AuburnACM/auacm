package com.auacm.api;

import com.auacm.service.ProblemService;
import com.auacm.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProblemController {
    @Autowired
    private ProblemService problemService;

    @Autowired
    private JsonUtil jsonUtil;

    //@Autowired
    //TODO private UpdateProblemValidator updateProblemValidator;

    //@InitBinder(value = "updateProblem")
    //TODO protected void initBinder(final WebDataBinder binder) { binder.addValidators(updateProblemValidator); }

    @RequestMapping(path = "/api/problems", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody String getProblems() {
        return jsonUtil.toJson(problemService.getProblemListResponse(problemService.getAllProblems()));
    }

    @RequestMapping(path = "/api/problems/{identifier}", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody String getProblem(@PathVariable String identifier) {
        return jsonUtil.toJson(problemService.getProblemResponse(problemService.getProblem(identifier)));
    }
    //TODO GetProblem and UpdateProblem and CreateProblem

}
