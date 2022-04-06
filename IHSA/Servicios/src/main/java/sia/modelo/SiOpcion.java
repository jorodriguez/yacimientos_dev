/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author sluis
 */
@Entity
@Table(name = "SI_OPCION")
@SequenceGenerator(sequenceName = "si_opcion_id_seq", name = "si_opcion_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SiOpcion.findAll", query = "SELECT s FROM SiOpcion s")})
public class SiOpcion implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "si_opcion_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 64)
    @Column(name = "NOMBRE")
    private String nombre;
    @Size(max = 256)
    @Column(name = "PAGINA")
    private String pagina;
    @Size(max = 256)
    @Column(name = "PAGINALISTENER")
    private String paginaListener;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    
    @Column(name = "ESTATUS_CONTAR")
    private Integer estatusContar;
    
    @JoinColumn(name = "GENERO", referencedColumnName = "ID" )
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "SI_MODULO", referencedColumnName = "ID")
    @ManyToOne
    private SiModulo siModulo;

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
    @Column(name = "POSICION")
    private Integer posicion;
    
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @OneToMany(mappedBy = "siOpcion")
    private Collection<SiOpcion> siOpcionCollection;
    @JoinColumn(name = "SI_OPCION", referencedColumnName = "ID")
    @ManyToOne
    private SiOpcion siOpcion;
    @OneToMany(mappedBy = "siOpcion")
    private Collection<CoNoticia> coNoticiaCollection;
    
    public SiOpcion() {
    }

    public SiOpcion(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPagina() {
        return pagina;
    }

    public void setPagina(String pagina) {
        this.pagina = pagina;
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

    public SiModulo getSiModulo() {
        return siModulo;
    }

    public void setSiModulo(SiModulo siModulo) {
        this.siModulo = siModulo;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SiOpcion)) {
            return false;
        }
        SiOpcion other = (SiOpcion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.SiOpcion[ id=" + id + " ]";
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

    public Integer getPosicion() {
        return posicion;
    }

    public void setPosicion(Integer posicion) {
        this.posicion = posicion;
    }

    public Usuario getModifico() {
        return modifico;
    }

    public void setModifico(Usuario modifico) {
        this.modifico = modifico;
    }

    public Collection<SiOpcion> getSiOpcionCollection() {
        return siOpcionCollection;
    }

    public void setSiOpcionCollection(Collection<SiOpcion> siOpcionCollection) {
        this.siOpcionCollection = siOpcionCollection;
    }

    public SiOpcion getSiOpcion() {
        return siOpcion;
    }

    public void setSiOpcion(SiOpcion siOpcion) {
        this.siOpcion = siOpcion;
    }

    public Collection<CoNoticia> getCoNoticiaCollection() {
        return coNoticiaCollection;
    }

    public void setCoNoticiaCollection(Collection<CoNoticia> coNoticiaCollection) {
        this.coNoticiaCollection = coNoticiaCollection;
    }

    /**
     * @return the estatusContar
     */
    public Integer getEstatusContar() {
        return estatusContar;
    }

    /**
     * @param estatusContar the estatusContar to set
     */
    public void setEstatusContar(Integer estatusContar) {
        this.estatusContar = estatusContar;
    }

    /**
     * @return the paginaListener
     */
    public String getPaginaListener() {
        return paginaListener;
    }

    /**
     * @param paginaListener the paginaListener to set
     */
    public void setPaginaListener(String paginaListener) {
        this.paginaListener = paginaListener;
    }

}
