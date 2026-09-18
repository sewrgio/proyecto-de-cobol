package magno.com.ve.facturacion.domain.model;

import magno.com.ve.facturacion.domain.enums.TipoContribuyente;

public class Cliente {

    private Long id;
    private String cedula;
    private TipoContribuyente tipo;
    private String nombres;
    private String apellidos;
    private Direccion direccion;
    private Contacto contacto;
    private boolean activo;

    public Cliente() {
        this.activo = true;
        this.direccion = new Direccion();
        this.contacto = new Contacto();
    }

    public Cliente(String cedula, String nombres, String apellidos,
                   Direccion direccion, Contacto contacto) {
        this.cedula = cedula;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.direccion = direccion;
        this.contacto = contacto;
        this.activo = true;
        actualizarTipoContribuyente();
    }

    public void actualizarTipoContribuyente() {
        if (cedula != null && !cedula.isEmpty()) {
            this.tipo = TipoContribuyente.fromPrefijo(cedula.substring(0, 1));
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) {
        this.cedula = cedula;
        actualizarTipoContribuyente();
    }

    public TipoContribuyente getTipo() { return tipo; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public Direccion getDireccion() { return direccion; }
    public void setDireccion(Direccion direccion) { this.direccion = direccion; }

    public Contacto getContacto() { return contacto; }
    public void setContacto(Contacto contacto) { this.contacto = contacto; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getNombreCompleto() {
        StringBuilder sb = new StringBuilder();
        if (nombres != null) sb.append(nombres);
        if (apellidos != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(apellidos);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return cedula + " - " + getNombreCompleto();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cliente otro = (Cliente) obj;
        return cedula != null && cedula.equals(otro.cedula);
    }

    @Override
    public int hashCode() {
        return cedula != null ? cedula.hashCode() : 0;
    }
}
