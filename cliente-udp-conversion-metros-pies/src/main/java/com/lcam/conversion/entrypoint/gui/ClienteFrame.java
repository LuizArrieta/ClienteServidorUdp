package com.lcam.conversion.entrypoint.gui;

import com.lcam.conversion.adaptadores.notificacion.ObservadorCliente;
import com.lcam.conversion.adaptadores.red.util.RedUtil;
import com.lcam.conversion.aplicacion.dto.ConectarCommand;
import com.lcam.conversion.aplicacion.dto.ConvertirCommand;
import com.lcam.conversion.aplicacion.puertos.entrada.ConvertirInputPort;
import com.lcam.conversion.aplicacion.puertos.entrada.GestionarConexionInputPort;
import com.lcam.conversion.dominio.enums.EstadoConexion;
import com.lcam.conversion.dominio.excepciones.DominioException;
import com.lcam.conversion.dominio.modelos.EventoCliente;
import com.lcam.conversion.dominio.modelos.ResultadoConversion;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.Serial;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Ventana principal del Cliente (Entrypoint GUI).
 *
 * <p>Responsabilidad unica: capturar datos, validar el formato de entrada y
 * delegar en los puertos de entrada. NO realiza la conversion: el calculo es
 * exclusivo del servidor.
 *
 * <p><b>Modelo de hilos:</b> implementa {@link ObservadorCliente}, cuyos
 * metodos son invocados desde el hilo de red. Por eso cada actualizacion visual
 * se envuelve en {@code SwingUtilities.invokeLater}.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class ClienteFrame extends JFrame implements ObservadorCliente {

  @Serial
  private static final long serialVersionUID = 1L;

  private static final DateTimeFormatter FECHA =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  private static final Color COLOR_OK = new Color(46, 125, 50);
  private static final Color COLOR_OFF = new Color(198, 40, 40);

  private final transient GestionarConexionInputPort conexion;
  private final transient ConvertirInputPort conversion;

  private final JTextField host = new JTextField("127.0.0.1");
  private final JTextField puerto = new JTextField("9876");
  private final JTextField metros = new JTextField();
  private final JButton btnConectar = new JButton("Conectar");
  private final JButton btnConvertir = new JButton("Convertir a pies");
  private final JLabel estado = new JLabel("\u25CF DESCONECTADO");
  private final JLabel resultado = new JLabel("Pies: --");
  private final JTextArea logs = new JTextArea();

  public ClienteFrame(
      final GestionarConexionInputPort conexion, final ConvertirInputPort conversion) {
    super("Cliente UDP - Conversion de Metros a Pies (Arquitectura Hexagonal)");
    this.conexion = Objects.requireNonNull(conexion, "El puerto de conexion es obligatorio.");
    this.conversion = Objects.requireNonNull(conversion, "El puerto de conversion es obligatorio.");
    construirInterfaz();
  }

  private void construirInterfaz() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(800, 560);
    setLocationRelativeTo(null);

    estado.setFont(new Font("Segoe UI", Font.BOLD, 13));
    estado.setForeground(COLOR_OFF);
    resultado.setFont(new Font("Segoe UI", Font.BOLD, 14));
    resultado.setForeground(COLOR_OK);

    final JPanel panelRed = new JPanel(new GridLayout(3, 2, 8, 8));
    panelRed.setBorder(
        BorderFactory.createTitledBorder("Red - IP local del cliente: " + RedUtil.obtenerIpLocal()));
    panelRed.add(new JLabel("Host del servidor:"));
    panelRed.add(host);
    panelRed.add(new JLabel("Puerto del servidor:"));
    panelRed.add(puerto);
    panelRed.add(estado);
    panelRed.add(btnConectar);

    final JPanel panelDatos = new JPanel(new GridLayout(2, 2, 8, 8));
    panelDatos.setBorder(BorderFactory.createTitledBorder("Datos de la conversion"));
    panelDatos.add(new JLabel("Longitud en metros:"));
    panelDatos.add(metros);
    panelDatos.add(resultado);
    panelDatos.add(btnConvertir);

    final JPanel centro = new JPanel(new GridLayout(2, 1, 8, 8));
    centro.add(panelRed);
    centro.add(panelDatos);

    logs.setEditable(false);
    logs.setFont(new Font("Consolas", Font.PLAIN, 12));
    final JButton btnLimpiar = new JButton("Limpiar log");
    btnLimpiar.addActionListener(evento -> logs.setText(""));

    final JPanel sur = new JPanel(new BorderLayout());
    sur.add(btnLimpiar, BorderLayout.NORTH);
    sur.add(new JScrollPane(logs), BorderLayout.CENTER);

    final JPanel contenido = new JPanel(new BorderLayout(10, 10));
    contenido.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
    contenido.add(centro, BorderLayout.NORTH);
    contenido.add(sur, BorderLayout.CENTER);
    add(contenido);

    btnConectar.addActionListener(evento -> alternarConexion());
    btnConvertir.addActionListener(evento -> solicitarConversion());
    habilitarConversion(false);
  }

  private void alternarConexion() {
    if (conexion.estaConectado()) {
      conexion.desconectar();
      return;
    }
    try {
      final int numeroPuerto = Integer.parseInt(puerto.getText().trim());
      conexion.conectar(new ConectarCommand(host.getText(), numeroPuerto));
    } catch (final NumberFormatException excepcion) {
      mostrarError("El puerto debe ser un numero entero valido.");
    } catch (final DominioException excepcion) {
      mostrarError(excepcion.getMessage());
    }
  }

  /**
   * Valida el formato del dato antes de enviarlo. La conversion en si nunca se
   * realiza aqui: se delega al servidor via el puerto de entrada.
   */
  private void solicitarConversion() {
    final String texto = metros.getText().trim().replace(',', '.');
    if (texto.isEmpty()) {
      mostrarError("Debe ingresar la longitud en metros.");
      return;
    }
    try {
      final double valorMetros = Double.parseDouble(texto);
      btnConvertir.setEnabled(false);
      conversion.convertir(new ConvertirCommand(valorMetros));
    } catch (final NumberFormatException excepcion) {
      mostrarError("El valor ingresado no es numerico. Ejemplo valido: 12.5");
    } catch (final DominioException excepcion) {
      mostrarError(excepcion.getMessage());
    }
  }

  @Override
  public void onEstado(final EstadoConexion nuevoEstado, final String endpoint) {
    SwingUtilities.invokeLater(() -> {
      estado.setText("\u25CF " + nuevoEstado + (endpoint.isBlank() ? "" : " - " + endpoint));
      final boolean conectado = nuevoEstado == EstadoConexion.CONECTADO;
      estado.setForeground(conectado ? COLOR_OK : COLOR_OFF);
      host.setEditable(!conectado);
      puerto.setEditable(!conectado);
      btnConectar.setText(conectado ? "Desconectar" : "Conectar");
      habilitarConversion(conectado);
    });
  }

  @Override
  public void onEvento(final EventoCliente evento) {
    SwingUtilities.invokeLater(() -> {
      logs.append("[" + evento.fechaHora().format(FECHA) + "] [" + evento.categoria() + "] "
          + evento.descripcion() + System.lineSeparator());
      logs.setCaretPosition(logs.getDocument().getLength());
    });
  }

  @Override
  public void onResultado(final ResultadoConversion valor) {
    SwingUtilities.invokeLater(() -> {
      resultado.setText("Pies: " + valor.piesFormateado() + " ft (" + valor.clasificacion() + ")");
      btnConvertir.setEnabled(true);
    });
  }

  @Override
  public void onError(final String mensaje) {
    SwingUtilities.invokeLater(() -> {
      habilitarConversion(conexion.estaConectado());
      mostrarError(mensaje);
    });
  }

  private void habilitarConversion(final boolean habilitado) {
    metros.setEnabled(habilitado);
    btnConvertir.setEnabled(habilitado);
  }

  private void mostrarError(final String mensaje) {
    JOptionPane.showMessageDialog(this, mensaje, "Cliente UDP", JOptionPane.WARNING_MESSAGE);
  }
}
