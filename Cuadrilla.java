public class Cuadrilla {

    private String codigo;
    private double capacidadHoras;
    private boolean certificadaRefrigeracion;

    public Cuadrilla(String codigo, double capacidadHoras, boolean certificadaRefrigeracion) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El codigo de la cuadrilla no puede estar vacio.");
        }
        if (capacidadHoras <= 0) {
            throw new IllegalArgumentException("La capacidad de horas debe ser mayor que 0.");
        }
        this.codigo = codigo.trim();
        this.capacidadHoras = capacidadHoras;
        this.certificadaRefrigeracion = certificadaRefrigeracion;
    }

    public String getCodigo() {
        return codigo;
    }

    public double getCapacidadHoras() {
        return capacidadHoras;
    }

    public boolean isCertificadaRefrigeracion() {
        return certificadaRefrigeracion;
    }

    @Override
    public String toString() {
        String cert = certificadaRefrigeracion ? "certificada en refrigeracion" : "no certificada";
        return String.format("%-6s capacidad %.2f h  (%s)", codigo, capacidadHoras, cert);
    }
}
