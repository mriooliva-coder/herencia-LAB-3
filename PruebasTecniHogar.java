public class PruebasTecniHogar {

    private static int aprobadas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) {
        System.out.println("ID|Descripcion|Entrada|Esperado|Obtenido|Resultado");
        probarCobros();
        probarTiempos();
        probarValidaciones();
        probarCapacidadYCertificacion();
        probarPoliticaDeVisitas();
        probarIngresos();
        System.out.println();
        System.out.println("Pruebas aprobadas: " + aprobadas + " / " + (aprobadas + fallidas));
    }


    private static void probarCobros() {
        verificar("C01", "Diagnostico metropolitano", "1.0 h, metropolitana",
                150.00, new Diagnostico("X", "Cli", "Dir", 1.0, Zona.METROPOLITANA).calcularCosto());
        verificar("C02", "Diagnostico periferico (precio fijo sin importar horas)", "1.5 h, periferica",
                225.00, new Diagnostico("X", "Cli", "Dir", 1.5, Zona.PERIFERICA).calcularCosto());
        verificar("C03", "Instalacion: horas por puntos > estimadas (ejemplo del enunciado)", "2.5 h, 5 puntos, metropolitana",
                460.00, new Instalacion("X", "Cli", "Dir", 2.5, Zona.METROPOLITANA, 5).calcularCosto());
        verificar("C04", "Instalacion: estimadas > puntos y hora por fraccion (3.2 -> 4)", "3.2 h, 2 puntos, periferica",
                535.00, new Instalacion("X", "Cli", "Dir", 3.2, Zona.PERIFERICA, 2).calcularCosto());
        verificar("C05", "Instalacion: horas exactas, sin fraccion", "3.0 h, 4 puntos, metropolitana",
                370.00, new Instalacion("X", "Cli", "Dir", 3.0, Zona.METROPOLITANA, 4).calcularCosto());
        verificar("C06", "Certificada: instalacion + Q120 + garantia 5% (ejemplo del enunciado)", "2.5 h, 5 puntos, Q2000, metropolitana",
                680.00, new InstalacionElectricaCertificada("X", "Cli", "Dir", 2.5, Zona.METROPOLITANA, 5, 2000).calcularCosto());
        verificar("C07", "Certificada periferica (tarifa base Q175)", "1.0 h, 2 puntos, Q3000, periferica",
                625.00, new InstalacionElectricaCertificada("X", "Cli", "Dir", 1.0, Zona.PERIFERICA, 2, 3000).calcularCosto());
        verificar("C08", "Mantenimiento A/C: base + Q110/h + Q80 gas", "2.0 h, 12000 BTU, metropolitana",
                400.00, new MantenimientoAireAcondicionado("X", "Cli", "Dir", 2.0, Zona.METROPOLITANA, 12000).calcularCosto());
        verificar("C09", "Mantenimiento A/C con horas decimales (no se redondea)", "1.5 h, 36000 BTU, metropolitana",
                345.00, new MantenimientoAireAcondicionado("X", "Cli", "Dir", 1.5, Zona.METROPOLITANA, 36000).calcularCosto());
    }

 

    private static void probarTiempos() {
        verificar("T01", "Dias diagnostico metropolitano", "metropolitana", 1,
                new Diagnostico("X", "Cli", "Dir", 1.0, Zona.METROPOLITANA).getDiasAtencion());
        verificar("T02", "Dias diagnostico periferico", "periferica", 2,
                new Diagnostico("X", "Cli", "Dir", 1.0, Zona.PERIFERICA).getDiasAtencion());
        verificar("T03", "Dias instalacion metropolitana", "metropolitana", 3,
                new Instalacion("X", "Cli", "Dir", 2.0, Zona.METROPOLITANA, 1).getDiasAtencion());
        verificar("T04", "Dias instalacion periferica", "periferica", 5,
                new Instalacion("X", "Cli", "Dir", 2.0, Zona.PERIFERICA, 1).getDiasAtencion());
        verificar("T05", "Dias certificada metropolitana (3 + 2)", "metropolitana", 5,
                new InstalacionElectricaCertificada("X", "Cli", "Dir", 2.0, Zona.METROPOLITANA, 1, 500).getDiasAtencion());
        verificar("T06", "Dias certificada periferica (5 + 2)", "periferica", 7,
                new InstalacionElectricaCertificada("X", "Cli", "Dir", 2.0, Zona.PERIFERICA, 1, 500).getDiasAtencion());
        verificar("T07", "Dias mantenimiento A/C (siempre 2)", "metropolitana", 2,
                new MantenimientoAireAcondicionado("X", "Cli", "Dir", 2.0, Zona.METROPOLITANA, 12000).getDiasAtencion());
    }

 

    private static void probarValidaciones() {
        TecniHogar empresa = new TecniHogar();
        empresa.cargarOrdenInicial(new Diagnostico("D-1", "Cli", "Dir", 1.0, Zona.METROPOLITANA));

        verificarRechazo("V01", "Diagnostico de mas de 1.5 horas", "2.0 h", () ->
                new Diagnostico("X", "Cli", "Dir", 2.0, Zona.METROPOLITANA));
        verificarRechazo("V02", "Mantenimiento A/C en zona periferica", "periferica", () ->
                new MantenimientoAireAcondicionado("X", "Cli", "Dir", 2.0, Zona.PERIFERICA, 12000));
        verificarRechazo("V03", "Mantenimiento A/C con BTU bajo el rango", "8000 BTU", () ->
                new MantenimientoAireAcondicionado("X", "Cli", "Dir", 2.0, Zona.METROPOLITANA, 8000));
        verificarRechazo("V04", "Mantenimiento A/C con BTU sobre el rango", "70000 BTU", () ->
                new MantenimientoAireAcondicionado("X", "Cli", "Dir", 2.0, Zona.METROPOLITANA, 70000));
        verificarRechazo("V05", "Instalacion con 0 puntos", "0 puntos", () ->
                new Instalacion("X", "Cli", "Dir", 2.0, Zona.METROPOLITANA, 0));
        verificarRechazo("V06", "Horas estimadas negativas", "-1 h", () ->
                new Instalacion("X", "Cli", "Dir", -1, Zona.METROPOLITANA, 2));
        verificarRechazo("V07", "Certificada con materiales en 0", "Q0", () ->
                new InstalacionElectricaCertificada("X", "Cli", "Dir", 2.0, Zona.METROPOLITANA, 2, 0));
        verificarRechazo("V08", "Codigo de orden vacio", "codigo \"\"", () ->
                new Diagnostico("  ", "Cli", "Dir", 1.0, Zona.METROPOLITANA));
        verificarRechazo("V09", "Codigo de orden repetido", "D-1 ya existe", () ->
                empresa.registrarOrden(new Diagnostico("D-1", "Otro", "Dir", 1.0, Zona.METROPOLITANA)));
        verificar("V10", "El codigo repetido no agrego orden ni ingreso", "despues de V09",
                "1 orden, Q0.00", empresa.getOrdenes().size() + " orden, Q" + String.format("%.2f", empresa.getIngresoTotal()));
        verificarRechazo("V11", "Cuadrilla con capacidad 0", "0 h", () ->
                new Cuadrilla("C-X", 0, true));
        empresa.registrarCuadrilla(new Cuadrilla("C-1", 5, false));
        verificarRechazo("V12", "Codigo de cuadrilla repetido", "C-1 ya existe", () ->
                empresa.registrarCuadrilla(new Cuadrilla("C-1", 8, true)));
    }



    private static void probarCapacidadYCertificacion() {
        TecniHogar empresa = new TecniHogar();
        empresa.cargarOrdenInicial(new Instalacion("I-1", "Cli", "Dir", 3.0, Zona.METROPOLITANA, 2));
        empresa.cargarOrdenInicial(new Instalacion("I-2", "Cli", "Dir", 2.0, Zona.METROPOLITANA, 2));
        empresa.cargarOrdenInicial(new MantenimientoAireAcondicionado("M-1", "Cli", "Dir", 2.0, Zona.METROPOLITANA, 12000));
        empresa.registrarCuadrilla(new Cuadrilla("C-NC", 4.0, false));
        empresa.registrarCuadrilla(new Cuadrilla("C-CE", 8.0, true));
        empresa.registrarCuadrilla(new Cuadrilla("C-NC2", 10.0, false));

        Jornada j1 = empresa.crearJornada("C-NC");
        empresa.asignarOrden(j1.getCodigo(), "I-1");
        verificarRechazo("K01", "Asignacion que excede la capacidad", "C-NC 4 h: I-1 (3 h) + I-2 (2 h)", () ->
                empresa.asignarOrden(j1.getCodigo(), "I-2"));
        verificar("K02", "Tras el rechazo, horas e I-2 sin cambios", "despues de K01",
                "3.00 h, I-2 PENDIENTE",
                String.format("%.2f h, I-2 %s", j1.getHorasAsignadas(), empresa.buscarOrden("I-2").getEstado()));
        Jornada jNoCert = empresa.crearJornada("C-NC2");
        verificarRechazo("K03", "A/C en cuadrilla no certificada (con capacidad suficiente)", "M-1 (2 h) en C-NC2 (10 h libres)", () ->
                empresa.asignarOrden(jNoCert.getCodigo(), "M-1"));
        verificar("K04", "M-1 sigue PENDIENTE tras el rechazo", "despues de K03",
                "PENDIENTE", empresa.buscarOrden("M-1").getEstado().toString());

        Jornada j2 = empresa.crearJornada("C-CE");
        empresa.asignarOrden(j2.getCodigo(), "M-1");
        verificar("K05", "A/C en cuadrilla certificada se acepta", "M-1 en C-CE",
                "ASIGNADA", empresa.buscarOrden("M-1").getEstado().toString());
        verificarRechazo("K06", "Orden ya asignada a otra jornada", "I-1 (en J1) a J2", () ->
                empresa.asignarOrden(j2.getCodigo(), "I-1"));
        verificar("K07", "Limite exacto de capacidad se acepta", "C-CE 8 h: M-1 (2 h) + I-2 (2 h)",
                "4.00 h", asignarYLeer(empresa, j2, "I-2"));
        verificarRechazo("K08", "Segunda jornada sin cerrar para la misma cuadrilla", "C-NC ya tiene J1", () ->
                empresa.crearJornada("C-NC"));

        Cuadrilla cVacia = new Cuadrilla("C-V", 5, false);
        empresa.registrarCuadrilla(cVacia);
        Jornada jVacia = empresa.crearJornada("C-V");
        verificarRechazo("K09", "Iniciar jornada vacia", "jornada sin ordenes", () ->
                empresa.iniciarJornada(jVacia.getCodigo()));

        empresa.iniciarJornada(j1.getCodigo());
        verificarRechazo("K10", "Retirar orden de una jornada ya iniciada", "I-1 de J1 en curso", () ->
                empresa.retirarOrden(j1.getCodigo(), "I-1"));
        verificarRechazo("K11", "Asignar a una jornada ya iniciada", "I-2 a J1 en curso", () ->
                empresa.asignarOrden(j1.getCodigo(), "I-2"));
        verificarRechazo("K12", "Registrar resultado en jornada en preparacion", "J2 sin iniciar", () ->
                empresa.registrarResultado(j2.getCodigo(), "M-1", true));
    }

    private static String asignarYLeer(TecniHogar empresa, Jornada jornada, String codigoOrden) {
        empresa.asignarOrden(jornada.getCodigo(), codigoOrden);
        return String.format("%.2f h", jornada.getHorasAsignadas());
    }

    private static void probarPoliticaDeVisitas() {
        TecniHogar empresa = new TecniHogar();
        empresa.registrarCuadrilla(new Cuadrilla("C-1", 8, true));
        empresa.cargarOrdenInicial(new Diagnostico("D-1", "Cli", "Dir", 1.0, Zona.METROPOLITANA));
        empresa.cargarOrdenInicial(new Instalacion("I-1", "Cli", "Dir", 2.0, Zona.METROPOLITANA, 2));
        empresa.cargarOrdenInicial(new InstalacionElectricaCertificada("E-1", "Cli", "Dir", 2.0, Zona.METROPOLITANA, 2, 1000));
        empresa.cargarOrdenInicial(new MantenimientoAireAcondicionado("M-1", "Cli", "Dir", 2.0, Zona.METROPOLITANA, 12000));
        empresa.cargarOrdenInicial(new Instalacion("I-2", "Cli", "Dir", 2.0, Zona.METROPOLITANA, 2));

        verificar("P01", "Diagnostico: 3 visitas fallidas -> CANCELADA", "fallida x3",
                "CANCELADA, 3 visitas, Q80.00", visitasFallidas(empresa, "D-1", 3));
        verificar("P02", "Instalacion: 3 visitas fallidas -> CANCELADA", "fallida x3",
                "CANCELADA, 3 visitas, Q80.00", visitasFallidas(empresa, "I-1", 3));
        verificar("P03", "Certificada: 2 visitas fallidas -> CANCELADA", "fallida x2",
                "CANCELADA, 2 visitas, Q40.00", visitasFallidas(empresa, "E-1", 2));
        verificar("P04", "Mantenimiento A/C: 1 visita fallida -> CANCELADA", "fallida x1",
                "CANCELADA, 1 visitas, Q0.00", visitasFallidas(empresa, "M-1", 1));

        Jornada j = empresa.crearJornada("C-1");
        verificarRechazo("P05", "Orden cancelada no puede asignarse", "D-1 cancelada", () ->
                empresa.asignarOrden(j.getCodigo(), "D-1"));

   
        empresa.asignarOrden(j.getCodigo(), "I-2");
        empresa.iniciarJornada(j.getCodigo());
        empresa.registrarResultado(j.getCodigo(), "I-2", false);
        OrdenServicio i2 = empresa.buscarOrden("I-2");
        verificar("P06", "Visita fallida con visitas restantes -> PENDIENTE", "I-2 fallida (1/3)",
                "PENDIENTE", i2.getEstado().toString());
        verificar("P07", "La jornada se cierra cuando todas tienen resultado y la cuadrilla queda libre", "J con 1 orden",
                "CERRADA, libre", j.getEstado() + ", " + (empresa.buscarJornadaAbierta(j.getCuadrilla()) == null ? "libre" : "ocupada"));

        Jornada j2 = empresa.crearJornada("C-1");
        empresa.asignarOrden(j2.getCodigo(), "I-2");
        verificar("P08", "El cargo de Q40 NO se registra al asignar", "I-2 asignada por 2da vez",
                "Q0.00", String.format("Q%.2f", i2.getMontoVisitasAdicionales()));
        empresa.iniciarJornada(j2.getCodigo());
        verificar("P09", "El cargo de Q40 se registra al iniciar la jornada", "inicio de la 2da jornada",
                "Q40.00", String.format("Q%.2f", i2.getMontoVisitasAdicionales()));
        empresa.registrarResultado(j2.getCodigo(), "I-2", true);
        verificar("P10", "Segunda visita completada", "I-2 completada",
                "COMPLETADA", i2.getEstado().toString());
        verificarRechazo("P11", "Registrar resultado en una jornada ya cerrada", "I-2 en jornada cerrada", () ->
                empresa.registrarResultado(j2.getCodigo(), "I-2", true));

        empresa.cargarOrdenInicial(new Diagnostico("D-7", "Cli", "Dir", 1.0, Zona.METROPOLITANA));
        empresa.cargarOrdenInicial(new Diagnostico("D-8", "Cli", "Dir", 1.0, Zona.METROPOLITANA));
        Jornada j3 = empresa.crearJornada("C-1");
        empresa.asignarOrden(j3.getCodigo(), "D-7");
        empresa.asignarOrden(j3.getCodigo(), "D-8");
        empresa.iniciarJornada(j3.getCodigo());
        empresa.registrarResultado(j3.getCodigo(), "D-7", true);
        verificar("P12", "Con una orden sin resultado la jornada sigue abierta", "D-7 completada, D-8 sin resultado",
                "EN_CURSO", j3.getEstado().toString());
        verificarRechazo("P13", "Registrar dos veces el resultado de una orden", "D-7 otra vez", () ->
                empresa.registrarResultado(j3.getCodigo(), "D-7", false));
    }


    private static String visitasFallidas(TecniHogar empresa, String codigoOrden, int veces) {
        for (int i = 0; i < veces; i++) {
            Jornada j = empresa.crearJornada("C-1");
            empresa.asignarOrden(j.getCodigo(), codigoOrden);
            empresa.iniciarJornada(j.getCodigo());
            empresa.registrarResultado(j.getCodigo(), codigoOrden, false);
        }
        OrdenServicio orden = empresa.buscarOrden(codigoOrden);
        return String.format("%s, %d visitas, Q%.2f", orden.getEstado(), orden.getVisitasRealizadas(),
                orden.getMontoVisitasAdicionales());
    }

    

    private static void probarIngresos() {
        TecniHogar empresa = new TecniHogar();
        empresa.registrarCuadrilla(new Cuadrilla("C-1", 8, false));
        empresa.registrarCuadrilla(new Cuadrilla("C-2", 8, false));
        empresa.cargarOrdenInicial(new Diagnostico("D-1", "Cli", "Dir", 1.0, Zona.METROPOLITANA));
        verificar("R01", "Los datos iniciales no generan ingresos", "1 orden precargada",
                "Q0.00", String.format("Q%.2f", empresa.getIngresoTotal()));

     
        OrdenServicio cotizacion = new Instalacion("I-9", "Cli", "Dir", 2.5, Zona.METROPOLITANA, 5);
        cotizacion.calcularCosto();
        cotizacion.getDiasAtencion();
        verificar("R02", "Una cotizacion no modifica ingresos ni ordenes", "cotizar I-9 (Q460) sin aceptar",
                "Q0.00, 1 orden", String.format("Q%.2f, %d orden", empresa.getIngresoTotal(), empresa.getOrdenes().size()));

        empresa.registrarOrden(cotizacion);
        verificar("R03", "Registrar una orden cobra el monto completo", "aceptar I-9",
                "Q460.00", String.format("Q%.2f", empresa.getIngresosPorRegistro()));

        Jornada j = empresa.crearJornada("C-1");
        empresa.asignarOrden(j.getCodigo(), "I-9");
        empresa.retirarOrden(j.getCodigo(), "I-9");
        verificar("R04", "Asignar y retirar no modifica ingresos", "asignar y retirar I-9",
                "Q460.00, I-9 PENDIENTE", String.format("Q%.2f, I-9 %s", empresa.getIngresoTotal(),
                        empresa.buscarOrden("I-9").getEstado()));

        double antes = empresa.getIngresoTotal();
        intentar(() -> empresa.registrarOrden(new Diagnostico("I-9", "Cli", "Dir", 1.0, Zona.METROPOLITANA)));
        intentar(() -> empresa.registrarOrden(new Diagnostico("D-5", "Cli", "Dir", 3.0, Zona.METROPOLITANA)));
        intentar(() -> empresa.iniciarJornada(j.getCodigo()));   
        intentar(() -> empresa.crearJornada("C-1"));             
        verificar("R05", "Operaciones rechazadas no modifican ingresos", "codigo repetido, diag 3 h, jornada vacia, 2da jornada",
                String.format("Q%.2f", antes), String.format("Q%.2f", empresa.getIngresoTotal()));


        empresa.registrarOrden(new Diagnostico("D-2", "Cli", "Dir", 1.0, Zona.PERIFERICA));   
        empresa.asignarOrden(j.getCodigo(), "D-2");
        empresa.iniciarJornada(j.getCodigo());
        empresa.registrarResultado(j.getCodigo(), "D-2", false);
        Jornada j2 = empresa.crearJornada("C-2");
        empresa.asignarOrden(j2.getCodigo(), "D-2");
        empresa.iniciarJornada(j2.getCodigo());
        verificar("R06", "Ingresos separados: registro y visitas adicionales", "I-9 Q460 + D-2 Q225, D-2 con 2da visita",
                "registro Q685.00, visitas Q40.00, total Q725.00",
                String.format("registro Q%.2f, visitas Q%.2f, total Q%.2f", empresa.getIngresosPorRegistro(),
                        empresa.getIngresosPorVisitas(), empresa.getIngresoTotal()));
        verificar("R07", "Ingreso por categoria Diagnostico", "D-1 precargada Q0 + D-2 (Q225 + Q40)",
                "Q265.00", String.format("Q%.2f", empresa.buscarOrden("D-2").getIngresoTotal()
                        + empresa.buscarOrden("D-1").getIngresoTotal()));
    }


    private static void verificar(String id, String descripcion, String entrada, double esperado, double obtenido) {
        verificar(id, descripcion, entrada, String.format("Q%.2f", esperado), String.format("Q%.2f", obtenido));
    }

    private static void verificar(String id, String descripcion, String entrada, int esperado, int obtenido) {
        verificar(id, descripcion, entrada, esperado + " dia(s)", obtenido + " dia(s)");
    }

    private static void verificar(String id, String descripcion, String entrada, String esperado, String obtenido) {
        boolean ok = esperado.equals(obtenido);
        if (ok) {
            aprobadas++;
        } else {
            fallidas++;
        }
        System.out.println(id + "|" + descripcion + "|" + entrada + "|" + esperado + "|" + obtenido + "|" + (ok ? "PASA" : "FALLA"));
    }


    private static void verificarRechazo(String id, String descripcion, String entrada, Runnable operacion) {
        String obtenido;
        try {
            operacion.run();
            obtenido = "Aceptada (no se rechazo)";
        } catch (IllegalArgumentException | IllegalStateException e) {
            obtenido = "Rechazada: " + e.getMessage();
        }
        boolean ok = obtenido.startsWith("Rechazada");
        if (ok) {
            aprobadas++;
        } else {
            fallidas++;
        }
        System.out.println(id + "|" + descripcion + "|" + entrada + "|Rechazada con mensaje|" + obtenido + "|" + (ok ? "PASA" : "FALLA"));
    }

    private static void intentar(Runnable operacion) {
        try {
            operacion.run();
        } catch (IllegalArgumentException | IllegalStateException e) {
            // se espera el rechazo
        }
    }
}
