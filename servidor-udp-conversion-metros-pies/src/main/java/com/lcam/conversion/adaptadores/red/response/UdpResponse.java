package com.lcam.conversion.adaptadores.red.response;

import java.nio.charset.StandardCharsets;

/**
 * Representacion del datagrama de red a transmitir hacia un cliente UDP.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class UdpResponse {

  private final String payload;
  private final String ipDestino;
  private final int puertoDestino;

  public UdpResponse(final String payload, final String ipDestino, final int puertoDestino) {
    this.payload = payload;
    this.ipDestino = ipDestino;
    this.puertoDestino = puertoDestino;
  }

  public String getPayload() {
    return payload;
  }

  public String getIpDestino() {
    return ipDestino;
  }

  public int getPuertoDestino() {
    return puertoDestino;
  }

  public byte[] getBytes() {
    return payload.getBytes(StandardCharsets.UTF_8);
  }
}
