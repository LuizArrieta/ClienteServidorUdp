package com.lcam.conversion.dominio.excepciones;

import java.io.Serial;

/**
 * Excepcion lanzada cuando una respuesta al cliente es incoherente.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class RespuestaInvalidaException extends DominioException {

  @Serial
  private static final long serialVersionUID = 1L;

  public RespuestaInvalidaException(final String mensaje) {
    super(mensaje);
  }
}
