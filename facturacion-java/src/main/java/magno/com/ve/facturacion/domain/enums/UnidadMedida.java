package magno.com.ve.facturacion.domain.enums;

public enum UnidadMedida {

    KILOGRAMO("kg", "Kilogramo"),
    GRAMO("g", "Gramo"),
    LIBRA("lb", "Libra"),
    CENTIMETRO("cm", "Centímetro"),
    METRO("m", "Metro"),
    MILIMETRO("mm", "Milímetro"),
    PULGADA("in", "Pulgada"),
    LITRO("L", "Litro"),
    MILILITRO("mL", "Mililitro"),
    UNIDAD("u", "Unidad");

    private final String simbolo;
    private final String descripcion;

    UnidadMedida(String simbolo, String descripcion) {
        this.simbolo = simbolo;
        this.descripcion = descripcion;
    }

    public String getSimbolo() { return simbolo; }
    public String getDescripcion() { return descripcion; }

    @Override
    public String toString() {
        return descripcion + " (" + simbolo + ")";
    }
}