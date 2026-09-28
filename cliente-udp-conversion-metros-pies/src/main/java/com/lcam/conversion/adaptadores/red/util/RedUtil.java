package com.lcam.conversion.adaptadores.red.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Utilidad para detectar la IP local del equipo donde corre el cliente.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class RedUtil {

  private RedUtil() {
    // Clase de utilidad estatica: evita instanciacion.
  }

  public static String obtenerIpLocal() {
    try {
      final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements()) {
        final NetworkInterface interfaz = interfaces.nextElement();
        if (interfaz.isLoopback() || !interfaz.isUp()) {
          continue;
        }
        final Enumeration<InetAddress> direcciones = interfaz.getInetAddresses();
        while (direcciones.hasMoreElements()) {
          final InetAddress direccion = direcciones.nextElement();
          if (direccion instanceof Inet4Address && !direccion.isLoopbackAddress()) {
            return direccion.getHostAddress();
          }
        }
      }
      return InetAddress.getLocalHost().getHostAddress();
    } catch (final SocketException | UnknownHostException excepcion) {
      return "127.0.0.1";
    }
  }
}
