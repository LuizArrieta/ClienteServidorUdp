package com.lcam.conversion.dominio.puertos.salida;

import com.lcam.conversion.dominio.enums.EstadoServidor;
import com.lcam.conversion.dominio.modelos.EventoServidor;

/**
 * Puerto de salida para la emision de eventos y cambios de estado del servidor
 * hacia observadores externos (interfaz grafica, logging).
 *
 * @author Luis Camilo Arrieta Muriel
 */
public interface PuertoNotificacionEvento {
  void notificarEvento(EventoServidor evento);

  void notificarCambioEstado(EstadoServidor nuevoEstado, int puerto);
}
