package com.lcam.conversion.dominio.excepciones;

import java.io.Serial;

/**
 * Excepcion lanzada cuando no puede construirse una conversion valida.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class ConversionInvalidaException extends DominioException {

  @Serial
  private static final long serialVersionUID = 1L;

  public ConversionInvalidaException(final String mensaje) {
    super(mensaje);
  }
}
