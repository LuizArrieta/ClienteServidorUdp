package com.lcam.conversion.dominio.excepciones;

import java.io.Serial;

/**
 * Excepcion lanzada cuando el destino del servidor es invalido.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class DestinoIncorrectoException extends DominioException {

  @Serial
  private static final long serialVersionUID = 1L;

  public DestinoIncorrectoException(final String mensaje) {
    super(mensaje);
  }
}
