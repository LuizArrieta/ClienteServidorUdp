package com.lcam.conversion.adaptadores.red;

import com.lcam.conversion.adaptadores.red.response.UdpResponse;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Objects;

/**
 * Encapsula la administracion y operaciones sobre el DatagramSocket del sistema
 * operativo. A diferencia de TCP, un unico DatagramSocket atiende a todos los
 * clientes: no existen sockets dedicados por conexion.
 *
 * <p>Los metodos de ciclo de vida son sincronizados porque el socket es
 * compartido entre el hilo de la GUI (abrir/cerrar) y el hilo receptor.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public class CanalUdp {

  private DatagramSocket socket;
  private volatile boolean abierto;
  private int puertoActual;

  public synchronized void abrir(final int puerto) throws SocketException {
    if (abierto && Objects.nonNull(socket) && !socket.isClosed()) {
      return;
    }
    socket = new DatagramSocket(puerto);
    puertoActual = puerto;
    abierto = true;
  }

  public synchronized void cerrar() {
    abierto = false;
    if (Objects.nonNull(socket) && !socket.isClosed()) {
      socket.close();
    }
    socket = null;
    puertoActual = 0;
  }

  public synchronized boolean isAbierto() {
    return abierto && Objects.nonNull(socket) && !socket.isClosed();
  }

  public synchronized int getPuertoActual() {
    return puertoActual;
  }

  /**
   * Envia una respuesta usando el socket propiedad del canal.
   * El socket se toma prestado y no debe cerrarse aqui.
   */
  @SuppressWarnings("resource")
  public void enviar(final UdpResponse respuesta) throws IOException {
    final DatagramSocket socketAbierto = obtenerSocketAbierto();
    final byte[] buffer = respuesta.getBytes();
    final InetAddress direccion = InetAddress.getByName(respuesta.getIpDestino());
    final DatagramPacket paquete =
        new DatagramPacket(buffer, buffer.length, direccion, respuesta.getPuertoDestino());
    socketAbierto.send(paquete);
  }

  /**
   * Recibe un paquete usando el socket propiedad del canal.
   * Esta llamada es bloqueante hasta que llegue un datagrama.
   */
  @SuppressWarnings("resource")
  public void recibir(final DatagramPacket paquete) throws IOException {
    obtenerSocketAbierto().receive(paquete);
  }

  private synchronized DatagramSocket obtenerSocketAbierto() throws SocketException {
    if (Objects.isNull(socket) || socket.isClosed()) {
      throw new SocketException("El socket UDP esta cerrado.");
    }
    return socket;
  }
}
