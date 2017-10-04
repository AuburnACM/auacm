package com.auacm.api;

import com.auacm.Auacm;
import com.auacm.database.model.User;
import com.auacm.database.model.UserPrincipal;
import com.auacm.database.service.BlogPostService;
import com.auacm.database.service.SolvedProblemService;
import com.auacm.database.service.UserService;
import com.google.gson.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Auacm.class)
@WebAppConfiguration
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private Gson gson;

    private HttpHeaders headers;

    @Autowired
    private UserService userService;

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
    public void login() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                .param("username", "admin").param("password", "password"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void loginInvalidPassword() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                .param("username", "admin").param("password", "wrong_password"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void loginInvalidUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                .param("username", "wrong_user").param("password", "password"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user")
    public void logout() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/logout"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "admin")
    public void me() throws Exception {
        setSecurityContext("admin");
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/me"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        JsonObject object = new JsonParser().parse(response).getAsJsonObject().get("data").getAsJsonObject();
        Assert.assertNotNull(object);
        Assert.assertEquals(true, object.has("username"));
        Assert.assertEquals(true, object.has("displayName"));
        Assert.assertEquals(true, object.has("isAdmin"));
        Assert.assertEquals(true, object.has("permissions"));
        Assert.assertEquals("admin", object.get("username").getAsString());
        Assert.assertEquals("Admin", object.get("displayName").getAsString());
        Assert.assertEquals(true, object.get("isAdmin").getAsBoolean());

        JsonArray perms = object.get("permissions").getAsJsonArray();
        Assert.assertEquals(true, perms.contains(new JsonPrimitive("ROLE_ADMIN")));
    }

    @Test
    @WithMockUser(username = "user")
    public void meNonAdmin() throws Exception {
        setSecurityContext("user");
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/me"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        JsonObject object = new JsonParser().parse(response).getAsJsonObject().get("data").getAsJsonObject();
        Assert.assertNotNull(object);
        Assert.assertEquals(true, object.has("username"));
        Assert.assertEquals(true, object.has("displayName"));
        Assert.assertEquals(false, object.has("isAdmin"));
        Assert.assertEquals(true, object.has("permissions"));
        Assert.assertEquals("user", object.get("username").getAsString());
        Assert.assertEquals("User", object.get("displayName").getAsString());

        JsonArray perms = object.get("permissions").getAsJsonArray();
        Assert.assertEquals(true, perms.contains(new JsonPrimitive("ROLE_USER")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void createUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/create_user")
                .param("username", "test").param("password", "password")
                .param("display", "Test User").param("admin", "true").headers(headers))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "user")
    public void createUserNotAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/create_user")
                .param("username", "test").param("password", "password")
                .param("display", "Test User").param("admin", "true").headers(headers))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void createUserNotLoggedIn() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/create_user")
                .param("username", "test").param("password", "password")
                .param("display", "Test User").param("admin", "true").headers(headers))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user")
    public void changePassword() throws Exception {
        setSecurityContext("user");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/change_password")
            .param("oldPassword", "password").param("newPassword", "password1").headers(headers))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Assert.assertEquals(true, userService.validatePassword("user", "password1"));
    }

    @Test
    @WithMockUser(username = "user")
    public void changePasswordInvalidOldPassword() throws Exception {
        setSecurityContext("user");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/change_password")
                .param("oldPassword", "invalid_password").param("newPassword", "password1").headers(headers))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user")
    public void updateUserDisplayName() throws Exception {
        setSecurityContext("user");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/update_user")
                .param("display", "New Display").headers(headers))
                .andExpect(MockMvcResultMatchers.status().isOk());
        User user = userService.getUser("user");
        Assert.assertNotNull(user);
        Assert.assertEquals("New Display", user.getDisplay());
    }

    @Test
    public void getRanksAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/ranking"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private void setSecurityContext(String username) {
        User user = userService.getUser(username);
        SecurityContextHolder.getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(new UserPrincipal(user), "password"));
    }
}