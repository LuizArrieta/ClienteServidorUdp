package com.lcam.conversion.dominio.enums;

/**
 * Clasificacion descriptiva de la magnitud de una longitud convertida.
 * Permite acompaniar el resultado numerico con un contexto comprensible
 * para el usuario final.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public enum EscalaLongitud {
  MINIMA("Longitud minima", "Equivale a una medida menor que un pie."),
  PEQUENA("Longitud pequenia", "Escala tipica de objetos domesticos."),
  MEDIA("Longitud media", "Escala tipica de habitaciones o vehiculos."),
  GRANDE("Longitud grande", "Escala tipica de edificaciones."),
  MUY_GRANDE("Longitud muy grande", "Escala tipica de terrenos o distancias amplias.");

  private final String clasificacion;
  private final String descripcion;

  EscalaLongitud(final String clasificacion, final String descripcion) {
    this.clasificacion = clasificacion;
    this.descripcion = descripcion;
  }

  public String getClasificacion() {
    return clasificacion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  /**
   * Determina la escala segun el valor convertido en pies.
   *
   * @param pies longitud ya convertida.
   * @return la categoria correspondiente.
   */
  public static EscalaLongitud dePies(final double pies) {
    if (pies < 1.0) {
      return MINIMA;
    } else if (pies < 10.0) {
      return PEQUENA;
    } else if (pies < 100.0) {
      return MEDIA;
    } else if (pies < 1000.0) {
      return GRANDE;
    } else {
      return MUY_GRANDE;
    }
  }
}
