package com.auacm.api;

import com.auacm.api.model.DataWrapper;
import com.auacm.api.model.ProblemResponse;
import com.auacm.database.model.BlogPost;
import com.auacm.database.model.Problem;
import com.auacm.database.service.ProblemService;
import com.auacm.database.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
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
        List<Problem> Problems = problemService.getAllProblems();
        List<ProblemResponse> responseList = new ArrayList<>();
        for (Problem problem : Problems){
            responseList.add(new ProblemResponse(problem));
        }
        return new DataWrapper<>(responseList, response.getStatus());
    }

    //TODO GetProblem and UpdateProblem and CreateProblem
}
