public class InstalacionElectricaCertificada extends Instalacion {

    public static final double CARGO_CERTIFICADO = 120.0;
    public static final double PORCENTAJE_GARANTIA = 0.05;
    public static final int DIAS_EXTRA = 2;

    private double valorMateriales;

    public InstalacionElectricaCertificada(String codigo, String cliente, String direccion,
                                           double horasEstimadas, Zona zona,
                                           int cantidadPuntos, double valorMateriales) {
        super(codigo, cliente, direccion, horasEstimadas, zona, cantidadPuntos);
        if (valorMateriales <= 0) {
            throw new IllegalArgumentException("El valor declarado de los materiales debe ser mayor que 0.");
        }
        this.valorMateriales = valorMateriales;
    }

    public double getValorMateriales() {
        return valorMateriales;
    }

    public double getGarantia() {
        return valorMateriales * PORCENTAJE_GARANTIA;
    }

    @Override
    public double calcularCosto() {
        return super.calcularCosto() + CARGO_CERTIFICADO + getGarantia();
    }

    @Override
    public int getDiasAtencion() {
        return super.getDiasAtencion() + DIAS_EXTRA;
    }

    @Override
    public int getMaxVisitas() {
        return 2;
    }

    @Override
    public String getCategoria() {
        return "Instalacion electrica certificada";
    }

    @Override
    public String getDetalleCategoria() {
        return super.getDetalleCategoria()
                + String.format(" | Materiales: Q%.2f | Certificado: Q%.2f | Garantia: Q%.2f",
                valorMateriales, CARGO_CERTIFICADO, getGarantia());
    }
}
