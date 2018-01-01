package com.auacm.api;

import com.auacm.database.model.Competition;
import com.auacm.database.model.CompetitionUser;
import com.auacm.exception.CompetitionNotFoundException;
import com.auacm.model.MockCompetitionBuilder;
import com.auacm.model.MockProblemBuilder;
import com.auacm.request.MockRequest;
import com.auacm.service.CompetitionService;
import com.auacm.service.ProblemService;
import com.auacm.service.UserService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
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
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CompetitionControllerTest {

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private Gson gson;

    private HttpHeaders headers;

    @Autowired
    private UserService userService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private ProblemService problemService;

    @Before
    public void setup() throws Exception {
        gson = new GsonBuilder().setPrettyPrinting().create();
        this.mockMvc = webAppContextSetup(webApplicationContext).apply(SecurityMockMvcConfigurers.springSecurity()).build();
        userService.createUser("Admin", "admin", "password", true);
        userService.createUser("User", "user", "password", false);
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
    @WithMockUser(username = "admin")
    public void registerCurrentUser() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
        // Create a problem
        mockMvc.perform(MockRequest.getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk());
        // Create a competition
        mockMvc.perform(MockRequest.getNewCompetition("userNames")).andExpect(MockMvcResultMatchers.status().isOk());
        // Register for the competition
        mockMvc.perform(MockRequest.registerCurrentUserCompetition(1)).andExpect(MockMvcResultMatchers.status().isOk());
        Competition competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        Assert.assertEquals(1, competition.getCompetitionUsers().size());
        CompetitionUser user = competition.getCompetitionUsers().get(0);
        Assert.assertEquals("admin", user.getUsername());
    }

    @Test
    public void registerCurrentUserNotLoggedIn() throws Exception {
        // Register for the competition
        mockMvc.perform(MockRequest.registerCurrentUserCompetition(1)).andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin")
    public void registerOneUser() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
        // Create a problem
        mockMvc.perform(MockRequest.getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk());
        // Create a competition
        mockMvc.perform(MockRequest.getNewCompetition("userNames")).andExpect(MockMvcResultMatchers.status().isOk());
        // Register for the competition
        mockMvc.perform(MockRequest.registerUsersCompetition(1, "user")).andExpect(MockMvcResultMatchers.status().isOk());
        Competition competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        Assert.assertEquals(1, competition.getCompetitionUsers().size());
        CompetitionUser user = competition.getCompetitionUsers().get(0);
        Assert.assertEquals("user", user.getUsername());
    }

    @Test
    @WithMockUser(username = "admin")
    public void registerMultipleUsers() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
        // Create a problem
        mockMvc.perform(MockRequest.getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk());
        // Create a competition
        mockMvc.perform(MockRequest.getNewCompetition("userNames")).andExpect(MockMvcResultMatchers.status().isOk());
        // Register for the competition
        mockMvc.perform(MockRequest.registerUsersCompetition(1, "admin", "user")).andExpect(MockMvcResultMatchers.status().isOk());
        Competition competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        Assert.assertEquals(2, competition.getCompetitionUsers().size());
    }

    @Test
    @WithMockUser(username = "user")
    public void registerMultipleUsersNotAdmin() throws Exception {
        MockRequest.setSecurityContext(userService, "user");
        // Create the competitions
        createNewCompetition();
        // Register for the competition
        mockMvc.perform(MockRequest.registerUsersCompetition(1, "admin", "user")).andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin")
    public void registerMultipleUsersMissing() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
        // Create a problem
        mockMvc.perform(MockRequest.getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk());
        // Create a competition
        mockMvc.perform(MockRequest.getNewCompetition("userNames")).andExpect(MockMvcResultMatchers.status().isOk());
        // Register for the competition
        mockMvc.perform(MockRequest.registerUsersCompetition(1, "admin", "user", "derp")).andExpect(MockMvcResultMatchers.status().isOk());
        Competition competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        Assert.assertEquals(2, competition.getCompetitionUsers().size());
    }

    @Test
    @WithMockUser(username = "user")
    public void unregisterCurrentUser() throws Exception {
        MockRequest.setSecurityContext(userService, "user");
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
    @WithMockUser(username = "admin")
    public void unregisterMultipleUsers() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
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
    @WithMockUser(username = "user")
    public void unregisterMultipleUsersNotAdmin() throws Exception {
        MockRequest.setSecurityContext(userService, "user");
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
    @WithMockUser(username = "admin")
    public void createCompetition() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
        mockMvc.perform(MockRequest.getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk());
        String content = mockMvc.perform(MockRequest.getNewCompetition())
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        System.out.println(content);
        JsonObject object = new JsonParser().parse(content).getAsJsonObject().get("data").getAsJsonObject();
        Assert.assertNotNull(object);
        Assert.assertEquals(true, object.has("competition"));
        JsonObject competition = object.get("competition").getAsJsonObject();
        Assert.assertEquals(true, competition.has("cid"));
        Assert.assertEquals(true, competition.has("length"));
        Assert.assertEquals(true, competition.has("startTime"));
        Assert.assertEquals(true, competition.has("registered"));
        Assert.assertEquals(1, competition.get("cid").getAsInt());
        Assert.assertEquals(3600, competition.get("length").getAsInt());
        Assert.assertEquals(100, competition.get("startTime").getAsInt());
        Assert.assertEquals(true, competition.get("registered").getAsBoolean());
        Competition competition1 = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition1);
    }

    @Test
    @WithMockUser(username = "user")
    public void createCompetitionNotAdmin() throws Exception {
        MockRequest.setSecurityContext(userService, "user");
        mockMvc.perform(MockRequest.getNewCompetition()).andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin")
    public void createCompetitionMissingName() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
        mockMvc.perform(MockRequest.getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockRequest.getNewCompetition("name")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin")
    public void createCompetitionMissingStartTime() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
        mockMvc.perform(MockRequest.getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockRequest.getNewCompetition("startTime")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin")
    public void createCompetitionMissingLength() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
        mockMvc.perform(MockRequest.getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockRequest.getNewCompetition("length")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin")
    public void createCompetitionMissingClosed() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
        mockMvc.perform(MockRequest.getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockRequest.getNewCompetition("closed")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin")
    public void updateCompetitionName() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
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
    @WithMockUser(username = "admin")
    public void updateCompetitionStartTime() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
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
    @WithMockUser(username = "admin")
    public void updateCompetitionLength() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
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
    @WithMockUser(username = "admin")
    public void updateCompetitionClosed() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
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
    @WithMockUser(username = "admin")
    public void updateCompetitionAddUsers() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
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
    @WithMockUser(username = "admin")
    public void updateCompetitionRemoveUsers() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
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
    @WithMockUser(username = "admin")
    public void updateCompetitionAddProblems() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
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
    @WithMockUser(username = "admin")
    public void updateCompetitionRemoveProblems() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
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
    @WithMockUser(username = "admin")
    public void deleteCompetition() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
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
    @WithMockUser(username = "user")
    public void deleteCompetitionNotAdmin() throws Exception {
        MockRequest.setSecurityContext(userService, "user");
        createNewCompetition();
        Competition competition = competitionService.getCompetitionById(1L);
        Assert.assertNotNull(competition);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/competitions/1"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    private void createNewCompetition() {
        problemService.createProblem(new MockProblemBuilder().build());
        competitionService.createCompetition(new MockCompetitionBuilder().build());
    }

    private void registerUsers(long competitionId, String...userNames) {
        competitionService.registerUsers(competitionId, Arrays.asList(userNames));
    }
}