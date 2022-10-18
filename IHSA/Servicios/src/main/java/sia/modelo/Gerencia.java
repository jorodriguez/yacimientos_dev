/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import sia.constantes.Constantes;

/**
 *
 * @author hacosta
 */
@Entity
@Table(name = "GERENCIA")
@SequenceGenerator(sequenceName = "gerencia_id_seq", name = "gerencia_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Gerencia.findAll", query = "SELECT g FROM Gerencia g")})
public class Gerencia implements Serializable {
    @Column(name =     "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name =     "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    @Column(name =     "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name =     "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    @OneToMany(mappedBy = "gerencia")
    private Collection<ApCampoGerencia> apCampoGerenciaCollection;
    @OneToMany(mappedBy = "gerenciaResponsable")
    private Collection<SgSolicitudViaje> sgSolicitudViajeCollection;
    @OneToMany(mappedBy = "gerencia")
    private Collection<Usuario> usuarioCollection;
    @OneToMany(mappedBy = "gerencia")
    private Collection<SgCadenaAprobacion> sgCadenaAprobacionCollection;
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "gerencia_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 100)
    @Column(name = "NOMBRE")
    private String nombre;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    
    @Column(name = "ABREV")
    private String abrev;

    public Gerencia() {
    }

    public Gerencia(int id) {
        this.id = id;
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
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
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
        if (!(object instanceof Gerencia)) {
            return false;
        }
        Gerencia other = (Gerencia) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName())
            .append("{")
            .append("id=").append(getId())
            .append(", nombre=").append(getNombre())
            .append(", genero=").append(getGenero() != null ? getGenero().getId() : null)
            .append(", fechaGenero=").append(getFechaGenero() != null ? (Constantes.FMT_ddMMyyy.format(getFechaGenero())) : null)
            .append(", horaGenero=").append(getHoraGenero() != null ? (Constantes.FMT_HHmmss.format(getHoraGenero())) : null)
            .append(", modifico=").append(getModifico() != null ? getModifico().getId() : null)
            .append(", fechaModifico=").append(getFechaModifico() != null ? (Constantes.FMT_ddMMyyy.format(getFechaModifico())) : null)
            .append(", horaModifico=").append(getHoraModifico() != null ? (Constantes.FMT_HHmmss.format(getHoraModifico())) : null)
            .append(", eliminado=").append(isEliminado())
            .append("}");
        return sb.toString();
    }



    public Collection<Usuario> getUsuarioCollection() {
        return usuarioCollection;
    }

    public void setUsuarioCollection(Collection<Usuario> usuarioCollection) {
        this.usuarioCollection = usuarioCollection;
    }

    public Collection<SgCadenaAprobacion> getSgCadenaAprobacionCollection() {
        return sgCadenaAprobacionCollection;
    }

    public void setSgCadenaAprobacionCollection(Collection<SgCadenaAprobacion> sgCadenaAprobacionCollection) {
        this.sgCadenaAprobacionCollection = sgCadenaAprobacionCollection;
    }

    public Collection<SgSolicitudViaje> getSgSolicitudViajeCollection() {
        return sgSolicitudViajeCollection;
    }

    public void setSgSolicitudViajeCollection(Collection<SgSolicitudViaje> sgSolicitudViajeCollection) {
        this.sgSolicitudViajeCollection = sgSolicitudViajeCollection;
    }

    public Collection<ApCampoGerencia> getApCampoGerenciaCollection() {
        return apCampoGerenciaCollection;
    }

    public void setApCampoGerenciaCollection(Collection<ApCampoGerencia> apCampoGerenciaCollection) {
        this.apCampoGerenciaCollection = apCampoGerenciaCollection;
    }
    /**
     * @return the abrev
     */
    public String getAbrev() {
        return abrev;
    }

    /**
     * @param abrev the abrev to set
     */
    public void setAbrev(String abrev) {
        this.abrev = abrev;
    }

}