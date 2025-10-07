package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int usuarioId;
    @Column(name = "NombreUsuario")
    private String nombreUsuario;
    @Column(name = "ContrasenaHash")
    private String contra;
    // Mantenemos rolId solo para compatibilidad
    @Column(name = "RolId", insertable = false, updatable = false)
    private int rolId;
    // Relación ManyToOne con Rol
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RolId")
    private Rol rol;
	
	
	public int getUsuarioId() {
		return usuarioId;
	}
	public void setUsuarioId(int usuarioId) {
		this.usuarioId = usuarioId;
	}
	public String getNombreUsuario() {
		return nombreUsuario;
	}
	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}
	public String getContra() {
		return contra;
	}
	public void setContra(String contra) {
		this.contra = contra;
	}
	public int getRolId() {
		return rolId;
	}
	public void setRolId(int rolId) {
		this.rolId = rolId;
	}
	
	public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
    
	@Override
	public String toString() {
		return "Usuario [usuarioId=" + usuarioId + ", nombreUsuario=" + nombreUsuario + ", contra=" + contra
				+ ", rolId=" + rolId + "]";
	}
	
	public boolean esJefeUnidad() {
	    return nombreUsuario.equalsIgnoreCase("jefeUnidad");
	}

}
