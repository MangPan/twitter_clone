package com.example.minitwitter.post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.minitwitter.post.domain.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

        long countByAuthorId(Long authorId);

        @Query("""
                        select p
                        from Post p
                        join fetch p.author
                        order by p.id desc
                            """)
        List<Post> findAllWithAuthorOrderByIdDesc();

        @Query("""
                        select p
                        from Post p
                        join fetch p.author
                        where p.id = :id
                            """)
        Optional<Post> findByIdwithAuthor(Long id);

        @Query("""
                        select p
                        from Post p
                        join fetch p.author
                        where p.author.id in (
                            select f.following.id
                            from Follow f
                            where f.follower.id = :userId
                        )
                        order by p.id desc
                        """)
        List<Post> findTimeLineFirstPage(Long userId, Pageable pageable);

        @Query("""
                        select p
                        from Post p
                        join fetch p.author
                        where p.id < :cursor
                            and p.author.id in (
                                select f.following.id
                                from Follow f
                                where f.follower.id = :userId
                            )
                        order by p.id desc
                        """)
        List<Post> findTimelineByCursor(Long userId, Long cursor, Pageable pageable);

        @Query("""
                        select p
                        from Post p
                        join fetch p.author
                        where p.author.id = :authorId
                        order by p.id desc
                        """)
        List<Post> findByAuthorIdWithAuthorOrderByIdDesc(Long authorId);

        @Query("""
                        select p
                        from Post p
                        join fetch p.author
                        order by p.id desc
                        """)
        List<Post> findFeedFirstPage(Pageable pageable);

        @Query("""
                        select p
                        from Post p
                        join fetch p.author
                        where p.id < :cursor
                        order by p.id desc
                        """)
        List<Post> findFeedByCursor(Long cursor, Pageable pageable);

}