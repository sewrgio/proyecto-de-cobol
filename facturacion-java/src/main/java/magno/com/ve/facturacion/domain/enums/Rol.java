package magno.com.ve.facturacion.domain.enums;

public enum Rol {

    ADMINISTRADOR("Administrador", "Acceso total al sistema"),
    CAJERO("Cajero", "Solo puede facturar"),
    ALMACENISTA("Almacenista", "Solo gestiona el almacén");

    private final String nombre;
    private final String descripcion;

    Rol(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }

    @Override
    public String toString() { return nombre; }
}