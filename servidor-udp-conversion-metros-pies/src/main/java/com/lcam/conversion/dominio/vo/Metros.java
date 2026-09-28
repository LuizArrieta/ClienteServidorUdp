package com.lcam.conversion.dominio.vo;

import com.lcam.conversion.dominio.excepciones.MetrosIncorrectosException;

/**
 * Value Object inmutable que encapsula y valida una longitud expresada en metros.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public record Metros(double valor) {

  private static final String MENSAJE_ERROR =
      "Longitud incorrecta: debe ser un numero finito mayor o igual a 0.";

  public Metros {
    if (!Double.isFinite(valor) || valor < 0) {
      throw new MetrosIncorrectosException(MENSAJE_ERROR);
    }
  }
}
