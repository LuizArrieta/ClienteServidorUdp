package com.lcam.conversion.adaptadores.notificacion;

import com.lcam.conversion.dominio.enums.EstadoConexion;
import com.lcam.conversion.dominio.modelos.EventoCliente;
import com.lcam.conversion.dominio.modelos.ResultadoConversion;
import com.lcam.conversion.dominio.puertos.salida.PuertoNotificacionCliente;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Adaptador de salida que difunde las notificaciones a los observadores.
 *
 * <p>Usa CopyOnWriteArrayList porque las notificaciones se emiten desde el hilo
 * de red mientras la interfaz puede registrarse desde el hilo de Swing.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class AdaptadorNotificacionCliente implements PuertoNotificacionCliente {

  private final CopyOnWriteArrayList<ObservadorCliente> observadores = new CopyOnWriteArrayList<>();

  public void registrar(final ObservadorCliente observador) {
    observadores.addIfAbsent(Objects.requireNonNull(observador, "El observador es obligatorio."));
  }

  public void remover(final ObservadorCliente observador) {
    observadores.remove(observador);
  }

  @Override
  public void notificarEstado(final EstadoConexion estado, final String endpoint) {
    Objects.requireNonNull(estado, "El estado es obligatorio.");
    Objects.requireNonNull(endpoint, "El endpoint es obligatorio.");
    observadores.forEach(observador -> observador.onEstado(estado, endpoint));
  }

  @Override
  public void notificarEvento(final EventoCliente evento) {
    Objects.requireNonNull(evento, "El evento es obligatorio.");
    observadores.forEach(observador -> observador.onEvento(evento));
  }

  @Override
  public void notificarResultado(final ResultadoConversion resultado) {
    Objects.requireNonNull(resultado, "El resultado es obligatorio.");
    observadores.forEach(observador -> observador.onResultado(resultado));
  }

  @Override
  public void notificarError(final String mensaje) {
    Objects.requireNonNull(mensaje, "El mensaje es obligatorio.");
    observadores.forEach(observador -> observador.onError(mensaje));
  }
}
