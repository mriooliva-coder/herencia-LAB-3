public class MantenimientoAireAcondicionado extends OrdenServicio {

    public static final int BTU_MINIMO = 9000;
    public static final int BTU_MAXIMO = 60000;
    public static final double TARIFA_POR_HORA = 110.0;
    public static final double CARGO_GAS = 80.0;

    private int capacidadBTU;

    public MantenimientoAireAcondicionado(String codigo, String cliente, String direccion,
                                          double horasEstimadas, Zona zona, int capacidadBTU) {
        super(codigo, cliente, direccion, horasEstimadas, zona);
        if (zona != Zona.METROPOLITANA) {
            throw new IllegalArgumentException("Solo se aceptan mantenimientos de aire acondicionado en la zona metropolitana.");
        }
        if (capacidadBTU < BTU_MINIMO || capacidadBTU > BTU_MAXIMO) {
            throw new IllegalArgumentException("La capacidad del equipo debe estar entre "
                    + BTU_MINIMO + " y " + BTU_MAXIMO + " BTU.");
        }
        this.capacidadBTU = capacidadBTU;
    }

    public int getCapacidadBTU() {
        return capacidadBTU;
    }

    @Override
    public double calcularCosto() {
        return getTarifaBase() + TARIFA_POR_HORA * getHorasEstimadas() + CARGO_GAS;
    }

    @Override
    public int getDiasAtencion() {
        return 2;
    }

    @Override
    public int getMaxVisitas() {
        return 1;
    }

    @Override
    public String getCategoria() {
        return "Mantenimiento de aire acondicionado";
    }

    @Override
    public boolean esCompatibleCon(Cuadrilla cuadrilla) {
        return cuadrilla.isCertificadaRefrigeracion();
    }

    @Override
    public String getDetalleCategoria() {
        return String.format("Capacidad: %d BTU | Gas refrigerante: Q%.2f", capacidadBTU, CARGO_GAS);
    }
}
