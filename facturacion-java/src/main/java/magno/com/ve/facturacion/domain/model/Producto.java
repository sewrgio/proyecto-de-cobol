package magno.com.ve.facturacion.domain.model;

import magno.com.ve.facturacion.domain.enums.ClasificacionProducto;
import magno.com.ve.facturacion.domain.enums.UnidadMedida;

public class Producto {

    // ══════════════════════════════════════════════
    // IDENTIFICACIÓN DEL PRODUCTO
    // ══════════════════════════════════════════════
    private Long id;
    private String codigoBarra;                 // Nº de barra
    private String nombreProducto;              // Nombre
    private ClasificacionProducto tipoProducto; // Tipo (enum)
    private String marcaProducto;               // Marca
    private String unidadMedida;                // "Unidad", "Docena", "Bulto"...
    private int cantidad;                       // Cantidad de unidades
    private String descripcion;                 // Descripción

    // ══════════════════════════════════════════════
    // FÁBRICA
    // ══════════════════════════════════════════════
    private String companiaFabricacion;         // Compañía
    private String paisOrigen;                  // País
    private String identificador;               // RIF / identificación

    // ══════════════════════════════════════════════
    // MEDIDAS
    // ══════════════════════════════════════════════
    private UnidadMedida unidadPeso;            // Tipo de medida (kg, g, L...)
    private Double cantidadMedida;              // Cantidad (ej: 1)
    private Double contenido;                   // Contenido (ej: 1000)
    private UnidadMedida unidadDimension;       // Tipo de dimensión (cm, mm...)
    private Double cantidadDimension;           // Cantidad de la dimensión

    // ══════════════════════════════════════════════
    // PRECIO
    // ══════════════════════════════════════════════
    private double precio;                      // Precio según factura

    // ══════════════════════════════════════════════
    // CONSTRUCTORES
    // ══════════════════════════════════════════════
    public Producto() {
        this.unidadPeso = UnidadMedida.KILOGRAMO;
        this.unidadDimension = UnidadMedida.CENTIMETRO;
    }

    // ══════════════════════════════════════════════
    // GETTERS / SETTERS — Identificación
    // ══════════════════════════════════════════════
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigoBarra() { return codigoBarra; }
    public void setCodigoBarra(String codigoBarra) { this.codigoBarra = codigoBarra; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public ClasificacionProducto getTipoProducto() { return tipoProducto; }
    public void setTipoProducto(ClasificacionProducto tipoProducto) { this.tipoProducto = tipoProducto; }

    public String getMarcaProducto() { return marcaProducto; }
    public void setMarcaProducto(String marcaProducto) { this.marcaProducto = marcaProducto; }

    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    // ══════════════════════════════════════════════
    // GETTERS / SETTERS — Fábrica
    // ══════════════════════════════════════════════
    public String getCompaniaFabricacion() { return companiaFabricacion; }
    public void setCompaniaFabricacion(String companiaFabricacion) { this.companiaFabricacion = companiaFabricacion; }

    public String getPaisOrigen() { return paisOrigen; }
    public void setPaisOrigen(String paisOrigen) { this.paisOrigen = paisOrigen; }

    public String getIdentificador() { return identificador; }
    public void setIdentificador(String identificador) { this.identificador = identificador; }

    // ══════════════════════════════════════════════
    // GETTERS / SETTERS — Medidas
    // ══════════════════════════════════════════════
    public UnidadMedida getUnidadPeso() { return unidadPeso; }
    public void setUnidadPeso(UnidadMedida unidadPeso) { this.unidadPeso = unidadPeso; }

    public Double getCantidadMedida() { return cantidadMedida; }
    public void setCantidadMedida(Double cantidadMedida) { this.cantidadMedida = cantidadMedida; }

    public Double getContenido() { return contenido; }
    public void setContenido(Double contenido) { this.contenido = contenido; }

    public UnidadMedida getUnidadDimension() { return unidadDimension; }
    public void setUnidadDimension(UnidadMedida unidadDimension) { this.unidadDimension = unidadDimension; }

    public Double getCantidadDimension() { return cantidadDimension; }
    public void setCantidadDimension(Double cantidadDimension) { this.cantidadDimension = cantidadDimension; }

    // ══════════════════════════════════════════════
    // GETTERS / SETTERS — Precio
    // ══════════════════════════════════════════════
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    // ══════════════════════════════════════════════
    // MÉTODOS DE NEGOCIO
    // ══════════════════════════════════════════════
    public double getSubtotal() {
        return precio * cantidad;
    }

    public String getMedidaFormateada() {
        if (cantidadMedida == null && contenido == null) return "—";
        StringBuilder sb = new StringBuilder();
        if (cantidadMedida != null) sb.append(cantidadMedida);
        if (unidadPeso != null) sb.append(" ").append(unidadPeso.getSimbolo());
        if (contenido != null) {
            if (sb.length() > 0) sb.append(" × ");
            sb.append(contenido);
        }
        return sb.toString();
    }

    public String getDimensionFormateada() {
        if (cantidadDimension == null) return "—";
        String unidad = (unidadDimension != null) ? unidadDimension.getSimbolo() : "";
        return cantidadDimension + " " + unidad;
    }

    @Override
    public String toString() {
        return (nombreProducto != null ? nombreProducto : "(sin nombre)")
                + " [" + (codigoBarra != null ? codigoBarra : "sin cod") + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Producto otro = (Producto) obj;
        return codigoBarra != null && codigoBarra.equals(otro.codigoBarra);
    }

    @Override
    public int hashCode() {
        return codigoBarra != null ? codigoBarra.hashCode() : 0;
    }
}