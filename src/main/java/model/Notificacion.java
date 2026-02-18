package model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "Notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int notificacionId;

    @ManyToOne
    @JoinColumn(name = "UsuarioId", nullable = false)
    private Usuario usuario;

    @Column(name = "Mensaje", nullable = false)
    private String mensaje;

    @Column(name = "Fecha")
    private LocalDate fecha;

    @Column(name = "Visto")
    private boolean visto = false;

	public int getNotificacionId() {
		return notificacionId;
	}

	public void setNotificacionId(int notificacionId) {
		this.notificacionId = notificacionId;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public boolean isVisto() {
		return visto;
	}

	public void setVisto(boolean visto) {
		this.visto = visto;
	}
}
