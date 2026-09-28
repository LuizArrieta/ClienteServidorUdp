package com.lcam.conversion.dominio.puertos.salida;

import com.lcam.conversion.aplicacion.excepciones.ServidorRedException;
import com.lcam.conversion.dominio.vo.PuertoRed;

/**
 * Puerto de salida para el control de la infraestructura de red del servidor.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public interface ControladorServidorRedPort {
  void iniciar(PuertoRed puerto) throws ServidorRedException;

  void detener();

  boolean isActivo();

  PuertoRed getPuertoActual();
}
