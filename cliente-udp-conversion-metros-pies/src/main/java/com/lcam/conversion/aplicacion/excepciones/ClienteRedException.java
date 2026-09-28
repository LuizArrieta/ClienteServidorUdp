package com.lcam.conversion.aplicacion.excepciones;

import java.io.Serial;

/**
 * Excepcion de la capa de aplicacion ante fallos de comunicacion UDP.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class ClienteRedException extends Exception {

  @Serial
  private static final long serialVersionUID = 1L;

  public ClienteRedException(final String mensaje) {
    super(mensaje);
  }

  public ClienteRedException(final String mensaje, final Throwable causa) {
    super(mensaje, causa);
  }
}
