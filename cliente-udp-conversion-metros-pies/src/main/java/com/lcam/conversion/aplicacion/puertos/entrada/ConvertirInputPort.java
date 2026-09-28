package com.lcam.conversion.aplicacion.puertos.entrada;

import com.lcam.conversion.aplicacion.dto.ConvertirCommand;
import com.lcam.conversion.dominio.modelos.ResultadoConversion;
import java.util.concurrent.CompletableFuture;

/**
 * Puerto de entrada para solicitar la conversion al servidor.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public interface ConvertirInputPort {

  CompletableFuture<ResultadoConversion> convertir(ConvertirCommand comando);
}
