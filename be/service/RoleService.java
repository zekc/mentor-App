package com.obss.mentorapp.service;

import com.obss.mentorapp.entity.Role;
import com.obss.mentorapp.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Optional<Role> findByName(Role.RoleName roleName) {
        return roleRepository.findByName(roleName);
    }

    public Role save(Role role) {
        return roleRepository.save(role);
    }
}
