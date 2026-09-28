package com.lcam.conversion.dominio.puertos.salida;

import com.lcam.conversion.dominio.modelos.RespuestaCliente;

/**
 * Puerto de salida para la comunicacion de red con clientes.
 * Cumple el estandar hexagonal: recibe exclusivamente objetos del dominio.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public interface PuertoSalidaRed {
  void enviarRespuesta(RespuestaCliente respuesta);
}
