package com.example.usuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.usuarios.model.Ejemplo;

@Repository
public interface EjemploRepository extends JpaRepository<Ejemplo, Long> {
}
