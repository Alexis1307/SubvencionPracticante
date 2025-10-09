package model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "InformeFlujo")
public class InformeFlujo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FlujoId")
    private int flujoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "InformeId", nullable = false)
    private Informe informe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UsuarioId", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RolOrigenId", nullable = false)
    private Rol rolOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RolDestinoId")
    private Rol rolDestino;

    @Column(name = "Estado", nullable = false)
    private String estado;

    @Column(name = "Comentario")
    private String comentario;

    @Column(name = "Fecha")
    private LocalDateTime fecha;

    @PrePersist
    protected void onCreate() {
        this.fecha = LocalDateTime.now();
    }

	public int getFlujoId() {
		return flujoId;
	}

	public void setFlujoId(int flujoId) {
		this.flujoId = flujoId;
	}

	public Informe getInforme() {
		return informe;
	}

	public void setInforme(Informe informe) {
		this.informe = informe;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Rol getRolOrigen() {
		return rolOrigen;
	}

	public void setRolOrigen(Rol rolOrigen) {
		this.rolOrigen = rolOrigen;
	}

	public Rol getRolDestino() {
		return rolDestino;
	}

	public void setRolDestino(Rol rolDestino) {
		this.rolDestino = rolDestino;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}

}
