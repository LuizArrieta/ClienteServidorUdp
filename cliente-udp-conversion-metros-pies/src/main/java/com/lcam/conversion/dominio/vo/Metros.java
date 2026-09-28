package com.lcam.conversion.dominio.vo;

import com.lcam.conversion.dominio.excepciones.MetrosIncorrectosException;

/**
 * Value Object inmutable que valida la longitud en metros antes de enviarla.
 * Esta validacion ocurre en el cliente para no gastar red con datos invalidos,
 * pero el calculo real siempre lo hace el servidor.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public record Metros(double valor) {

  public Metros {
    if (!Double.isFinite(valor) || valor < 0) {
      throw new MetrosIncorrectosException(
          "La longitud debe ser un numero finito mayor o igual a cero.");
    }
  }
}
