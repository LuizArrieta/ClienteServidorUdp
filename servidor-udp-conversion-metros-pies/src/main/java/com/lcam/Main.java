package com.lcam;

import com.lcam.conversion.adaptadores.red.AdaptadorControlServidorRed;
import com.lcam.conversion.adaptadores.red.AdaptadorNotificacionEvento;
import com.lcam.conversion.adaptadores.red.AdaptadorSalidaUdp;
import com.lcam.conversion.adaptadores.red.CanalUdp;
import com.lcam.conversion.adaptadores.red.mapper.UdpNetworkMapper;
import com.lcam.conversion.aplicacion.mapper.ConversionMapper;
import com.lcam.conversion.aplicacion.mapper.PeticionMapper;
import com.lcam.conversion.aplicacion.puertos.entrada.GestionarServidorInputPort;
import com.lcam.conversion.aplicacion.puertos.entrada.ProcesarPeticionUdpInputPort;
import com.lcam.conversion.aplicacion.servicios.GestionarServidorService;
import com.lcam.conversion.aplicacion.servicios.ProcesarPeticionUdpService;
import com.lcam.conversion.dominio.puertos.salida.ControladorServidorRedPort;
import com.lcam.conversion.dominio.puertos.salida.PuertoSalidaRed;
import com.lcam.conversion.entrypoint.gui.ServidorFrame;
import com.lcam.conversion.entrypoint.udp.ReceptorPeticionesUdp;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Punto de entrada del servidor (Composition Root). Responsabilidad unica:
 * ensamblar las dependencias segun los principios SOLID y la Arquitectura
 * Hexagonal, y lanzar la interfaz de usuario.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class Main {

  private static final System.Logger LOG = System.getLogger(Main.class.getName());

  private Main() {
    // Clase de arranque estatica: evita instanciacion.
  }

  public static void main(final String[] args) {
    aplicarLookAndFeel();

    // 1. Infraestructura de red compartida
    final CanalUdp canalUdp = new CanalUdp();
    final AdaptadorNotificacionEvento notificador = new AdaptadorNotificacionEvento();
    final UdpNetworkMapper redMapper = new UdpNetworkMapper();
    final PuertoSalidaRed puertoSalidaRed = new AdaptadorSalidaUdp(canalUdp, redMapper, notificador);

    // 2. Mappers de aplicacion
    final PeticionMapper peticionMapper = new PeticionMapper();
    final ConversionMapper conversionMapper = new ConversionMapper();

    // 3. Casos de uso (servicios de aplicacion)
    final ProcesarPeticionUdpInputPort procesarPeticionPort =
        new ProcesarPeticionUdpService(puertoSalidaRed, notificador, peticionMapper, conversionMapper);

    // 4. Entrypoints
    final ReceptorPeticionesUdp receptorUdp =
        new ReceptorPeticionesUdp(canalUdp, procesarPeticionPort, notificador);

    final ControladorServidorRedPort controladorRed =
        new AdaptadorControlServidorRed(canalUdp, receptorUdp::iniciar, receptorUdp::detener);

    final GestionarServidorInputPort gestionarServidorPort =
        new GestionarServidorService(controladorRed, notificador);

    // 5. Lanzar GUI en el Event Dispatch Thread de Swing
    SwingUtilities.invokeLater(() -> {
      final ServidorFrame frame = new ServidorFrame(gestionarServidorPort);
      notificador.registrarObservador(frame);
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
          "No fue posible aplicar la apariencia del sistema; se usara la predeterminada.", excepcion);
    }
  }
}
