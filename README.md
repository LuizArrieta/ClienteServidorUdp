# Conversión de Metros a Pies — Cliente/Servidor UDP en Java (Arquitectura Hexagonal)

Aplicación distribuida que convierte una longitud en metros a su equivalente en pies.
El cliente solicita el dato y lo envía; **el cálculo lo realiza exclusivamente el servidor**.

**Autor:** Luis Camilo Arrieta Muriel

---

## Proyectos

| Proyecto | Artefacto | Clase principal |
|---|---|---|
| `cmp-udp-server` | `cmp-udp-server-java` | `com.lcam.Main` |
| `cmp-udp-client` | `cmp-udp-cliente-java` | `com.lcam.Main` |

Ambos son proyectos Maven independientes, Java 17, sin dependencias externas en tiempo de
ejecución (solo JUnit para pruebas).

---

## Requisitos

- JDK 17 o superior
- Apache Maven 3.8+

---

## Cómo ejecutar

**1. Servidor** (primero):

```bash
cd cmp-udp-server
mvn clean compile
mvn exec:java
```

En la ventana: verificar el puerto (por defecto `9876`) y presionar **Iniciar Servicio**.

**2. Cliente** (después, en otra terminal):

```bash
cd cmp-udp-client
mvn clean compile
mvn exec:java
```

En la ventana: host `127.0.0.1`, puerto `9876`, presionar **Conectar**, escribir los metros
y presionar **Convertir a pies**.

También se puede generar el `.jar` con `mvn clean package` y ejecutar con `java -jar`.

---

## Fórmula

```
pies = metros × 3.28084
```

Definida en `com.lcam.conversion.dominio.modelos.Conversion` (servidor).

---

## Protocolo de aplicación sobre UDP

Todos los mensajes son texto UTF-8 con campos separados por `;`.

**Peticiones (cliente → servidor)**

| Mensaje | Significado |
|---|---|
| `CONECTAR` | Abre la sesión lógica |
| `DESCONECTAR` | Cierra la sesión lógica |
| `CONVERTIR;<metros>` | Solicita la conversión |

**Respuestas (servidor → cliente)**

| Mensaje | Significado |
|---|---|
| `CONECTADO_OK;<mensaje>` | Sesión aceptada |
| `DESCONECTADO_OK;<mensaje>` | Sesión finalizada |
| `OK_CONVERSION;<pies>;<metros>;<clasificación>;<descripción>` | Resultado del cálculo |
| `ERROR;<mensaje>` | Dato inválido o comando desconocido |

Ejemplo de intercambio real:

```
cliente  -->  CONVERTIR;12.5000
servidor -->  OK_CONVERSION;41.0105;12.5;Longitud media;Escala típica de habitaciones o vehículos.
```

---

## Arquitectura hexagonal (puertos y adaptadores)

```
entrypoint  →  puertos de entrada  →  aplicación  →  dominio
                                           ↓
                                  puertos de salida
                                           ↓
                                     adaptadores
```

Estructura de paquetes (idéntica en ambos proyectos):

| Capa | Paquete | Contenido |
|---|---|---|
| Dominio | `dominio/` | Modelos, value objects, enums, excepciones y puertos de salida. No importa nada de infraestructura. |
| Aplicación | `aplicacion/` | Casos de uso (servicios), DTOs, mappers y puertos de entrada. |
| Adaptadores | `adaptadores/` | Implementaciones concretas: sockets UDP, notificación a observadores. |
| Entrypoints | `entrypoint/` | GUI Swing y, en el servidor, el receptor de datagramas. |

El ensamblado de dependencias (*Composition Root*) ocurre únicamente en `Main.java`.

---

## Validación y control de errores

**En el cliente (antes de usar la red):**
- Campo vacío, valor no numérico y valor negativo se rechazan con `JOptionPane`.
- El puerto debe ser entero entre 1024 y 65535 (`DestinoServidor`).
- Si el servidor no responde, `SocketTimeoutException` produce un mensaje claro en pantalla.

**En el servidor:**
- `Metros` rechaza valores no finitos o negativos.
- Formato incorrecto, comando desconocido o mensaje vacío devuelven `ERROR;<motivo>`.
- Todo queda registrado en el log de la ventana con fecha, IP y puerto del cliente.

---

## Nota sobre las dependencias

La versión original de referencia usaba Lombok y SLF4J. En este proyecto se sustituyeron por
código Java estándar y `System.Logger` (incorporado en el JDK desde Java 9), de modo que ambos
proyectos compilen sin descargar librerías adicionales y el código sea legible tal cual está
escrito, sin anotaciones que generen métodos en tiempo de compilación.

---

## Licencia

Proyecto desarrollado con fines académicos.
