package com.example.demo.repository;

import com.example.demo.entity.PadreB;
import org.springframework.data.jpa.repository.JpaRepository;

// CRUD básico de PadreB (sin métodos propios)
public interface PadreBRepository extends JpaRepository<PadreB, Long> {
}
