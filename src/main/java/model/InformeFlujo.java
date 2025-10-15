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

    @Column(name = "RolOrigen", nullable = false)
    private String rolOrigen;

    @Column(name = "RolDestino")
    private String rolDestino;

    @Column(name = "Estado", nullable = false)
    private String estado;

    @Column(name = "Comentario")
    private String comentario;

    @Column(name = "Fecha")
    private LocalDateTime fecha;


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

    public String getRolOrigen() {
        return rolOrigen;
    }

    public void setRolOrigen(String rolOrigen) {
        this.rolOrigen = rolOrigen;
    }

    public String getRolDestino() {
        return rolDestino;
    }

    public void setRolDestino(String rolDestino) {
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
