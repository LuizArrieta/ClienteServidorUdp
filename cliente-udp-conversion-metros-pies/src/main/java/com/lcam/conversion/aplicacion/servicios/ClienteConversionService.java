package com.lcam.conversion.aplicacion.servicios;

import com.lcam.conversion.aplicacion.dto.ConectarCommand;
import com.lcam.conversion.aplicacion.dto.ConvertirCommand;
import com.lcam.conversion.aplicacion.excepciones.ClienteRedException;
import com.lcam.conversion.aplicacion.mapper.ClienteMapper;
import com.lcam.conversion.aplicacion.puertos.entrada.ConvertirInputPort;
import com.lcam.conversion.aplicacion.puertos.entrada.GestionarConexionInputPort;
import com.lcam.conversion.dominio.enums.EstadoConexion;
import com.lcam.conversion.dominio.modelos.DatosConversion;
import com.lcam.conversion.dominio.modelos.EventoCliente;
import com.lcam.conversion.dominio.modelos.ResultadoConversion;
import com.lcam.conversion.dominio.puertos.salida.ClienteUdpPort;
import com.lcam.conversion.dominio.puertos.salida.PuertoNotificacionCliente;
import com.lcam.conversion.dominio.vo.DestinoServidor;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

/**
 * Servicio de aplicacion del cliente. Implementa los dos casos de uso:
 * gestionar la sesion y solicitar la conversion.
 *
 * <p><b>Modelo de hilos:</b> toda operacion de red se ejecuta con
 * {@code CompletableFuture} sobre un {@link Executor} dedicado. Esto evita que
 * la espera bloqueante del datagrama congele el Event Dispatch Thread de Swing.
 * El resultado vuelve a la interfaz por medio del puerto de notificacion.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class ClienteConversionService
    implements GestionarConexionInputPort, ConvertirInputPort {

  private static final System.Logger LOG =
      System.getLogger(ClienteConversionService.class.getName());

  private final ClienteUdpPort clienteUdp;
  private final PuertoNotificacionCliente notificador;
  private final ClienteMapper mapper;
  private final Executor executor;

  public ClienteConversionService(
      final ClienteUdpPort clienteUdp,
      final PuertoNotificacionCliente notificador,
      final ClienteMapper mapper,
      final Executor executor) {
    this.clienteUdp = Objects.requireNonNull(clienteUdp, "El puerto UDP es obligatorio.");
    this.notificador = Objects.requireNonNull(notificador, "El notificador es obligatorio.");
    this.mapper = Objects.requireNonNull(mapper, "El mapper es obligatorio.");
    this.executor = Objects.requireNonNull(executor, "El executor es obligatorio.");
  }

  @Override
  public CompletableFuture<Void> conectar(final ConectarCommand comando) {
    final DestinoServidor destino = mapper.toDestino(comando);
    notificador.notificarEstado(EstadoConexion.CONECTANDO, destino.endpoint());

    return CompletableFuture.runAsync(() -> {
      try {
        clienteUdp.conectar(destino);
        notificador.notificarEstado(EstadoConexion.CONECTADO, destino.endpoint());
        notificador.notificarEvento(
            new EventoCliente("CONEXION", "Conectado a " + destino.endpoint()));
      } catch (final ClienteRedException excepcion) {
        LOG.log(System.Logger.Level.ERROR, "Fallo la operacion UDP conectar", excepcion);
        notificador.notificarEstado(EstadoConexion.DESCONECTADO, destino.endpoint());
        notificador.notificarError(excepcion.getMessage());
        throw new CompletionException(excepcion);
      }
    }, executor);
  }

  @Override
  public CompletableFuture<Void> desconectar() {
    return CompletableFuture.runAsync(() -> {
      try {
        clienteUdp.desconectar();
        notificador.notificarEstado(EstadoConexion.DESCONECTADO, "");
        notificador.notificarEvento(new EventoCliente("CONEXION", "Cliente desconectado."));
      } catch (final ClienteRedException excepcion) {
        LOG.log(System.Logger.Level.ERROR, "Fallo la operacion UDP desconectar", excepcion);
        notificador.notificarError(excepcion.getMessage());
        throw new CompletionException(excepcion);
      }
    }, executor);
  }

  @Override
  public CompletableFuture<ResultadoConversion> convertir(final ConvertirCommand comando) {
    final DatosConversion datos = mapper.toDatos(comando);

    return CompletableFuture.supplyAsync(() -> {
      try {
        final ResultadoConversion resultado = clienteUdp.solicitarConversion(datos);
        notificador.notificarResultado(resultado);
        notificador.notificarEvento(
            new EventoCliente(
                "CONVERSION",
                resultado.metrosFormateado() + " m --> " + resultado.piesFormateado() + " ft"));
        return resultado;
      } catch (final ClienteRedException excepcion) {
        LOG.log(System.Logger.Level.ERROR, "Fallo la operacion UDP convertir", excepcion);
        notificador.notificarError(excepcion.getMessage());
        throw new CompletionException(excepcion);
      }
    }, executor);
  }

  @Override
  public boolean estaConectado() {
    return clienteUdp.estaConectado();
  }
}
