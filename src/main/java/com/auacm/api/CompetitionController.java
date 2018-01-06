package com.auacm.api;

import com.auacm.api.model.CompetitionTeams;
import com.auacm.api.model.CreateCompetition;
import com.auacm.api.model.RegisterUsers;
import com.auacm.api.model.SimpleTeam;
import com.auacm.api.proto.CompetitionOuterClass;
import com.auacm.api.validator.CreateCompetitionValidator;
import com.auacm.database.model.User;
import com.auacm.database.model.UserPrincipal;
import com.auacm.exception.ForbiddenException;
import com.auacm.service.CompetitionService;
import com.auacm.util.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
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
    public @ResponseBody void register(@PathVariable long competitionId, @ModelAttribute RegisterUsers users, HttpServletResponse response) {
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
    public @ResponseBody void unregister(@PathVariable int competitionId, @ModelAttribute RegisterUsers users, HttpServletResponse response) {
        User user = ((UserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
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
    public @ResponseBody
    CompetitionOuterClass.SingleCompetitionWrapper
    createCompetition(@Validated @ModelAttribute("newCompetition") CreateCompetition newCompetition) {
        return competitionService.getCompetitionResponse(competitionService.createCompetition(newCompetition));
    }

    @RequestMapping(path = "/api/competitions/{competitionId}", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody
    CompetitionOuterClass.SingleCompetitionWrapper updateCompetition(@PathVariable long competitionId, @ModelAttribute("updateCompetition") CreateCompetition competition) {
        return competitionService.getCompetitionResponse(competitionService.updateCompetition(competitionId, competition));
    }

    @RequestMapping(path = "/api/competitions/{competitionId}", produces = "application/json", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCompetition(@PathVariable long competitionId) {
        competitionService.deleteCompetition(competitionId);
    }

    @RequestMapping(path = "/api/competitions", method = RequestMethod.GET)
    public @ResponseBody
    CompetitionOuterClass.CompetitionListWrapper getAllCompetitions() {
        return competitionService.getCompetitionListResponse(competitionService.getAllCompetitions());
    }

    @RequestMapping(path = "/api/competitions/{cid}", method = RequestMethod.GET)
    public @ResponseBody
    CompetitionOuterClass.SingleCompetitionWrapper getCompetition(@PathVariable long cid) {
        return competitionService.getCompetitionResponse(competitionService.getCompetitionById(cid));
    }

    @RequestMapping(path = "/api/competitions/{cid}/teams", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody CompetitionOuterClass.TeamList getCompetitionTeams(@PathVariable long cid) {
        return competitionService.getTeamListResponse(competitionService.getCompetitionById(cid));
    }

    @RequestMapping(path = "/api/competitions/{cid}/teams", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody CompetitionOuterClass.TeamList updateCompetitionTeams(@PathVariable long cid, @RequestBody CompetitionTeams competitionTeams) {
        return competitionService.getTeamListResponse(competitionService.updateCompetitionTeams(cid, competitionTeams));
    }

    @MessageMapping("/competitions/{cid}/teams")
    public void updateCompetitionTeamsSocket(@DestinationVariable long cid, CompetitionTeams teams) {
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
