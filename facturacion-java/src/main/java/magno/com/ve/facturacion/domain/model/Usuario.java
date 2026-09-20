package magno.com.ve.facturacion.domain.model;

import magno.com.ve.facturacion.domain.enums.Rol;

public class Usuario {

    private Long id;
    private String username;        // Nombre de usuario
    private String passwordHash;    // Contraseña hasheada (nunca en claro)
    private String nombreCompleto;
    private Rol rol;
    private boolean activo;

    public Usuario() {}

    public Usuario(String username, String passwordHash, String nombreCompleto, Rol rol) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.activo = true;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public boolean tienePermiso(Rol... rolesPermitidos) {
        for (Rol r : rolesPermitidos) {
            if (r == this.rol) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return nombreCompleto + " (" + username + " - " + (rol != null ? rol.getNombre() : "—") + ")";
    }
}