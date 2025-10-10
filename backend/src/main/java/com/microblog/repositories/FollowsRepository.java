package com.microblog.repositories;

import com.microblog.models.Follows;
import com.microblog.models.FollowsId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowsRepository extends JpaRepository<Follows, FollowsId> {
  List<Follows> findByFollowee_Id(String followeeId);

  List<Follows> findByFollower_Id(String followerId);

  boolean existsByFollower_IdAndFollowee_Id(String followerId, String followeeId);
}
