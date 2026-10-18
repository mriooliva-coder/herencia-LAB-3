import java.util.ArrayList;


public class Jornada {

    private String codigo;
    private Cuadrilla cuadrilla;
    private ArrayList<OrdenServicio> ordenes;
    private ArrayList<OrdenServicio> ordenesConResultado;
    private EstadoJornada estado;

    public Jornada(String codigo, Cuadrilla cuadrilla) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El codigo de la jornada no puede estar vacio.");
        }
        if (cuadrilla == null) {
            throw new IllegalArgumentException("La jornada necesita una cuadrilla.");
        }
        this.codigo = codigo;
        this.cuadrilla = cuadrilla;
        this.ordenes = new ArrayList<>();
        this.ordenesConResultado = new ArrayList<>();
        this.estado = EstadoJornada.EN_PREPARACION;
    }


    public void asignarOrden(OrdenServicio orden) {
        if (estado != EstadoJornada.EN_PREPARACION) {
            throw new IllegalStateException("Solo se pueden asignar ordenes a una jornada en preparacion (jornada "
                    + codigo + " esta " + estado + ").");
        }
        if (orden.getEstado() != EstadoOrden.PENDIENTE) {
            throw new IllegalStateException("La orden " + orden.getCodigo() + " no esta pendiente (estado actual: "
                    + orden.getEstado() + "). No se puede asignar.");
        }
        double horasTotales = getHorasAsignadas() + orden.getHorasEstimadas();
        if (horasTotales > cuadrilla.getCapacidadHoras() + 0.0001) {   // margen por decimales
            throw new IllegalArgumentException(String.format(
                    "Capacidad excedida: la cuadrilla %s tiene %.2f h disponibles y la orden %s requiere %.2f h.",
                    cuadrilla.getCodigo(), getCapacidadDisponible(), orden.getCodigo(), orden.getHorasEstimadas()));
        }

        if (!orden.esCompatibleCon(cuadrilla)) {
            throw new IllegalArgumentException("La orden " + orden.getCodigo() + " (" + orden.getCategoria()
                    + ") no es compatible con la cuadrilla " + cuadrilla.getCodigo()
                    + (cuadrilla.isCertificadaRefrigeracion() ? "." : " (no certificada en refrigeracion)."));
        }
        orden.marcarAsignada();
        ordenes.add(orden);
    }


    public void retirarOrden(OrdenServicio orden) {
        if (estado != EstadoJornada.EN_PREPARACION) {
            throw new IllegalStateException("Solo se pueden retirar ordenes de una jornada en preparacion (jornada "
                    + codigo + " esta " + estado + ").");
        }
        if (!ordenes.contains(orden)) {
            throw new IllegalArgumentException("La orden " + orden.getCodigo() + " no pertenece a la jornada " + codigo + ".");
        }
        orden.marcarRetirada();
        ordenes.remove(orden);
    }


    public void iniciar() {
        if (estado != EstadoJornada.EN_PREPARACION) {
            throw new IllegalStateException("La jornada " + codigo + " ya fue iniciada (estado: " + estado + ").");
        }
        if (ordenes.isEmpty()) {
            throw new IllegalStateException("No se puede iniciar la jornada " + codigo + " porque no tiene ordenes.");
        }
        for (OrdenServicio orden : ordenes) {
            orden.iniciarVisita();  
        }
        estado = EstadoJornada.EN_CURSO;
    }

    public void registrarResultado(OrdenServicio orden, boolean completada) {
        if (estado != EstadoJornada.EN_CURSO) {
            throw new IllegalStateException("Solo se registran resultados en una jornada en curso (jornada "
                    + codigo + " esta " + estado + ").");
        }
        if (!ordenes.contains(orden)) {
            throw new IllegalArgumentException("La orden " + orden.getCodigo() + " no pertenece a la jornada " + codigo + ".");
        }
        if (ordenesConResultado.contains(orden)) {
            throw new IllegalStateException("La orden " + orden.getCodigo() + " ya tiene resultado en esta jornada.");
        }
        orden.registrarResultado(completada);
        ordenesConResultado.add(orden);
        if (ordenesConResultado.size() == ordenes.size()) {
            estado = EstadoJornada.CERRADA;
        }
    }

    public double getHorasAsignadas() {
        double total = 0;
        for (OrdenServicio orden : ordenes) {
            total += orden.getHorasEstimadas();
        }
        return total;
    }

    public double getCapacidadDisponible() {
        return cuadrilla.getCapacidadHoras() - getHorasAsignadas();
    }

    public boolean estaAbierta() {
        return estado != EstadoJornada.CERRADA;
    }

    public boolean tieneResultado(OrdenServicio orden) {
        return ordenesConResultado.contains(orden);
    }

    public String getCodigo() {
        return codigo;
    }

    public Cuadrilla getCuadrilla() {
        return cuadrilla;
    }

    public EstadoJornada getEstado() {
        return estado;
    }

    public ArrayList<OrdenServicio> getOrdenes() {
        return new ArrayList<>(ordenes);
    }

    @Override
    public String toString() {
        String codigos = "";
        for (OrdenServicio orden : ordenes) {
            codigos += orden.getCodigo() + " ";
        }
        if (codigos.isEmpty()) {
            codigos = "(sin ordenes)";
        }
        return String.format("%-5s cuadrilla %-6s %-15s asignadas %.2f h  disponibles %.2f h  ordenes: %s",
                codigo, cuadrilla.getCodigo(), estado, getHorasAsignadas(), getCapacidadDisponible(), codigos.trim());
    }
}
