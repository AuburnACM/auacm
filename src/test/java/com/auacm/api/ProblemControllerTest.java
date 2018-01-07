package com.auacm.api;

import com.auacm.Auacm;
import com.auacm.TestingConfig;
import com.auacm.database.model.Problem;
import com.auacm.exception.ProblemNotFoundException;
import com.auacm.model.MockProblemBuilder;
import com.auacm.request.MockRequest;
import com.auacm.service.FileSystemService;
import com.auacm.service.ProblemService;
import com.auacm.service.UserService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import org.springframework.security.test.context.support.WithMockUser;
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
import java.io.FileInputStream;
import java.io.IOException;

import static com.auacm.request.MockRequest.getCreateProblemRequest;
import static com.auacm.request.MockRequest.getProblemTestCases2;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Auacm.class, TestingConfig.class})
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = TestingConfig.class)
public class ProblemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    private HttpHeaders headers;

    @Autowired
    private UserService userService;

    @Autowired
    private FileSystemService fileSystemService;

    @Autowired
    private ProblemService problemService;

    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        return multipartResolver;
    }

    @Before
    public void setup() throws Exception {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    }

    @Test
    @WithMockUser(username = "admin")
    public void createProblem() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
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
        MockRequest.setSecurityContext(userService, "admin");
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
        MockRequest.setSecurityContext(userService, "admin");
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
        MockRequest.setSecurityContext(userService, "admin");
        mockMvc.perform(getCreateProblemRequest("name")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin")
    public void createProblemMissingDescription() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
        mockMvc.perform(getCreateProblemRequest("description")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin")
    public void createProblemMissingInputDescription() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
        mockMvc.perform(getCreateProblemRequest("inputDesc")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin")
    public void createProblemMissingOutputDescription() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
        mockMvc.perform(getCreateProblemRequest("outputDesc")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin")
    public void createProblemMissingInputFiles() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
        mockMvc.perform(getCreateProblemRequest("inputFiles")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin")
    public void createProblemMissingOutputFiles() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
        mockMvc.perform(getCreateProblemRequest("outputFiles")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin")
    public void createProblemMissingSolutionFile() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
        mockMvc.perform(getCreateProblemRequest("solutionFile")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin")
    public void createProblemMissingSampleCases() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
        mockMvc.perform(getCreateProblemRequest("sampleCases")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin")
    public void updateProblemDifficulty() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
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
        MockRequest.setSecurityContext(userService, "admin");
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
        MockRequest.setSecurityContext(userService, "admin");
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
        MockRequest.setSecurityContext(userService, "admin");
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
        MockRequest.setSecurityContext(userService, "admin");
        mockMvc.perform(getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk());
        String response = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/api/problems/1")
                .param("sampleCases", getProblemTestCases2())).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonObject object = new JsonParser().parse(response).getAsJsonObject().get("data").getAsJsonObject();
        Assert.assertEquals(true, object.has("sampleCases"));
        Assert.assertEquals(getProblemTestCases2(), object.get("sampleCases").getAsJsonArray().toString());
    }

    @Test
    @WithMockUser(username = "admin")
    public void updateProblemInputFile() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
        mockMvc.perform(getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/api/problems/1")
                .file(new MockMultipartFile("inputFiles", "in1.txt", "text/plain",
                        "TestUpdate\nLine2Update\n".getBytes()))).andExpect(MockMvcResultMatchers.status().isOk());
        Assert.assertEquals(true, fileSystemService.doesFileExist("data/problems/1/in/in1.txt"));
    }

    @Test
    @WithMockUser(username = "admin")
    public void updateProblemOutputFile() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
        mockMvc.perform(getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/api/problems/1")
                .file(new MockMultipartFile("outputFiles", "out1.txt", "text/plain",
                        "TestOutputUpdate\nOutputLine2Update\n".getBytes()))).andExpect(MockMvcResultMatchers.status().isOk());
        Assert.assertEquals(true, fileSystemService.doesFileExist("data/problems/1/out/out1.txt"));
    }

    @Test
    @WithMockUser(username = "admin")
    public void updateProblemSolutionFile() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
        mockMvc.perform(getCreateProblemRequest()).andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/api/problems/1")
                .file(new MockMultipartFile("solutionFile", "Solution.java", "text/plain",
                        "TestSolutionUpdate\nSolutionUpdatedLine2\n".getBytes()))).andExpect(MockMvcResultMatchers.status().isOk());
        Assert.assertEquals(true, fileSystemService.doesFileExist("data/problems/1/test/Solution.java"));
    }

    @Test
    public void getProblems() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
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
        MockRequest.setSecurityContext(userService, "admin");
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
        MockRequest.setSecurityContext(userService, "admin");
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

    @Test
    @WithMockUser(username = "admin")
    public void deleteProblem() throws Exception {
        MockRequest.setSecurityContext(userService, "admin");
        problemService.createProblem(new MockProblemBuilder().build());
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/problems/1")).andExpect(MockMvcResultMatchers.status().isOk());
        Problem problem = null;
        try {
            problem = problemService.getProblemForPid(1);
            Assert.assertNull(problem);
        } catch (ProblemNotFoundException e) {
            Assert.assertNull(problem);
        }
    }

    @Test
    @WithMockUser(username = "user")
    public void deleteProblemNotAdmin() throws Exception {
        MockRequest.setSecurityContext(userService, "user");
        problemService.createProblem(new MockProblemBuilder().build());
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/problems/1")).andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @After
    public void cleanUp() throws Exception {
        fileSystemService.deleteFile("data");
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