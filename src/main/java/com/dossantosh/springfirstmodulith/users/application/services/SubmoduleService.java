package com.dossantosh.springfirstmodulith.users.application.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dossantosh.springfirstmodulith.users.domain.Submodules;
import com.dossantosh.springfirstmodulith.users.infrastructure.mappers.AccessReferenceMapper;
import com.dossantosh.springfirstmodulith.users.infrastructure.repos.SubmoduleRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SubmoduleService {

    private final SubmoduleRepository submoduleRepository;

    public Submodules findById(Long id) {
        return submoduleRepository.findById(id)
                .map(AccessReferenceMapper::toDomain)
                .orElseThrow(() -> new EntityNotFoundException("Submodulo con ID " + id + " no encontrado"));
    }

    public List<Submodules> findAllById(List<Long> listaId) {
        return submoduleRepository.findAllById(listaId).stream()
                .map(AccessReferenceMapper::toDomain)
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    }

    public List<Submodules> findAll() {
        return submoduleRepository.findAll().stream()
                .map(AccessReferenceMapper::toDomain)
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    }

    public Submodules save(Submodules submodule) {
        return AccessReferenceMapper.toDomain(
                submoduleRepository.save(AccessReferenceMapper.toJpaEntity(submodule)));
    }

    public boolean existById(Long id) {
        return submoduleRepository.existsById(id);
    }
}
