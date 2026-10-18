public abstract class OrdenServicio {

    public static final double CARGO_VISITA_ADICIONAL = 40.0;

    private String codigo;
    private String cliente;
    private String direccion;
    private double horasEstimadas;
    private Zona zona;
    private EstadoOrden estado;
    private int visitasRealizadas;
    private double montoRegistro;            // lo cobrado al registrar la orden
    private double montoVisitasAdicionales;  // suma de los cargos de Q40

    public OrdenServicio(String codigo, String cliente, String direccion,
                         double horasEstimadas, Zona zona) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El codigo de la orden no puede estar vacio.");
        }
        if (cliente == null || cliente.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacio.");
        }
        if (direccion == null || direccion.trim().isEmpty()) {
            throw new IllegalArgumentException("La direccion no puede estar vacia.");
        }
        if (horasEstimadas <= 0) {
            throw new IllegalArgumentException("Las horas estimadas deben ser mayores que 0.");
        }
        if (zona == null) {
            throw new IllegalArgumentException("Debe indicar la zona.");
        }
        this.codigo = codigo.trim();
        this.cliente = cliente.trim();
        this.direccion = direccion.trim();
        this.horasEstimadas = horasEstimadas;
        this.zona = zona;
        this.estado = EstadoOrden.PENDIENTE;
        this.visitasRealizadas = 0;
        this.montoRegistro = 0;
        this.montoVisitasAdicionales = 0;
    }




    public abstract double calcularCosto();


    public abstract int getDiasAtencion();

    public abstract int getMaxVisitas();


    public abstract String getCategoria();

    
    public boolean esCompatibleCon(Cuadrilla cuadrilla) {
        return true;
    }

    public String getDetalleCategoria() {
        return "";
    }


    protected double getTarifaBase() {
        if (zona == Zona.METROPOLITANA) {
            return 100.0;
        }
        return 175.0;
    }


    public void cobrarRegistro() {
        if (montoRegistro > 0) {
            throw new IllegalStateException("La orden " + codigo + " ya fue cobrada.");
        }
        montoRegistro = calcularCosto();
    }

    public void marcarAsignada() {
        if (estado != EstadoOrden.PENDIENTE) {
            throw new IllegalStateException("La orden " + codigo + " no esta pendiente (estado actual: "
                    + estado + "). Solo se pueden asignar ordenes pendientes.");
        }
        estado = EstadoOrden.ASIGNADA;
    }

    public void marcarRetirada() {
        if (estado != EstadoOrden.ASIGNADA) {
            throw new IllegalStateException("La orden " + codigo + " no esta asignada (estado actual: " + estado + ").");
        }
        estado = EstadoOrden.PENDIENTE;
    }

    public void iniciarVisita() {
        if (estado != EstadoOrden.ASIGNADA) {
            throw new IllegalStateException("La orden " + codigo + " no esta asignada (estado actual: " + estado + ").");
        }
        visitasRealizadas++;
        if (visitasRealizadas > 1) {
            montoVisitasAdicionales += CARGO_VISITA_ADICIONAL;
        }
        estado = EstadoOrden.EN_ATENCION;
    }

    public void registrarResultado(boolean completada) {
        if (estado != EstadoOrden.EN_ATENCION) {
            throw new IllegalStateException("La orden " + codigo + " no esta en atencion (estado actual: " + estado + ").");
        }
        if (completada) {
            estado = EstadoOrden.COMPLETADA;
        } else if (visitasRealizadas >= getMaxVisitas()) {
            estado = EstadoOrden.CANCELADA;
        } else {
            estado = EstadoOrden.PENDIENTE;
        }
    }

   
    public String getCodigo() {
        return codigo;
    }

    public String getCliente() {
        return cliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public double getHorasEstimadas() {
        return horasEstimadas;
    }

    public Zona getZona() {
        return zona;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public int getVisitasRealizadas() {
        return visitasRealizadas;
    }

    public double getMontoRegistro() {
        return montoRegistro;
    }

    public double getMontoVisitasAdicionales() {
        return montoVisitasAdicionales;
    }

    public double getIngresoTotal() {
        return montoRegistro + montoVisitasAdicionales;
    }

    @Override
    public String toString() {
        String texto = String.format("%-8s %-36s %-20s %-13s %5.2f h  %-11s visitas %d/%d  costo Q%.2f",
                codigo, getCategoria(), cliente, zona, horasEstimadas, estado,
                visitasRealizadas, getMaxVisitas(), calcularCosto());
        String detalle = getDetalleCategoria();
        if (!detalle.isEmpty()) {
            texto += "\n         -> " + detalle;
        }
        return texto;
    }
}
