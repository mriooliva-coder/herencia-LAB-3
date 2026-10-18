Lab 3 Herencia: TecniHogar

Manuel Rio 16639

Aplicación de consola en Java para gestionar las órdenes de servicio de TecniHogar:
cotización y registro de órdenes de cuatro categorías, cuadrillas, jornadas, visitas
y reportes de órdenes, ingresos y jornadas abiertas.

## Compilar y ejecutar

## Ejecutar las pruebas automáticas

Cada línea muestra `ID | caso | entrada | esperado | obtenido | PASA/FALLA` y al final el total de pruebas aprobadas.

## Estructura

Archivo - Descripción 

| OrdenServicio.java | Clase abstracta: datos comunes, estados, visitas y montos de una orden |

| Diagnostico.java | Precio fijo por zona, máximo 1.5 horas, 3 visitas |

| Instalacion.java | Horas facturables (horas vs. puntos × 0.75), cobro por hora o fracción, 3 visitas |

| InstalacionElectricaCertificada.java | Hereda de Instalacion: + certificado Q120 + garantía 5 %, +2 días, 2 visitas |

| MantenimientoAireAcondicionado.java | Solo zona metropolitana, BTU 9,000–60,000, cuadrilla certificada, 1 visita |

| Cuadrilla.java | Código, capacidad de horas y certificación en refrigeración |

| Jornada.java | Asignación (capacidad y compatibilidad), retiro, inicio y resultados |

| TecniHogar.java | Registro de órdenes, cuadrillas y jornadas; reportes |

| Zona.java, EstadoOrden.java, EstadoJornada.java | Enumeraciones |

| Main.java | Menú de consola y datos iniciales (contiene main) |

| PruebasTecniHogar.java | Pruebas automáticas de las reglas |

## Datos iniciales

- Órdenes pendientes (sin ingresos): `D-001`, `D-002`, `I-001`, `I-002`, `E-001`, `E-002`, `M-001`, `M-002`.
  `I-001` tiene más horas por puntos (3.75) que horas estimadas (2.5).
- Cuadrillas: `C-01` (8 h, certificada en refrigeración), `C-02` (6 h, no certificada), `C-03` (4 h, no certificada).

## Uso rápido

1. Opción **3** para crear una jornada (se le asigna un código `J1`, `J2`, …).
2. Opción **4** para asignarle órdenes pendientes.
3. Opción **6** para iniciarla y opción **7** para registrar el resultado de cada orden.
4. Opciones **10**, **11** y **12** para los reportes.

Los números decimales se escriben con punto (por ejemplo `2.5`).
