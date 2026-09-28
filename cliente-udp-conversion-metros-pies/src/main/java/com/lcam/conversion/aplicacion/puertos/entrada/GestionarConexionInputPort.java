package com.lcam.conversion.aplicacion.puertos.entrada;

import com.lcam.conversion.aplicacion.dto.ConectarCommand;
import java.util.concurrent.CompletableFuture;

/**
 * Puerto de entrada para gestionar la sesion logica con el servidor.
 * Las operaciones retornan CompletableFuture porque se ejecutan fuera del
 * hilo de la interfaz grafica.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public interface GestionarConexionInputPort {

  CompletableFuture<Void> conectar(ConectarCommand comando);

  CompletableFuture<Void> desconectar();

  boolean estaConectado();
}
