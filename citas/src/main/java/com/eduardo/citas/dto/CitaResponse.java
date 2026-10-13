package com.eduardo.citas.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Datos de una cita")
public record CitaResponse(
        @Schema(description = "Identificador unico de una cita", example = "1")
        Long id,

        @Schema(description = "Informacion del paciente asociado a una cita")
        DatosPaciente paciente,

        @Schema(description = "Informacion del medico que atendera la cita")
        DatosMedico medico,

        @Schema(description = "Fecha y hora programada para la cita", example = "25/109/2026",
                type = "string",
                format = "date-time"
        )
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime fechaCita,

        @Schema(
                description = "Descripcion de los sintomas indicados por el paciente",
                example = "El paciente presenta dolor de cabeza intenso y fiebre desde hace 2 dias"
        )
        String sintomas,

        @Schema(description = "Estado actual de la cita", example = "Confirmada por el paciente")
        String estadoCita
) {
}
