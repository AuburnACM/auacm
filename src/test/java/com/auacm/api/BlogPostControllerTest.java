package com.auacm.api;

import com.auacm.Auacm;
import com.auacm.TestingConfig;
import com.auacm.database.model.BlogPost;
import com.auacm.service.BlogPostService;
import com.auacm.service.UserService;
import com.auacm.user.WithACMUser;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Auacm.class, TestingConfig.class})
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = TestingConfig.class)
@Slf4j
public class BlogPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private Gson gson;

    private HttpHeaders headers;

    @Before
    public void setup() throws Exception {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
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
    @WithACMUser(username = "admin")
    public void createBlogPostValid() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/blog")
                .param("title", "Test").param("subtitle", "Test").param("body", "the body")
                .headers(headers))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        System.out.println(response);
        BlogPost post = blogPostService.getBlogPostForId(1);
        Assert.assertNotNull(post);
        Assert.assertNotNull(post.getTitle());
        Assert.assertNotNull(post.getSubtitle());
        Assert.assertNotNull(post.getBody());
        Assert.assertNotNull(post.getUser());
        Assert.assertEquals("Test", post.getTitle());
        Assert.assertEquals("Test", post.getSubtitle());
        Assert.assertEquals("the body", post.getBody());
    }

    @Test
    @WithACMUser(username = "admin")
    public void createBlogPostMissingTitle() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/blog")
                .param("subtitle", "Test").param("body", "the body")
                .headers(headers))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithACMUser(username = "admin")
    public void createBlogPostMissingSubtitle() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/blog")
                .param("title", "Test").param("body", "the body")
                .headers(headers))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithACMUser(username = "admin")
    public void createBlogPostMissingBody() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/blog")
                .param("title", "Test").param("subtitle", "Test")
                .headers(headers))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
//    @WithACMUser(username = "user")
    public void getBlogPosts() throws Exception {
        blogPostService.addBlogPost("Title 1", "Subtitle 1", "Body 1", "admin");
        blogPostService.addBlogPost("Title 2", "Subtitle 2", "Body 2", "admin");
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/blog")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

        JsonObject responseObject = new JsonParser().parse(response).getAsJsonObject();
        JsonArray data = responseObject.get("data").getAsJsonArray();
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
//    @WithACMUser(username = "user")
    public void getBlogPost() throws Exception {
        blogPostService.addBlogPost("Title 1", "Subtitle 1", "Body 1", "admin");
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/blog/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        log.debug(response);
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
    @WithACMUser(username = "admin")
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
    @WithACMUser(username = "admin")
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
    @WithACMUser(username = "admin")
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
    @WithACMUser(username = "admin")
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
    @WithACMUser(username = "admin")
    public void deleteBlogPost() throws Exception {
        blogPostService.addBlogPost("Title 1", "Subtitle 1", "Body 1", "admin");
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/blog/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithACMUser(username = "admin")
    public void deleteBlogPostNonExistent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/blog/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}