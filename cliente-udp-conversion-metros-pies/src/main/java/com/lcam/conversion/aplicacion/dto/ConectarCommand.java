package com.lcam.conversion.aplicacion.dto;

/**
 * Comando DTO para solicitar la conexion logica con el servidor.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public record ConectarCommand(String host, int puerto) {
}
