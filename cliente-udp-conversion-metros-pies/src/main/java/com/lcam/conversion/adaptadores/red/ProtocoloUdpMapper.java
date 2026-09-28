package com.lcam.conversion.adaptadores.red;

import com.lcam.conversion.dominio.excepciones.RespuestaServidorException;
import com.lcam.conversion.dominio.modelos.DatosConversion;
import com.lcam.conversion.dominio.modelos.ResultadoConversion;
import java.util.Locale;
import java.util.Objects;

/**
 * Traduce entre el dominio y el protocolo de texto que viaja en los datagramas.
 *
 * <p>Peticiones que emite el cliente:
 * <ul>
 *   <li>{@code CONECTAR}</li>
 *   <li>{@code DESCONECTAR}</li>
 *   <li>{@code CONVERTIR;<metros>}</li>
 * </ul>
 *
 * <p>Respuestas que interpreta del servidor:
 * <ul>
 *   <li>{@code CONECTADO_OK;<mensaje>}</li>
 *   <li>{@code OK_CONVERSION;<pies>;<metros>;<clasificacion>;<descripcion>}</li>
 *   <li>{@code ERROR;<mensaje>}</li>
 * </ul>
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class ProtocoloUdpMapper {

  public static final String CONECTAR = "CONECTAR";
  public static final String DESCONECTAR = "DESCONECTAR";

  private static final String PREFIJO_CONECTADO = "CONECTADO_OK;";
  private static final String PREFIJO_ERROR = "ERROR;";
  private static final String TOKEN_OK_CONVERSION = "OK_CONVERSION";
  private static final int PARTES_ESPERADAS = 5;

  public String serializarConversion(final DatosConversion datos) {
    Objects.requireNonNull(datos, "Los datos de conversion son obligatorios.");
    return String.format(Locale.US, "CONVERTIR;%.4f", datos.metros().valor());
  }

  public void validarConexion(final String respuesta) {
    if (Objects.isNull(respuesta) || !respuesta.startsWith(PREFIJO_CONECTADO)) {
      throw new RespuestaServidorException("Respuesta de conexion inesperada: " + respuesta);
    }
  }

  public ResultadoConversion parsearConversion(final String respuesta) {
    if (Objects.isNull(respuesta) || respuesta.isBlank()) {
      throw new RespuestaServidorException("El servidor envio una respuesta vacia.");
    }
    if (respuesta.startsWith(PREFIJO_ERROR)) {
      throw new RespuestaServidorException(
          "Error del servidor: " + respuesta.substring(PREFIJO_ERROR.length()));
    }

    final String[] partes = respuesta.split(";", -1);
    if (partes.length != PARTES_ESPERADAS || !TOKEN_OK_CONVERSION.equals(partes[0])) {
      throw new RespuestaServidorException("Respuesta de conversion no estructurada: " + respuesta);
    }

    try {
      final double pies = Double.parseDouble(partes[1].replace(',', '.'));
      return new ResultadoConversion(pies, partes[1], partes[2], partes[3], partes[4]);
    } catch (final NumberFormatException excepcion) {
      throw new RespuestaServidorException("El valor en pies recibido no es numerico.");
    }
  }
}
