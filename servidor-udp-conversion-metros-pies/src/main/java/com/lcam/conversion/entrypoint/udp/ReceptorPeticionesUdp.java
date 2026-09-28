package com.lcam.conversion.entrypoint.udp;

import com.lcam.conversion.adaptadores.red.CanalUdp;
import com.lcam.conversion.aplicacion.dto.ProcesarPeticionUdpCommand;
import com.lcam.conversion.aplicacion.puertos.entrada.ProcesarPeticionUdpInputPort;
import com.lcam.conversion.dominio.modelos.EventoServidor;
import com.lcam.conversion.dominio.puertos.salida.PuertoNotificacionEvento;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Entrypoint primario para las peticiones UDP entrantes. Responsabilidad unica:
 * escuchar datagramas en segundo plano, extraer la carga util y despachar el
 * comando al puerto de entrada.
 *
 * <p><b>Modelo de hilos:</b> se usa un unico hilo demonio dedicado a la
 * recepcion. Esto es suficiente en UDP porque no hay conexiones persistentes:
 * cada datagrama es autonomo y se atiende en milisegundos. La bandera
 * {@code escuchando} es volatile para garantizar visibilidad entre el hilo de
 * la GUI (que la modifica) y el hilo receptor (que la lee).
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class ReceptorPeticionesUdp {

  private static final int BUFFER_SIZE = 2048;

  private final CanalUdp canalUdp;
  private final ProcesarPeticionUdpInputPort inputPort;
  private final PuertoNotificacionEvento notificador;

  private Thread hiloEscucha;
  private volatile boolean escuchando;

  public ReceptorPeticionesUdp(
      final CanalUdp canalUdp,
      final ProcesarPeticionUdpInputPort inputPort,
      final PuertoNotificacionEvento notificador) {
    this.canalUdp = Objects.requireNonNull(canalUdp, "El canal UDP es obligatorio.");
    this.inputPort = Objects.requireNonNull(inputPort, "El puerto de entrada es obligatorio.");
    this.notificador = Objects.requireNonNull(notificador, "El notificador es obligatorio.");
  }

  /** Arranca el hilo demonio de escucha si aun no esta activo. */
  public synchronized void iniciar() {
    if (escuchando) {
      return;
    }
    escuchando = true;
    hiloEscucha = new Thread(
        this::cicloEscucha, "Thread-ReceptorPeticionesUdp-" + canalUdp.getPuertoActual());
    hiloEscucha.setDaemon(true);
    hiloEscucha.start();
  }

  /** Solicita la detencion del hilo e interrumpe la espera bloqueante. */
  public synchronized void detener() {
    escuchando = false;
    if (Objects.nonNull(hiloEscucha) && hiloEscucha.isAlive()) {
      hiloEscucha.interrupt();
    }
    hiloEscucha = null;
  }

  public boolean isEscuchando() {
    return escuchando;
  }

  private void cicloEscucha() {
    final byte[] buffer = new byte[BUFFER_SIZE];

    while (escuchando && canalUdp.isAbierto()) {
      try {
        final DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        canalUdp.recibir(paquete);

        final String texto =
            new String(paquete.getData(), 0, paquete.getLength(), StandardCharsets.UTF_8).trim();
        final InetAddress direccion = paquete.getAddress();
        final int puerto = paquete.getPort();

        final ProcesarPeticionUdpCommand comando =
            new ProcesarPeticionUdpCommand(direccion.getHostAddress(), puerto, texto);

        inputPort.procesar(comando);

      } catch (final SocketException excepcion) {
        if (!escuchando) {
          break;
        }
        notificarError("Error de socket al recibir paquete: " + excepcion.getMessage());
      } catch (final IOException excepcion) {
        if (escuchando) {
          notificarError("Error I/O al recibir datagrama: " + excepcion.getMessage());
        }
      }
    }
  }

  private void notificarError(final String mensaje) {
    notificador.notificarEvento(new EventoServidor("ERROR", "SOCKET", mensaje));
  }
}
