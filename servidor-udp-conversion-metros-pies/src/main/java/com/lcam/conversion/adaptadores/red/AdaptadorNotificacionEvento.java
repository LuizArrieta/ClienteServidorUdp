package com.lcam.conversion.adaptadores.red;

import com.lcam.conversion.dominio.enums.EstadoServidor;
import com.lcam.conversion.dominio.modelos.EventoServidor;
import com.lcam.conversion.dominio.puertos.salida.PuertoNotificacionEvento;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Adaptador de salida para publicar eventos del dominio hacia la interfaz
 * grafica u otros observadores.
 *
 * <p>Usa CopyOnWriteArrayList porque la lista se lee desde el hilo receptor
 * UDP mientras puede modificarse desde el hilo de la GUI.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class AdaptadorNotificacionEvento implements PuertoNotificacionEvento {

  private final List<ObservadorServidor> observadores = new CopyOnWriteArrayList<>();

  public void registrarObservador(final ObservadorServidor observador) {
    if (Objects.nonNull(observador) && !observadores.contains(observador)) {
      observadores.add(observador);
    }
  }

  public void removerObservador(final ObservadorServidor observador) {
    observadores.remove(observador);
  }

  @Override
  public void notificarEvento(final EventoServidor evento) {
    Objects.requireNonNull(evento, "El evento es obligatorio.");
    for (final ObservadorServidor observador : observadores) {
      observador.onEvento(evento);
    }
  }

  @Override
  public void notificarCambioEstado(final EstadoServidor nuevoEstado, final int puerto) {
    Objects.requireNonNull(nuevoEstado, "El estado es obligatorio.");
    for (final ObservadorServidor observador : observadores) {
      observador.onCambioEstado(nuevoEstado, puerto);
    }
  }
}
