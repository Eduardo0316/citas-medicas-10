package com.eduardo.auth.services;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import com.eduardo.auth.dto.UsuarioRequest;
import com.eduardo.auth.dto.UsuarioResponse;
import com.eduardo.auth.entities.Rol;
import com.eduardo.auth.entities.Usuario;
import com.eduardo.auth.exceptions.RecursoNoEncontradoException;
import com.eduardo.auth.mapper.UsuarioMapper;
import com.eduardo.auth.repositories.RolRepository;
import com.eduardo.auth.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private final RolRepository rolRepository;

    private final UsuarioMapper usuarioMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Set<UsuarioResponse> listar() {
        log.info("Listado de todos los usuarios solicitado");
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::entityToResponse).collect(Collectors.toSet());
    }

    @Override
    public UsuarioResponse registrar(UsuarioRequest request) {
        log.info("Buscando usuario {}", request.username());
        if (usuarioRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("El usuario " + request.username() + " ya está registrado");
        }

        Set<Rol> roles = request.roles().stream().map(rol ->
                rolRepository.findByNombre(rol).orElseThrow(() ->
                        new RecursoNoEncontradoException("Rol " + rol + " no encontrado"))
        ).collect(Collectors.toSet());

        Usuario usuario = usuarioMapper.requestToEntity(request,
                passwordEncoder.encode(request.password()), roles);

        usuario = usuarioRepository.save(usuario);
        return usuarioMapper.entityToResponse(usuario);
    }

    @Override
    public UsuarioResponse eliminar(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario: " + username));
        usuarioRepository.delete(usuario);
        return usuarioMapper.entityToResponse(usuario);
    }
}