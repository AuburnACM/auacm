package com.auacm.api;

import com.auacm.api.model.CreateCompetition;
import com.auacm.api.model.RegisterUsers;
import com.auacm.api.validator.CreateCompetitionValidator;
import com.auacm.database.model.User;
import com.auacm.database.model.UserPrincipal;
import com.auacm.service.CompetitionService;
import com.auacm.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;

@RestController
public class CompetitionController {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CreateCompetitionValidator createCompetitionValidator;

    @Autowired
    private JsonUtil jsonUtil;

    @InitBinder("newCompetition")
    protected void initBinder(final WebDataBinder binder) {
        binder.addValidators(createCompetitionValidator);
    }

    @RequestMapping(path = "/api/competitions/{competitionId}/register", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody void register(@PathVariable long competitionId, @ModelAttribute RegisterUsers users, HttpServletResponse response) {
        User user = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        if (users.getUserNames() == null) {
            competitionService.registerUsers(competitionId, Collections.singletonList(user.getUsername()));
        } else {
            if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                competitionService.registerUsers(competitionId, users.getUserNames());
            } else {
                response.setStatus(HttpStatus.FORBIDDEN.value());
            }
        }
    }

    @RequestMapping(path = "/api/competitions/{competitionId}/unregister", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody void unregister(@PathVariable int competitionId, @ModelAttribute RegisterUsers users, HttpServletResponse response) {
        User user = ((UserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        if (users.getUserNames() == null) {
            competitionService.unregisterUsers(competitionId, Collections.singletonList(user.getUsername()));
        } else {
            if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                competitionService.unregisterUsers(competitionId, users.getUserNames());
            } else {
                response.setStatus(HttpStatus.FORBIDDEN.value());
            }
        }
    }

    @RequestMapping(path = "/api/competitions", produces = "application/json", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody String createCompetition(@Validated @ModelAttribute("newCompetition") CreateCompetition newCompetition) {
        return jsonUtil.toJson(competitionService.getCompetitionResponse(competitionService.createCompetition(newCompetition)));
    }

    @RequestMapping(path = "/api/competitions/{competitionId}", produces = "application/json", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody String updateCompetition(@PathVariable long competitionId, @ModelAttribute("updateCompetition") CreateCompetition competition) {
        return jsonUtil.toJson(competitionService.getCompetitionResponse(competitionService.updateCompetition(competitionId, competition)));
    }

    @RequestMapping(path = "/api/competitions/{competitionId}", produces = "application/json", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCompetition(@PathVariable long competitionId) {
        competitionService.deleteCompetition(competitionId);
    }

    @RequestMapping(path = "/api/competitions", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody String getAllCompetitions() {
        return jsonUtil.toJson(competitionService.getCompetitionListResponse(competitionService.getAllCompetitions()));
    }

    @RequestMapping(path = "/api/competitions/{cid}", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody String getCompetition(@PathVariable long cid) {
        return jsonUtil.toJson(competitionService.getCompetitionResponse(competitionService.getCompetitionById(cid)));
    }
}
