package com.auacm.database.dao;

import com.auacm.database.model.BlogPost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogPostDao extends JpaRepository<BlogPost, Long> {
    List<BlogPost> findByUsernameIgnoreCase(String username);

    List<BlogPost> findAllByUsernameOrderByPostTimeDesc(String username, Pageable pageable);
}
