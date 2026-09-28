package com.lcam.conversion.dominio.modelos;

import com.lcam.conversion.dominio.excepciones.RespuestaServidorException;
import java.util.Objects;

/**
 * Modelo de dominio con el resultado que el servidor devolvio al cliente.
 * El cliente NO calcula estos valores: solo los recibe y valida su integridad.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public record ResultadoConversion(
    double pies, String piesFormateado, String metrosFormateado,
    String clasificacion, String descripcion) {

  public ResultadoConversion {
    if (!Double.isFinite(pies)
        || Objects.isNull(piesFormateado) || piesFormateado.isBlank()
        || Objects.isNull(metrosFormateado) || metrosFormateado.isBlank()
        || Objects.isNull(clasificacion) || clasificacion.isBlank()
        || Objects.isNull(descripcion)) {
      throw new RespuestaServidorException("El resultado recibido esta incompleto o es invalido.");
    }
  }
}
