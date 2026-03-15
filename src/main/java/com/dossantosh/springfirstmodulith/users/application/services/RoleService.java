package com.dossantosh.springfirstmodulith.users.application.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dossantosh.springfirstmodulith.users.domain.Roles;
import com.dossantosh.springfirstmodulith.users.infrastructure.mappers.AccessReferenceMapper;
import com.dossantosh.springfirstmodulith.users.infrastructure.repos.RoleRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public Roles findById(Long id) {
        return roleRepository.findById(id)
                .map(AccessReferenceMapper::toDomain)
                .orElseThrow(() -> new EntityNotFoundException("Rol con ID " + id + " no encontrado"));
    }

    public List<Roles> findAllById(List<Long> listaId) {
        return roleRepository.findAllById(listaId).stream()
                .map(AccessReferenceMapper::toDomain)
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    }

    public List<Roles> findAll() {
        return roleRepository.findAll().stream()
                .map(AccessReferenceMapper::toDomain)
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    }

    public boolean existById(Long id) {
        return roleRepository.existsById(id);
    }
}
