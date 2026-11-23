package com.example.usuarios.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.usuarios.exception.ResourceNotFoundException;
import com.example.usuarios.model.Ejemplo;
import com.example.usuarios.service.EjemploService;

import jakarta.validation.Valid;

@RestController
public class EjemploController {

    private static final Logger log = LoggerFactory.getLogger(EjemploController.class);

    @Autowired
    private EjemploService ejemploService;

    @GetMapping("/ejemplos")
    public ResponseEntity<List<Ejemplo>> getAllEjemplos() {
        log.info("Obteniendo la lista de ejemplos");
        return ResponseEntity.ok(ejemploService.getAllEjemplos());
    }

    @GetMapping("/ejemplos/{idEjemplo}")
    public ResponseEntity<Ejemplo> getEjemploById(@PathVariable("idEjemplo") Long idEjemplo) {
        log.info("Buscando ejemplo con ID: {}", idEjemplo);
        return ejemploService.getEjemploById(idEjemplo)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el Ejemplo con el id: " + idEjemplo));
    }

    @PostMapping("/ejemplos")
    public ResponseEntity<Ejemplo> createEjemplo(@Valid @RequestBody Ejemplo ejemplo) {
        log.info("Creando un nuevo ejemplo: {}", ejemplo.getNombre());
        return new ResponseEntity<>(ejemploService.saveEjemplo(ejemplo), HttpStatus.CREATED);
    }

    @PutMapping("/ejemplos/{idEjemplo}")
    public ResponseEntity<Ejemplo> updateEjemplo(@PathVariable("idEjemplo") Long idEjemplo, @Valid @RequestBody Ejemplo ejemplo) {
        log.info("Actualizando ejemplo con ID: {}", idEjemplo);
        return ResponseEntity.ok(ejemploService.updateEjemplo(idEjemplo, ejemplo));
    }

    @DeleteMapping("/ejemplos/{idEjemplo}")
    public ResponseEntity<Void> deleteEjemplo(@PathVariable("idEjemplo") Long idEjemplo) {
        log.info("Eliminando ejemplo con ID: {}", idEjemplo);
        ejemploService.deleteEjemploById(idEjemplo);
        return ResponseEntity.noContent().build();
    }
}
