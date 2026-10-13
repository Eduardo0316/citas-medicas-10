package com.eduardo.pacientes.service;

import com.eduardo.commons.dto.pacientes.PacienteRequest;
import com.eduardo.commons.dto.pacientes.PacienteResponse;
import com.eduardo.commons.service.CRUDService;

public interface PacienteService extends CRUDService<PacienteRequest, PacienteResponse> {
    PacienteResponse obtenerPacientePorIdSinEstado(Long id);
}
