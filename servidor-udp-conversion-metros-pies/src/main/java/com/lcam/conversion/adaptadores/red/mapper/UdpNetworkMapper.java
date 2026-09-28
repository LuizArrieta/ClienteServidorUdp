package com.lcam.conversion.adaptadores.red.mapper;

import com.lcam.conversion.adaptadores.red.response.UdpResponse;
import com.lcam.conversion.dominio.excepciones.RespuestaInvalidaException;
import com.lcam.conversion.dominio.modelos.RespuestaCliente;
import com.lcam.conversion.dominio.modelos.Resultado;
import java.util.Objects;

/**
 * Mapper del adaptador de red. Responsabilidad unica: convertir objetos de
 * dominio (RespuestaCliente) a respuestas formateadas para el protocolo UDP.
 *
 * <p>Formato de salida:
 * <ul>
 *   <li>CONECTADO_OK;&lt;mensaje&gt;</li>
 *   <li>DESCONECTADO_OK;&lt;mensaje&gt;</li>
 *   <li>OK_CONVERSION;&lt;pies&gt;;&lt;metros&gt;;&lt;clasificacion&gt;;&lt;descripcion&gt;</li>
 *   <li>ERROR;&lt;mensaje&gt;</li>
 * </ul>
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class UdpNetworkMapper {

  public UdpResponse toNetworkResponse(final RespuestaCliente respuesta) {
    if (Objects.isNull(respuesta)) {
      throw new RespuestaInvalidaException("No se puede serializar una respuesta nula.");
    }

    final String payload = switch (respuesta.getTipo()) {
      case CONECTADO -> "CONECTADO_OK;" + respuesta.getMensaje();
      case DESCONECTADO -> "DESCONECTADO_OK;" + respuesta.getMensaje();
      case OK_CONVERSION -> buildPayloadConversion(respuesta.getResultado());
      case ERROR ->
        "ERROR;" + (Objects.nonNull(respuesta.getMensaje())
            ? respuesta.getMensaje()
            : "Error desconocido.");
    };

    return new UdpResponse(payload, respuesta.getDestinatario().ip(), respuesta.getDestinatario().puerto());
  }

  private static String buildPayloadConversion(final Resultado resultado) {
    if (Objects.isNull(resultado)) {
      throw new RespuestaInvalidaException("No se puede serializar una conversion sin resultado.");
    }
    return String.format(
        "OK_CONVERSION;%s;%s;%s;%s",
        resultado.getPiesFormateado(),
        resultado.getMetrosFormateado(),
        resultado.getClasificacion(),
        resultado.getDescripcion());
  }
}
