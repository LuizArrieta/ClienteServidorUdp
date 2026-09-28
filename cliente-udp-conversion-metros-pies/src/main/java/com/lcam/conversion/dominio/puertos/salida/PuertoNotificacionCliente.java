package com.lcam.conversion.dominio.puertos.salida;

import com.lcam.conversion.dominio.enums.EstadoConexion;
import com.lcam.conversion.dominio.modelos.EventoCliente;
import com.lcam.conversion.dominio.modelos.ResultadoConversion;

/**
 * Puerto de salida para notificar a la interfaz (patron Observer).
 *
 * @author Luis Camilo Arrieta Muriel
 */
public interface PuertoNotificacionCliente {

  void notificarEstado(EstadoConexion estado, String endpoint);

  void notificarEvento(EventoCliente evento);

  void notificarResultado(ResultadoConversion resultado);

  void notificarError(String mensaje);
}
