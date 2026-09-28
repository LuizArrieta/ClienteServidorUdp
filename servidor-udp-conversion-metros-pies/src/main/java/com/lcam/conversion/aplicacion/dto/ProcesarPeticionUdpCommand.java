package com.lcam.conversion.aplicacion.dto;

/**
 * Comando DTO para procesar una peticion entrante desde la red UDP.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public record ProcesarPeticionUdpCommand(String ipCliente, int puertoCliente, String mensaje) {
}
