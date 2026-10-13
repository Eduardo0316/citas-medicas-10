package com.eduardo.medicos.service;

import com.eduardo.commons.dto.medicos.MedicoRequest;
import com.eduardo.commons.dto.medicos.MedicoResponse;
import com.eduardo.commons.service.CRUDService;

public interface MedicoService extends CRUDService<MedicoRequest, MedicoResponse> {
    MedicoResponse obtenerMedicoPorIdSinEstado(Long id);

    void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad);
}
