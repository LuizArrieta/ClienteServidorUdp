package com.lcam.conversion.dominio.modelos;

import com.lcam.conversion.dominio.enums.EscalaLongitud;
import com.lcam.conversion.dominio.excepciones.ConversionInvalidaException;
import com.lcam.conversion.dominio.vo.Metros;
import java.util.Objects;

/**
 * Entidad del dominio para la conversion de metros a pies.
 * Responsabilidad unica: aplicar la formula de conversion del Sistema
 * Internacional al sistema imperial.
 *
 * <p>Factor oficial: 1 metro = 3.28084 pies.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class Conversion {

  /** Factor de conversion oficial de metros a pies. */
  public static final double FACTOR_METRO_A_PIE = 3.28084;

  private final Metros metros;

  public Conversion(final Metros metros) {
    if (Objects.isNull(metros)) {
      throw new ConversionInvalidaException("La longitud en metros es obligatoria para convertir.");
    }
    this.metros = metros;
  }

  /**
   * Convierte la longitud almacenada a pies y determina su escala descriptiva.
   *
   * @return objeto Resultado con el valor en pies, la escala y su descripcion.
   */
  public Resultado convertir() {
    final double pies = metros.valor() * FACTOR_METRO_A_PIE;
    final EscalaLongitud escala = EscalaLongitud.dePies(pies);

    return new Resultado(
        pies, metros.valor(), escala.getClasificacion(), escala.getDescripcion(), escala);
  }

  public Metros getMetros() {
    return metros;
  }
}
