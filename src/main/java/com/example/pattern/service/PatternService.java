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

    public List<Pattern> searchPatterns(String form, String style, String color, String theme) {
        Specification<Pattern> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

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

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return patternRepository.findAll(spec);
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
