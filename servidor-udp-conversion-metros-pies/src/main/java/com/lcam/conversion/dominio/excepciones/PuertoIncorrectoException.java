package com.lcam.conversion.dominio.excepciones;

import java.io.Serial;

/**
 * Excepcion lanzada cuando un puerto no pertenece al rango permitido.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class PuertoIncorrectoException extends DominioException {

  @Serial
  private static final long serialVersionUID = 1L;

  public PuertoIncorrectoException(final String mensaje) {
    super(mensaje);
  }
}
