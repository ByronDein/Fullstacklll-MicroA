package com.example.usuarios.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.usuarios.exception.ResourceNotFoundException;
import com.example.usuarios.model.Ejemplo;
import com.example.usuarios.repository.EjemploRepository;

@Service
public class EjemploServiceImpl implements EjemploService {

    @Autowired
    private EjemploRepository ejemploRepository;

    @Override
    public List<Ejemplo> getAllEjemplos() {
        return ejemploRepository.findAll();
    }

    @Override
    public Optional<Ejemplo> getEjemploById(Long id) {
        return ejemploRepository.findById(id);
    }

    @Override
    public Ejemplo saveEjemplo(Ejemplo ejemplo) {
        ejemplo.setId(null);
        return ejemploRepository.save(ejemplo);
    }

    @Override
    public Ejemplo updateEjemplo(Long id, Ejemplo ejemploActualizado) {
        Optional<Ejemplo> ejemploOpt = ejemploRepository.findById(id);
        if (ejemploOpt.isEmpty()) {
            throw new ResourceNotFoundException("No se pudo actualizar el Ejemplo. No existe el id: " + id);
        }

        Ejemplo ejemploExistente = ejemploOpt.get();
        ejemploExistente.setNombre(ejemploActualizado.getNombre());
        ejemploExistente.setDescripcion(ejemploActualizado.getDescripcion());
        return ejemploRepository.save(ejemploExistente);
    }

    @Override
    public void deleteEjemploById(Long id) {
        if (!ejemploRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se pudo eliminar el Ejemplo. No existe el id: " + id);
        }
        ejemploRepository.deleteById(id);
    }
}
