package com.eduardo.commons.clients;

import com.eduardo.commons.dto.pacientes.PacienteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "pacientes")
public interface PacienteClient {
    @GetMapping("/id-paciente/{id}")
    PacienteResponse obtenerPacienteSinEstadoPorId(@PathVariable Long id);

    @GetMapping("/{id}")
    PacienteResponse obtenerPacientePorIdActivo(@PathVariable Long id);
}
