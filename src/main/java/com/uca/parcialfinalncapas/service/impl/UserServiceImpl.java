package com.uca.parcialfinalncapas.service.impl;

import com.uca.parcialfinalncapas.dto.request.UserCreateRequest;
import com.uca.parcialfinalncapas.dto.request.UserUpdateRequest;
import com.uca.parcialfinalncapas.dto.response.UserResponse;
import com.uca.parcialfinalncapas.entities.User;
import com.uca.parcialfinalncapas.exceptions.UserNotFoundException;
import com.uca.parcialfinalncapas.repository.UserRepository;
import com.uca.parcialfinalncapas.service.UserService;
import com.uca.parcialfinalncapas.utils.mappers.UserMapper;

import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ------------------------------------------------
    // Implementación de UserDetailsService para Security
    // ------------------------------------------------
    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        User user = userRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        // Convertimos el rol almacenado (p.ej. "USER" o "TECH") a GrantedAuthority
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE_" + user.getNombreRol());

        return new org.springframework.security.core.userdetails.User(
                user.getCorreo(),
                user.getPassword(),
                Collections.singletonList(authority)
        );
    }

    // -------------------------------
    // Implementación de UserService
    // -------------------------------
    @Override
    public UserResponse findByCorreo(String correo) {
        return UserMapper.toDTO(
                userRepository.findByCorreo(correo)
                        .orElseThrow(() ->
                                new UserNotFoundException("Usuario no encontrado con correo: " + correo))
        );
    }

    @Override
    public UserResponse save(UserCreateRequest req) {
        if (userRepository.findByCorreo(req.getCorreo()).isPresent()) {
            throw new UserNotFoundException("Ya existe un usuario con el correo: " + req.getCorreo());
        }
        User u = UserMapper.toEntityCreate(req);
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        return UserMapper.toDTO(userRepository.save(u));
    }

    @Override
    public UserResponse update(UserUpdateRequest req) {
        User existing = userRepository.findById(req.getId())
                .orElseThrow(() ->
                        new UserNotFoundException("No se encontró un usuario con el ID: " + req.getId()));

        User u = UserMapper.toEntityUpdate(req);
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            u.setPassword(passwordEncoder.encode(req.getPassword()));
        } else {
            u.setPassword(existing.getPassword());
        }
        return UserMapper.toDTO(userRepository.save(u));
    }

    @Override
    public void delete(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new UserNotFoundException("No se encontró un usuario con el ID: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public List<UserResponse> findAll() {
        return UserMapper.toDTOList(userRepository.findAll());
    }
}
