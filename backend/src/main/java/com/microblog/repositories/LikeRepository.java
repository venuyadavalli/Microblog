package com.microblog.repositories;

import com.microblog.models.Like;
import com.microblog.models.LikeId;
import com.microblog.models.Post;
import com.microblog.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, LikeId> {
  List<Like> findByUser(User user);

  List<Like> findByPost(Post post);

  Optional<Like> findByUserAndPost(User user, Post post);

  void deleteByUserAndPost(User user, Post post);

  long countByPost(Post post);
}
