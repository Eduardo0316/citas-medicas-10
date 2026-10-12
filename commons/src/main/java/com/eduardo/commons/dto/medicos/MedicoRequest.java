package com.eduardo.commons.dto.medicos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos para registrar un médico")
public record MedicoRequest(
        @Schema(description = "Nombre del medico", example = "Juan")
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
        String nombre,

        @Schema(description = "El apellido paterno del medico", example = "Perez")
        @NotBlank(message = "El apellido paterno es requerido")
        @Size(min = 1, max = 50, message = "El apellido paterno debe tener entre 1 y 50 caracteres")
        String apellidoPaterno,

        @Schema(description = "El apellido materno del medico", example = "Perez")
        @NotBlank(message = "El apellido materno es requerido")
        @Size(min = 1, max = 50, message = "El apellido materno debe tener entre 1 y 50 caracteres")
        String apellidoMaterno,

        @Schema(description = "La edad del medico", example = "20")
        @NotNull(message = "La edad es requerida")
        @Positive(message = "La edad del medico debe ser positiva")
        Short edad,

        @Schema(description = "Email del medico", example = "laura.martinez@medical.com")
        @NotBlank(message = "El email es requerido")
        @Size(min = 5, max = 100, message = "El email debe tener entre 5 y 100 caracteres")
        @Email(message = "El email debe tener un formato valido (ejemplo@dominio.com)")
        String email,

        @Schema(description = "El telefono del medico", example = "9191011001")
        @NotBlank(message = "El teléfono es requerido")
        @Pattern(regexp = "^[0-9]{10}", message = "El telefono debe tener 10 caracteres numericos")
        String telefono,

        @Schema(description = "Cedula profesional del medico. Debe contener 12 caracteres")
        @NotBlank(message = "La cedula es requerida")
        @Size(min = 12, max = 12, message = "La cedula profesional debe tener exactamente 12 caracteres")
        String cedulaProfesional,

        @Schema(description = "Identificador de la especialidad del medico", example = "20")
        @NotNull(message = "El identificador es requerido")
        @Positive(message = "El id de la especialidad debe ser positivo")
        Long idEspecialidad
) {
}
