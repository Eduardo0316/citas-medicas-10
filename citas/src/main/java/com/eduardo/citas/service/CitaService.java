package com.eduardo.citas.service;

import com.eduardo.citas.dto.CitaRequest;
import com.eduardo.citas.dto.CitaResponse;
import com.eduardo.commons.service.CRUDService;

public interface CitaService extends CRUDService<CitaRequest, CitaResponse> {
    void actualizarEstadoCita(Long idCita, Long idEstadoCita);

    void pacienteConCitaActiva(Long idPaciente);

    void medicoConCitaActiva(Long idMedico);
}
