package com.example.usuarios.service;

import java.util.List;
import java.util.Optional;

import com.example.usuarios.model.Usuario;

public interface UsuarioService {

    List<Usuario> getAllUsuarios();

    Optional<Usuario> getUsuarioById(Long id);

    List<Usuario> getUsuariosPorRol(Long idRol);

    Usuario saveUsuario(Usuario usuario);

    Usuario updateUsuario(Long id, Usuario usuario);

    void deleteUsuarioById(Long id);

    Usuario login(String correo, String clave);
}
