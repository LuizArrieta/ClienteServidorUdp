package com.lcam.conversion.aplicacion.mapper;

import com.lcam.conversion.aplicacion.dto.ProcesarPeticionUdpCommand;
import com.lcam.conversion.aplicacion.excepciones.ComandoInvalidoException;
import com.lcam.conversion.dominio.vo.Destinatario;
import java.util.Objects;

/**
 * Mapper de la capa de aplicacion. Responsabilidad unica: convertir datos de
 * comandos de peticion a objetos de valor del dominio.
 *
 * @author Luis Camilo Arrieta Muriel
 */
public final class PeticionMapper {

  public Destinatario toDestinatario(final ProcesarPeticionUdpCommand comando) {
    if (Objects.isNull(comando)) {
      throw new ComandoInvalidoException("El comando de peticion no puede ser nulo.");
    }
    return new Destinatario(comando.ipCliente(), comando.puertoCliente());
  }
}
