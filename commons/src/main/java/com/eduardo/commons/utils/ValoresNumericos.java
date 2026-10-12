package com.eduardo.commons.utils;

import java.math.BigDecimal;

import com.eduardo.commons.exceptions.DatoInvalidoException;
public class ValoresNumericos {
    public static <N> void validarNumeroRequerido(N numero, String mensaje){
        if(numero == null)
            throw new DatoInvalidoException(mensaje);
    }

    public static void validarEnteroPositivo(Integer numero, String mensaje){
        validarNumeroRequerido(numero, mensaje);

        if (numero <= 0)
            throw new DatoInvalidoException(mensaje);
    }

    public static <N extends Number> void validarPositivo(
            N numero,
            String mensajeError
    ) {
        validarNumeroRequerido(numero, mensajeError);

        BigDecimal valor = convertir(numero);

        if (valor.compareTo(BigDecimal.ZERO) <= 0)
            throw new DatoInvalidoException(mensajeError);
    }

    public static <N extends Number> void validarRango(
            N numero,
            N minimo,
            N maximo,
            String mensajeError
    ) {
        validarNumeroRequerido(numero, mensajeError);
        validarNumeroRequerido(minimo, mensajeError);
        validarNumeroRequerido(maximo, mensajeError);

        BigDecimal valor = convertir(numero);
        BigDecimal min = convertir(minimo);
        BigDecimal max = convertir(maximo);

        if (valor.compareTo(min) < 0 || valor.compareTo(max) > 0)
            throw new DatoInvalidoException(mensajeError);
    }

    private static BigDecimal convertir(Number numero) {
        return new BigDecimal(numero.toString());
    }
}