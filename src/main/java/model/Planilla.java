package model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "Planillas")
public class Planilla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PlanillaId")
    private Integer planillaId;

    // Relación ManyToOne con Usuario (Especialista)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EspecialistaId", nullable = false)
    private Usuario especialista;

    @Column(name = "RutaDocumento", nullable = false, length = 255)
    private String rutaDocumento;

    @Column(name = "FechaCreacion", nullable = false)
    private LocalDate fechaCreacion;

    @Column(name = "EstadoPlanilla", nullable = false, length = 50)
    private String estadoPlanilla;


    // Getters y setters

    public Integer getPlanillaId() {
        return planillaId;
    }

    public void setPlanillaId(Integer planillaId) {
        this.planillaId = planillaId;
    }

    public Usuario getEspecialista() {
        return especialista;
    }

    public void setEspecialista(Usuario especialista) {
        this.especialista = especialista;
    }

    public String getRutaDocumento() {
        return rutaDocumento;
    }

    public void setRutaDocumento(String rutaDocumento) {
        this.rutaDocumento = rutaDocumento;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getEstadoPlanilla() {
        return estadoPlanilla;
    }

    public void setEstadoPlanilla(String estadoPlanilla) {
        this.estadoPlanilla = estadoPlanilla;
    }
}
