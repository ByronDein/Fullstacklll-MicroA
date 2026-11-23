package com.example.usuarios.service;

import java.util.List;
import java.util.Optional;

import com.example.usuarios.model.Ejemplo;

public interface EjemploService {

    List<Ejemplo> getAllEjemplos();

    Optional<Ejemplo> getEjemploById(Long id);

    Ejemplo saveEjemplo(Ejemplo ejemplo);

    Ejemplo updateEjemplo(Long id, Ejemplo ejemplo);

    void deleteEjemploById(Long id);
}
