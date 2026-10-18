public class Instalacion extends OrdenServicio {

    public static final double HORAS_POR_PUNTO = 0.75;
    public static final double TARIFA_POR_HORA = 90.0;

    private int cantidadPuntos;

    public Instalacion(String codigo, String cliente, String direccion,
                       double horasEstimadas, Zona zona, int cantidadPuntos) {
        super(codigo, cliente, direccion, horasEstimadas, zona);
        if (cantidadPuntos <= 0) {
            throw new IllegalArgumentException("La cantidad de puntos debe ser un entero positivo.");
        }
        this.cantidadPuntos = cantidadPuntos;
    }

    public int getCantidadPuntos() {
        return cantidadPuntos;
    }

    public double getHorasPorPuntos() {
        return cantidadPuntos * HORAS_POR_PUNTO;
    }

    public double getHorasFacturables() {
        return Math.max(getHorasEstimadas(), getHorasPorPuntos());
    }

    @Override
    public double calcularCosto() {
        return getTarifaBase() + TARIFA_POR_HORA * Math.ceil(getHorasFacturables());
    }

    @Override
    public int getDiasAtencion() {
        if (getZona() == Zona.METROPOLITANA) {
            return 3;
        }
        return 5;
    }

    @Override
    public int getMaxVisitas() {
        return 3;
    }

    @Override
    public String getCategoria() {
        return "Instalacion";
    }

    @Override
    public String getDetalleCategoria() {
        return String.format("Puntos: %d | Horas por puntos: %.2f | Horas facturadas: %.0f",
                cantidadPuntos, getHorasPorPuntos(), Math.ceil(getHorasFacturables()));
    }
}
