package com.lcam.conversion.aplicacion.puertos.entrada;

import com.lcam.conversion.aplicacion.dto.ConvertirCommand;
import com.lcam.conversion.aplicacion.dto.ResultadoConversionDto;

/**
 * Puerto de entrada (caso de uso) para la conversion de metros a pies.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public interface ConvertirInputPort {
  ResultadoConversionDto convertir(ConvertirCommand comando);
}
