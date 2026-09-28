package com.lcam.conversion.aplicacion.excepciones;

import java.io.Serial;

/**
 * Excepcion del caso de uso de gestion del servidor ante fallos de red.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class ServidorRedException extends Exception {

  @Serial
  private static final long serialVersionUID = 1L;

  public ServidorRedException(final String mensaje, final Throwable causa) {
    super(mensaje, causa);
  }
}
