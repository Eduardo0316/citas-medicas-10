package com.eduardo.citas.controller;

import com.eduardo.citas.dto.CitaRequest;
import com.eduardo.citas.dto.CitaResponse;
import com.eduardo.citas.service.CitaService;
import com.eduardo.commons.controller.CRUDController;
import com.eduardo.commons.docs.ProblemaDoc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "API citas", description = "Metodos para la gestion de citas")
public class CitaController extends CRUDController<CitaRequest, CitaResponse, CitaService> {
    public CitaController(CitaService service){
        super(service);
    }

    @Operation(
            summary = "Actualizar estado de la cita",
            description = "Actualiza el estado de una cita utilizando el identificador de la cita y el identificador del nuevo estado"
    )
    @ApiResponse(responseCode = "204", description = "Estado de la cita actualizado")
    @ApiResponse(responseCode = "409", description = "El estado de la cita no puede actualizarse por regla de negocio",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemaDoc.class)))
    @PatchMapping("/{idCita}/estado/{idEstado}")
    public ResponseEntity<Void> actualizarEstadoCita(
            @PathVariable @Positive(message = "El idCita debe ser positivo") Long idCita,
            @PathVariable @Positive(message = "El idEstado debe ser positivo") Long idEstado
    ){
        service.actualizarEstadoCita(idCita, idEstado);
        return ResponseEntity.noContent().build();
    }

    @ApiResponse(responseCode = "204", description = "El paciente no tiene cita activa")
    @ApiResponse(responseCode = "409", description = "El paciente si tiene cita activa",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(
                            implementation = ProblemaDoc.class
                    )
            )

    )
    @GetMapping("/cita-paciente/{idPaciente}")
    public ResponseEntity<Void> pacienteConCitaActiva(
            @PathVariable @Positive(message = "El idPaciente debe ser positivo") Long idPaciente
    ){
        service.pacienteConCitaActiva(idPaciente);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cita-medico/{idMedico}")
    @ApiResponse(responseCode = "204", description = "El medico no tiene cita activa")
    @ApiResponse(responseCode = "409", description = "El medico si tiene cita activa",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(
                            implementation = ProblemaDoc.class
                    )
            )

    )
    public ResponseEntity<Void> medicoConCitaActiva(
            @PathVariable @Positive(message = "El idMedico debe ser positivo") Long idMedico
    ){
        service.medicoConCitaActiva(idMedico);
        return ResponseEntity.noContent().build();
    }
}
