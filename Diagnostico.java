public class Diagnostico extends OrdenServicio {

    public static final double HORAS_MAXIMAS = 1.5;

    public Diagnostico(String codigo, String cliente, String direccion,
                       double horasEstimadas, Zona zona) {
        super(codigo, cliente, direccion, horasEstimadas, zona);
        if (horasEstimadas > HORAS_MAXIMAS) {
            throw new IllegalArgumentException("Un diagnostico no puede superar " + HORAS_MAXIMAS + " horas.");
        }
    }

    @Override
    public double calcularCosto() {
        if (getZona() == Zona.METROPOLITANA) {
            return 150.0;
        }
        return 225.0;
    }

    @Override
    public int getDiasAtencion() {
        if (getZona() == Zona.METROPOLITANA) {
            return 1;
        }
        return 2;
    }

    @Override
    public int getMaxVisitas() {
        return 3;
    }

    @Override
    public String getCategoria() {
        return "Diagnostico";
    }
}
