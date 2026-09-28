package com.lcam.conversion.dominio.vo;

import com.lcam.conversion.dominio.excepciones.DestinoIncorrectoException;
import java.util.Objects;

/**
 * Value Object que identifica el servidor UDP destino.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public record DestinoServidor(String host, int puerto) {

  public DestinoServidor {
    if (Objects.isNull(host) || host.isBlank()) {
      throw new DestinoIncorrectoException("El host del servidor es obligatorio.");
    }
    if (puerto < 1024 || puerto > 65535) {
      throw new DestinoIncorrectoException("El puerto debe estar entre 1024 y 65535.");
    }
    host = host.trim();
  }

  public String endpoint() {
    return host + ":" + puerto;
  }
}
