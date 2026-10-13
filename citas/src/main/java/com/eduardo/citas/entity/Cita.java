package com.eduardo.citas.entity;

import com.eduardo.citas.enums.EstadoCita;
import com.eduardo.commons.enums.EstadoRegistro;
import com.eduardo.commons.exceptions.ConflictoException;
import com.eduardo.commons.exceptions.DatoInvalidoException;
import com.eduardo.commons.utils.StringCustomUtils;
import com.eduardo.commons.utils.ValoresNumericos;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.concurrent.ConcurrentException;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "CITAS")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Getter
public class Cita {
    @Id
    @Column(name = "ID_CITA")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ID_PACIENTE", nullable = false)
    private Long idPaciente;

    @Column(name = "ID_MEDICO", nullable = false)
    private Long idMedico;

    @Column(name = "FECHA_CITA", nullable = false)
    private LocalDateTime fechaCita;

    @Column(name = "SINTOMAS", nullable = false, length = 500)
    private String sintomas;

    @Column(name = "ESTADO_CITA", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoCita estadoCita;

    @Column(name = "ESTADO_REGISTRO", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoRegistro estadoRegistro;

    private void validarNoEliminado(){
        if(this.estadoRegistro == EstadoRegistro.ELIMINADO)
            throw new ConflictoException("La cita ya esta eliminada");
    }

    private static void validarId(Long id, String campo){
        ValoresNumericos.validarPositivo(id, "El id del " + campo + " es requerido y debe ser positivo");
    }

    private static void validarFecha(LocalDateTime fechaCita){
        if(fechaCita == null || !fechaCita.isAfter(LocalDateTime.now()))
            throw new DatoInvalidoException("La fecha de cita es requerida y debe ser posterior a hoy");
    }

    public static void validarDatos(Long idPaciente, Long idMedico, LocalDateTime fechaCita, String sintomas){
        validarId(idPaciente, "paciente");
        validarId(idMedico, "paciente");
        validarFecha(fechaCita);
        StringCustomUtils.validarTamanio(sintomas, 20, 500,
                "Los síntomas son requeridos y deben ser entre 20 y 500 caracteres");
    }

    private void validarEliminacionPermitida(){
        validarNoEliminado();

        if(!estadoCita.isEliminable())
            throw new ConflictoException(
                    "La cita con estado " + estadoCita + " no puede eliminarse");
    }

    private void validarActualizacionPermitida(){
        validarNoEliminado();

        if(!estadoCita.isActualizable())
            throw new ConflictoException(
                    "La cita con estado " + estadoCita + " no puede actualizarse");
    }

    public void actualizar(Long idPaciente, Long idMedico, LocalDateTime fechaCita, String sintomas){
        validarActualizacionPermitida();
        validarDatos(idPaciente, idMedico, fechaCita, sintomas);

        this.idPaciente = idPaciente;
        this.idMedico = idMedico;
        this.fechaCita = fechaCita;
        this.sintomas = sintomas.trim();
    }

    public void actualizarEstadoCita(EstadoCita nuevoEstado){
        validarActualizacionPermitida();

        if(nuevoEstado == null)
            throw new DatoInvalidoException("El nuevo estado de la cita es requerida");

        if(!estadoCita.puedeCambiarA(nuevoEstado))
            throw new ConflictoException("La cita con estado " + estadoCita
                    + " solo puede cambiar a " + estadoCita.puedeCambiar());

        this.estadoCita = nuevoEstado;
    }

    public void eliminar(){
        validarEliminacionPermitida();

        this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }

    public static Cita crear(Long idPaciente, Long idMedico, LocalDateTime fechaCita, String sintomas){
        validarDatos(idPaciente, idMedico, fechaCita, sintomas);

        return Cita.builder()
                .idPaciente(idPaciente)
                .idMedico(idMedico)
                .fechaCita(fechaCita)
                .sintomas(sintomas.trim())
                .estadoCita(EstadoCita.PENDIENTE)
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();
    }
}
