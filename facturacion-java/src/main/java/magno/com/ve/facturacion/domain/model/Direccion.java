package magno.com.ve.facturacion.domain.model;

public class Direccion {

    private String calle;
    private String numero;
    private String urbanizacion;
    private String ciudad;
    private String estado;
    private String codigoPostal;
    private String pais;

    public Direccion() {
        this.pais = "Venezuela";
    }

    public Direccion(String calle, String numero, String urbanizacion,
                     String ciudad, String estado, String codigoPostal, String pais) {
        this.calle = calle;
        this.numero = numero;
        this.urbanizacion = urbanizacion;
        this.ciudad = ciudad;
        this.estado = estado;
        this.codigoPostal = codigoPostal;
        this.pais = (pais == null || pais.isEmpty()) ? "Venezuela" : pais;
    }

    public String getCalle() { return calle; }
    public void setCalle(String calle) { this.calle = calle; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getUrbanizacion() { return urbanizacion; }
    public void setUrbanizacion(String urbanizacion) { this.urbanizacion = urbanizacion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getDireccionCompleta() {
        StringBuilder sb = new StringBuilder();
        if (calle != null && !calle.isEmpty()) sb.append(calle);
        if (numero != null && !numero.isEmpty()) sb.append(" #").append(numero);
        if (urbanizacion != null && !urbanizacion.isEmpty()) sb.append(", ").append(urbanizacion);
        if (ciudad != null && !ciudad.isEmpty()) sb.append(", ").append(ciudad);
        if (estado != null && !estado.isEmpty()) sb.append(", ").append(estado);
        if (codigoPostal != null && !codigoPostal.isEmpty()) sb.append(" ").append(codigoPostal);
        if (pais != null && !pais.isEmpty()) sb.append(", ").append(pais);
        return sb.toString();
    }

    @Override
    public String toString() {
        return getDireccionCompleta();
    }
}
