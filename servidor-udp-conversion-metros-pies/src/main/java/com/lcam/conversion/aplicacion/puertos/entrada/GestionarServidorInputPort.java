package com.lcam.conversion.aplicacion.puertos.entrada;

import com.lcam.conversion.aplicacion.excepciones.ServidorRedException;

/**
 * Puerto de entrada (caso de uso) para el control del ciclo de vida del
 * servidor UDP. Invocado por el entrypoint de la interfaz grafica.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public interface GestionarServidorInputPort {
  void iniciarServidor(int puerto) throws ServidorRedException;

  void detenerServidor();

  boolean estaCorriendo();

  int getPuertoActual();
}
