package com.dossantosh.springfirstmodulith.users.application.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dossantosh.springfirstmodulith.users.domain.Modules;
import com.dossantosh.springfirstmodulith.users.infrastructure.mappers.AccessReferenceMapper;
import com.dossantosh.springfirstmodulith.users.infrastructure.repos.ModuleRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;

    public Modules findById(Long id) {
        return moduleRepository.findById(id)
                .map(AccessReferenceMapper::toDomain)
                .orElseThrow(() -> new EntityNotFoundException("Modulo con ID " + id + " no encontrado"));
    }

    public List<Modules> findAllById(List<Long> lista) {
        return moduleRepository.findAllById(lista).stream()
                .map(AccessReferenceMapper::toDomain)
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    }

    public List<Modules> findAll() {
        return moduleRepository.findAll().stream()
                .map(AccessReferenceMapper::toDomain)
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    }

    public boolean existById(Long id) {
        return moduleRepository.existsById(id);
    }
}
