package magno.com.ve.facturacion.domain.enums;

public enum ClasificacionProducto {
    ALIMENTO("Alimento"),
    BEBIDA("Bebida"),
    LIMPIEZA("Limpieza"),
    HIGIENE("Higiene"),
    ELECTRONICA("Electrónica"),
    HERRAMIENTA("Herramienta"),
    ROPA("Ropa"),
    MEDICINA("Medicina"),
    OTRO("Otro");

    private final String etiqueta;

    ClasificacionProducto(String etiqueta) { this.etiqueta = etiqueta; }
    public String getEtiqueta() { return etiqueta; }

    @Override
    public String toString() { return etiqueta; }

    public static ClasificacionProducto desdeTexto(String texto) {
        if (texto == null) return OTRO;
        for (ClasificacionProducto c : values()) {
            if (c.etiqueta.equalsIgnoreCase(texto) || c.name().equalsIgnoreCase(texto)) {
                return c;
            }
        }
        return OTRO;
    }
}