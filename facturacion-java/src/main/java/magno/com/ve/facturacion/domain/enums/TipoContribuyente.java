package magno.com.ve.facturacion.domain.enums;

public enum TipoContribuyente {

    VENEZOLANO("V", "Venezolano", 7, 8),
    EXTRANJERO("E", "Extranjero", 7, 8),
    JURIDICO("J", "Jurídico (RIF)", 8, 9),
    GUBERNAMENTAL("G", "Gubernamental", 8, 9),
    PASAPORTE("P", "Pasaporte", 7, 9);

    private final String prefijo;
    private final String descripcion;
    private final int longitudMinima;
    private final int longitudMaxima;

    TipoContribuyente(String prefijo, String descripcion, int longitudMinima, int longitudMaxima) {
        this.prefijo = prefijo;
        this.descripcion = descripcion;
        this.longitudMinima = longitudMinima;
        this.longitudMaxima = longitudMaxima;
    }

    public String getPrefijo() { return prefijo; }
    public String getDescripcion() { return descripcion; }
    public int getLongitudMinima() { return longitudMinima; }
    public int getLongitudMaxima() { return longitudMaxima; }

    public static TipoContribuyente fromPrefijo(String prefijo) {
        if (prefijo == null || prefijo.isEmpty()) return null;
        String pref = prefijo.trim().toUpperCase();
        for (TipoContribuyente tipo : values()) {
            if (tipo.prefijo.equals(pref)) return tipo;
        }
        return null;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
