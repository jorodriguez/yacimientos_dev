/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import sia.constantes.Constantes;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SG_RUTA_TERRESTRE")
@SequenceGenerator(sequenceName = "sg_ruta_terrestre_id_seq", name = "sg_ruta_terrestre_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgRutaTerrestre.findAll", query = "SELECT s FROM SgRutaTerrestre s")})
public class SgRutaTerrestre implements Serializable {

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
    @OneToMany(mappedBy = "sgRutaTerrestre")
    private Collection<SgDetalleRutaCiudad> sgDetalleRutaCiudadCollection;
    @JoinColumn(name = "SG_TIPO_ESPECIFICO", referencedColumnName = "ID")
    @ManyToOne
    private SgTipoEspecifico sgTipoEspecifico;
//    @OneToMany(mappedBy = "sgRutaTerrestre")
//    private Collection<SgEstadoSemaforo> sgEstadoSemaforoCollection;
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_ruta_terrestre_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 10)
    @Column(name = "TIEMPO_VIAJE")
    private String tiempoViaje;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @Size(max = 64)
    @Column(name = "NOMBRE")
    private String nombre;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "SG_OFICINA", referencedColumnName = "ID")
    @ManyToOne
    private SgOficina sgOficina;    
    @Column(name = "HORA_MINIMARUTA")
    @Temporal(TemporalType.TIME)
    private Date horaMinimaRuta;
    @Column(name = "HORA_MAXIMARUTA")
    @Temporal(TemporalType.TIME)
    private Date horaMaximaRuta;

    public SgRutaTerrestre() {
    }

    public SgRutaTerrestre(Integer id) {
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
     * @return the tiempoViaje
     */
    public String getTiempoViaje() {
        return tiempoViaje;
    }

    /**
     * @param tiempoViaje the tiempoViaje to set
     */
    public void setTiempoViaje(String tiempoViaje) {
        this.tiempoViaje = tiempoViaje;
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
     * @return the sgOficina
     */
    public SgOficina getSgOficina() {
        return sgOficina;
    }

    /**
     * @param sgOficina the sgOficina to set
     */
    public void setSgOficina(SgOficina sgOficina) {
        this.sgOficina = sgOficina;
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

    
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgRutaTerrestre)) {
            return false;
        }
        SgRutaTerrestre other = (SgRutaTerrestre) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName());
        sb.append("{");
        sb.append("id=").append(getId());
        sb.append(", nombre=").append(getNombre());
        sb.append(", tiempoViaje=").append(getTiempoViaje());
        sb.append(", sgOficina=").append(getSgOficina() != null ? getSgOficina().getId() : null);
        sb.append(", genero=").append(getGenero() != null ? getGenero().getId() : null);
        sb.append(", fechaGenero=").append(getFechaGenero() != null ? (Constantes.FMT_ddMMyyy.format(getFechaGenero())) : null);
        sb.append(", horaGenero=").append(getHoraGenero() != null ? (Constantes.FMT_HHmmss.format(getHoraGenero())) : null);
        sb.append(", modifico=").append(getModifico() != null ? getModifico().getId() : null);
        sb.append(", fechaModifico=").append(getFechaModifico() != null ? (Constantes.FMT_ddMMyyy.format(getFechaModifico())) : null);
        sb.append(", horaModifico=").append(getHoraModifico() != null ? (Constantes.FMT_HHmmss.format(getHoraModifico())) : null);
        sb.append(", eliminado=").append(isEliminado());        
        sb.append(", horaMinimaRuta=").append(getHoraMinimaRuta()!= null ? (Constantes.FMT_HHmmss.format(getHoraMinimaRuta())) : null);
        sb.append(", horaMaximaRuta=").append(getHoraMaximaRuta()!= null ? (Constantes.FMT_HHmmss.format(getHoraMaximaRuta())) : null);
        sb.append("}");

        return sb.toString();
    }

//    public Collection<SgEstadoSemaforo> getSgEstadoSemaforoCollection() {
//        return sgEstadoSemaforoCollection;
//    }
//
//    public void setSgEstadoSemaforoCollection(Collection<SgEstadoSemaforo> sgEstadoSemaforoCollection) {
//        this.sgEstadoSemaforoCollection = sgEstadoSemaforoCollection;
//    }
    public Collection<SgDetalleRutaCiudad> getSgDetalleRutaCiudadCollection() {
        return sgDetalleRutaCiudadCollection;
    }

    public void setSgDetalleRutaCiudadCollection(Collection<SgDetalleRutaCiudad> sgDetalleRutaCiudadCollection) {
        this.sgDetalleRutaCiudadCollection = sgDetalleRutaCiudadCollection;
    }

    public SgTipoEspecifico getSgTipoEspecifico() {
        return sgTipoEspecifico;
    }

    public void setSgTipoEspecifico(SgTipoEspecifico sgTipoEspecifico) {
        this.sgTipoEspecifico = sgTipoEspecifico;
    }

    /**
     * @return the horaMinimaRuta
     */
    public Date getHoraMinimaRuta() {
        return horaMinimaRuta;
    }

    /**
     * @param horaMinimaRuta the horaMinimaRuta to set
     */
    public void setHoraMinimaRuta(Date horaMinimaRuta) {
        this.horaMinimaRuta = horaMinimaRuta;
    }

    /**
     * @return the horaMaximaRuta
     */
    public Date getHoraMaximaRuta() {
        return horaMaximaRuta;
    }

    /**
     * @param horaMaximaRuta the horaMaximaRuta to set
     */
    public void setHoraMaximaRuta(Date horaMaximaRuta) {
        this.horaMaximaRuta = horaMaximaRuta;
    }
}
