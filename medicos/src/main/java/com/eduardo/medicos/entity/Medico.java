package com.eduardo.medicos.entity;

import com.eduardo.commons.enums.DisponibilidadMedico;
import com.eduardo.commons.enums.EspecialidadMedico;
import com.eduardo.commons.enums.EstadoRegistro;
import com.eduardo.commons.exceptions.ConflictoException;
import com.eduardo.commons.exceptions.DatoInvalidoException;
import com.eduardo.commons.utils.StringCustomUtils;
import com.eduardo.commons.utils.ValoresNumericos;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MEDICOS")
@AllArgsConstructor
@NoArgsConstructor
@Builder @Getter
public class Medico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MEDICO")
    private Long id;

    @Column(name = "NOMBRE", length = 50, nullable = false)
    private String nombre;

    @Column(name = "APELLIDO_PATERNO", length = 50, nullable = false)
    private String apellidoPaterno;

    @Column(name = "APELLIDO_MATERNO", length = 50, nullable = false)
    private String apellidoMaterno;

    @Column(name = "EDAD", nullable = false)
    private Short edad;

    @Column(name = "EMAIL", length = 100, nullable = false)
    private String email;

    @Column(name = "TELEFONO", length = 10, nullable = false)
    private String telefono;

    @Column(name = "CEDULA_PROFESIONAL", length = 12, nullable = false)
    private String cedulaProfesional;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESPECIALIDAD", nullable = false)
    private EspecialidadMedico especialidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "DISPONIBILIDAD", nullable = false)
    private DisponibilidadMedico disponibilidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_REGISTRO", nullable = false)
    private EstadoRegistro estadoRegistro;

    private static void validarDatos(String nombre, String apellidoPaterno, String apellidoMaterno, Short edad, String email,
                              String telefono, String cedulaProfesional){
        StringCustomUtils.validarTamanio(nombre, 1, 50,
                "El nombre es requerido y debe tener entre 1 y 50 caracteres");

        StringCustomUtils.validarTamanio(apellidoPaterno, 1, 50,
                "El apellido paterno es requerido y debe tener entre 1 y 50 caracteres");

        StringCustomUtils.validarTamanio(apellidoMaterno, 1, 50,
                "El apellido materno es requerido y debe tener entre 1 y 50 caracteres");

        StringCustomUtils.validarTamanio(email, 1, 100,
                "El email es requerido y debe contener entre 1 y 100 caracteres");

        StringCustomUtils.validarTamanio(telefono, 10, 10,
                "El telefono es requerido y debe tener exactemente 10 digitos (0-9)");

        StringCustomUtils.validarTamanio(cedulaProfesional, 12, 12,
                "La cedula profesional es requerida y debe tener exactamente 12 caracteres");

        ValoresNumericos.validarRango(edad, 18, 100,
                "La edad es requerida y debe ser entre 18 y 100 años");
    }

    private void validarNoEliminado(){
        if(this.estadoRegistro == EstadoRegistro.ELIMINADO)
            throw new ConflictoException("El médico ya esta eliminado");
    }

    public void eliminar(){
        validarNoEliminado();
        this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }

    public void actualizarEspecialidad(EspecialidadMedico especialidad){
        validarNoEliminado();
        if(especialidad == null)
            throw new DatoInvalidoException("La especialidad es requerida");

        this.especialidad = especialidad;
    }

    public void actualizarDisponibilidad(DisponibilidadMedico disponibilidad){
        validarNoEliminado();
        if(disponibilidad == null)
            throw new DatoInvalidoException("La disponibilidad es requerida");

        this.disponibilidad = disponibilidad;
    }

    public void actualizar(String nombre, String apellidoPaterno, String apellidoMaterno,
                           Short edad, String email, String telefono, String cedulaProfesional, EspecialidadMedico especialidad) {
        validarNoEliminado();
        validarDatos(nombre, apellidoPaterno, apellidoMaterno, edad, email, telefono, cedulaProfesional);
        actualizarEspecialidad(especialidad);
        this.nombre = nombre.trim();
        this.apellidoPaterno = apellidoPaterno.trim();
        this.apellidoMaterno = apellidoMaterno.trim();
        this.edad = edad;
        this.email = email.trim().toLowerCase();
        this.telefono = telefono.trim();
        this.cedulaProfesional = cedulaProfesional.trim();
    }

    public static Medico crear(String nombre, String apellidoPaterno, String apellidoMaterno,
                               Short edad, String email, String telefono, String cedulaProfesional) {
        validarDatos(nombre, apellidoPaterno, apellidoMaterno, edad, email, telefono, cedulaProfesional);

        return Medico.builder()
                .nombre(nombre.trim())
                .apellidoPaterno(apellidoPaterno.trim())
                .apellidoMaterno(apellidoMaterno.trim())
                .edad(edad)
                .email(email.trim().toLowerCase())
                .telefono(telefono.trim())
                .cedulaProfesional(cedulaProfesional.trim())
                .disponibilidad(DisponibilidadMedico.DISPONIBLE)
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();
    }
}
