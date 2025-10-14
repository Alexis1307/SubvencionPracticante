package model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "FirmasDigitales")
public class FirmaDigital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FirmaId")
    private Integer firmaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "InformeId")
    private Informe informe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PlanillaId")
    private Planilla planilla;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UsuarioId", nullable = false)
    private Usuario usuario;

    @Column(name = "RutaImagenFirma", nullable = false, length = 255)
    private String rutaImagenFirma;

    @Column(name = "Fecha", nullable = false)
    private LocalDateTime fecha;

    // Constructor vacío
    public FirmaDigital() {
        this.fecha = LocalDateTime.now();
    }

    // Getters y setters

    public Integer getFirmaId() {
        return firmaId;
    }

    public void setFirmaId(Integer firmaId) {
        this.firmaId = firmaId;
    }

    public Informe getInforme() {
        return informe;
    }

    public void setInforme(Informe informe) {
        this.informe = informe;
    }

    public Planilla getPlanilla() {
        return planilla;
    }

    public void setPlanilla(Planilla planilla) {
        this.planilla = planilla;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getRutaImagenFirma() {
        return rutaImagenFirma;
    }

    public void setRutaImagenFirma(String rutaImagenFirma) {
        this.rutaImagenFirma = rutaImagenFirma;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
