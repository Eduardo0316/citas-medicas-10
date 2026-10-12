package com.eduardo.commons.dto.medicos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información de un medico")
public record MedicoResponse(
        @Schema(description = "Id del medico")
        Long id,

        @Schema(description = "Nombre del medico", example = "Juan")
        String nombre,

        @Schema(description = "El apellido paterno del medico", example = "Perez")
        String apellidoPaterno,

        @Schema(description = "El apellido materno del medico", example = "Perez")
        String apellidoMaterno,

        @Schema(description = "La edad del medico", example = "20")
        Short edad,

        @Schema(description = "Email del medico", example = "laura.martinez@medical.com")
        String email,

        @Schema(description = "El telefono del medico", example = "9191011001")
        String telefono,

        @Schema(description = "Cedula profesional del medico")
        String cedulaProfesional,

        @Schema(description = "Identificador de la especialidad del medico", example = "20")
        Long idEspecialidad
) {
}
