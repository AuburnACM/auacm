package com.auacm.api;

import com.auacm.api.model.CreateProblem;
import com.auacm.api.validator.CreateProblemValidator;
import com.auacm.api.validator.UpdateProblemValidator;
import com.auacm.service.FileSystemService;
import com.auacm.service.ProblemService;
import com.auacm.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProblemController {
    @Autowired
    private ProblemService problemService;

    @Autowired
    private JsonUtil jsonUtil;

    @Autowired
    private FileSystemService fileSystemService;

    @Autowired
    private CreateProblemValidator createProblemValidator;

    @Autowired
    private UpdateProblemValidator updateProblemValidator;

    @InitBinder("newProblem")
    protected void initBinder(final WebDataBinder binder) {
        binder.addValidators(createProblemValidator);
    }

    @InitBinder("updateProblem")
    private void initUpdateBinder(final WebDataBinder binder) {
        binder.addValidators(updateProblemValidator);
    }

    @RequestMapping(value = "/api/problems", method = RequestMethod.POST, produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody String createProblem(@Validated @ModelAttribute("newProblem") CreateProblem newProblem) {
        return jsonUtil.toJson(problemService.getProblemResponse(problemService.createProblem(newProblem)));
    }

    @RequestMapping(value = "/api/problems/{identifier}", method = {RequestMethod.PUT, RequestMethod.POST}, produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody String updateProblem(@PathVariable String identifier, @ModelAttribute("updateProblem") CreateProblem newProblem) {
        return jsonUtil.toJson(problemService.getProblemResponse(problemService.updateProblem(identifier, newProblem)));
    }

    @RequestMapping(value = "/api/problems/{identifier}", method = {RequestMethod.DELETE}, produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProblem(@PathVariable String identifier) {
        problemService.deleteProblem(identifier);
    }

    @RequestMapping(value = "/api/problems", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody String getProblems() {
        return jsonUtil.toJson(problemService.getProblemListResponse(problemService.getAllProblems()));
    }

    @RequestMapping(value = "/api/problems/{identifier}", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody String getProblem(@PathVariable String identifier) {
        return jsonUtil.toJson(problemService.getProblemResponse(problemService.getProblem(identifier)));
    }
    //TODO GetProblem and UpdateProblem and CreateProblem
    @RequestMapping(value = "/problems/{shortName}/info.pdf", method = RequestMethod.GET)
    public ResponseEntity<Resource> getProblemPdf(@PathVariable String shortName) {
        return new ResponseEntity<>(fileSystemService.getProblemPdf(shortName), HttpStatus.OK);
    }
}
