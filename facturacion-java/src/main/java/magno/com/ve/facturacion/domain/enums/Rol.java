package magno.com.ve.facturacion.domain.enums;

import java.util.Arrays;
import java.util.List;

public enum Rol {

    ADMINISTRADOR("Administrador", "Acceso total al sistema", 
        Arrays.asList("FACTURAR", "ALMACEN", "REPORTES_Z", "GESTION_USUARIOS", "CONFIGURACION")),
        
    CAJERO("Cajero", "Operaciones de caja y facturación", 
        Arrays.asList("FACTURAR", "DEVOLUCIONES_PARCIALES", "CONSULTAR_INVENTARIO")),
        
    ALMACENISTA("Almacenista", "Gestión de inventario y entradas", 
        Arrays.asList("ALMACEN", "ENTRADAS_MERCANCIA", "AJUSTE_STOCK"));

    private final String nombre;
    private final String descripcion;
    private final List<String> permisos;

    Rol(String nombre, String descripcion, List<String> permisos) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.permisos = permisos;
    }

    public String getNombre() { 
        return nombre; 
    }
    
    public String getDescripcion() { 
        return descripcion; 
    }

    public List<String> getPermisos() { 
        return permisos; 
    }

    // Método clave para validar si el rol puede ejecutar una acción específica
    public boolean tienePermiso(String permiso) {
        return this.permisos.contains(permiso);
    }

    @Override
    public String toString() { 
        return nombre; 
    }
}