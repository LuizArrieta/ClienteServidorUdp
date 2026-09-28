package com.lcam.conversion.dominio.modelos;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Evento interno del cliente para el registro visible en la interfaz.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public record EventoCliente(LocalDateTime fechaHora, String categoria, String descripcion) {

  public EventoCliente {
    Objects.requireNonNull(fechaHora, "La fecha es obligatoria.");
    Objects.requireNonNull(categoria, "La categoria es obligatoria.");
    Objects.requireNonNull(descripcion, "La descripcion es obligatoria.");
  }

  public EventoCliente(final String categoria, final String descripcion) {
    this(LocalDateTime.now(), categoria, descripcion);
  }
}
