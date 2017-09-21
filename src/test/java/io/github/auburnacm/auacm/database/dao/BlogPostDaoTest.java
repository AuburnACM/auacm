package io.github.auburnacm.auacm.database.dao;

import io.github.auburnacm.auacm.database.model.BlogPost;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureDataJpa
public class BlogPostDaoTest {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private BlogPostDao blogPostDao;

//    @Before
//    public void setup() {
//        blogPostDao = new BlogPostDaoImpl(entityManager.getEntityManager(), entityManagerFactory.unwrap(SessionFactory.class));
//    }

    @Test
    public void addBlogPost() throws Exception {
        BlogPost post = new BlogPost("Test", "Subtitle", "Body", "Mac-Genius");
        blogPostDao.save(post);

        BlogPost post1 = entityManager.find(BlogPost.class, 1L);
        Assert.assertNotNull(post1);
        Assert.assertNotNull(post1.getTitle());
        Assert.assertEquals("Test", post1.getTitle());
        Assert.assertNotNull(post1.getSubtitle());
        Assert.assertEquals("Subtitle", post1.getSubtitle());
        Assert.assertNotNull(post1.getBody());
        Assert.assertEquals("Body", post1.getBody());
        Assert.assertNotNull(post1.getPostTime());
        Assert.assertNotNull(post1.getUsername());
        Assert.assertEquals("Mac-Genius", post1.getUsername());
    }

    @Test
    public void getBlogPosts() throws Exception {

    }

    @Test
    public void getBlogPosts1() throws Exception {

    }

    @Test
    public void getBlogPost() throws Exception {

    }

    @Test
    public void updateBlogPost() throws Exception {

    }

    @Test
    public void deleteBlogPost() throws Exception {

    }

}