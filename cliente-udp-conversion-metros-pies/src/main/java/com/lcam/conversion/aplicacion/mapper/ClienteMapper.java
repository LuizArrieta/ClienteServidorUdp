package com.lcam.conversion.aplicacion.mapper;

import com.lcam.conversion.aplicacion.dto.ConectarCommand;
import com.lcam.conversion.aplicacion.dto.ConvertirCommand;
import com.lcam.conversion.dominio.modelos.DatosConversion;
import com.lcam.conversion.dominio.vo.DestinoServidor;
import com.lcam.conversion.dominio.vo.Metros;
import java.util.Objects;

/**
 * Mapper de la capa de aplicacion: convierte DTOs a objetos del dominio.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class ClienteMapper {

  public DestinoServidor toDestino(final ConectarCommand comando) {
    Objects.requireNonNull(comando, "El comando de conexion es obligatorio.");
    return new DestinoServidor(comando.host(), comando.puerto());
  }

  public DatosConversion toDatos(final ConvertirCommand comando) {
    Objects.requireNonNull(comando, "El comando de conversion es obligatorio.");
    return new DatosConversion(new Metros(comando.metros()));
  }
}
