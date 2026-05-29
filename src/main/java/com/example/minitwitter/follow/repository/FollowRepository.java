package com.example.minitwitter.follow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.minitwitter.follow.domain.Follow;
import com.example.minitwitter.user.domain.User;

public interface FollowRepository extends JpaRepository<Follow, Long> {

        long countByFollowerId(Long followerId);

        long countByFollowingId(Long followingId);

        boolean existsByFollowerAndFollowing(User follower, User following);

        Optional<Follow> findByFollowerAndFollowing(User follower, User following);

        @Query("""
                        select f
                        from Follow f
                        join fetch f.following
                        where f.follower = :follower
                        order by f.id desc
                        """)
        List<Follow> findFollowingsByFollower(User follower);

        @Query("""
                        select f
                        from Follow f
                        join fetch f.follower
                        where f.following = :following
                        order by f.id desc
                        """)
        List<Follow> findFollowersByFollowing(User following);

}
