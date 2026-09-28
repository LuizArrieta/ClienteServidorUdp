package com.lcam.conversion.dominio.modelos;

import com.lcam.conversion.dominio.enums.EscalaLongitud;
import com.lcam.conversion.dominio.excepciones.ResultadoInvalidoException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;

/**
 * Representa el resultado inmutable de una conversion de metros a pies.
 * Responsabilidad unica: encapsular los datos del resultado y su formateo.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class Resultado {

  private final double pies;
  private final double metrosOriginales;
  private final String clasificacion;
  private final String descripcion;
  private final EscalaLongitud escala;

  public Resultado(
      final double pies,
      final double metrosOriginales,
      final String clasificacion,
      final String descripcion,
      final EscalaLongitud escala) {
    if (!Double.isFinite(pies)
        || !Double.isFinite(metrosOriginales)
        || Objects.isNull(clasificacion)
        || Objects.isNull(descripcion)
        || Objects.isNull(escala)) {
      throw new ResultadoInvalidoException("El resultado de la conversion contiene datos invalidos.");
    }
    this.pies = pies;
    this.metrosOriginales = metrosOriginales;
    this.clasificacion = clasificacion;
    this.descripcion = descripcion;
    this.escala = escala;
  }

  public Resultado(final double pies, final double metrosOriginales,
      final String clasificacion, final String descripcion) {
    this(pies, metrosOriginales, clasificacion, descripcion, EscalaLongitud.dePies(pies));
  }

  public double getPies() {
    return pies;
  }

  public double getMetrosOriginales() {
    return metrosOriginales;
  }

  public String getClasificacion() {
    return clasificacion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public EscalaLongitud getEscala() {
    return escala;
  }

  public String getPiesFormateado() {
    return formatear(pies);
  }

  public String getMetrosFormateado() {
    return formatear(metrosOriginales);
  }

  private static String formatear(final double valor) {
    final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
    final DecimalFormat df = new DecimalFormat("#.####", symbols);
    return df.format(valor);
  }
}
