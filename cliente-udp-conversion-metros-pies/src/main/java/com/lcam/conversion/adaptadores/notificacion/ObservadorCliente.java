package com.lcam.conversion.adaptadores.notificacion;

import com.lcam.conversion.dominio.enums.EstadoConexion;
import com.lcam.conversion.dominio.modelos.EventoCliente;
import com.lcam.conversion.dominio.modelos.ResultadoConversion;

/**
 * Contrato que implementa la interfaz grafica para recibir notificaciones.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public interface ObservadorCliente {

  void onEstado(EstadoConexion estado, String endpoint);

  void onEvento(EventoCliente evento);

  void onResultado(ResultadoConversion resultado);

  void onError(String mensaje);
}
