package com.lcam.conversion.aplicacion.dto;

/**
 * DTO que contiene el resultado de la conversion para la capa de aplicacion.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public record ResultadoConversionDto(
    double pies, String piesFormateado, String metrosFormateado,
    String clasificacion, String descripcion) {
}
