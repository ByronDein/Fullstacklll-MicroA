package com.example.usuarios.service;

import java.util.List;
import java.util.Optional;

import com.example.usuarios.model.Rol;

public interface RolService {

    List<Rol> getAllRoles();

    Optional<Rol> getRolById(Long id);

    Rol saveRol(Rol rol);

    Rol updateRol(Long id, Rol rol);
}
