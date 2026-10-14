package com.eduardo.auth.services;

import com.eduardo.auth.dto.UsuarioRequest;
import com.eduardo.auth.dto.UsuarioResponse;

import java.util.Set;

public interface UsuarioService {

    Set<UsuarioResponse> listar();

    UsuarioResponse registrar(UsuarioRequest request);

    UsuarioResponse eliminar(String username);
}
