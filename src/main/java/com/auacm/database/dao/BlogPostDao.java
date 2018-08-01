package com.auacm.database.dao;

import com.auacm.database.model.BlogPost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogPostDao extends JpaRepository<BlogPost, Long> {
    List<BlogPost> findByUserUsernameIgnoreCase(String username);

    List<BlogPost> findAllByUserUsernameOrderByPostTimeDesc(String username, Pageable pageable);
}
