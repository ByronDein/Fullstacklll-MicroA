package com.example.usuarios;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.usuarios.model.Rol;
import com.example.usuarios.model.Usuario;
import com.example.usuarios.repository.RolRepository;
import com.example.usuarios.repository.UsuarioRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) {
        if (rolRepository.count() > 0) {
            System.out.println("Datos de usuarios ya existen. No se reinicializa la base.");
            return;
        }

        Rol rolUsuario = new Rol();
        rolUsuario.setNombre("USUARIO");
        rolUsuario.setDescripcion("Puede ver, publicar y comentar temas en el foro");

        Rol rolModerador = new Rol();
        rolModerador.setNombre("MODERADOR");
        rolModerador.setDescripcion("Puede revisar y moderar publicaciones y comentarios");

        Rol rolAdmin = new Rol();
        rolAdmin.setNombre("ADMIN");
        rolAdmin.setDescripcion("Administra usuarios y todo el foro");

        rolRepository.save(rolUsuario);
        rolRepository.save(rolModerador);
        rolRepository.save(rolAdmin);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        Usuario u1 = new Usuario();
        u1.setNombre("Byron Gonzalez");
        u1.setCorreo("byron@foro.cl");
        u1.setClave(encoder.encode("admin123"));
        u1.setFechaRegistro(LocalDate.now());
        u1.setRol(rolAdmin);

        Usuario u2 = new Usuario();
        u2.setNombre("Camila Rojas");
        u2.setCorreo("camila@foro.cl");
        u2.setClave(encoder.encode("mod123"));
        u2.setFechaRegistro(LocalDate.now());
        u2.setRol(rolModerador);

        Usuario u3 = new Usuario();
        u3.setNombre("Matias Soto");
        u3.setCorreo("matias@foro.cl");
        u3.setClave(encoder.encode("user123"));
        u3.setFechaRegistro(LocalDate.now());
        u3.setRol(rolUsuario);

        usuarioRepository.save(u1);
        usuarioRepository.save(u2);
        usuarioRepository.save(u3);

        System.out.println("Datos de usuarios inicializados con exito.");
    }
}
