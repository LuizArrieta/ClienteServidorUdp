package com.lcam.conversion.dominio.excepciones;

import java.io.Serial;

/**
 * Excepcion lanzada cuando la longitud en metros es invalida.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class MetrosIncorrectosException extends DominioException {

  @Serial
  private static final long serialVersionUID = 1L;

  public MetrosIncorrectosException(final String mensaje) {
    super(mensaje);
  }
}
