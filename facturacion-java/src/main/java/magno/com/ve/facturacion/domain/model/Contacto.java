package magno.com.ve.facturacion.domain.model;

public class Contacto {

    private String telefonoPrincipal;
    private String telefonoSecundario;
    private String email;

    public Contacto() {}

    public Contacto(String telefonoPrincipal, String telefonoSecundario, String email) {
        this.telefonoPrincipal = telefonoPrincipal;
        this.telefonoSecundario = telefonoSecundario;
        this.email = email;
    }

    public String getTelefonoPrincipal() { return telefonoPrincipal; }
    public void setTelefonoPrincipal(String telefonoPrincipal) { this.telefonoPrincipal = telefonoPrincipal; }

    public String getTelefonoSecundario() { return telefonoSecundario; }
    public void setTelefonoSecundario(String telefonoSecundario) { this.telefonoSecundario = telefonoSecundario; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "Tel: " + telefonoPrincipal + " | Email: " + email;
    }
}
