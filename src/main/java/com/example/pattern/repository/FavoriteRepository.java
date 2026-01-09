package com.example.pattern.repository;

import com.example.pattern.entity.UserFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<UserFavorite, Long> {
    Optional<UserFavorite> findByUserIdAndPatternId(Long userId, Long patternId);

    List<UserFavorite> findByUserId(Long userId);

    void deleteByUserIdAndPatternId(Long userId, Long patternId);

    void deleteByUserId(Long userId);

    void deleteByUserIdAndPatternIdIn(Long userId, List<Long> patternIds);
}
