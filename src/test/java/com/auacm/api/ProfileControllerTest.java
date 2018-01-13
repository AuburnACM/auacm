package com.auacm.api;

import com.auacm.Auacm;
import com.auacm.TestingConfig;
import com.auacm.api.model.CompetitionTeams;
import com.auacm.api.model.SimpleTeam;
import com.auacm.database.dao.BlogPostDao;
import com.auacm.database.dao.ProblemDao;
import com.auacm.database.dao.SubmissionDao;
import com.auacm.database.model.BlogPost;
import com.auacm.database.model.Submission;
import com.auacm.model.MockBlogPostBuilder;
import com.auacm.model.MockCompetitionBuilder;
import com.auacm.model.MockProblemBuilder;
import com.auacm.service.*;
import com.auacm.user.WithACMUser;
import com.google.gson.*;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Auacm.class, TestingConfig.class})
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = TestingConfig.class)
public class ProfileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @Autowired
    private UserService userService;

    @Autowired
    private FileSystemService systemService;

    @Autowired
    private BlogPostDao blogPostDao;

    @Autowired
    private ProblemService problemService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private SubmissionDao submissionDao;

    private HttpHeaders headers;

    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        return multipartResolver;
    }

    @Before
    public void setup() throws Exception {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        FileUtils.copyDirectory(new File("src/main/resources/data/"), new File("data/"));
    }

    @Test
    @WithACMUser(username = "user")
    public void getProfilePicture() throws Exception {
        byte[] data = mockMvc.perform(MockMvcRequestBuilders.get("/api/profile/user/image"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsByteArray();
        System.out.println(data.length);
    }

    @Test
    @WithACMUser(username = "user")
    public void setProfilePicture() throws Exception {
        byte[] data = getTestUser();
        mockMvc.perform(MockMvcRequestBuilders.post("/api/profile/user/image")
                .content(String.format("{\"data\": \"%s\"}", Base64.getEncoder().encodeToString(data)))
                .header("Content-Type", "application/json")).andExpect(MockMvcResultMatchers.status().isOk());
        Assert.assertEquals(true, systemService.doesFileExist("data/profile/user.png"));
    }

    @Test
    @WithACMUser(username = "user")
    public void setProfilePictureDifferent() throws Exception {
        byte[] data = getTestUser();
        mockMvc.perform(MockMvcRequestBuilders.post("/api/profile/admin/image")
                .content(String.format("{\"data\": \"%s\"}", Base64.getEncoder().encodeToString(data)))
                .header("Content-Type", "application/json"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void getProfile() throws Exception {
        configureProfile();
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/profile/user"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        JsonObject object = new JsonParser().parse(response).getAsJsonObject();
        Assert.assertNotNull(object);
        object = object.get("data").getAsJsonObject();
        Assert.assertNotNull(object);
        Assert.assertEquals(true, object.has("displayName"));
        Assert.assertEquals(true,  object.has("problemsSolved"));
        Assert.assertEquals(true, object.has("recentAttempts"));
        Assert.assertEquals(true, object.has("recentBlogPosts"));
        Assert.assertEquals(true, object.has("recentCompetitions"));
        Assert.assertEquals("User", object.get("displayName").getAsString());
        Assert.assertEquals(1, object.get("problemsSolved").getAsInt());
        JsonArray array = object.get("recentAttempts").getAsJsonArray();
        Assert.assertNotNull(array);
        for (JsonElement e : array) {
            JsonObject current = e.getAsJsonObject();
            Assert.assertEquals(true, current.has("name"));
            Assert.assertEquals(true, current.has("shortName"));
            Assert.assertEquals(true, current.has("pid"));
            Assert.assertEquals(true, current.has("submissionCount"));
            Assert.assertEquals(true, current.has("submissionIds"));
        }
        array = object.get("recentBlogPosts").getAsJsonArray();
        for (JsonElement e : array) {
            JsonObject current = e.getAsJsonObject();
            Assert.assertEquals(true, current.has("title"));
            Assert.assertEquals(true, current.has("subtitle"));
            Assert.assertEquals(true, current.has("postTime"));
            Assert.assertEquals(true, current.has("id"));
        }
        array = object.get("recentCompetitions").getAsJsonArray();
        for (JsonElement e : array) {
            JsonObject current = e.getAsJsonObject();
            Assert.assertEquals(true, current.has("cid"));
            Assert.assertEquals(true, current.has("compName"));
            Assert.assertEquals(true, current.has("teamName"));
            Assert.assertEquals(true, current.has("teamSize"));
        }
    }

    @Test
    public void getRecentCompetitions() throws Exception {
        configureProfile();
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/profile/user/competitions"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        JsonObject object = new JsonParser().parse(response).getAsJsonObject();
        Assert.assertNotNull(object);
        JsonArray array = object.get("data").getAsJsonArray();
        for (JsonElement e : array) {
            JsonObject current = e.getAsJsonObject();
            Assert.assertEquals(true, current.has("cid"));
            Assert.assertEquals(true, current.has("compName"));
            Assert.assertEquals(true, current.has("teamName"));
            Assert.assertEquals(true, current.has("teamSize"));
        }
    }

    @Test
    public void getRecentSubmits() throws Exception {
        configureProfile();
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/profile/user/submits"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        JsonObject object = new JsonParser().parse(response).getAsJsonObject();
        Assert.assertNotNull(object);
        JsonArray array = object.get("data").getAsJsonArray();
        for (JsonElement e : array) {
            JsonObject current = e.getAsJsonObject();
            Assert.assertEquals(true, current.has("name"));
            Assert.assertEquals(true, current.has("shortName"));
            Assert.assertEquals(true, current.has("pid"));
            Assert.assertEquals(true, current.has("submissionCount"));
            Assert.assertEquals(true, current.has("submissionIds"));
        }
    }

    @Test
    public void getRecentBlogPosts() throws Exception {
        configureProfile();
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/profile/user/blogs"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        JsonObject object = new JsonParser().parse(response).getAsJsonObject();
        Assert.assertNotNull(object);
        JsonArray array = object.get("data").getAsJsonArray();
        for (JsonElement e : array) {
            JsonObject current = e.getAsJsonObject();
            Assert.assertEquals(true, current.has("title"));
            Assert.assertEquals(true, current.has("subtitle"));
            Assert.assertEquals(true, current.has("postTime"));
            Assert.assertEquals(true, current.has("id"));
        }
    }

    @After
    public void cleanUp() throws Exception {
        FileUtils.deleteDirectory(new File("data/"));
    }

    private byte[] getTestUser() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        FileInputStream inputStream = new FileInputStream("src/test/resources/test.jpg");
        int size;
        byte[] buff = new byte[2048];
        while ((size = inputStream.read(buff)) >= 0) {
            outputStream.write(buff, 0, size);
        }
        inputStream.close();
        outputStream.close();
        return outputStream.toByteArray();
    }

    private void configureProfile() {
        blogPostDao.save(new BlogPost("Test Title", "Test Subtitle", "Test Body", "user"));
        blogPostDao.save(new BlogPost("Test Title", "Test Subtitle", "Test Body", "user"));
        problemService.createProblem(new MockProblemBuilder().build());
        problemService.createProblem(new MockProblemBuilder().build());
        competitionService.createCompetition(new MockCompetitionBuilder().addUser("user").build());
        Submission submission = new Submission();
        submission.setAutoId(false);
        submission.setFileType("java");
        submission.setPid(1);
        submission.setResult("good");
        submission.setShortName("testproblem");
        submission.setUsername("user");
        submission.setSubmitTime(500L);
        Submission submission1 = new Submission();
        submission1.setAutoId(false);
        submission1.setFileType("java");
        submission1.setPid(2);
        submission1.setResult("wrong");
        submission1.setShortName("testproblem1");
        submission1.setUsername("user");
        submission1.setSubmitTime(2000L);
        submissionDao.save(submission);
        submissionDao.save(submission1);
    }
}