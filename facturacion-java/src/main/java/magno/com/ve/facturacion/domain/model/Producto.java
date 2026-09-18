package magno.com.ve.facturacion.domain.model;

import magno.com.ve.facturacion.domain.enums.UnidadMedida;

public class Producto {

    // ===== Identificación =====
    private Long id;
    private String codigo;                     // Código de 12 dígitos
    private String codigoFabricacion;          // Código del fabricante
    private String nombre;                     // Nombre del producto
    private String descripcion;                // Descripción opcional

    // ===== Fabricación =====
    private String companiaFabricacion;        // Nombre de la compañía (NUEVO)
    private String paisOrigen;                 // País de origen (NUEVO)

    // ===== Medidas físicas =====
    private Double peso;
    private UnidadMedida unidadPeso;
    private Double altura;
    private Double anchura;
    private Double grosor;
    private UnidadMedida unidadDimension;

    // ===== Precio y venta =====
    private double precio;
    private int cantidad;

    public Producto() {
        this.unidadPeso = UnidadMedida.KILOGRAMO;
        this.unidadDimension = UnidadMedida.CENTIMETRO;
    }

    public Producto(String codigo, String codigoFabricacion, String nombre,
                    String descripcion,
                    String companiaFabricacion, String paisOrigen,
                    Double peso, UnidadMedida unidadPeso,
                    Double altura, Double anchura, Double grosor,
                    UnidadMedida unidadDimension,
                    double precio, int cantidad) {
        this.codigo = codigo;
        this.codigoFabricacion = codigoFabricacion;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.companiaFabricacion = companiaFabricacion;
        this.paisOrigen = paisOrigen;
        this.peso = peso;
        this.unidadPeso = unidadPeso != null ? unidadPeso : UnidadMedida.KILOGRAMO;
        this.altura = altura;
        this.anchura = anchura;
        this.grosor = grosor;
        this.unidadDimension = unidadDimension != null ? unidadDimension : UnidadMedida.CENTIMETRO;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    // ===== Getters y Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getCodigoFabricacion() { return codigoFabricacion; }
    public void setCodigoFabricacion(String codigoFabricacion) { this.codigoFabricacion = codigoFabricacion; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCompaniaFabricacion() { return companiaFabricacion; }
    public void setCompaniaFabricacion(String companiaFabricacion) { this.companiaFabricacion = companiaFabricacion; }

    public String getPaisOrigen() { return paisOrigen; }
    public void setPaisOrigen(String paisOrigen) { this.paisOrigen = paisOrigen; }

    public Double getPeso() { return peso; }
    public void setPeso(Double peso) { this.peso = peso; }

    public UnidadMedida getUnidadPeso() { return unidadPeso; }
    public void setUnidadPeso(UnidadMedida unidadPeso) { this.unidadPeso = unidadPeso; }

    public Double getAltura() { return altura; }
    public void setAltura(Double altura) { this.altura = altura; }

    public Double getAnchura() { return anchura; }
    public void setAnchura(Double anchura) { this.anchura = anchura; }

    public Double getGrosor() { return grosor; }
    public void setGrosor(Double grosor) { this.grosor = grosor; }

    public UnidadMedida getUnidadDimension() { return unidadDimension; }
    public void setUnidadDimension(UnidadMedida unidadDimension) { this.unidadDimension = unidadDimension; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    // ===== Métodos de negocio =====
    public double getSubtotal() {
        return precio * cantidad;
    }

    public String getDimensionesFormateadas() {
        if (altura == null && anchura == null && grosor == null) return "—";
        StringBuilder sb = new StringBuilder();
        if (altura != null) sb.append(altura);
        if (anchura != null) {
            if (sb.length() > 0) sb.append(" × ");
            sb.append(anchura);
        }
        if (grosor != null) {
            if (sb.length() > 0) sb.append(" × ");
            sb.append(grosor);
        }
        if (unidadDimension != null) sb.append(" ").append(unidadDimension.getSimbolo());
        return sb.toString();
    }

    public String getPesoFormateado() {
        if (peso == null) return "—";
        String unidad = (unidadPeso != null) ? unidadPeso.getSimbolo() : "";
        return peso + " " + unidad;
    }

    /**
     * Devuelve la información de fabricación formateada.
     */
    public String getInfoFabricacion() {
        StringBuilder sb = new StringBuilder();
        if (companiaFabricacion != null && !companiaFabricacion.isEmpty()) {
            sb.append(companiaFabricacion);
        }
        if (paisOrigen != null && !paisOrigen.isEmpty()) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(paisOrigen);
        }
        return sb.length() > 0 ? sb.toString() : "—";
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Producto otro = (Producto) obj;
        return codigo != null && codigo.equals(otro.codigo);
    }

    @Override
    public int hashCode() {
        return codigo != null ? codigo.hashCode() : 0;
    }
}
