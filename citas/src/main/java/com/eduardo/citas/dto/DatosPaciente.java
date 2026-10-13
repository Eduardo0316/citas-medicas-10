package com.eduardo.citas.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de un paciente asociado a una cita")
public record DatosPaciente(
        @Schema(description = "Nombre completo del paciente", example = "Eduardo Garcia Mendoza")
        String nombre,

        @Schema(description = "Numero de expediente del paciente", example = "1X2X3X4X5X6X7X8X9X0X")
        String numExpediente,

        @Schema(description = "Edad del paciente", example = "16")
        String edad,

        @Schema(description = "Peso del paciente", example = "67.7")
        String peso,

        @Schema(description = "Estatura del paciente", example = "1.70")
        String estatura,

        @Schema(description = "IMC del paciente", example = "22.2 peso normal")
        String imc,

        @Schema(description = "Telefono del paciente", example = "1234567890")
        String telefono
) {
}
