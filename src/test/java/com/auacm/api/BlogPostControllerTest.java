package com.auacm.api;

import com.auacm.Auacm;
import com.auacm.database.model.BlogPost;
import com.auacm.database.model.User;
import com.auacm.database.model.UserPrincipal;
import com.auacm.database.service.BlogPostService;
import com.auacm.database.service.UserService;
import com.auacm.exception.UserException;
import com.google.gson.*;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Auacm.class)
@WebAppConfiguration
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BlogPostControllerTest {

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private Gson gson;

    private HttpHeaders headers;

    @Before
    public void setup() throws Exception {
        gson = new GsonBuilder().setPrettyPrinting().create();
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        userService.createUser("Admin", "admin", "password", true);
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        User user = userService.getUser("admin");
        UserPrincipal principal = new UserPrincipal(user);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal,
                "password", principal.getAuthorities()));
    }

    @Autowired
    private BlogPostService blogPostService;

    @Autowired
    private UserService userService;

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
    public void createBlogPostValid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/blog")
                .param("title", "Test").param("subtitle", "Test").param("body", "the body")
                .headers(headers))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        BlogPost post = blogPostService.getBlogPostForId(1);
        Assert.assertNotNull(post);
        Assert.assertNotNull(post.getTitle());
        Assert.assertNotNull(post.getSubtitle());
        Assert.assertNotNull(post.getBody());
        Assert.assertNotNull(post.getUsername());
        Assert.assertEquals("Test", post.getTitle());
        Assert.assertEquals("Test", post.getSubtitle());
        Assert.assertEquals("the body", post.getBody());
        Assert.assertEquals("admin", post.getUsername());
    }

    @Test
    public void createBlogPostMissingTitle() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/blog")
                .param("subtitle", "Test").param("body", "the body")
                .headers(headers))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void createBlogPostMissingSubtitle() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/blog")
                .param("title", "Test").param("body", "the body")
                .headers(headers))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void createBlogPostMissingBody() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/blog")
                .param("title", "Test").param("subtitle", "Test")
                .headers(headers))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void getBlogPosts() throws Exception {
        blogPostService.addBlogPost("Title 1", "Subtitle 1", "Body 1", "admin");
        blogPostService.addBlogPost("Title 2", "Subtitle 2", "Body 2", "admin");
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/blog")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

        JsonArray data = new JsonParser().parse(response).getAsJsonObject().get("data").getAsJsonArray();
        Assert.assertNotNull(data);
        Assert.assertEquals(2, data.size());
        JsonObject object = data.get(0).getAsJsonObject();
        Assert.assertEquals(true, object.has("title"));
        Assert.assertEquals(true, object.has("subtitle"));
        Assert.assertEquals(true, object.has("body"));

        Assert.assertEquals("Title 2", object.get("title").getAsString());
        Assert.assertEquals("Subtitle 2", object.get("subtitle").getAsString());
        Assert.assertEquals("Body 2", object.get("body").getAsString());

        object = data.get(1).getAsJsonObject();
        Assert.assertEquals(true, object.has("title"));
        Assert.assertEquals(true, object.has("subtitle"));
        Assert.assertEquals(true, object.has("body"));

        Assert.assertEquals("Title 1", object.get("title").getAsString());
        Assert.assertEquals("Subtitle 1", object.get("subtitle").getAsString());
        Assert.assertEquals("Body 1", object.get("body").getAsString());
    }

    @Test
    public void getBlogPost() throws Exception {
        blogPostService.addBlogPost("Title 1", "Subtitle 1", "Body 1", "admin");
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/blog/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        JsonObject object = new JsonParser().parse(response).getAsJsonObject().get("data").getAsJsonObject();
        Assert.assertNotNull(object);
        Assert.assertEquals(true, object.has("title"));
        Assert.assertEquals(true, object.has("subtitle"));
        Assert.assertEquals(true, object.has("body"));

        Assert.assertEquals("Title 1", object.get("title").getAsString());
        Assert.assertEquals("Subtitle 1", object.get("subtitle").getAsString());
        Assert.assertEquals("Body 1", object.get("body").getAsString());
    }

    @Test
    public void getBlogPostNotExistent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/blog/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void updateBlogPostTitle() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        blogPostService.addBlogPost("Title 1", "Subtitle 1", "Body 1", "admin");
        mockMvc.perform(MockMvcRequestBuilders.put("/api/blog/1")
                .param("title", "New Title 1").headers(headers))
                .andExpect(MockMvcResultMatchers.status().isOk());
        BlogPost post = blogPostService.getBlogPostForId(1);
        Assert.assertNotNull(post);
        Assert.assertNotNull(post.getTitle());
        Assert.assertEquals("New Title 1", post.getTitle());
    }

    @Test
    public void updateBlogPostSubtitle() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        blogPostService.addBlogPost("Title 1", "Subtitle 1", "Body 1", "admin");
        mockMvc.perform(MockMvcRequestBuilders.put("/api/blog/1")
                .param("subtitle", "New Subtitle 1").headers(headers))
                .andExpect(MockMvcResultMatchers.status().isOk());
        BlogPost post = blogPostService.getBlogPostForId(1);
        Assert.assertNotNull(post);
        Assert.assertNotNull(post.getSubtitle());
        Assert.assertEquals("New Subtitle 1", post.getSubtitle());
    }

    @Test
    public void updateBlogPostBody() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        blogPostService.addBlogPost("Title 1", "Subtitle 1", "Body 1", "admin");
        mockMvc.perform(MockMvcRequestBuilders.put("/api/blog/1")
                .param("body", "New Body 1").headers(headers))
                .andExpect(MockMvcResultMatchers.status().isOk());
        BlogPost post = blogPostService.getBlogPostForId(1);
        Assert.assertNotNull(post);
        Assert.assertNotNull(post.getBody());
        Assert.assertEquals("New Body 1", post.getBody());
    }

    @Test
    public void updateBlogPostNotExistent() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/blog/1")
                .param("title", "New Title 1")
                .headers(headers)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deleteBlogPost() throws Exception {
        blogPostService.addBlogPost("Title 1", "Subtitle 1", "Body 1", "admin");
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/blog/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void deleteBlogPostNonExistent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/blog/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}