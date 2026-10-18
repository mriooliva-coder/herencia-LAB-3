import java.util.ArrayList;
import java.util.LinkedHashMap;


public class TecniHogar {

    private ArrayList<OrdenServicio> ordenes;
    private ArrayList<Cuadrilla> cuadrillas;
    private ArrayList<Jornada> jornadas;
    private int contadorJornadas;

    public TecniHogar() {
        ordenes = new ArrayList<>();
        cuadrillas = new ArrayList<>();
        jornadas = new ArrayList<>();
        contadorJornadas = 0;
    }

  

 
    public void registrarOrden(OrdenServicio orden) {
        validarCodigoOrdenNuevo(orden.getCodigo());
        ordenes.add(orden);
        orden.cobrarRegistro();
    }

   
    public void cargarOrdenInicial(OrdenServicio orden) {
        validarCodigoOrdenNuevo(orden.getCodigo());
        ordenes.add(orden);
    }

    public void validarCodigoOrdenNuevo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El codigo de la orden no puede estar vacio.");
        }
        if (buscarOrden(codigo) != null) {
            throw new IllegalArgumentException("Ya existe una orden con el codigo " + codigo.trim() + ".");
        }
    }

    public OrdenServicio buscarOrden(String codigo) {
        for (OrdenServicio orden : ordenes) {
            if (orden.getCodigo().equalsIgnoreCase(codigo.trim())) {
                return orden;
            }
        }
        return null;
    }

    private OrdenServicio obtenerOrden(String codigo) {
        OrdenServicio orden = buscarOrden(codigo);
        if (orden == null) {
            throw new IllegalArgumentException("No existe una orden con el codigo " + codigo + ".");
        }
        return orden;
    }



    public void registrarCuadrilla(Cuadrilla cuadrilla) {
        if (buscarCuadrilla(cuadrilla.getCodigo()) != null) {
            throw new IllegalArgumentException("Ya existe una cuadrilla con el codigo " + cuadrilla.getCodigo() + ".");
        }
        cuadrillas.add(cuadrilla);
    }

    public Cuadrilla buscarCuadrilla(String codigo) {
        for (Cuadrilla cuadrilla : cuadrillas) {
            if (cuadrilla.getCodigo().equalsIgnoreCase(codigo.trim())) {
                return cuadrilla;
            }
        }
        return null;
    }


    public Jornada buscarJornadaAbierta(Cuadrilla cuadrilla) {
        for (Jornada jornada : jornadas) {
            if (jornada.getCuadrilla() == cuadrilla && jornada.estaAbierta()) {
                return jornada;
            }
        }
        return null;
    }



    public Jornada crearJornada(String codigoCuadrilla) {
        Cuadrilla cuadrilla = buscarCuadrilla(codigoCuadrilla);
        if (cuadrilla == null) {
            throw new IllegalArgumentException("No existe una cuadrilla con el codigo " + codigoCuadrilla + ".");
        }
        Jornada abierta = buscarJornadaAbierta(cuadrilla);
        if (abierta != null) {
            throw new IllegalStateException("La cuadrilla " + cuadrilla.getCodigo()
                    + " ya tiene una jornada sin cerrar (" + abierta.getCodigo() + ").");
        }
        contadorJornadas++;
        Jornada jornada = new Jornada("J" + contadorJornadas, cuadrilla);
        jornadas.add(jornada);
        return jornada;
    }

    public Jornada buscarJornada(String codigo) {
        for (Jornada jornada : jornadas) {
            if (jornada.getCodigo().equalsIgnoreCase(codigo.trim())) {
                return jornada;
            }
        }
        return null;
    }

    private Jornada obtenerJornada(String codigo) {
        Jornada jornada = buscarJornada(codigo);
        if (jornada == null) {
            throw new IllegalArgumentException("No existe una jornada con el codigo " + codigo + ".");
        }
        return jornada;
    }

    public void asignarOrden(String codigoJornada, String codigoOrden) {
        Jornada jornada = obtenerJornada(codigoJornada);
        OrdenServicio orden = obtenerOrden(codigoOrden);
        jornada.asignarOrden(orden);
    }

    public void retirarOrden(String codigoJornada, String codigoOrden) {
        Jornada jornada = obtenerJornada(codigoJornada);
        OrdenServicio orden = obtenerOrden(codigoOrden);
        jornada.retirarOrden(orden);
    }

    public void iniciarJornada(String codigoJornada) {
        obtenerJornada(codigoJornada).iniciar();
    }

    public void registrarResultado(String codigoJornada, String codigoOrden, boolean completada) {
        Jornada jornada = obtenerJornada(codigoJornada);
        OrdenServicio orden = obtenerOrden(codigoOrden);
        jornada.registrarResultado(orden, completada);
    }



    public double getIngresosPorRegistro() {
        double total = 0;
        for (OrdenServicio orden : ordenes) {
            total += orden.getMontoRegistro();
        }
        return total;
    }

    public double getIngresosPorVisitas() {
        double total = 0;
        for (OrdenServicio orden : ordenes) {
            total += orden.getMontoVisitasAdicionales();
        }
        return total;
    }

    public double getIngresoTotal() {
        return getIngresosPorRegistro() + getIngresosPorVisitas();
    }


    public String reporteOrdenesPorCategoriaYEstado() {
        LinkedHashMap<String, int[]> conteo = new LinkedHashMap<>();
        EstadoOrden[] estados = EstadoOrden.values();
        for (OrdenServicio orden : ordenes) {
            String categoria = orden.getCategoria();
            if (!conteo.containsKey(categoria)) {
                conteo.put(categoria, new int[estados.length]);
            }
            conteo.get(categoria)[orden.getEstado().ordinal()]++;
        }

        String reporte = String.format("%-36s", "Categoria");
        for (EstadoOrden estado : estados) {
            reporte += String.format("%12s", estado);
        }
        reporte += String.format("%8s%n", "TOTAL");

        int[] totalesPorEstado = new int[estados.length];
        for (String categoria : conteo.keySet()) {
            int[] fila = conteo.get(categoria);
            int totalFila = 0;
            reporte += String.format("%-36s", categoria);
            for (int i = 0; i < fila.length; i++) {
                reporte += String.format("%12d", fila[i]);
                totalFila += fila[i];
                totalesPorEstado[i] += fila[i];
            }
            reporte += String.format("%8d%n", totalFila);
        }
        reporte += String.format("%-36s", "TOTAL");
        for (int total : totalesPorEstado) {
            reporte += String.format("%12d", total);
        }
        reporte += String.format("%8d%n", ordenes.size());
        return reporte;
    }

  
    public String reporteIngresos() {
        LinkedHashMap<String, Double> porCategoria = new LinkedHashMap<>();
        for (OrdenServicio orden : ordenes) {
            String categoria = orden.getCategoria();
            double acumulado = porCategoria.getOrDefault(categoria, 0.0);
            porCategoria.put(categoria, acumulado + orden.getIngresoTotal());
        }
        String reporte = String.format("Ingresos por registro:           Q%10.2f%n", getIngresosPorRegistro());
        reporte += String.format("Ingresos por visitas adicionales: Q%10.2f%n", getIngresosPorVisitas());
        reporte += String.format("INGRESO TOTAL:                    Q%10.2f%n%n", getIngresoTotal());
        reporte += "Ingreso por categoria:\n";
        for (String categoria : porCategoria.keySet()) {
            reporte += String.format("  %-36s Q%10.2f%n", categoria, porCategoria.get(categoria));
        }
        return reporte;
    }


    public String reporteJornadasAbiertas() {
        String reporte = "";
        for (Jornada jornada : jornadas) {
            if (jornada.estaAbierta()) {
                reporte += jornada + "\n";
            }
        }
        if (reporte.isEmpty()) {
            reporte = "No hay jornadas sin cerrar.\n";
        }
        return reporte;
    }


    public ArrayList<OrdenServicio> getOrdenes() {
        return new ArrayList<>(ordenes);
    }

    public ArrayList<Cuadrilla> getCuadrillas() {
        return new ArrayList<>(cuadrillas);
    }

    public ArrayList<Jornada> getJornadas() {
        return new ArrayList<>(jornadas);
    }
}
