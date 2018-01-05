package com.auacm.api;

import com.auacm.Auacm;
import com.auacm.TestingConfig;
import com.auacm.database.dao.UserDao;
import com.auacm.database.model.Competition;
import com.auacm.database.model.CompetitionUser;
import com.auacm.exception.CompetitionNotFoundException;
import com.auacm.model.MockCompetitionBuilder;
import com.auacm.model.MockCompetitionTeamBuilder;
import com.auacm.model.MockProblemBuilder;
import com.auacm.request.MockRequest;
import com.auacm.service.*;
import com.auacm.user.WithACMUser;
import com.google.gson.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Auacm.class, TestingConfig.class})
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = TestingConfig.class)
public class CompetitionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private Gson gson;

    private HttpHeaders headers;

    @Autowired
    private UserService userService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private ProblemService problemService;

    @Autowired
    private FileSystemService fileSystemService;

    @Before
    public void setup() throws Exception {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    }

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Test
    @WithACMUser(username = "admin")
    public void registerCurrentUser() throws Exception {
        createNewCompetition();
        // Register for the competition
        mockMvc.perform(MockRequest.registerCurrentUserCompetition(1)).andExpect(MockMvcResultMatchers.status().isOk());
        Competition competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        Assert.assertEquals(1, competition.getCompetitionUsers().size());
        CompetitionUser user = competition.getCompetitionUsers().get(0);
        Assert.assertEquals("admin", user.getUsername());
    }

    @Test
    @WithACMUser(username = "user")
    public void registerCurrentUserClosed() throws Exception {
        // Create a problem and Competition
        problemService.createProblem(new MockProblemBuilder().build());
        competitionService.createCompetition(new MockCompetitionBuilder().setClosed(true).build());
        mockMvc.perform(MockRequest.registerCurrentUserCompetition(1)).andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void registerCurrentUserNotLoggedIn() throws Exception {
        // Register for the competition
        mockMvc.perform(MockRequest.registerCurrentUserCompetition(1)).andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithACMUser(username = "admin")
    public void registerOneUser() throws Exception {
        createNewCompetition();
        // Register for the competition
        mockMvc.perform(MockRequest.registerUsersCompetition(1, "user")).andExpect(MockMvcResultMatchers.status().isOk());
        Competition competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        Assert.assertEquals(1, competition.getCompetitionUsers().size());
        CompetitionUser user = competition.getCompetitionUsers().get(0);
        Assert.assertEquals("user", user.getUsername());
    }

    @Test
    @WithACMUser(username = "admin")
    public void registerMultipleUsers() throws Exception {
        createNewCompetition();
        mockMvc.perform(MockRequest.registerUsersCompetition(1, "admin", "user")).andExpect(MockMvcResultMatchers.status().isOk());
        Competition competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        Assert.assertEquals(2, competition.getCompetitionUsers().size());
    }

    @Test
    @WithACMUser(username = "user")
    public void registerMultipleUsersNotAdmin() throws Exception {
        createNewCompetition();
        mockMvc.perform(MockRequest.registerUsersCompetition(1, "admin", "user")).andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithACMUser(username = "admin")
    public void registerMultipleUsersMissing() throws Exception {
        createNewCompetition();
        mockMvc.perform(MockRequest.registerUsersCompetition(1, "admin", "user", "derp")).andExpect(MockMvcResultMatchers.status().isOk());
        Competition competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        Assert.assertEquals(2, competition.getCompetitionUsers().size());
    }

    @Test
    @WithACMUser(username = "user")
    public void unregisterCurrentUser() throws Exception {
        createNewCompetition();
        ArrayList<String> users = new ArrayList<>();
        users.add("user");
        competitionService.registerUsers(1, users);
        Competition competition = competitionService.getCompetitionById(1L);
        Assert.assertEquals(1, competition.getCompetitionUsers().size());
        mockMvc.perform(MockRequest.unregisterCurrentUserCompetition(1)).andExpect(MockMvcResultMatchers.status().isOk());
        competition = competitionService.getCompetitionById(1L);
        Assert.assertEquals(0, competition.getCompetitionUsers().size());
    }

    @Test
    public void unregisterCurrentUserNotLoggedIn() throws Exception {
        createNewCompetition();
        ArrayList<String> users = new ArrayList<>();
        users.add("user");
        competitionService.registerUsers(1, users);
        Competition competition = competitionService.getCompetitionById(1L);
        Assert.assertEquals(1, competition.getCompetitionUsers().size());
        mockMvc.perform(MockRequest.unregisterCurrentUserCompetition(1)).andExpect(MockMvcResultMatchers.status().isForbidden());
        competition = competitionService.getCompetitionById(1L);
        Assert.assertEquals(1, competition.getCompetitionUsers().size());
    }

    @Test
    @WithACMUser(username = "admin")
    public void unregisterMultipleUsers() throws Exception {
        // Create a competition
        createNewCompetition();
        registerUsers(1, "user", "admin");
        Competition competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        Assert.assertEquals(2, competition.getCompetitionUsers().size());
        // Register for the competition
        mockMvc.perform(MockRequest.unregisterUsersCompetition(1, "admin", "user")).andExpect(MockMvcResultMatchers.status().isOk());
        competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        Assert.assertEquals(0, competition.getCompetitionUsers().size());
    }

    @Test
    @WithACMUser(username = "user")
    public void unregisterMultipleUsersNotAdmin() throws Exception {
        // Create a competition
        createNewCompetition();
        registerUsers(1, "user", "admin");
        Competition competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        Assert.assertEquals(2, competition.getCompetitionUsers().size());
        // Register for the competition
        mockMvc.perform(MockRequest.unregisterUsersCompetition(1, "admin", "user")).andExpect(MockMvcResultMatchers.status().isForbidden());
        competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        Assert.assertEquals(2, competition.getCompetitionUsers().size());
    }

    @Test
    @WithACMUser(username = "admin")
    public void createCompetition() throws Exception {
        // Create a problem
        problemService.createProblem(new MockProblemBuilder().build());
        // Perform the request
        String content = mockMvc.perform(MockRequest.getNewCompetition())
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        System.out.println(content);
        // Check the data object
        JsonObject object = new JsonParser().parse(content).getAsJsonObject().get("data").getAsJsonObject();
        Assert.assertNotNull(object);
        Assert.assertEquals(true, object.has("competition"));
        Assert.assertEquals(true, object.has("compProblems"));
        Assert.assertEquals(true, object.has("teams"));

        // Check the competition object
        JsonObject competition = object.get("competition").getAsJsonObject();
        Assert.assertEquals(true, competition.has("cid"));
        Assert.assertEquals(true, competition.has("length"));
        Assert.assertEquals(true, competition.has("startTime"));
        Assert.assertEquals(true, competition.has("registered"));
        Assert.assertEquals(true, competition.has("name"));
        Assert.assertEquals(1, competition.get("cid").getAsInt());
        Assert.assertEquals(3600, competition.get("length").getAsInt());
        Assert.assertEquals(100, competition.get("startTime").getAsInt());
        Assert.assertEquals(true, competition.get("registered").getAsBoolean());
        Assert.assertEquals("Test Competition", competition.get("name").getAsString());

        // Check the competition problems
        JsonObject compProblems = object.get("compProblems").getAsJsonObject();
        Assert.assertNotNull(compProblems);
        Assert.assertEquals(1, compProblems.size());
        Assert.assertEquals(true, compProblems.has("A"));
        // Check the problem
        JsonObject aProblem = compProblems.get("A").getAsJsonObject();
        Assert.assertNotNull(aProblem);
        Assert.assertEquals(true, aProblem.has("name"));
        Assert.assertEquals(true, aProblem.has("pid"));
        Assert.assertEquals(true, aProblem.has("shortName"));
        Assert.assertEquals("Test Problem", aProblem.get("name").getAsString());
        Assert.assertEquals(1, aProblem.get("pid").getAsInt());
        Assert.assertEquals("testproblem", aProblem.get("shortName").getAsString());

        // Check the teams
        JsonArray teams = object.get("teams").getAsJsonArray();
        Assert.assertNotNull(teams);
        Assert.assertEquals(1, teams.size());
        JsonObject team = teams.get(0).getAsJsonObject();
        Assert.assertNotNull(team);
        Assert.assertEquals(true, team.has("displayNames"));
        Assert.assertEquals(true, team.has("name"));
        Assert.assertEquals(true, team.has("problemData"));
        Assert.assertEquals(true, team.has("users"));
        Assert.assertEquals(1, team.get("displayNames").getAsJsonArray().size());
        Assert.assertEquals("Admin", team.get("displayNames").getAsJsonArray().get(0).getAsString());
        Assert.assertEquals("Admin", team.get("name").getAsString());
        Assert.assertEquals(1, team.get("users").getAsJsonArray().size());
        Assert.assertEquals("admin", team.get("users").getAsJsonArray().get(0).getAsString());

        // Check team problem data
        JsonObject problemData = team.get("problemData").getAsJsonObject();
        Assert.assertNotNull(problemData);
        Assert.assertEquals(true, problemData.has("1"));
        JsonObject problem = problemData.get("1").getAsJsonObject();
        Assert.assertNotNull(problem);
        Assert.assertEquals(true, problem.has("label"));
        Assert.assertEquals(true, problem.has("status"));
        Assert.assertEquals("A", problem.get("label").getAsString());
        Assert.assertEquals("unattempted", problem.get("status").getAsString());
        Competition competition1 = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition1);
    }

    @Test
    @WithACMUser(username = "user")
    public void createCompetitionNotAdmin() throws Exception {
        mockMvc.perform(MockRequest.getNewCompetition()).andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithACMUser(username = "admin")
    public void createCompetitionMissingName() throws Exception {
        mockMvc.perform(MockRequest.getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockRequest.getNewCompetition("name")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithACMUser(username = "admin")
    public void createCompetitionMissingStartTime() throws Exception {
        mockMvc.perform(MockRequest.getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockRequest.getNewCompetition("startTime")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithACMUser(username = "admin")
    public void createCompetitionMissingLength() throws Exception {
        mockMvc.perform(MockRequest.getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockRequest.getNewCompetition("length")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithACMUser(username = "admin")
    public void createCompetitionMissingClosed() throws Exception {
        mockMvc.perform(MockRequest.getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockRequest.getNewCompetition("closed")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithACMUser(username = "admin")
    public void updateCompetitionName() throws Exception {
        createNewCompetition();
        Competition competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        Assert.assertEquals("Test Competition", competition.getName());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/competitions/1")
                .param("name", "Updated Competition Name")).andExpect(MockMvcResultMatchers.status().isOk());
        competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        Assert.assertEquals("Updated Competition Name", competition.getName());
    }

    @Test
    @WithACMUser(username = "admin")
    public void updateCompetitionStartTime() throws Exception {
        createNewCompetition();
        Competition competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        Assert.assertEquals(100, (long) competition.getStart());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/competitions/1")
                .param("startTime", "200")).andExpect(MockMvcResultMatchers.status().isOk());
        competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        Assert.assertEquals(200, (long) competition.getStart());
        Assert.assertEquals(3800, (long) competition.getStop());
    }

    @Test
    @WithACMUser(username = "admin")
    public void updateCompetitionLength() throws Exception {
        createNewCompetition();
        Competition competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        Assert.assertEquals(100, (long) competition.getStart());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/competitions/1")
                .param("length", "2000")).andExpect(MockMvcResultMatchers.status().isOk());
        competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        Assert.assertEquals(2100, (long) competition.getStop());
    }

    @Test
    @WithACMUser(username = "admin")
    public void updateCompetitionClosed() throws Exception {
        createNewCompetition();
        Competition competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        Assert.assertEquals(false, competition.isClosed());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/competitions/1")
                .param("closed", "true")).andExpect(MockMvcResultMatchers.status().isOk());
        competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        Assert.assertEquals(true, competition.isClosed());
    }

    @Test
    @WithACMUser(username = "admin")
    public void updateCompetitionAddUsers() throws Exception {
        createNewCompetition();
        Competition competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        Assert.assertEquals(0, competition.getCompetitionUsers().size());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/competitions/1")
                .param("userNames", "admin").param("userNames", "user"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        Assert.assertEquals(2, competition.getCompetitionUsers().size());
    }

    @Test
    @WithACMUser(username = "admin")
    public void updateCompetitionRemoveUsers() throws Exception {
        createNewCompetition();
        ArrayList<String> users = new ArrayList<>();
        users.add("admin");
        users.add("user");
        competitionService.registerUsers(1, users);
        Competition competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        Assert.assertEquals(2, competition.getCompetitionUsers().size());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/competitions/1")
                .param("userNames", "admin"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        Assert.assertEquals(1, competition.getCompetitionUsers().size());
    }

    @Test
    @WithACMUser(username = "admin")
    public void updateCompetitionAddProblems() throws Exception {
        createNewCompetition();
        problemService.createProblem(new MockProblemBuilder().build());
        Competition competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        Assert.assertEquals(1, competition.getCompetitionProblems().size());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/competitions/1")
                .param("problems", "1").param("problems", "2"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        Assert.assertEquals(2, competition.getCompetitionProblems().size());
    }

    @Test
    @WithACMUser(username = "admin")
    public void updateCompetitionRemoveProblems() throws Exception {
        problemService.createProblem(new MockProblemBuilder().build());
        problemService.createProblem(new MockProblemBuilder().build());
        competitionService.createCompetition(new MockCompetitionBuilder().addProblem(2).build());
        Competition competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        Assert.assertEquals(2, competition.getCompetitionProblems().size());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/competitions/1")
                .param("problems", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        Assert.assertEquals(1, competition.getCompetitionProblems().size());
    }

    @Test
    @WithACMUser(username = "admin")
    public void deleteCompetition() throws Exception {
        createNewCompetition();
        Competition competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/competitions/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        try {
            competition = competitionService.getCompetitionById(1L);
            Assert.assertNull(competition);
        } catch (CompetitionNotFoundException e) {
            competition = null;
            Assert.assertNull(competition);
        }
    }

    @Test
    @WithACMUser(username = "user")
    public void deleteCompetitionNotAdmin() throws Exception {
        createNewCompetition();
        Competition competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/competitions/1"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithACMUser(username = "admin")
    public void getCompetitionTeams() throws Exception {
        problemService.createProblem(new MockProblemBuilder().build());
        competitionService.createCompetition(new MockCompetitionBuilder().addUser("admin").build());
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/competitions/1/teams"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        System.out.println(response);
        JsonObject object = new JsonParser().parse(response).getAsJsonObject().get("data").getAsJsonObject();
        Assert.assertNotNull(object);
        Assert.assertEquals(true, object.has("Admin"));
        JsonArray adminArray = object.get("Admin").getAsJsonArray();
        Assert.assertNotNull(adminArray);
        Assert.assertEquals(1, adminArray.size());
        Assert.assertEquals(true, adminArray.get(0).getAsJsonObject().has("display"));
        Assert.assertEquals(true, adminArray.get(0).getAsJsonObject().has("username"));
        Assert.assertEquals("Admin", adminArray.get(0).getAsJsonObject().get("display").getAsString());
        Assert.assertEquals("admin", adminArray.get(0).getAsJsonObject().get("username").getAsString());
    }

    @Test
    @WithACMUser(username = "user")
    public void getCompetitionTeamsNotAdmin() throws Exception {
        problemService.createProblem(new MockProblemBuilder().build());
        competitionService.createCompetition(new MockCompetitionBuilder().addUser("admin").build());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/competitions/1/teams"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithACMUser(username = "admin")
    public void updateCompetitionTeamsRenameTeam() throws Exception {
        problemService.createProblem(new MockProblemBuilder().build());
        competitionService.createCompetition(new MockCompetitionBuilder().addUser("admin").build());
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/competitions/1/teams")
                .content("{\"teams\": {\"New Team Name\": [{\"display\": \"Admin\", \"username\": \"admin\"}]}}")
                .header("Content-Type", "application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        JsonObject object = new JsonParser().parse(response).getAsJsonObject().get("data").getAsJsonObject();
        Assert.assertNotNull(object);
        Assert.assertEquals(true, object.has("New Team Name"));
        Assert.assertEquals(1, object.get("New Team Name").getAsJsonArray().size());
    }

    @Test
    @WithACMUser(username = "admin")
    public void updateCompetitionTeamsOneTeam() throws Exception {
        problemService.createProblem(new MockProblemBuilder().build());
        competitionService.createCompetition(new MockCompetitionBuilder().addUser("admin").addUser("user").build());
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/competitions/1/teams")
                .content("{\"teams\": {\"New Team Name\": [{\"display\": \"Admin\", \"username\": \"admin\"}, {\"display\": \"User\", \"username\": \"user\"}]}}")
                .header("Content-Type", "application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        JsonObject object = new JsonParser().parse(response).getAsJsonObject().get("data").getAsJsonObject();
        Assert.assertNotNull(object);
        Assert.assertEquals(true, object.has("New Team Name"));
        Assert.assertEquals(2, object.get("New Team Name").getAsJsonArray().size());
    }

    @Test
    @WithACMUser(username = "admin")
    public void updateCompetitionTeamsRemoveUser() throws Exception {
        problemService.createProblem(new MockProblemBuilder().build());
        competitionService.createCompetition(new MockCompetitionBuilder().addUser("admin").addUser("user").build());
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/competitions/1/teams")
                .content("{\"teams\": {\"New Team Name\": [{\"display\": \"Admin\", \"username\": \"admin\"}]}}")
                .header("Content-Type", "application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        JsonObject object = new JsonParser().parse(response).getAsJsonObject().get("data").getAsJsonObject();
        Assert.assertNotNull(object);
        Assert.assertEquals(true, object.has("New Team Name"));
        Assert.assertEquals(1, object.size());
        Assert.assertEquals(1, object.get("New Team Name").getAsJsonArray().size());
    }

    @Test
    @WithACMUser(username = "admin")
    public void updateCompetitionTeamsSeparateTeams() throws Exception {
        problemService.createProblem(new MockProblemBuilder().build());
        competitionService.createCompetition(new MockCompetitionBuilder().addUser("admin").addUser("user").build());
        Competition competition = competitionService.updateCompetitionTeams(1, new MockCompetitionTeamBuilder()
                .addUser("admin", "Admin", "One Team")
                .addUser("user", "User", "One Team").build());
        for (CompetitionUser user : competition.getCompetitionUsers()) {
            Assert.assertEquals("One Team", user.getTeam());
        }
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/competitions/1/teams")
                .content("{\"teams\": {\"Team One\": [{\"display\": \"Admin\", \"username\": \"admin\"}], " +
                        "\"Team Two\": [{\"display\": \"User\", \"username\": \"user\"}]}}")
                .header("Content-Type", "application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        JsonObject object = new JsonParser().parse(response).getAsJsonObject().get("data").getAsJsonObject();
        Assert.assertNotNull(object);
        Assert.assertEquals(true, object.has("Team One"));
        Assert.assertEquals(true, object.has("Team Two"));
        Assert.assertEquals(2, object.size());
        Assert.assertEquals(1, object.get("Team One").getAsJsonArray().size());
        Assert.assertEquals(1, object.get("Team Two").getAsJsonArray().size());
    }

    @After
    public void cleanUp() throws Exception {
        fileSystemService.deleteFile("data");
    }

    private void createNewCompetition() {
        problemService.createProblem(new MockProblemBuilder().build());
        competitionService.createCompetition(new MockCompetitionBuilder().build());
    }

    private void registerUsers(long competitionId, String...userNames) {
        competitionService.registerUsers(competitionId, Arrays.asList(userNames));
    }
}