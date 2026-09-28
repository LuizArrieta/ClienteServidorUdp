package com.lcam.conversion.dominio.modelos;

import com.lcam.conversion.dominio.vo.Metros;
import java.util.Objects;

/**
 * Modelo de dominio con los datos que el cliente envia para convertir.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public record DatosConversion(Metros metros) {

  public DatosConversion {
    Objects.requireNonNull(metros, "La longitud en metros es obligatoria.");
  }
}
