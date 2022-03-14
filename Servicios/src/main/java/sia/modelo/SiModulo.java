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
import sia.constantes.Constantes;

/**
 *
 * @author sluis
 */
@Entity
@Table(name = "SI_MODULO")
@SequenceGenerator(sequenceName = "si_modulo_id_seq", name = "si_modulo_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SiModulo.findAll", query = "SELECT s FROM SiModulo s")})
public class SiModulo implements Serializable {
    @Column(name =     "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    @Column(name =     "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name =     "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name =     "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    @OneToMany(mappedBy = "siModulo")
    private Collection<SiRol> siRolCollection;
    @OneToMany(mappedBy = "siModulo")
    private Collection<SiOpcion> siOpcionCollection;
    @OneToMany(mappedBy = "siModulo")
    private Collection<SiAdjunto> siAdjuntoCollection;

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "si_modulo_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 32)
    @Column(name = "NOMBRE")
    private String nombre;
    @Size(max = 64)
    @Column(name = "RUTA")
    private String ruta;
    @NotNull
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;    
    @Size(max = 128)
    @Column(name = "ICONO")
    private String icono;    
    @Size(max = 128)
    @Column(name = "RUTASERVLET")
    private String rutaServlet;    
    @Size(max = 128)
    @Column(name = "TOOLTIP")
    private String toolTip;        
    @Size(max = 128)
    @Column(name = "EXTRALINKRENDER")
    private String extraLinkRender;    

    public SiModulo() {
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
     * @return the ruta
     */
    public String getRuta() {
        return ruta;
    }

    /**
     * @param ruta the ruta to set
     */
    public void setRuta(String ruta) {
        this.ruta = ruta;
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

    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName())
            .append("{")
            .append("id=").append(getId())
            .append(", nombre=").append(getNombre())
            .append(", ruta=").append(getRuta())
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


    public Collection<SiOpcion> getSiOpcionCollection() {
        return siOpcionCollection;
    }

    public void setSiOpcionCollection(Collection<SiOpcion> siOpcionCollection) {
        this.siOpcionCollection = siOpcionCollection;
    }

    public Collection<SiAdjunto> getSiAdjuntoCollection() {
        return siAdjuntoCollection;
    }

    public void setSiAdjuntoCollection(Collection<SiAdjunto> siAdjuntoCollection) {
        this.siAdjuntoCollection = siAdjuntoCollection;
    }


    public Collection<SiRol> getSiRolCollection() {
        return siRolCollection;
    }

    public void setSiRolCollection(Collection<SiRol> siRolCollection) {
        this.siRolCollection = siRolCollection;
    }

    /**
     * @return the icono
     */
    public String getIcono() {
        return icono;
    }

    /**
     * @param icono the icono to set
     */
    public void setIcono(String icono) {
        this.icono = icono;
    }

    /**
     * @return the rutaServlet
     */
    public String getRutaServlet() {
        return rutaServlet;
    }

    /**
     * @param rutaServlet the rutaServlet to set
     */
    public void setRutaServlet(String rutaServlet) {
        this.rutaServlet = rutaServlet;
    }

    /**
     * @return the toolTip
     */
    public String getToolTip() {
        return toolTip;
    }

    /**
     * @param toolTip the toolTip to set
     */
    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
    }

    /**
     * @return the extraLinkRender
     */
    public String getExtraLinkRender() {
        return extraLinkRender;
    }

    /**
     * @param extraLinkRender the extraLinkRender to set
     */
    public void setExtraLinkRender(String extraLinkRender) {
        this.extraLinkRender = extraLinkRender;
    }

}
