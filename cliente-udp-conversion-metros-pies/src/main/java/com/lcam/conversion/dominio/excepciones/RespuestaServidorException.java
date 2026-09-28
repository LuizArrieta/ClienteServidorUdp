package com.lcam.conversion.dominio.excepciones;

import java.io.Serial;

/**
 * Excepcion lanzada cuando la respuesta del servidor es invalida.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class RespuestaServidorException extends DominioException {

  @Serial
  private static final long serialVersionUID = 1L;

  public RespuestaServidorException(final String mensaje) {
    super(mensaje);
  }
}
