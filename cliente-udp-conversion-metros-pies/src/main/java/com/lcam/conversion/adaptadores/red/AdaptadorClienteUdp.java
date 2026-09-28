package com.lcam.conversion.adaptadores.red;

import com.lcam.conversion.aplicacion.excepciones.ClienteRedException;
import com.lcam.conversion.dominio.excepciones.RespuestaServidorException;
import com.lcam.conversion.dominio.modelos.DatosConversion;
import com.lcam.conversion.dominio.modelos.ResultadoConversion;
import com.lcam.conversion.dominio.puertos.salida.ClienteUdpPort;
import com.lcam.conversion.dominio.vo.DestinoServidor;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * Adaptador de salida que implementa {@link ClienteUdpPort} sobre datagramas.
 *
 * <p><b>Sobre la "conexion" en UDP:</b> UDP no tiene conexiones reales. Lo que
 * aqui se llama conectar es una <i>sesion logica</i>: se envia el comando
 * CONECTAR y se espera confirmacion del servidor. Si llega, se guarda el
 * destino en {@code conexion} y la interfaz habilita la conversion.
 *
 * <p><b>Concurrencia:</b> los metodos publicos son {@code synchronized} porque
 * el executor podria encolar dos operaciones; el campo {@code conexion} es
 * volatile para que {@code estaConectado()} lea siempre el valor actualizado
 * desde el hilo de Swing.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class AdaptadorClienteUdp implements ClienteUdpPort {

  private static final System.Logger LOG =
      System.getLogger(AdaptadorClienteUdp.class.getName());

  private final CanalUdp canal;
  private final ProtocoloUdpMapper protocolo;
  private volatile ConexionActiva conexion;

  public AdaptadorClienteUdp(final CanalUdp canal, final ProtocoloUdpMapper protocolo) {
    this.canal = Objects.requireNonNull(canal, "El canal es obligatorio.");
    this.protocolo = Objects.requireNonNull(protocolo, "El protocolo es obligatorio.");
  }

  @Override
  public synchronized void conectar(final DestinoServidor nuevoDestino) throws ClienteRedException {
    Objects.requireNonNull(nuevoDestino, "El destino es obligatorio.");
    try {
      final InetAddress nuevaDireccion = InetAddress.getByName(nuevoDestino.host());
      canal.abrir();
      final String respuesta =
          canal.intercambiar(ProtocoloUdpMapper.CONECTAR, nuevaDireccion, nuevoDestino.puerto());
      protocolo.validarConexion(respuesta);
      conexion = new ConexionActiva(nuevaDireccion, nuevoDestino);
    } catch (final SocketTimeoutException excepcion) {
      LOG.log(System.Logger.Level.WARNING, "Fallo UDP durante conexion por timeout", excepcion);
      canal.cerrar();
      throw new ClienteRedException(
          "El servidor no respondio dentro del tiempo esperado. Verifique que este iniciado.",
          excepcion);
    } catch (final UnknownHostException excepcion) {
      LOG.log(System.Logger.Level.WARNING, "Fallo UDP durante resolucion del host", excepcion);
      canal.cerrar();
      throw new ClienteRedException(
          "No se pudo resolver el host " + nuevoDestino.host() + ".", excepcion);
    } catch (final IOException | RespuestaServidorException excepcion) {
      LOG.log(System.Logger.Level.ERROR, "Fallo UDP durante conexion", excepcion);
      canal.cerrar();
      throw new ClienteRedException(
          "No se pudo conectar con " + nuevoDestino.endpoint() + ".", excepcion);
    }
  }

  @Override
  public synchronized void desconectar() throws ClienteRedException {
    try {
      if (Objects.nonNull(conexion)) {
        final ConexionActiva actual = conexion;
        canal.enviar(ProtocoloUdpMapper.DESCONECTAR, actual.direccion(), actual.destino().puerto());
      }
    } catch (final IOException excepcion) {
      LOG.log(System.Logger.Level.WARNING, "Fallo UDP durante desconexion", excepcion);
      throw new ClienteRedException("No se pudo notificar la desconexion.", excepcion);
    } finally {
      conexion = null;
      canal.cerrar();
    }
  }

  @Override
  public synchronized ResultadoConversion solicitarConversion(final DatosConversion datos)
      throws ClienteRedException {
    if (Objects.isNull(conexion)) {
      throw new ClienteRedException("Debe conectarse antes de solicitar una conversion.");
    }
    try {
      final ConexionActiva actual = conexion;
      final String respuesta = canal.intercambiar(
          protocolo.serializarConversion(datos), actual.direccion(), actual.destino().puerto());
      return protocolo.parsearConversion(respuesta);
    } catch (final SocketTimeoutException excepcion) {
      LOG.log(System.Logger.Level.WARNING, "Fallo UDP durante conversion por timeout", excepcion);
      throw new ClienteRedException(
          "El servidor no respondio a la conversion (datagrama posiblemente perdido).", excepcion);
    } catch (final IOException | RespuestaServidorException excepcion) {
      LOG.log(System.Logger.Level.ERROR, "Fallo UDP durante conversion", excepcion);
      throw new ClienteRedException("Fallo la comunicacion de la conversion.", excepcion);
    }
  }

  @Override
  public boolean estaConectado() {
    return Objects.nonNull(conexion) && canal.estaAbierto();
  }

  /** Datos inmutables de la sesion logica vigente. */
  private record ConexionActiva(InetAddress direccion, DestinoServidor destino) {
    private ConexionActiva {
      Objects.requireNonNull(direccion, "La direccion es obligatoria.");
      Objects.requireNonNull(destino, "El destino es obligatorio.");
    }
  }
}
