package com.example.pattern.repository;

import com.example.pattern.entity.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface PatternRepository extends JpaRepository<Pattern, Long>, JpaSpecificationExecutor<Pattern> {
    // Category based search removed
}