package model;

import java.time.LocalDateTime;

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
@Table(name = "Informes")
public class Informe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "InformeId")
    private int informeID;
    @Column(name = "PracticanteId", insertable = false, updatable = false)
    private int practicanteId;
    @Column(name = "Asunto")
    private String asunto;
    @Column(name = "RolId", insertable = false, updatable = false)
    private int rolId;
    @Column(name = "PeriodoPracticasMeses")
    private int periodoPracticas;
    @Column(name = "Actividades")
    private String actividades;
    @Column(name = "RutaDocumento")
    private String rutaDocumento;
    @Column(name = "FechaEnvio")
    private LocalDateTime fechaEnvio;
    @Column(name = "EstadoInforme")
    private String estado;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PracticanteId")
    private Usuario practicante;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RolId")
    private Rol rol;
	
	
	public int getInformeID() {
		return informeID;
	}
	public void setInformeID(int informeID) {
		this.informeID = informeID;
	}
	public int getPracticanteId() {
		return practicanteId;
	}
	public void setPracticanteId(int practicanteId) {
		this.practicanteId = practicanteId;
	}
	public String getAsunto() {
		return asunto;
	}
	public void setAsunto(String asunto) {
		this.asunto = asunto;
	}
	public int getRolId() {
		return rolId;
	}
	public void setRolId(int rolId) {
		this.rolId = rolId;
	}
	public int getPeriodoPracticas() {
		return periodoPracticas;
	}
	public void setPeriodoPracticas(int periodoPracticas) {
		this.periodoPracticas = periodoPracticas;
	}
	public String getActividades() {
		return actividades;
	}
	public void setActividades(String actividades) {
		this.actividades = actividades;
	}
	public String getRutaDocumento() {
		return rutaDocumento;
	}
	public void setRutaDocumento(String rutaDocumento) {
		this.rutaDocumento = rutaDocumento;
	}
	public LocalDateTime getFechaEnvio() {
		return fechaEnvio;
	}
	public void setFechaEnvio(LocalDateTime fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
    
	@Override
	public String toString() {
		return "Informe [informeID=" + informeID + ", practicanteId=" + practicanteId + ", asunto=" + asunto
				+ ", rolId=" + rolId + ", periodoPracticas=" + periodoPracticas + ", actividades=" + actividades
				+ ", rutaDocumento=" + rutaDocumento + ", fechaEnvio=" + fechaEnvio + ", estado=" + estado + "]";
	}
}
