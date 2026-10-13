package com.eduardo.medicos.controller;

import com.eduardo.commons.controller.CRUDController;
import com.eduardo.commons.docs.ProblemaDoc;
import com.eduardo.commons.dto.medicos.MedicoRequest;
import com.eduardo.commons.dto.medicos.MedicoResponse;
import com.eduardo.medicos.service.MedicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "API medicos", description = "Api para control de operaciones de pacientes")
public class MedicoController extends CRUDController<MedicoRequest, MedicoResponse, MedicoService> {
    public MedicoController(MedicoService service){
        super(service);
    }

    @GetMapping("/id-medico/{id}")
    @Operation(summary = "Obtener metodo por id sin importar el estado del registro")
    @ApiResponse(responseCode = "200", description = "Medico sin estado encontrado")
    @ApiResponse(responseCode = "404", description = "El medico sin estado no existe",
        content = @Content(mediaType = "application/problem+json",
            schema = @Schema(implementation = ProblemaDoc.class)))
    public ResponseEntity<MedicoResponse> obtenerMedicoPorIdSinEstado(
            @PathVariable @Positive(message = "El ID debe ser positivo") Long id
    ){
        return ResponseEntity.ok(service.obtenerMedicoPorIdSinEstado(id));
    }

    @Operation(summary = "Actualizar disponibilidad del medico" +
            "No es endpoint librem debe gestionarlo el sistema de citas")
    @ApiResponse(responseCode = "204", description = "Disponibilidad del medico actualizada")
    @ApiResponse(responseCode = "409", description = "La disponibilidad del médico no puede actualizarse por regla de negocio",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemaDoc.class)))
    @PutMapping("/{idMedico}/disponibilidad/{idDisponibilidad}")
    public ResponseEntity<Void> actualizarDisponibilidadMedico(
            @PathVariable @Positive(message = "El idMedico debe ser positivo") Long idMedico,
            @PathVariable @Positive(message = "El idDisponibilidad debe ser positivo") Long idDisponibilidad
    ){
        service.actualizarDisponibilidadMedico(idMedico, idDisponibilidad);
        return ResponseEntity.noContent().build();
    }
}
