package com.example.usuarios.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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
import com.example.usuarios.model.LoginRequest;
import com.example.usuarios.model.Usuario;
import com.example.usuarios.service.UsuarioService;

import jakarta.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class UsuarioController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/usuarios")
    public ResponseEntity<CollectionModel<EntityModel<Usuario>>> getAllUsuarios() {
        log.info("Obteniendo la lista de todos los usuarios");
        List<EntityModel<Usuario>> usuarios = usuarioService.getAllUsuarios().stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(usuarios,
                linkTo(methodOn(UsuarioController.class).getAllUsuarios()).withSelfRel()));
    }

    @GetMapping("/usuarios/{idUsuario}")
    public ResponseEntity<EntityModel<Usuario>> getUsuarioById(@PathVariable("idUsuario") Long idUsuario) {
        log.info("Buscando usuario con ID: {}", idUsuario);
        Optional<Usuario> usuarioOpt = usuarioService.getUsuarioById(idUsuario);
        if (usuarioOpt.isPresent()) {
            return ResponseEntity.ok(toModel(usuarioOpt.get()));
        }

        throw new ResourceNotFoundException("No se encontro el Usuario con el id: " + idUsuario);
    }

    @GetMapping("/usuarios/rol/{idRol}")
    public ResponseEntity<List<Usuario>> getUsuariosPorRol(@PathVariable("idRol") Long idRol) {
        log.info("Buscando usuarios con el rol ID: {}", idRol);
        return ResponseEntity.ok(usuarioService.getUsuariosPorRol(idRol));
    }

    @PostMapping("/usuarios")
    public ResponseEntity<Usuario> createUsuario(@Valid @RequestBody Usuario usuario) {
        log.info("Creando un nuevo usuario: {}", usuario.getNombre());
        Usuario savedUsuario = usuarioService.saveUsuario(usuario);
        return new ResponseEntity<>(savedUsuario, HttpStatus.CREATED);
    }

    @PutMapping("/usuarios/{idUsuario}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable("idUsuario") Long idUsuario, @Valid @RequestBody Usuario usuario) {
        log.info("Actualizando usuario con ID: {}", idUsuario);
        Usuario updatedUsuario = usuarioService.updateUsuario(idUsuario, usuario);
        return ResponseEntity.ok(updatedUsuario);
    }

    @DeleteMapping("/usuarios/{idUsuario}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable("idUsuario") Long idUsuario) {
        log.info("Eliminando usuario con ID: {}", idUsuario);
        usuarioService.deleteUsuarioById(idUsuario);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/usuarios/login")
    public ResponseEntity<Usuario> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Intento de inicio de sesion del correo: {}", loginRequest.getCorreo());
        Usuario usuario = usuarioService.login(loginRequest.getCorreo(), loginRequest.getClave());
        return ResponseEntity.ok(usuario);
    }

    private EntityModel<Usuario> toModel(Usuario usuario) {
        return EntityModel.of(usuario,
                linkTo(methodOn(UsuarioController.class).getUsuarioById(usuario.getId())).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).getAllUsuarios()).withRel("usuarios"));
    }
}
