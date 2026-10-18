import java.util.Scanner;

/*
 * Menu de consola de TecniHogar. Solo se encarga de pedir datos y mostrar resultados;
 * las reglas de negocio estan en las demas clases.
 */
public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static TecniHogar empresa = new TecniHogar();

    public static void main(String[] args) {
        cargarDatosIniciales();
        System.out.println("=== TecniHogar - Sistema de ordenes de servicio ===");

        int opcion = -1;
        while (opcion != 0) {
            mostrarMenu();
            opcion = leerEntero("Seleccione una opcion: ");
            try {
                switch (opcion) {
                    case 1: cotizarYRegistrarOrden(); break;
                    case 2: registrarCuadrilla(); break;
                    case 3: crearJornada(); break;
                    case 4: asignarOrden(); break;
                    case 5: retirarOrden(); break;
                    case 6: iniciarJornada(); break;
                    case 7: registrarResultado(); break;
                    case 8: consultarOrdenes(); break;
                    case 9: consultarCuadrillas(); break;
                    case 10: System.out.println(empresa.reporteOrdenesPorCategoriaYEstado()); break;
                    case 11: System.out.println(empresa.reporteIngresos()); break;
                    case 12: System.out.println(empresa.reporteJornadasAbiertas()); break;
                    case 0: System.out.println("Hasta pronto."); break;
                    default: System.out.println("Opcion invalida.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("ERROR: " + e.getMessage() + " No se realizo ningun cambio.");
            }
        }
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("------------------- MENU -------------------");
        System.out.println(" 1. Cotizar / registrar orden");
        System.out.println(" 2. Registrar cuadrilla");
        System.out.println(" 3. Crear jornada");
        System.out.println(" 4. Asignar orden a jornada");
        System.out.println(" 5. Retirar orden de jornada");
        System.out.println(" 6. Iniciar jornada");
        System.out.println(" 7. Registrar resultado de visita");
        System.out.println(" 8. Consultar ordenes");
        System.out.println(" 9. Consultar cuadrillas");
        System.out.println("10. Reporte: ordenes por categoria y estado");
        System.out.println("11. Reporte: ingresos");
        System.out.println("12. Reporte: jornadas sin cerrar");
        System.out.println(" 0. Salir");
    }

   //menu

    private static void cotizarYRegistrarOrden() {
        System.out.println("Categorias: 1) Diagnostico  2) Instalacion  3) Instalacion electrica certificada  4) Mantenimiento de A/C");
        int categoria = leerOpcion("Categoria: ", 1, 4);
        String codigo = leerTexto("Codigo de la orden: ");
        empresa.validarCodigoOrdenNuevo(codigo);
        String cliente = leerTexto("Nombre del cliente: ");
        String direccion = leerTexto("Direccion: ");
        double horas = leerDecimal("Horas estimadas: ");
        Zona zona = leerZona();

        // El menu solo decide QUE objeto crear; las reglas viven en cada subclase.
        OrdenServicio orden;
        if (categoria == 1) {
            orden = new Diagnostico(codigo, cliente, direccion, horas, zona);
        } else if (categoria == 2) {
            int puntos = leerEntero("Cantidad de puntos a instalar: ");
            orden = new Instalacion(codigo, cliente, direccion, horas, zona, puntos);
        } else if (categoria == 3) {
            int puntos = leerEntero("Cantidad de puntos a instalar: ");
            double materiales = leerDecimal("Valor declarado de los materiales (Q): ");
            orden = new InstalacionElectricaCertificada(codigo, cliente, direccion, horas, zona, puntos, materiales);
        } else {
            int btu = leerEntero("Capacidad del equipo (BTU): ");
            orden = new MantenimientoAireAcondicionado(codigo, cliente, direccion, horas, zona, btu);
        }

        System.out.println(" COTIZACION ");
        System.out.println("Categoria: " + orden.getCategoria());
        if (!orden.getDetalleCategoria().isEmpty()) {
            System.out.println(orden.getDetalleCategoria());
        }
        System.out.printf("Costo: Q%.2f%n", orden.calcularCosto());
        System.out.println("Tiempo estimado de atencion: " + orden.getDiasAtencion() + " dia(s) habil(es)");

        String respuesta = leerTexto("El cliente acepta la cotizacion? (s/n): ");
        if (respuesta.equalsIgnoreCase("s")) {
            empresa.registrarOrden(orden);
            System.out.printf("Orden %s registrada como PENDIENTE. Se cobraron Q%.2f%n",
                    orden.getCodigo(), orden.getMontoRegistro());
        } else {
            System.out.println("Cotizacion descartada. No se registro nada.");
        }
    }

    private static void registrarCuadrilla() {
        String codigo = leerTexto("Codigo de la cuadrilla: ");
        double capacidad = leerDecimal("Capacidad maxima de horas por jornada: ");
        String cert = leerTexto("Esta certificada en refrigeracion? (s/n): ");
        Cuadrilla cuadrilla = new Cuadrilla(codigo, capacidad, cert.equalsIgnoreCase("s"));
        empresa.registrarCuadrilla(cuadrilla);
        System.out.println("Cuadrilla registrada: " + cuadrilla);
    }

    private static void crearJornada() {
        consultarCuadrillas();
        String codigo = leerTexto("Codigo de la cuadrilla para la jornada: ");
        Jornada jornada = empresa.crearJornada(codigo);
        System.out.println("Jornada " + jornada.getCodigo() + " creada EN PREPARACION para la cuadrilla "
                + jornada.getCuadrilla().getCodigo() + ".");
    }

    private static void asignarOrden() {
        System.out.print(empresa.reporteJornadasAbiertas());
        String codigoJornada = leerTexto("Codigo de la jornada: ");
        mostrarOrdenesPendientes();
        String codigoOrden = leerTexto("Codigo de la orden a asignar: ");
        empresa.asignarOrden(codigoJornada, codigoOrden);
        System.out.println("Orden asignada. Estado de la jornada:");
        System.out.println(empresa.buscarJornada(codigoJornada));
    }

    private static void retirarOrden() {
        System.out.print(empresa.reporteJornadasAbiertas());
        String codigoJornada = leerTexto("Codigo de la jornada: ");
        String codigoOrden = leerTexto("Codigo de la orden a retirar: ");
        empresa.retirarOrden(codigoJornada, codigoOrden);
        System.out.println("Orden retirada; vuelve a quedar PENDIENTE.");
        System.out.println(empresa.buscarJornada(codigoJornada));
    }

    private static void iniciarJornada() {
        System.out.print(empresa.reporteJornadasAbiertas());
        String codigoJornada = leerTexto("Codigo de la jornada a iniciar: ");
        empresa.iniciarJornada(codigoJornada);
        System.out.println("Jornada iniciada. Sus ordenes estan EN ATENCION.");
        System.out.println(empresa.buscarJornada(codigoJornada));
    }

    private static void registrarResultado() {
        System.out.print(empresa.reporteJornadasAbiertas());
        String codigoJornada = leerTexto("Codigo de la jornada: ");
        Jornada jornada = empresa.buscarJornada(codigoJornada);
        if (jornada != null) {
            System.out.println("Ordenes sin resultado:");
            for (OrdenServicio orden : jornada.getOrdenes()) {
                if (!jornada.tieneResultado(orden)) {
                    System.out.println("  " + orden.getCodigo() + " - " + orden.getCategoria());
                }
            }
        }
        String codigoOrden = leerTexto("Codigo de la orden: ");
        int resultado = leerOpcion("Resultado (1 = completada, 2 = visita fallida): ", 1, 2);
        empresa.registrarResultado(codigoJornada, codigoOrden, resultado == 1);
        OrdenServicio orden = empresa.buscarOrden(codigoOrden);
        System.out.println("Resultado registrado. La orden " + orden.getCodigo() + " queda " + orden.getEstado()
                + " (visitas " + orden.getVisitasRealizadas() + "/" + orden.getMaxVisitas() + ").");
        System.out.println("Estado de la jornada " + jornada.getCodigo() + ": " + jornada.getEstado());
    }

    private static void consultarOrdenes() {
        System.out.println("Ordenes registradas:");
        for (OrdenServicio orden : empresa.getOrdenes()) {
            System.out.println(orden);
        }
    }

    private static void mostrarOrdenesPendientes() {
        System.out.println("Ordenes pendientes:");
        for (OrdenServicio orden : empresa.getOrdenes()) {
            if (orden.getEstado() == EstadoOrden.PENDIENTE) {
                System.out.printf("  %-8s %-36s %.2f h%n", orden.getCodigo(), orden.getCategoria(), orden.getHorasEstimadas());
            }
        }
    }

    private static void consultarCuadrillas() {
        System.out.println("Cuadrillas:");
        for (Cuadrilla cuadrilla : empresa.getCuadrillas()) {
            Jornada abierta = empresa.buscarJornadaAbierta(cuadrilla);
            String situacion = (abierta == null) ? "libre" : "jornada " + abierta.getCodigo() + " sin cerrar";
            System.out.println("  " + cuadrilla + " - " + situacion);
        }
    }

    //lectura segura de datos

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        if (!scanner.hasNextLine()) {
            System.out.println("\nFin de la entrada. Saliendo del programa.");
            System.exit(0);
        }
        return scanner.nextLine().trim();
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje);
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Formato invalido: ingrese un numero entero.");
            }
        }
    }

    private static double leerDecimal(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje);
            try {
                return Double.parseDouble(texto);
            } catch (NumberFormatException e) {
                System.out.println("Formato invalido: ingrese un numero (use punto decimal, por ejemplo 2.5).");
            }
        }
    }

    private static int leerOpcion(String mensaje, int minimo, int maximo) {
        while (true) {
            int valor = leerEntero(mensaje);
            if (valor >= minimo && valor <= maximo) {
                return valor;
            }
            System.out.println("Opcion fuera de rango: elija entre " + minimo + " y " + maximo + ".");
        }
    }

    private static Zona leerZona() {
        int opcion = leerOpcion("Zona (1 = metropolitana, 2 = periferica): ", 1, 2);
        if (opcion == 1) {
            return Zona.METROPOLITANA;
        }
        return Zona.PERIFERICA;
    }

//datos iniciales

    private static void cargarDatosIniciales() {

        empresa.cargarOrdenInicial(new Diagnostico("D-001", "Ana Lopez", "Zona 10, Ciudad de Guatemala", 1.0, Zona.METROPOLITANA));
        empresa.cargarOrdenInicial(new Diagnostico("D-002", "Bruno Perez", "Mixco", 1.5, Zona.PERIFERICA));
  
        empresa.cargarOrdenInicial(new Instalacion("I-001", "Carla Ruiz", "Zona 15, Ciudad de Guatemala", 2.5, Zona.METROPOLITANA, 5));
        empresa.cargarOrdenInicial(new Instalacion("I-002", "Diego Mendez", "Villa Nueva", 3.2, Zona.PERIFERICA, 2));

        empresa.cargarOrdenInicial(new InstalacionElectricaCertificada("E-001", "Elena Castillo", "Zona 14, Ciudad de Guatemala", 2.5, Zona.METROPOLITANA, 5, 2000));
        empresa.cargarOrdenInicial(new InstalacionElectricaCertificada("E-002", "Fernando Solis", "San Lucas Sacatepequez", 1.0, Zona.PERIFERICA, 2, 3000));
       
        empresa.cargarOrdenInicial(new MantenimientoAireAcondicionado("M-001", "Gabriela Ortiz", "Zona 4, Ciudad de Guatemala", 2.0, Zona.METROPOLITANA, 12000));
        empresa.cargarOrdenInicial(new MantenimientoAireAcondicionado("M-002", "Hugo Ramirez", "Zona 9, Ciudad de Guatemala", 1.5, Zona.METROPOLITANA, 36000));


        empresa.registrarCuadrilla(new Cuadrilla("C-01", 8.0, true));
        empresa.registrarCuadrilla(new Cuadrilla("C-02", 6.0, false));
        empresa.registrarCuadrilla(new Cuadrilla("C-03", 4.0, false));
    }
}
