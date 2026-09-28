package com.lcam.conversion.dominio.puertos.salida;

import com.lcam.conversion.aplicacion.excepciones.ClienteRedException;
import com.lcam.conversion.dominio.modelos.DatosConversion;
import com.lcam.conversion.dominio.modelos.ResultadoConversion;
import com.lcam.conversion.dominio.vo.DestinoServidor;

/**
 * Puerto de salida hacia la infraestructura de red UDP.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public interface ClienteUdpPort {

  void conectar(DestinoServidor destino) throws ClienteRedException;

  void desconectar() throws ClienteRedException;

  ResultadoConversion solicitarConversion(DatosConversion datos) throws ClienteRedException;

  boolean estaConectado();
}
