package com.example.demo.repository;

import com.example.demo.entity.PadreA;
import org.springframework.data.jpa.repository.JpaRepository;

// CRUD básico de PadreA (sin métodos propios)
public interface PadreARepository extends JpaRepository<PadreA, Long> {
}
