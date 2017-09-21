package io.github.auburnacm.auacm.database.dao;

import io.github.auburnacm.auacm.database.model.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogPostDao extends JpaRepository<BlogPost, Long> {
    List<BlogPost> findByUsernameIgnoreCase(String username);
}
