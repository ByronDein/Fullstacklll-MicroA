package com.example.usuarios.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.usuarios.exception.ResourceNotFoundException;
import com.example.usuarios.model.Rol;
import com.example.usuarios.service.RolService;

import jakarta.validation.Valid;

@RestController
public class RolController {

    private static final Logger log = LoggerFactory.getLogger(RolController.class);

    @Autowired
    private RolService rolService;

    @GetMapping("/roles")
    public ResponseEntity<List<Rol>> getAllRoles() {
        log.info("Obteniendo la lista de roles");
        return ResponseEntity.ok(rolService.getAllRoles());
    }

    @GetMapping("/roles/{idRol}")
    public ResponseEntity<Rol> getRolById(@PathVariable("idRol") Long idRol) {
        log.info("Buscando rol con ID: {}", idRol);
        return rolService.getRolById(idRol)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el Rol con el id: " + idRol));
    }

    @PostMapping("/roles")
    public ResponseEntity<Rol> createRol(@Valid @RequestBody Rol rol) {
        log.info("Creando un nuevo rol: {}", rol.getNombre());
        return new ResponseEntity<>(rolService.saveRol(rol), HttpStatus.CREATED);
    }

    @PutMapping("/roles/{idRol}")
    public ResponseEntity<Rol> updateRol(@PathVariable("idRol") Long idRol, @Valid @RequestBody Rol rol) {
        log.info("Actualizando rol con ID: {}", idRol);
        return ResponseEntity.ok(rolService.updateRol(idRol, rol));
    }
}
