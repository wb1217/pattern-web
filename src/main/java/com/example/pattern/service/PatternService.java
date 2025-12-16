package com.example.pattern.service;

import com.example.pattern.entity.Pattern;
import com.example.pattern.repository.PatternRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PatternService {

    @Autowired
    private PatternRepository patternRepository;

    @Autowired
    private com.example.pattern.repository.FavoriteRepository favoriteRepository;

    // Updated search signature
    public List<Pattern> searchPatterns(String keyword, String form, String style, String color, String theme,
            Long userId, boolean onlyFavorites) {
        Specification<Pattern> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                predicates.add(cb.like(root.get("name"), "%" + keyword + "%"));
            }
            if (StringUtils.hasText(form)) {
                predicates.add(cb.equal(root.get("form"), form));
            }
            if (StringUtils.hasText(style)) {
                predicates.add(cb.equal(root.get("style"), style));
            }
            if (StringUtils.hasText(color)) {
                predicates.add(cb.equal(root.get("color"), color));
            }
            if (StringUtils.hasText(theme)) {
                predicates.add(cb.equal(root.get("theme"), theme));
            }

            // Filters based on favorites
            if (onlyFavorites && userId != null) {
                // Determine pattern IDs that are favorited
                // This approach is simple but might perform poorly if there are millions of
                // favorites.
                // For this scale it's fine.
                // Alternatively use a subquery if mapped, but they are not mapped.
                // I will fetch IDs first. But can't do that easily inside Specification lambda
                // efficiently without query execution.
                // Better to pass the IDs in.
                // But I can't change signature here effectively without breaking call site.
                // Let's handle it in the controller or do a second query here.
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // If onlyFavorites is true, we filter the results or add an IN clause
        if (onlyFavorites && userId != null) {
            List<com.example.pattern.entity.UserFavorite> favorites = favoriteRepository.findByUserId(userId);
            List<Long> patternIds = favorites.stream().map(com.example.pattern.entity.UserFavorite::getPatternId)
                    .toList();
            if (patternIds.isEmpty()) {
                return new ArrayList<>();
            }

            // We need to combine the spec with an IN clause
            Specification<Pattern> favoriteSpec = (root, query, cb) -> root.get("id").in(patternIds);
            spec = spec.and(favoriteSpec);
        }

        return patternRepository.findAll(spec);
    }

    public boolean toggleFavorite(Long userId, Long patternId) {
        Optional<com.example.pattern.entity.UserFavorite> existing = favoriteRepository.findByUserIdAndPatternId(userId,
                patternId);
        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            return false; // Not favorited anymore
        } else {
            com.example.pattern.entity.UserFavorite fav = new com.example.pattern.entity.UserFavorite();
            fav.setUserId(userId);
            fav.setPatternId(patternId);
            fav.setCreateTime(LocalDateTime.now());
            favoriteRepository.save(fav);
            return true; // Favorited
        }
    }

    public List<Long> getFavoritedPatternIds(Long userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(com.example.pattern.entity.UserFavorite::getPatternId)
                .toList();
    }

    public List<Pattern> findAll() {
        return patternRepository.findAll();
    }

    public Optional<Pattern> findById(Long id) {
        return patternRepository.findById(id);
    }

    public Pattern save(Pattern pattern) {
        if (pattern.getId() == null) {
            pattern.setCreateTime(LocalDateTime.now());
        }
        pattern.setUpdateTime(LocalDateTime.now());
        return patternRepository.save(pattern);
    }

    public void deleteById(Long id) {
        patternRepository.deleteById(id);
    }
}
