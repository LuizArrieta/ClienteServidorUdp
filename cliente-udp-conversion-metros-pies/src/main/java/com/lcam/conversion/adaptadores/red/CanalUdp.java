package com.lcam.conversion.adaptadores.red;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Encapsula el DatagramSocket del cliente.
 *
 * <p><b>Detalle UDP importante:</b> como UDP no garantiza entrega ni respuesta,
 * el socket se configura con {@code setSoTimeout}. Si el servidor no contesta
 * dentro de ese plazo, {@code receive} lanza SocketTimeoutException en lugar de
 * quedarse bloqueado para siempre. Ese timeout es la unica defensa del cliente
 * frente a datagramas perdidos.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public class CanalUdp {

  private static final int BUFFER_SIZE = 2048;

  private final int timeoutMs;
  private DatagramSocket socket;

  public CanalUdp(final int timeoutMs) {
    this.timeoutMs = timeoutMs;
  }

  public synchronized void abrir() throws IOException {
    cerrar();
    final DatagramSocket nuevoSocket = new DatagramSocket();
    try {
      nuevoSocket.setSoTimeout(timeoutMs);
      socket = nuevoSocket;
    } catch (final IOException | RuntimeException excepcion) {
      nuevoSocket.close();
      throw excepcion;
    }
  }

  public synchronized void cerrar() {
    if (Objects.nonNull(socket) && !socket.isClosed()) {
      socket.close();
    }
    socket = null;
  }

  public synchronized boolean estaAbierto() {
    return Objects.nonNull(socket) && !socket.isClosed();
  }

  /**
   * Envia un datagrama y espera la respuesta correlativa (patron peticion-respuesta).
   *
   * @throws java.net.SocketTimeoutException si el servidor no responde a tiempo.
   */
  public synchronized String intercambiar(
      final String mensaje, final InetAddress host, final int puerto) throws IOException {
    if (!estaAbierto()) {
      throw new IOException("El canal UDP esta cerrado.");
    }
    final byte[] salida = mensaje.getBytes(StandardCharsets.UTF_8);
    final DatagramSocket socketAbierto = socket;
    socketAbierto.send(new DatagramPacket(salida, salida.length, host, puerto));

    final DatagramPacket entrada = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
    socketAbierto.receive(entrada);
    return new String(entrada.getData(), 0, entrada.getLength(), StandardCharsets.UTF_8).trim();
  }

  /** Envio sin esperar respuesta (fire and forget), usado al desconectar. */
  public synchronized void enviar(final String mensaje, final InetAddress host, final int puerto)
      throws IOException {
    if (!estaAbierto()) {
      return;
    }
    final byte[] salida = mensaje.getBytes(StandardCharsets.UTF_8);
    socket.send(new DatagramPacket(salida, salida.length, host, puerto));
  }
}
