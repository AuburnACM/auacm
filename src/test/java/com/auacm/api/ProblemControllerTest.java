package com.auacm.api;

import com.auacm.Auacm;
import com.auacm.database.model.User;
import com.auacm.database.model.UserPrincipal;
import com.auacm.service.FileSystemService;
import com.auacm.service.UserService;
import com.google.gson.*;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Auacm.class)
@WebAppConfiguration
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProblemControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private Gson gson;

    private HttpHeaders headers;

    @Autowired
    private UserService userService;

    @Autowired
    private FileSystemService fileSystemService;

    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        return multipartResolver;
    }

    @Before
    public void setup() throws Exception {
        gson = new GsonBuilder().setPrettyPrinting().create();
        this.mockMvc = webAppContextSetup(webApplicationContext).apply(SecurityMockMvcConfigurers.springSecurity()).build();
        userService.createUser("Admin", "admin", "password", true);
        userService.createUser("User", "user", "password", false);
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    }

    @Test
    @WithMockUser(username = "admin")
    public void createProblem() throws Exception {
        setSecurityContext("admin");
        String response = mockMvc.perform(getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonObject object = new JsonParser().parse(response).getAsJsonObject().get("data").getAsJsonObject();
        Assert.assertEquals(true, object.has("added"));
        Assert.assertEquals(true, object.has("description"));
        Assert.assertEquals(true, object.has("difficulty"));
        Assert.assertEquals(true, object.has("inputDesc"));
        Assert.assertEquals(true, object.has("outputDesc"));
        Assert.assertEquals(true, object.has("name"));
        Assert.assertEquals(true, object.has("pid"));
        Assert.assertEquals(true, object.has("sampleCases"));
        Assert.assertEquals(true, object.has("shortName"));
        Assert.assertEquals(true, fileSystemService.doesFileExist("data/problems/1/in/"));
        Assert.assertEquals(true, fileSystemService.doesFileExist("data/problems/1/in/in1.txt"));
        Assert.assertEquals(true, fileSystemService.doesFileExist("data/problems/1/in/in2.txt"));
        Assert.assertEquals(true, fileSystemService.doesFileExist("data/problems/1/out/"));
        Assert.assertEquals(true, fileSystemService.doesFileExist("data/problems/1/out/out1.txt"));
        Assert.assertEquals(true, fileSystemService.doesFileExist("data/problems/1/out/out2.txt"));
        Assert.assertEquals(true, fileSystemService.doesFileExist("data/problems/1/test/"));
        Assert.assertEquals(true, fileSystemService.doesFileExist("data/problems/1/test/Solution.java"));
        Assert.assertEquals(true, fileSystemService.doesFileExist("data/problems/1/data.json"));
        Assert.assertEquals(true, fileSystemService.doesFileExist("data/problems/1/export.zip"));
    }

    @Test
    @WithMockUser(username = "admin")
    public void createProblemWithSameName() throws Exception {
        setSecurityContext("admin");
        mockMvc.perform(getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk());
        String response = mockMvc.perform(getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonObject object = new JsonParser().parse(response).getAsJsonObject().get("data").getAsJsonObject();
        Assert.assertEquals(true, object.has("shortName"));
        Assert.assertEquals("testproblem1", object.get("shortName").getAsString());
    }

    @Test
    @WithMockUser(username = "admin")
    public void createProblemWithInputZip() throws Exception {
        setSecurityContext("admin");
        String response = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/api/problems")
                .file(new MockMultipartFile("importZip", "export.zip", "application/zip", getExportBytes())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonObject object = new JsonParser().parse(response).getAsJsonObject().get("data").getAsJsonObject();
        Assert.assertEquals(true, object.has("added"));
        Assert.assertEquals(true, object.has("description"));
        Assert.assertEquals(true, object.has("difficulty"));
        Assert.assertEquals(true, object.has("inputDesc"));
        Assert.assertEquals(true, object.has("outputDesc"));
        Assert.assertEquals(true, object.has("name"));
        Assert.assertEquals(true, object.has("pid"));
        Assert.assertEquals(true, object.has("sampleCases"));
        Assert.assertEquals(true, object.has("shortName"));
        Assert.assertEquals("Test Problem", object.get("name").getAsString());
        Assert.assertEquals("Description for the problem.", object.get("description").getAsString());
        Assert.assertEquals("50", object.get("difficulty").getAsString());
        Assert.assertEquals("Some input", object.get("inputDesc").getAsString());
        Assert.assertEquals("Some output", object.get("outputDesc").getAsString());
        Assert.assertEquals(2, object.get("sampleCases").getAsJsonArray().size());
        Assert.assertEquals(2, object.get("timeLimit").getAsInt());
        Assert.assertEquals(true, fileSystemService.doesFileExist("data/problems/1/in/"));
        Assert.assertEquals(true, fileSystemService.doesFileExist("data/problems/1/in/in1.txt"));
        Assert.assertEquals(true, fileSystemService.doesFileExist("data/problems/1/in/in2.txt"));
        Assert.assertEquals(true, fileSystemService.doesFileExist("data/problems/1/out/"));
        Assert.assertEquals(true, fileSystemService.doesFileExist("data/problems/1/out/out1.txt"));
        Assert.assertEquals(true, fileSystemService.doesFileExist("data/problems/1/out/out2.txt"));
        Assert.assertEquals(true, fileSystemService.doesFileExist("data/problems/1/test/"));
        Assert.assertEquals(true, fileSystemService.doesFileExist("data/problems/1/test/Solution.java"));
        Assert.assertEquals(true, fileSystemService.doesFileExist("data/problems/1/data.json"));
        Assert.assertEquals(true, fileSystemService.doesFileExist("data/problems/1/export.zip"));
    }

    @Test
    @WithMockUser(username = "admin")
    public void createProblemMissingName() throws Exception {
        setSecurityContext("admin");
        mockMvc.perform(getCreateProblemRequest("name")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin")
    public void createProblemMissingDescription() throws Exception {
        setSecurityContext("admin");
        mockMvc.perform(getCreateProblemRequest("description")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin")
    public void createProblemMissingInputDescription() throws Exception {
        setSecurityContext("admin");
        mockMvc.perform(getCreateProblemRequest("inputDesc")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin")
    public void createProblemMissingOutputDescription() throws Exception {
        setSecurityContext("admin");
        mockMvc.perform(getCreateProblemRequest("outputDesc")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin")
    public void createProblemMissingInputFiles() throws Exception {
        setSecurityContext("admin");
        mockMvc.perform(getCreateProblemRequest("inputFiles")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin")
    public void createProblemMissingOutputFiles() throws Exception {
        setSecurityContext("admin");
        mockMvc.perform(getCreateProblemRequest("outputFiles")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin")
    public void createProblemMissingSolutionFile() throws Exception {
        setSecurityContext("admin");
        mockMvc.perform(getCreateProblemRequest("solutionFile")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin")
    public void createProblemMissingSampleCases() throws Exception {
        setSecurityContext("admin");
        mockMvc.perform(getCreateProblemRequest("sampleCases")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin")
    public void updateProblemDifficulty() throws Exception {
        setSecurityContext("admin");
        mockMvc.perform(getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk());
        String response = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/api/problems/1")
                .param("difficulty", "100")).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonObject object = new JsonParser().parse(response).getAsJsonObject().get("data").getAsJsonObject();
        Assert.assertEquals(true, object.has("difficulty"));
        Assert.assertEquals("100", object.get("difficulty").getAsString());
    }

    @Test
    @WithMockUser(username = "admin")
    public void updateProblemInputDesc() throws Exception {
        setSecurityContext("admin");
        mockMvc.perform(getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk());
        String response = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/api/problems/1")
                .param("inputDesc", "Updated input description")).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonObject object = new JsonParser().parse(response).getAsJsonObject().get("data").getAsJsonObject();
        Assert.assertEquals(true, object.has("inputDesc"));
        Assert.assertEquals("Updated input description", object.get("inputDesc").getAsString());
    }

    @Test
    @WithMockUser(username = "admin")
    public void updateProblemOutputDesc() throws Exception {
        setSecurityContext("admin");
        mockMvc.perform(getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk());
        String response = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/api/problems/1")
                .param("outputDesc", "Updated output description")).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonObject object = new JsonParser().parse(response).getAsJsonObject().get("data").getAsJsonObject();
        Assert.assertEquals(true, object.has("outputDesc"));
        Assert.assertEquals("Updated output description", object.get("outputDesc").getAsString());
    }

    @Test
    @WithMockUser(username = "admin")
    public void updateProblemName() throws Exception {
        setSecurityContext("admin");
        mockMvc.perform(getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk());
        String response = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/api/problems/1")
                .param("name", "New Test Problem")).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonObject object = new JsonParser().parse(response).getAsJsonObject().get("data").getAsJsonObject();
        Assert.assertEquals(true, object.has("name"));
        Assert.assertEquals(true, object.has("shortName"));
        Assert.assertEquals("New Test Problem", object.get("name").getAsString());
        Assert.assertEquals("newtestproblem", object.get("shortName").getAsString());
    }

    @Test
    @WithMockUser(username = "admin")
    public void updateProblemSampleCases() throws Exception {
        setSecurityContext("admin");
        mockMvc.perform(getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk());
        String response = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/api/problems/1")
                .param("sampleCases", getTestCases2())).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonObject object = new JsonParser().parse(response).getAsJsonObject().get("data").getAsJsonObject();
        Assert.assertEquals(true, object.has("sampleCases"));
        Assert.assertEquals(getTestCases2(), object.get("sampleCases").getAsJsonArray().toString());
    }

    @Test
    @WithMockUser(username = "admin")
    public void updateProblemInputFile() throws Exception {
        setSecurityContext("admin");
        mockMvc.perform(getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/api/problems/1")
                .file(new MockMultipartFile("inputFiles", "in1.txt", "text/plain",
                        "TestUpdate\nLine2Update\n".getBytes()))).andExpect(MockMvcResultMatchers.status().isOk());
        Assert.assertEquals(true, fileSystemService.doesFileExist("data/problems/1/in/in1.txt"));
    }

    @Test
    @WithMockUser(username = "admin")
    public void updateProblemOutputFile() throws Exception {
        setSecurityContext("admin");
        mockMvc.perform(getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/api/problems/1")
                .file(new MockMultipartFile("outputFiles", "out1.txt", "text/plain",
                        "TestOutputUpdate\nOutputLine2Update\n".getBytes()))).andExpect(MockMvcResultMatchers.status().isOk());
        Assert.assertEquals(true, fileSystemService.doesFileExist("data/problems/1/out/out1.txt"));
    }

    @Test
    @WithMockUser(username = "admin")
    public void updateProblemSolutionFile() throws Exception {
        setSecurityContext("admin");
        mockMvc.perform(getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/api/problems/1")
                .file(new MockMultipartFile("solutionFile", "Solution.java", "text/plain",
                        "TestSolutionUpdate\nSolutionUpdatedLine2\n".getBytes()))).andExpect(MockMvcResultMatchers.status().isOk());
        Assert.assertEquals(true, fileSystemService.doesFileExist("data/problems/1/test/Solution.java"));
    }

    @Test
    public void getProblems() throws Exception {
        setSecurityContext("admin");
        mockMvc.perform(getCreateProblemRequest())
                .andExpect(MockMvcResultMatchers.status().isOk());
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/problems"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        JsonArray object = new JsonParser().parse(response).getAsJsonObject().get("data").getAsJsonArray();
        Assert.assertEquals(1, object.size());
    }

    @Test
    @WithMockUser(username = "admin")
    public void getProblemByPid() throws Exception {
        setSecurityContext("admin");
        mockMvc.perform(getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk());
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/problems/1"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        JsonObject object = new JsonParser().parse(response).getAsJsonObject().get("data").getAsJsonObject();
        Assert.assertEquals(true, object.has("added"));
        Assert.assertEquals(true, object.has("description"));
        Assert.assertEquals(true, object.has("difficulty"));
        Assert.assertEquals(true, object.has("inputDesc"));
        Assert.assertEquals(true, object.has("outputDesc"));
        Assert.assertEquals(true, object.has("name"));
        Assert.assertEquals(true, object.has("pid"));
        Assert.assertEquals(true, object.has("sampleCases"));
        Assert.assertEquals(true, object.has("shortName"));
    }

    @Test
    @WithMockUser(username = "admin")
    public void getProblemByShortName() throws Exception {
        setSecurityContext("admin");
        mockMvc.perform(getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk());
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/problems/testproblem"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        JsonObject object = new JsonParser().parse(response).getAsJsonObject().get("data").getAsJsonObject();
        Assert.assertEquals(true, object.has("added"));
        Assert.assertEquals(true, object.has("description"));
        Assert.assertEquals(true, object.has("difficulty"));
        Assert.assertEquals(true, object.has("inputDesc"));
        Assert.assertEquals(true, object.has("outputDesc"));
        Assert.assertEquals(true, object.has("name"));
        Assert.assertEquals(true, object.has("pid"));
        Assert.assertEquals(true, object.has("sampleCases"));
        Assert.assertEquals(true, object.has("shortName"));
    }

    @After
    public void cleanUp() throws Exception {
        fileSystemService.deleteFile("data");
    }

    private MockHttpServletRequestBuilder getCreateProblemRequest(String...exclude) {
        List<String> excludeValues = Arrays.asList(exclude);
        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload("/api/problems");
        if (!excludeValues.contains("inputFiles")) {
            builder.file(new MockMultipartFile("inputFiles", "in1.txt",
                    "text/plain", "Test\nLine2\n".getBytes()))
                    .file(new MockMultipartFile("inputFiles", "in2.txt",
                            "text/plain", "Test2\nLine2\n".getBytes()));
        }
        if (!excludeValues.contains("outputFiles")) {
            builder.file(new MockMultipartFile("outputFiles", "out1.txt",
                    "text/plain", "TestOutput1\nLine2\n".getBytes()))
                    .file(new MockMultipartFile("outputFiles", "out2.txt",
                            "text/plain", "TestOutput2\nLine2\n".getBytes()));
        }
        if (!excludeValues.contains("solutionFile")) {
            builder.file(new MockMultipartFile("solutionFile", "Solution.java",
                    "text/plain", "TestSolution\nLine2\n".getBytes()));
        }
        if (!excludeValues.contains("name")) {
            builder.param("name", "Test Problem");
        }
        if (!excludeValues.contains("description")) {
            builder.param("description", "Description for the problem.");
        }
        if (!excludeValues.contains("inputDesc")) {
            builder.param("inputDesc", "Some input");
        }
        if (!excludeValues.contains("outputDesc")) {
            builder.param("outputDesc", "Some output");
        }
        if (!excludeValues.contains("sampleCases")) {
            builder.param("sampleCases", getTestCases());
        }
        if (!excludeValues.contains("timeLimit")) {
            builder.param("timeLimit", "2");
        }
        if (!excludeValues.contains("difficulty")) {
            builder.param("difficulty", "50");
        }
        return builder;
    }

    private String getTestCases() {
        JsonArray array = new JsonArray();
        for (int i = 0; i < 2; i++) {
            JsonObject object = new JsonObject();
            object.add("caseNum", new JsonPrimitive(i + 1));
            object.add("input", new JsonPrimitive("Sample Input"));
            object.add("output", new JsonPrimitive("Sample Output"));
            array.add(object);
        }
        return array.toString();
    }

    private String getTestCases2() {
        JsonArray array = new JsonArray();
        for (int i = 0; i < 2; i++) {
            JsonObject object = new JsonObject();
            object.add("caseNum", new JsonPrimitive(i + 1));
            object.add("input", new JsonPrimitive("Sample Input 2"));
            object.add("output", new JsonPrimitive("Sample Output 2"));
            array.add(object);
        }
        return array.toString();
    }

    private void setSecurityContext(String username) {
        User user = userService.getUser(username);
        SecurityContextHolder.getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(new UserPrincipal(user), "password"));
    }

    private byte[] getExportBytes() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            FileInputStream in = new FileInputStream("src/test/resources/export.zip");
            int size = 0;
            byte[] buff = new byte[1024];
            while ((size = in.read(buff)) >= 0) {
                out.write(buff, 0, size);
            }
            in.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}