package com.lcam.conversion.aplicacion.excepciones;

import java.io.Serial;

/**
 * Excepcion lanzada cuando un caso de uso recibe un comando invalido.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class ComandoInvalidoException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;

  public ComandoInvalidoException(final String mensaje) {
    super(mensaje);
  }
}
