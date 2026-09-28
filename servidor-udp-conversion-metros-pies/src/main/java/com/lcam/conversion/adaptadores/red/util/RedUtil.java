package com.lcam.conversion.adaptadores.red.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Utilidad de red para inspeccionar las interfaces del host.
 * Responsabilidad unica: deteccion de la direccion IP local.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class RedUtil {

  private RedUtil() {
    // Clase de utilidad estatica: evita instanciacion.
  }

  /**
   * Obtiene la direccion IPv4 local no loopback. Si no hay conectividad
   * externa, retorna la direccion de loopback.
   */
  public static String obtenerIpLocal() {
    try {
      final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements()) {
        final NetworkInterface iface = interfaces.nextElement();
        if (iface.isLoopback() || !iface.isUp()) {
          continue;
        }
        final Enumeration<InetAddress> addresses = iface.getInetAddresses();
        while (addresses.hasMoreElements()) {
          final InetAddress addr = addresses.nextElement();
          if (!addr.isLoopbackAddress() && addr instanceof Inet4Address) {
            return addr.getHostAddress();
          }
        }
      }
      return InetAddress.getLocalHost().getHostAddress();
    } catch (final SocketException | UnknownHostException excepcion) {
      return InetAddress.getLoopbackAddress().getHostAddress();
    }
  }
}
