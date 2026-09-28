package com.lcam.conversion.aplicacion.puertos.entrada;

import com.lcam.conversion.aplicacion.dto.ProcesarPeticionUdpCommand;

/**
 * Puerto de entrada para procesar las peticiones recibidas por el listener UDP.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public interface ProcesarPeticionUdpInputPort {
  void procesar(ProcesarPeticionUdpCommand comando);
}
