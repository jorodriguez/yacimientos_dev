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
@Table(name = "AP_CAMPO_USUARIO_RH_PUESTO")
@SequenceGenerator(sequenceName = "ap_campo_usuario_rh_puesto_id_seq", name = "ap_campo_usuario_rh_puesto_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "ApCampoUsuarioRhPuesto.findAll", query = "SELECT a FROM ApCampoUsuarioRhPuesto a")})
public class ApCampoUsuarioRhPuesto implements Serializable {

    @JoinColumn(name = "OC_SUBCAMPO", referencedColumnName = "ID")
    @ManyToOne
    private OcSubcampo ocSubcampo;
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "ap_campo_usuario_rh_puesto_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "USUARIO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario usuario;
    @JoinColumn(name = "RH_PUESTO", referencedColumnName = "ID")
    @ManyToOne
    private RhPuesto rhPuesto;
    @JoinColumn(name = "AP_CAMPO", referencedColumnName = "ID")
    @ManyToOne
    private ApCampo apCampo;
    @JoinColumn(name = "GERENCIA", referencedColumnName = "ID")
    @ManyToOne 
    private Gerencia gerencia;

    public ApCampoUsuarioRhPuesto() {
    }

    public ApCampoUsuarioRhPuesto(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Date getFechaModifico() {
        return fechaModifico;
    }

    public void setFechaModifico(Date fechaModifico) {
        this.fechaModifico = fechaModifico;
    }

    public Date getHoraModifico() {
        return horaModifico;
    }

    public void setHoraModifico(Date horaModifico) {
        this.horaModifico = horaModifico;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public Usuario getModifico() {
        return modifico;
    }

    public void setModifico(Usuario modifico) {
        this.modifico = modifico;
    }

    public Usuario getGenero() {
        return genero;
    }

    public void setGenero(Usuario genero) {
        this.genero = genero;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public RhPuesto getRhPuesto() {
        return rhPuesto;
    }

    public void setRhPuesto(RhPuesto rhPuesto) {
        this.rhPuesto = rhPuesto;
    }

    public ApCampo getApCampo() {
        return apCampo;
    }

    public void setApCampo(ApCampo apCampo) {
        this.apCampo = apCampo;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ApCampoUsuarioRhPuesto)) {
            return false;
        }
        ApCampoUsuarioRhPuesto other = (ApCampoUsuarioRhPuesto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.ApCampoUsuarioRhPuesto[ id=" + id + " ]";
    }

    /**
     * @return the gerencia
     */
    public Gerencia getGerencia() {
        return gerencia;
    }

    /**
     * @param gerencia the gerencia to set
     */
    public void setGerencia(Gerencia gerencia) {
        this.gerencia = gerencia;
    }

    public OcSubcampo getOcSubcampo() {
        return ocSubcampo;
    }

    public void setOcSubcampo(OcSubcampo ocSubcampo) {
        this.ocSubcampo = ocSubcampo;
    }
    
}
