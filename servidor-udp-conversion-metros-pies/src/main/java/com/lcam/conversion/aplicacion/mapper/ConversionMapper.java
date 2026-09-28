package com.lcam.conversion.aplicacion.mapper;

import com.lcam.conversion.aplicacion.dto.ConvertirCommand;
import com.lcam.conversion.aplicacion.dto.ResultadoConversionDto;
import com.lcam.conversion.aplicacion.excepciones.ComandoInvalidoException;
import com.lcam.conversion.dominio.modelos.Conversion;
import com.lcam.conversion.dominio.modelos.Resultado;
import com.lcam.conversion.dominio.vo.Metros;
import java.util.Objects;

/**
 * Mapper de la capa de aplicacion. Responsabilidad unica: convertir
 * comandos/DTOs a entidades del dominio y viceversa.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class ConversionMapper {

  public Conversion toDomain(final ConvertirCommand comando) {
    if (Objects.isNull(comando)) {
      throw new ComandoInvalidoException("El comando de conversion no puede ser nulo.");
    }
    return new Conversion(new Metros(comando.metros()));
  }

  public Conversion toDomain(final double valorMetros) {
    return new Conversion(new Metros(valorMetros));
  }

  public ResultadoConversionDto toDto(final Resultado resultado) {
    if (Objects.isNull(resultado)) {
      throw new ComandoInvalidoException("El resultado de la conversion no puede ser nulo.");
    }
    return new ResultadoConversionDto(
        resultado.getPies(),
        resultado.getPiesFormateado(),
        resultado.getMetrosFormateado(),
        resultado.getClasificacion(),
        resultado.getDescripcion());
  }
}
