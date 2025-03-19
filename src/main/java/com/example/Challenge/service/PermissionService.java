package com.example.Challenge.service;

import com.example.Challenge.model.Permission;
import com.example.Challenge.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    public Optional<Permission> getPermissionById(String id) {
        return permissionRepository.findById(id);
    }

    @Transactional
    public Permission savePermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    @Transactional
    public void deletePermission(String id) {
        permissionRepository.deleteById(id);
    }
}

