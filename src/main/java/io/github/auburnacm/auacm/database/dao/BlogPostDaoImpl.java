package io.github.auburnacm.auacm.database.dao;

import io.github.auburnacm.auacm.database.model.BlogPost;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class BlogPostDaoImpl implements BlogPostDao {
    private BaseDao<BlogPost> baseDao;

    public BlogPostDaoImpl(EntityManager entityManager, SessionFactory factory) {
        baseDao = new BaseDaoImpl<>(BlogPost.class, entityManager, factory);
    }

    @Override
    public void addBlogPost(BlogPost object) {
        baseDao.addEntity(object);
    }

    @Override
    public List<BlogPost> getBlogPosts() {
        return baseDao.getEntities();
    }

    @Override
    public List<BlogPost> getBlogPosts(String parameter, Object object) {
        return baseDao.getEntities(parameter, object);
    }

    @Override
    public BlogPost getBlogPost(String parameter, Object object) {
        return baseDao.getEntity(parameter, object);
    }

    @Override
    public void updateBlogPost(BlogPost object) {
        baseDao.updateEntity(object);
    }

    @Override
    public void deleteBlogPost(BlogPost object) {
        baseDao.deleteEntity(object);
    }
}
