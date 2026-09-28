package com.lcam.conversion.dominio.excepciones;

import java.io.Serial;

/**
 * Excepcion lanzada cuando un destinatario de red no es valido.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class DestinatarioIncorrectoException extends DominioException {

  @Serial
  private static final long serialVersionUID = 1L;

  public DestinatarioIncorrectoException(final String mensaje) {
    super(mensaje);
  }
}
