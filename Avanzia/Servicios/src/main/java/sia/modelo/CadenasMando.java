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
import javax.xml.bind.annotation.XmlRootElement;
import sia.constantes.Constantes;

/**
 *
 * @author hacosta
 */
@Entity
@Table(name = "CADENAS_MANDO")
@SequenceGenerator(sequenceName = "cadenas_mando_id_seq", name = "cadenas_mando_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CadenasMando.findAll", query = "SELECT c FROM CadenasMando c")})
public class CadenasMando implements Serializable {

    @JoinColumn(name = "OC_SUBCAMPO", referencedColumnName = "ID")
    @ManyToOne
    private OcSubcampo ocSubcampo;

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "cadenas_mando_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "APRUEBA", referencedColumnName = "ID")
    @ManyToOne
    private Usuario aprueba;
    @JoinColumn(name = "AUTORIZA_IHSA", referencedColumnName = "ID")
    @ManyToOne
    private Usuario autorizaIhsa;
    @JoinColumn(name = "AUTORIZA_MPG", referencedColumnName = "ID")
    @ManyToOne
    private Usuario autorizaMpg;
    @JoinColumn(name = "REVISA", referencedColumnName = "ID")
    @ManyToOne
    private Usuario revisa;
    @JoinColumn(name = "USUARIO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario usuario;
    @JoinColumn(name = "AP_CAMPO", referencedColumnName = "ID")
    @ManyToOne
    private ApCampo apCampo;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @Basic(optional = false)
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Basic(optional = false)
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;

    public CadenasMando() {
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the aprueba
     */
    public Usuario getAprueba() {
        return aprueba;
    }

    /**
     * @param aprueba the aprueba to set
     */
    public void setAprueba(Usuario aprueba) {
        this.aprueba = aprueba;
    }

    /**
     * @return the autorizaIhsa
     */
    public Usuario getAutorizaIhsa() {
        return autorizaIhsa;
    }

    /**
     * @param autorizaIhsa the autorizaIhsa to set
     */
    public void setAutorizaIhsa(Usuario autorizaIhsa) {
        this.autorizaIhsa = autorizaIhsa;
    }

    /**
     * @return the autorizaMpg
     */
    public Usuario getAutorizaMpg() {
        return autorizaMpg;
    }

    /**
     * @param autorizaMpg the autorizaMpg to set
     */
    public void setAutorizaMpg(Usuario autorizaMpg) {
        this.autorizaMpg = autorizaMpg;
    }

    /**
     * @return the revisa
     */
    public Usuario getRevisa() {
        return revisa;
    }

    /**
     * @param revisa the revisa to set
     */
    public void setRevisa(Usuario revisa) {
        this.revisa = revisa;
    }

    /**
     * @return the usuario
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * @return the apCampo
     */
    public ApCampo getApCampo() {
        return apCampo;
    }

    /**
     * @param apCampo the apCampo to set
     */
    public void setApCampo(ApCampo apCampo) {
        this.apCampo = apCampo;
    }

    /**
     * @return the genero
     */
    public Usuario getGenero() {
        return genero;
    }

    /**
     * @param genero the genero to set
     */
    public void setGenero(Usuario genero) {
        this.genero = genero;
    }

    /**
     * @return the fechaGenero
     */
    public Date getFechaGenero() {
        return fechaGenero;
    }

    /**
     * @param fechaGenero the fechaGenero to set
     */
    public void setFechaGenero(Date fechaGenero) {
        this.fechaGenero = fechaGenero;
    }

    /**
     * @return the horaGenero
     */
    public Date getHoraGenero() {
        return horaGenero;
    }

    /**
     * @param horaGenero the horaGenero to set
     */
    public void setHoraGenero(Date horaGenero) {
        this.horaGenero = horaGenero;
    }

    /**
     * @return the modifico
     */
    public Usuario getModifico() {
        return modifico;
    }

    /**
     * @param modifico the modifico to set
     */
    public void setModifico(Usuario modifico) {
        this.modifico = modifico;
    }

    /**
     * @return the fechaModifico
     */
    public Date getFechaModifico() {
        return fechaModifico;
    }

    /**
     * @param fechaModifico the fechaModifico to set
     */
    public void setFechaModifico(Date fechaModifico) {
        this.fechaModifico = fechaModifico;
    }

    /**
     * @return the horaModifico
     */
    public Date getHoraModifico() {
        return horaModifico;
    }

    /**
     * @param horaModifico the horaModifico to set
     */
    public void setHoraModifico(Date horaModifico) {
        this.horaModifico = horaModifico;
    }

    /**
     * @return the eliminado
     */
    public boolean isEliminado() {
        return eliminado;
    }

    /**
     * @param eliminado the eliminado to set
     */
    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CadenasMando)) {
            return false;
        }
        CadenasMando other = (CadenasMando) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName()).append("{").append("id=").append(this.getId()).append(", apCampo=").append(getApCampo() != null ? getApCampo().getId() : null).append(", aprueba=").append(getAprueba() != null ? getAprueba().getId() : null).append(", autorizaIhsa=").append(getAutorizaIhsa() != null ? getAutorizaIhsa().getId() : null).append(", autorizaMpg=").append(getAutorizaMpg() != null ? getAutorizaMpg().getId() : null).append(", revisa=").append(getRevisa() != null ? getRevisa().getId() : null).append(", usuario=").append(getUsuario() != null ? getUsuario().getId() : null).append(", genero=").append(getGenero() != null ? getGenero().getId() : null).append(", fechaGenero=").append(getFechaGenero() != null ? (Constantes.FMT_ddMMyyy.format(getFechaGenero())) : null).append(", horaGenero=").append(getHoraGenero() != null ? (Constantes.FMT_HHmmss.format(getHoraGenero())) : null).append(", modifico=").append(getModifico() != null ? getModifico().getId() : null).append(", fechaModifico=").append(getFechaModifico() != null ? (Constantes.FMT_ddMMyyy.format(getFechaModifico())) : null).append(", horaModifico=").append(getHoraModifico() != null ? (Constantes.FMT_HHmmss.format(getHoraModifico())) : null).append(", eliminado=").append(isEliminado()).append("}");

        return sb.toString();
    }

    public OcSubcampo getOcSubcampo() {
        return ocSubcampo;
    }

    public void setOcSubcampo(OcSubcampo ocSubcampo) {
        this.ocSubcampo = ocSubcampo;
    }
}
