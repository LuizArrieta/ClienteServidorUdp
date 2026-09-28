package com.lcam.conversion.adaptadores.red;

import com.lcam.conversion.dominio.enums.EstadoServidor;
import com.lcam.conversion.dominio.modelos.EventoServidor;

/**
 * Interfaz de observador para componentes interesados en eventos y estados
 * del servidor (patron Observer).
 *
 * @author Luis Camilo Arrieta Muriel
 */
public interface ObservadorServidor {
  void onEvento(EventoServidor evento);

  void onCambioEstado(EstadoServidor nuevoEstado, int puerto);
}
