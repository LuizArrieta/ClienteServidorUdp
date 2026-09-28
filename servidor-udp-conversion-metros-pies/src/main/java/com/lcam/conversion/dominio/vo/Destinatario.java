package com.lcam.conversion.dominio.vo;

import com.lcam.conversion.dominio.excepciones.DestinatarioIncorrectoException;
import com.lcam.conversion.dominio.excepciones.PuertoIncorrectoException;
import java.util.Objects;

/**
 * Value Object que representa la direccion de red de un cliente UDP.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public record Destinatario(String ip, int puerto) {

  public Destinatario {
    if (Objects.isNull(ip) || ip.isBlank()) {
      throw new DestinatarioIncorrectoException(
          "La direccion IP del destinatario no puede ser nula ni vacia.");
    }
    if (puerto <= 0 || puerto > 65535) {
      throw new PuertoIncorrectoException(
          "El puerto del destinatario debe ser un numero entre 1 y 65535.");
    }
    ip = ip.trim();
  }

  public String endpoint() {
    return ip + ":" + puerto;
  }
}
