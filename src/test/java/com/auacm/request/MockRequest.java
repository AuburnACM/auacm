package com.auacm.request;

import com.auacm.database.model.User;
import com.auacm.database.model.UserPrincipal;
import com.auacm.service.UserService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

public class MockRequest {
    public static MockHttpServletRequestBuilder getNewCompetition(String...exclude) {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/api/competitions");
        List<String> excludeParams = Arrays.asList(exclude);
        if (!excludeParams.contains("name")) {
            builder.param("name", "Test Competition");
        }
        if (!excludeParams.contains("startTime")) {
            builder.param("startTime", "100");
        }
        if (!excludeParams.contains("length")) {
            builder.param("length", "3600");
        }
        if (!excludeParams.contains("closed")) {
            builder.param("closed", "false");
        }
        if (!excludeParams.contains("problems")) {
            builder.param("problems", "1");
        }
        if (!excludeParams.contains("userNames")) {
            builder.param("userNames", "admin");
        }
        return builder;
    }

    public static MockHttpServletRequestBuilder registerCurrentUserCompetition(long competitionId) {
        return MockMvcRequestBuilders.post(String.format("/api/competitions/%d/register", competitionId));
    }

    public static MockHttpServletRequestBuilder registerUsersCompetition(long competitionId, String...userNames) {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(String.format("/api/competitions/%d/register", competitionId));
        for (String s : userNames) {
            builder.param("userNames", s);
        }
        return builder;
    }

    public static MockHttpServletRequestBuilder unregisterCurrentUserCompetition(long competitionId) {
        return MockMvcRequestBuilders.post(String.format("/api/competitions/%d/unregister", competitionId));
    }

    public static MockHttpServletRequestBuilder unregisterUsersCompetition(long competitionId, String...userNames) {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(String.format("/api/competitions/%d/unregister", competitionId));
        for (String s : userNames) {
            builder.param("userNames", s);
        }
        return builder;
    }

    public static MockHttpServletRequestBuilder getCreateProblemRequest(String...exclude) {
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
            builder.param("sampleCases", getProblemTestCases());
        }
        if (!excludeValues.contains("timeLimit")) {
            builder.param("timeLimit", "2");
        }
        if (!excludeValues.contains("difficulty")) {
            builder.param("difficulty", "50");
        }
        return builder;
    }

    public static String getProblemTestCases() {
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

    public static String getProblemTestCases2() {
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

    public static void setSecurityContext(UserService userService, String username) {
        User user = userService.getUser(username);
        SecurityContextHolder.getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(new UserPrincipal(user), "password"));
    }

}
