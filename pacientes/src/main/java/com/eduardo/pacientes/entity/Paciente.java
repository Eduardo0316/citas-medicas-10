package com.eduardo.pacientes.entity;

import com.eduardo.commons.enums.EstadoRegistro;
import com.eduardo.commons.exceptions.ConflictoException;
import com.eduardo.commons.utils.StringCustomUtils;
import com.eduardo.commons.utils.ValoresNumericos;
import com.sun.jdi.DoubleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Locale;

@Entity
@Table(name = "PACIENTES")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Builder
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PACIENTE")
    private Long id;

    @Column(name = "NOMBRE", length = 50, nullable = false)
    private String nombre;

    @Column(name = "APELLIDO_PATERNO", length = 50, nullable = false)
    private String apellidoPaterno;

    @Column(name = "APELLIDO_MATERNO", length = 50, nullable = false)
    private String apellidoMaterno;

    @Column(name = "EDAD", nullable = false)
    private Short edad;

    @Column(name = "PESO", nullable = false)
    private Double peso;

    @Column(name = "ESTATURA", nullable = false)
    private Double estatura;

    @Column(name = "IMC", nullable = false)
    private Double imc;

    @Column(name = "EMAIL", length = 100, nullable = false)
    private String email;

    @Column(name = "NUM_EXPEDIENTE", length = 20, nullable = false)
    private String numExpediente;

    @Column(name = "TELEFONO", length = 10, nullable = false)
    private String telefono;

    @Column(name = "DIRECCION", length = 150, nullable = false)
    private String direccion;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "ESTADO_REGISTRO", nullable = false)
    private EstadoRegistro estadoRegistro;

    public static void validarDatos(String nombre, String apellidoPaterno, String apellidoMaterno, Short edad,
                    Double peso, Double estatura, String email,
                    String telefono, String direccion) {
        StringCustomUtils.validarTamanio(nombre, 1, 50,
                "El nombre es requerido y debe tener entre 1 y 50 caracteres");

        StringCustomUtils.validarTamanio(apellidoPaterno, 1, 50,
                "El apellido paterno es requerido y debe tener entre 1 y 50 caracteres");

        StringCustomUtils.validarTamanio(apellidoMaterno, 1, 50,
                "El apellido materno es requerido y debe tener entre 1 y 50 caracteres");

        ValoresNumericos.validarRango(edad, 1, 100,
                "La edad es requerida y debe ser entre 1 y 100 años");

        ValoresNumericos.validarRango(peso, 0.1, 200,
                "El peso es requerido y debe ser entre 0.1 y 200 kg");

        ValoresNumericos.validarRango(estatura, 1.0, 2.0,
                "La estatura es requerida y debe ser entre 1.0 y 2.0 mtrs");

        StringCustomUtils.validarTamanio(email, 1, 100,
                "El email es requerido y debe contener entre 1 y 100 caracteres");

        StringCustomUtils.validarTamanio(telefono, 10, 10,
                "El telefono es requerido y debe tener exactemente 10 digitos (0-9)");

        StringCustomUtils.validarTamanio(direccion, 1, 150,
                "La dirección es requerida y debe contener entre 1 y 150 caracteres");
    }

    private void validarNoEliminado(){
        if(this.estadoRegistro == EstadoRegistro.ELIMINADO)
            throw new ConflictoException("El médico ya esta eliminado");
    }

    public void eliminar(){
        validarNoEliminado();
        this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }

    public void actualizar(String nombre, String apellidoPaterno, String apellidoMaterno, Short edad,
    Double peso, Double estatura, String email,
    String telefono, String direccion) {
        validarNoEliminado();
        validarDatos(nombre, apellidoPaterno, apellidoMaterno, edad, peso, estatura, email, telefono, direccion);

        this.nombre = nombre.trim();
        this.apellidoPaterno = apellidoPaterno.trim();
        this.apellidoMaterno = apellidoMaterno.trim();
        this.edad = edad;
        this.peso = peso;
        this.estatura = estatura;
        this.imc = (peso / (estatura * estatura));
        this.email = email.trim().toLowerCase();
        this.telefono = telefono;
        //El expediente tiene que cambiar ?????
        //Además podria hacer una funcion que calcule este pedo
//        this.numExpediente = telefono.chars().mapToObj(c -> (char) c + "X").reduce("", String::concat);
        this.direccion = direccion;
    }

    public static Paciente crear(String nombre, String apellidoPaterno, String apellidoMaterno, Short edad,
                                 Double peso, Double estatura, String email,
                                 String telefono, String direccion){
        validarDatos(nombre, apellidoPaterno, apellidoMaterno, edad, peso, estatura, email, telefono, direccion);

        return Paciente.builder()
                .nombre(nombre.trim())
                .apellidoPaterno(apellidoPaterno.trim())
                .apellidoMaterno(apellidoMaterno.trim())
                .edad(edad)
                .peso(peso)
                .estatura(estatura)
                .imc(peso / (estatura * estatura))
                .email(email.trim().toLowerCase())
                .numExpediente(telefono.chars().mapToObj(
                        c -> (char) c + "X").reduce("", String::concat))
                .telefono(telefono)
                .direccion(direccion)
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();
    }
}
