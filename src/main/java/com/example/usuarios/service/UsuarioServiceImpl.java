package com.example.usuarios.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.usuarios.exception.CredencialesInvalidasException;
import com.example.usuarios.exception.ResourceNotFoundException;
import com.example.usuarios.model.Rol;
import com.example.usuarios.model.Usuario;
import com.example.usuarios.repository.RolRepository;
import com.example.usuarios.repository.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    // para guardar las claves encriptadas
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> getUsuarioById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public List<Usuario> getUsuariosPorRol(Long idRol) {
        if (!rolRepository.existsById(idRol)) {
            throw new ResourceNotFoundException("No se encontro el Rol con el id: " + idRol);
        }
        return usuarioRepository.findByRolId(idRol);
    }

    @Override
    public Usuario saveUsuario(Usuario usuario) {
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new RuntimeException("Ya existe un usuario con el correo: " + usuario.getCorreo());
        }
        if (usuario.getClave() == null || usuario.getClave().isBlank()) {
            throw new RuntimeException("La clave es obligatoria");
        }

        usuario.setId(null);
        usuario.setRol(buscarRol(usuario.getRol()));
        usuario.setClave(passwordEncoder.encode(usuario.getClave()));
        usuario.setFechaRegistro(LocalDate.now());
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario updateUsuario(Long id, Usuario usuarioActualizado) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            throw new ResourceNotFoundException("No se pudo actualizar el Usuario. No existe el id: " + id);
        }

        Usuario usuarioExistente = usuarioOpt.get();

        if (!usuarioExistente.getCorreo().equals(usuarioActualizado.getCorreo())
                && usuarioRepository.existsByCorreo(usuarioActualizado.getCorreo())) {
            throw new RuntimeException("Ya existe un usuario con el correo: " + usuarioActualizado.getCorreo());
        }

        usuarioExistente.setNombre(usuarioActualizado.getNombre());
        usuarioExistente.setCorreo(usuarioActualizado.getCorreo());
        usuarioExistente.setActivo(usuarioActualizado.isActivo());

        // la clave y el rol solo se cambian si vienen en el body
        if (usuarioActualizado.getClave() != null && !usuarioActualizado.getClave().isBlank()) {
            usuarioExistente.setClave(passwordEncoder.encode(usuarioActualizado.getClave()));
        }
        if (usuarioActualizado.getRol() != null) {
            usuarioExistente.setRol(buscarRol(usuarioActualizado.getRol()));
        }

        return usuarioRepository.save(usuarioExistente);
    }

    @Override
    public void deleteUsuarioById(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se pudo eliminar el Usuario. No existe el id: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    public Usuario login(String correo, String clave) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new CredencialesInvalidasException("Correo o clave incorrectos"));

        if (!passwordEncoder.matches(clave, usuario.getClave())) {
            throw new CredencialesInvalidasException("Correo o clave incorrectos");
        }
        if (!usuario.isActivo()) {
            throw new CredencialesInvalidasException("El usuario esta desactivado");
        }
        return usuario;
    }

    // Si no viene rol se le asigna USUARIO por defecto
    private Rol buscarRol(Rol rol) {
        if (rol == null || rol.getId() == null) {
            return rolRepository.findByNombre("USUARIO")
                    .orElseThrow(() -> new RuntimeException("No existe el rol USUARIO en la base de datos"));
        }
        return rolRepository.findById(rol.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el Rol con el id: " + rol.getId()));
    }
}
