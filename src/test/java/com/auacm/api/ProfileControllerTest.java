package com.auacm.api;

import com.auacm.Auacm;
import com.auacm.TestingConfig;
import com.auacm.service.FileSystemService;
import com.auacm.service.UserService;
import com.auacm.user.WithACMUser;
import com.google.gson.Gson;
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
        byte[] data = mockMvc.perform(MockMvcRequestBuilders.get("/api/profile/image/user"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsByteArray();
        System.out.println(data.length);
    }

    @Test
    @WithACMUser(username = "user")
    public void setProfilePicture() throws Exception {
        byte[] data = getTestUser();
        mockMvc.perform(MockMvcRequestBuilders.post("/api/profile/image/user")
                .content(String.format("{\"data\": \"%s\"}", Base64.getEncoder().encodeToString(data)))
                .header("Content-Type", "application/json")).andExpect(MockMvcResultMatchers.status().isOk());
        Assert.assertEquals(true, systemService.doesFileExist("data/profile/user.png"));
    }

    @Test
    @WithACMUser(username = "user")
    public void setProfilePictureDifferent() throws Exception {
        byte[] data = getTestUser();
        mockMvc.perform(MockMvcRequestBuilders.post("/api/profile/image/admin")
                .content(String.format("{\"data\": \"%s\"}", Base64.getEncoder().encodeToString(data)))
                .header("Content-Type", "application/json"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
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
}