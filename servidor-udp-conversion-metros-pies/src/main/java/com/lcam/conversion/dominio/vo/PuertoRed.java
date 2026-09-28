package com.lcam.conversion.dominio.vo;

import com.lcam.conversion.dominio.excepciones.PuertoIncorrectoException;

/**
 * Value Object inmutable que representa y valida un puerto de red UDP del servidor.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public record PuertoRed(int valor) {

  public PuertoRed {
    if (valor < 1024 || valor > 65535) {
      throw new PuertoIncorrectoException("El puerto debe ser un numero entre 1024 y 65535.");
    }
  }
}
