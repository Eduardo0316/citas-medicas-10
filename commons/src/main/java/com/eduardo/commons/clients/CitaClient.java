package com.eduardo.commons.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "citas")
public interface CitaClient {
    @GetMapping("/cita-paciente/{idPaciente}")
    void pacienteConCitaActiva(@PathVariable Long idPaciente);

    @GetMapping("/cita-medico/{idMedico}")
    void medicoConCitaActiva(@PathVariable Long idMedico);
}
