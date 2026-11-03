package com.example.usuarios.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.usuarios.exception.ResourceNotFoundException;
import com.example.usuarios.model.Rol;
import com.example.usuarios.repository.RolRepository;

@Service
public class RolServiceImpl implements RolService {

    @Autowired
    private RolRepository rolRepository;

    @Override
    public List<Rol> getAllRoles() {
        return rolRepository.findAll();
    }

    @Override
    public Optional<Rol> getRolById(Long id) {
        return rolRepository.findById(id);
    }

    @Override
    public Rol saveRol(Rol rol) {
        if (rolRepository.findByNombre(rol.getNombre()).isPresent()) {
            throw new RuntimeException("Ya existe un rol con el nombre: " + rol.getNombre());
        }
        rol.setId(null);
        return rolRepository.save(rol);
    }

    @Override
    public Rol updateRol(Long id, Rol rolActualizado) {
        Optional<Rol> rolOpt = rolRepository.findById(id);
        if (rolOpt.isEmpty()) {
            throw new ResourceNotFoundException("No se pudo actualizar el Rol. No existe el id: " + id);
        }

        Rol rolExistente = rolOpt.get();
        rolExistente.setNombre(rolActualizado.getNombre());
        rolExistente.setDescripcion(rolActualizado.getDescripcion());
        return rolRepository.save(rolExistente);
    }
}
