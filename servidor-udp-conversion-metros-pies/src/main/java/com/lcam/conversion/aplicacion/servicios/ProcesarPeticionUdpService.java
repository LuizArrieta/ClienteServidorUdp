package com.lcam.conversion.aplicacion.servicios;

import com.lcam.conversion.aplicacion.dto.ProcesarPeticionUdpCommand;
import com.lcam.conversion.aplicacion.mapper.ConversionMapper;
import com.lcam.conversion.aplicacion.mapper.PeticionMapper;
import com.lcam.conversion.aplicacion.puertos.entrada.ProcesarPeticionUdpInputPort;
import com.lcam.conversion.dominio.excepciones.DominioException;
import com.lcam.conversion.dominio.modelos.Conversion;
import com.lcam.conversion.dominio.modelos.EventoServidor;
import com.lcam.conversion.dominio.modelos.RespuestaCliente;
import com.lcam.conversion.dominio.modelos.Resultado;
import com.lcam.conversion.dominio.puertos.salida.PuertoNotificacionEvento;
import com.lcam.conversion.dominio.puertos.salida.PuertoSalidaRed;
import com.lcam.conversion.dominio.vo.Destinatario;
import java.util.Locale;
import java.util.Objects;

/**
 * Servicio de aplicacion que procesa las peticiones del protocolo UDP.
 * Responsabilidad unica: decodificar y orquestar el procesamiento de los
 * comandos de red, coordinar con el dominio y despachar respuestas.
 *
 * <p>Protocolo soportado:
 * <ul>
 *   <li>CONECTAR</li>
 *   <li>DESCONECTAR</li>
 *   <li>CONVERTIR;&lt;metros&gt;</li>
 * </ul>
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class ProcesarPeticionUdpService implements ProcesarPeticionUdpInputPort {

  private static final String PREFIJO_CONVERTIR = "CONVERTIR;";

  private final PuertoSalidaRed puertoSalidaRed;
  private final PuertoNotificacionEvento puertoNotificacion;
  private final PeticionMapper peticionMapper;
  private final ConversionMapper conversionMapper;

  public ProcesarPeticionUdpService(
      final PuertoSalidaRed puertoSalidaRed,
      final PuertoNotificacionEvento puertoNotificacion,
      final PeticionMapper peticionMapper,
      final ConversionMapper conversionMapper) {
    this.puertoSalidaRed = Objects.requireNonNull(puertoSalidaRed, "El puerto de salida es obligatorio.");
    this.puertoNotificacion = Objects.requireNonNull(puertoNotificacion, "El notificador es obligatorio.");
    this.peticionMapper = Objects.requireNonNull(peticionMapper, "El mapper de peticion es obligatorio.");
    this.conversionMapper = Objects.requireNonNull(conversionMapper, "El mapper de conversion es obligatorio.");
  }

  @Override
  public void procesar(final ProcesarPeticionUdpCommand comando) {
    Objects.requireNonNull(comando, "El comando es obligatorio.");

    final Destinatario destinatario = peticionMapper.toDestinatario(comando);
    final String mensaje = comando.mensaje();

    if (Objects.isNull(mensaje) || mensaje.isBlank()) {
      puertoSalidaRed.enviarRespuesta(RespuestaCliente.error(destinatario, "Mensaje vacio recibido."));
      notificarEvento(destinatario.endpoint(), "datos recibidos --> [Vacio]");
      return;
    }

    final String comandoTexto = mensaje.trim();

    if (comandoTexto.equalsIgnoreCase("CONECTAR")) {
      puertoSalidaRed.enviarRespuesta(
          RespuestaCliente.conectado(
              destinatario, "Servidor UDP listo para convertir metros a pies"));
      notificarEvento(destinatario.endpoint(), "conectado --> Solicitud aceptada");
      return;
    }

    if (comandoTexto.equalsIgnoreCase("DESCONECTAR")) {
      puertoSalidaRed.enviarRespuesta(RespuestaCliente.desconectado(destinatario, "Sesion finalizada"));
      notificarEvento(destinatario.endpoint(), "desconectado --> Cliente cerro la sesion");
      return;
    }

    if (comandoTexto.toUpperCase(Locale.ROOT).startsWith(PREFIJO_CONVERTIR)) {
      procesarConversion(destinatario, comandoTexto);
      return;
    }

    puertoSalidaRed.enviarRespuesta(
        RespuestaCliente.error(destinatario, "Comando no reconocido por el servidor UDP."));
    notificarEvento(destinatario.endpoint(), "datos recibidos --> Comando desconocido: [" + comandoTexto + "]");
  }

  private void procesarConversion(final Destinatario destinatario, final String comandoTexto) {
    final String[] partes = comandoTexto.split(";", -1);
    if (partes.length != 2) {
      puertoSalidaRed.enviarRespuesta(
          RespuestaCliente.error(destinatario, "Formato invalido. Se esperaba CONVERTIR;metros"));
      notificarEvento(destinatario.endpoint(), "datos recibidos --> Formato incorrecto: " + comandoTexto);
      return;
    }

    try {
      final double valorMetros = Double.parseDouble(partes[1].trim().replace(',', '.'));

      final Conversion conversion = conversionMapper.toDomain(valorMetros);
      final Resultado resultado = conversion.convertir();

      puertoSalidaRed.enviarRespuesta(RespuestaCliente.conversionExitosa(destinatario, resultado));

      final String logInfo = String.format(
          Locale.US,
          "datos recibidos (Metros: %s) --> datos enviados (Pies: %s, %s)",
          resultado.getMetrosFormateado(),
          resultado.getPiesFormateado(),
          resultado.getClasificacion());

      notificarEvento(destinatario.endpoint(), logInfo);

    } catch (final NumberFormatException excepcion) {
      puertoSalidaRed.enviarRespuesta(
          RespuestaCliente.error(destinatario, "El parametro de metros debe ser numerico."));
      notificarEvento(destinatario.endpoint(), "datos recibidos --> Error de formato: " + comandoTexto);

    } catch (final DominioException excepcion) {
      puertoSalidaRed.enviarRespuesta(RespuestaCliente.error(destinatario, excepcion.getMessage()));
      notificarEvento(destinatario.endpoint(), "datos recibidos --> Validacion fallida: " + excepcion.getMessage());
    }
  }

  private void notificarEvento(final String endpoint, final String descripcion) {
    puertoNotificacion.notificarEvento(new EventoServidor("EVENTO", endpoint, descripcion));
  }
}
