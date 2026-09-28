package com.lcam.conversion.aplicacion.servicios;

import com.lcam.conversion.aplicacion.dto.ConvertirCommand;
import com.lcam.conversion.aplicacion.dto.ResultadoConversionDto;
import com.lcam.conversion.aplicacion.mapper.ConversionMapper;
import com.lcam.conversion.aplicacion.puertos.entrada.ConvertirInputPort;
import com.lcam.conversion.dominio.modelos.Conversion;
import com.lcam.conversion.dominio.modelos.Resultado;
import java.util.Objects;

/**
 * Servicio de aplicacion que implementa el caso de uso de convertir metros a pies.
 * Responsabilidad unica: orquestar la conversion del comando a dominio, invocar
 * la logica del dominio y retornar el resultado como DTO.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class ConvertirService implements ConvertirInputPort {

  private final ConversionMapper conversionMapper;

  public ConvertirService(final ConversionMapper conversionMapper) {
    this.conversionMapper = Objects.requireNonNull(conversionMapper, "El mapper es obligatorio.");
  }

  @Override
  public ResultadoConversionDto convertir(final ConvertirCommand comando) {
    final Conversion conversion = conversionMapper.toDomain(comando);
    final Resultado resultado = conversion.convertir();
    return conversionMapper.toDto(resultado);
  }
}
