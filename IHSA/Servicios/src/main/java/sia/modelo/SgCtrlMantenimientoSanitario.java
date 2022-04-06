/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SG_CTRL_MANTENIMIENTO_SANITARIO")
@SequenceGenerator(sequenceName = "sg_ctrl_mantenimiento_sanitario_id_seq", name = "sg_ctrl_mantenimiento_sanitario_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgCtrlMantenimientoSanitario.findAll", query = "SELECT s FROM SgCtrlMantenimientoSanitario s")})
public class SgCtrlMantenimientoSanitario implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_ctrl_mantenimiento_sanitario_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Column(name = "INICIO_REGISTRO")
    @Temporal(TemporalType.DATE)
    private Date inicioRegistro;
    @Column(name = "FIN_REGISTRO")
    @Temporal(TemporalType.DATE)
    private Date finRegistro;
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @Column(name = "NUMERO")
    private String numero;
    @Column(name = "OBSERVACION")
    private String observacion;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "SI_ADJUNTO", referencedColumnName = "ID")
    @ManyToOne
    private SiAdjunto siAdjunto;
    @JoinColumn(name = "SG_OFICNA_SANITARIO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SgSanitario sgOficnaSanitario;

    public SgCtrlMantenimientoSanitario() {
    }

    public SgCtrlMantenimientoSanitario(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getInicioRegistro() {
        return inicioRegistro;
    }

    public void setInicioRegistro(Date inicioRegistro) {
        this.inicioRegistro = inicioRegistro;
    }

    public Date getFinRegistro() {
        return finRegistro;
    }

    public void setFinRegistro(Date finRegistro) {
        this.finRegistro = finRegistro;
    }

    public Date getFechaGenero() {
        return fechaGenero;
    }

    public void setFechaGenero(Date fechaGenero) {
        this.fechaGenero = fechaGenero;
    }

    public Date getHoraGenero() {
        return horaGenero;
    }

    public void setHoraGenero(Date horaGenero) {
        this.horaGenero = horaGenero;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public Usuario getGenero() {
        return genero;
    }

    public void setGenero(Usuario genero) {
        this.genero = genero;
    }

    public SiAdjunto getSiAdjunto() {
        return siAdjunto;
    }

    public void setSiAdjunto(SiAdjunto siAdjunto) {
        this.siAdjunto = siAdjunto;
    }

    public SgSanitario getSgOficnaSanitario() {
        return sgOficnaSanitario;
    }

    public void setSgOficnaSanitario(SgSanitario sgOficnaSanitario) {
        this.sgOficnaSanitario = sgOficnaSanitario;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgCtrlMantenimientoSanitario)) {
            return false;
        }
        SgCtrlMantenimientoSanitario other = (SgCtrlMantenimientoSanitario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.SgCtrlMantenimientoSanitario[ id=" + id + " ]";
    }

    /**
     * @return the numero
     */
    public String getNumero() {
        return numero;
    }

    /**
     * @param numero the numero to set
     */
    public void setNumero(String numero) {
        this.numero = numero;
    }

    /**
     * @return the observacion
     */
    public String getObservacion() {
        return observacion;
    }

    /**
     * @param observacion the observacion to set
     */
    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
    
}
