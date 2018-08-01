package com.auacm.api;

import com.auacm.api.model.request.CreateCompetitionRequest;
import com.auacm.api.model.request.RegisterUsersRequest;
import com.auacm.api.model.request.UpdateTeamsRequest;
import com.auacm.api.model.response.CompetitionResponse;
import com.auacm.api.model.response.CompetitionTeamResponse;
import com.auacm.api.validator.CreateCompetitionValidator;
import com.auacm.database.model.Competition;
import com.auacm.database.model.User;
import com.auacm.exception.ForbiddenException;
import com.auacm.service.CompetitionService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class CompetitionController {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CreateCompetitionValidator createCompetitionValidator;

    @Autowired
    private Gson gson;

    @InitBinder("newCompetition")
    protected void initBinder(final WebDataBinder binder) {
        binder.addValidators(createCompetitionValidator);
    }

    @RequestMapping(path = "/api/competitions/{competitionId}/register", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody void register(@PathVariable long competitionId, @ModelAttribute RegisterUsersRequest users) {
        if (users.getUserNames() == null) {
            competitionService.registerCurrentUser(competitionId);
        } else {
            if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                competitionService.registerUsers(competitionId, users.getUserNames());
            } else {
                throw new ForbiddenException("You must be an admin to do that!");
            }
        }
    }

    @RequestMapping(path = "/api/competitions/{competitionId}/unregister", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody void unregister(@PathVariable int competitionId, @ModelAttribute RegisterUsersRequest users, HttpServletResponse response) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (users.getUserNames() == null) {
            competitionService.unregisterUsers(competitionId, Collections.singletonList(user.getUsername()));
        } else {
            if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                competitionService.unregisterUsers(competitionId, users.getUserNames());
            } else {
                throw new ForbiddenException("You must be an admin to do that!");
            }
        }
    }

    @RequestMapping(path = "/api/competitions", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody CompetitionResponse createCompetition(@Validated @ModelAttribute("newCompetition") CreateCompetitionRequest newCompetition) {
        return new CompetitionResponse(competitionService.createCompetition(newCompetition));
    }

    @RequestMapping(path = "/api/competitions/{competitionId}", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody CompetitionResponse updateCompetition(@PathVariable long competitionId, @ModelAttribute("updateCompetition") CreateCompetitionRequest competition) {
        return new CompetitionResponse(competitionService.updateCompetition(competitionId, competition));
    }

    @RequestMapping(path = "/api/competitions/{competitionId}", produces = "application/json", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCompetition(@PathVariable long competitionId) {
        competitionService.deleteCompetition(competitionId);
    }

    @RequestMapping(path = "/api/competitions", method = RequestMethod.GET)
    public @ResponseBody Map<String, List<Competition>> getAllCompetitions() {
        return competitionService.getAllCompetitions();
    }

    @RequestMapping(path = "/api/competitions/{cid}", method = RequestMethod.GET)
    public @ResponseBody CompetitionResponse getCompetition(@PathVariable long cid) {
        return new CompetitionResponse(competitionService.getCompetitionById(cid));
    }

    @RequestMapping(path = "/api/competitions/{cid}/teams", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody CompetitionTeamResponse getCompetitionTeams(@PathVariable long cid) {
        return new CompetitionTeamResponse().addTeams(competitionService.getTeams(cid));
    }

    @RequestMapping(path = "/api/competitions/{cid}/teams", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody CompetitionResponse updateCompetitionTeams(@PathVariable long cid, @RequestBody UpdateTeamsRequest competitionTeams) {
        return new CompetitionResponse(competitionService.updateCompetitionTeams(cid, competitionTeams));
    }

    @MessageMapping("/api/competitions/{cid}/teams")
    public void updateCompetitionTeamsSocket(@DestinationVariable long cid, UpdateTeamsRequest teams) {
        competitionService.updateCompetitionTeams(cid, teams);
    }

    @SubscribeMapping("/competitions/{cid}")
    @SendTo("/competitions/{cid}")
    public String initSubscriptionSocket(@DestinationVariable long cid) {
        JsonObject object = new JsonObject();
        object.add("eventType", new JsonPrimitive("systemTime"));
        JsonObject data = new JsonObject();
        data.add("systemTime", new JsonPrimitive(System.currentTimeMillis()));
        object.add("data", data);
        return object.toString();
    }
}
