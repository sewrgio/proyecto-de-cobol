package magno.com.ve.model;

public class Cliente {
    private String cedula;      
    private String nombres;
    private String apellidos;
    private String direccion;
    private String telefono;

    public Cliente(String cedula, String nombres, String apellidos, String direccion, String telefono) {
        this.cedula = cedula;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    public String getCedula() { return cedula; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getDireccion() { return direccion; }
    public String getTelefono() { return telefono; }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    @Override
    public String toString() {
        return getNombreCompleto() + " (" + cedula + ")";
    }
}