package com.lcam;

import com.lcam.conversion.adaptadores.notificacion.AdaptadorNotificacionCliente;
import com.lcam.conversion.adaptadores.red.AdaptadorClienteUdp;
import com.lcam.conversion.adaptadores.red.CanalUdp;
import com.lcam.conversion.adaptadores.red.ProtocoloUdpMapper;
import com.lcam.conversion.aplicacion.mapper.ClienteMapper;
import com.lcam.conversion.aplicacion.servicios.ClienteConversionService;
import com.lcam.conversion.entrypoint.gui.ClienteFrame;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Punto de entrada del cliente (Composition Root). Responsabilidad unica:
 * ensamblar las dependencias y lanzar la interfaz.
 *
 * <p><b>Modelo de hilos:</b> se crea un executor de un solo hilo demonio
 * llamado "cliente-udp". Todas las operaciones de red se ejecutan alli para no
 * bloquear el Event Dispatch Thread de Swing. Un solo hilo basta porque el
 * protocolo es estrictamente peticion-respuesta y serializa las operaciones.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class Main {

  private static final System.Logger LOG = System.getLogger(Main.class.getName());

  /** Tiempo maximo de espera de una respuesta UDP, en milisegundos. */
  private static final int TIMEOUT_MS = 3000;

  private Main() {
    // Clase de arranque estatica: evita instanciacion.
  }

  public static void main(final String[] args) {
    aplicarLookAndFeel();

    // 1. Adaptadores de salida
    final AdaptadorNotificacionCliente notificador = new AdaptadorNotificacionCliente();
    final AdaptadorClienteUdp udp =
        new AdaptadorClienteUdp(new CanalUdp(TIMEOUT_MS), new ProtocoloUdpMapper());

    // 2. Hilo dedicado a la red (demonio: no impide cerrar la aplicacion)
    final ExecutorService executor = Executors.newSingleThreadExecutor(tarea -> {
      final Thread hilo = new Thread(tarea, "cliente-udp");
      hilo.setDaemon(true);
      return hilo;
    });

    // 3. Caso de uso
    final ClienteConversionService servicio =
        new ClienteConversionService(udp, notificador, new ClienteMapper(), executor);

    // 4. Entrypoint GUI en el Event Dispatch Thread
    SwingUtilities.invokeLater(() -> {
      final ClienteFrame frame = new ClienteFrame(servicio, servicio);
      notificador.registrar(frame);
      frame.setVisible(true);
    });
  }

  private static void aplicarLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (final ClassNotFoundException
        | InstantiationException
        | IllegalAccessException
        | UnsupportedLookAndFeelException excepcion) {
      LOG.log(System.Logger.Level.DEBUG,
          "No fue posible aplicar la apariencia del sistema.", excepcion);
    }
  }
}
