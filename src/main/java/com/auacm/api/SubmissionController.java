package com.auacm.api;

import com.auacm.api.constant.SubmissionResult;
import com.auacm.api.model.Submission;
import com.auacm.api.model.SubmissionList;
import com.auacm.api.model.SubmissionStatus;
import com.auacm.api.validator.SubmissionValidator;
import com.auacm.exception.ForbiddenException;
import com.auacm.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
public class SubmissionController {
    @Autowired
    private SubmissionValidator submissionValidator;

    @Autowired
    private SubmissionService submissionService;

    @InitBinder("submit")
    public void initBinder(final WebDataBinder binder) {
        binder.addValidators(submissionValidator);
    }

    @RequestMapping(path = "/api/submit", method = RequestMethod.POST)
    public SubmissionStatus submit(@ModelAttribute("submit") Submission submission) {
//        return submissionService.submit(submission); Disabled until the judge exists
        com.auacm.database.model.Submission submission1 = new com.auacm.database.model.Submission();
        submission1.setJob(-1);
        submission1.setResult(SubmissionResult.ANSWER_INCORRECT);
        return new SubmissionStatus(submission1);
    }

    @RequestMapping(path = "/api/submit", method = RequestMethod.GET)
    public SubmissionList getSubmissions(@RequestParam(required = false) String username,
                                         @RequestParam(required = false) Integer limit) {
        if (username != null) {
            return submissionService.getSubmissionsForUser(username, limit == null ? 10 : limit);
        } else {
            return submissionService.getSubmissionsCurrentUser(limit == null ? 10 : limit);
        }
    }
}
