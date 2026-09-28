package com.lcam.conversion.dominio.excepciones;

import java.io.Serial;

/**
 * Excepcion lanzada cuando un resultado viola sus invariantes.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class ResultadoInvalidoException extends DominioException {

  @Serial
  private static final long serialVersionUID = 1L;

  public ResultadoInvalidoException(final String mensaje) {
    super(mensaje);
  }
}
