package com.eduardo.auth.dto;

public record CustomErrorResponse(
        int codigo,
        String mensaje
) { }
