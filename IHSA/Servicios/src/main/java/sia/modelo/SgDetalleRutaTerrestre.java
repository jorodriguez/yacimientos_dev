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
@Table(name = "SG_DETALLE_RUTA_TERRESTRE")
@SequenceGenerator(sequenceName = "sg_detalle_ruta_terrestre_id_seq", name = "sg_detalle_ruta_terrestre_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgDetalleRutaTerrestre.findAll", query = "SELECT s FROM SgDetalleRutaTerrestre s")})
public class SgDetalleRutaTerrestre implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_detalle_ruta_terrestre_seq", strategy = GenerationType.SEQUENCE)
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
    
    @Column(name = "DESTINO")
    private boolean destino;
    @JoinColumn(name = "MODFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modfico;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "SG_RUTA", referencedColumnName = "ID")
    @ManyToOne
    private SgRutaTerrestre sgRutaTerrestre;
    @JoinColumn(name = "SG_OFICINA", referencedColumnName = "ID")
    @ManyToOne
    private SgOficina sgOficina;

    public SgDetalleRutaTerrestre() {
    }

    public SgDetalleRutaTerrestre(Integer id) {
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

    public Usuario getModfico() {
        return modfico;
    }

    public void setModfico(Usuario modfico) {
        this.modfico = modfico;
    }

    public Usuario getGenero() {
        return genero;
    }

    public void setGenero(Usuario genero) {
        this.genero = genero;
    }

    public SgRutaTerrestre getSgRutaTerrestre() {
        return sgRutaTerrestre;
    }

    public void setSgRutaTerrestre(SgRutaTerrestre sgRutaTerrestre) {
        this.sgRutaTerrestre = sgRutaTerrestre;
    }

    public SgOficina getSgOficina() {
        return sgOficina;
    }

    public void setSgOficina(SgOficina sgOficina) {
        this.sgOficina = sgOficina;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgDetalleRutaTerrestre)) {
            return false;
        }
        SgDetalleRutaTerrestre other = (SgDetalleRutaTerrestre) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "SgDetalleRutaTerrestre{" + "id=" + id + ", fechaGenero=" + fechaGenero + ", horaGenero=" + horaGenero + ", fechaModifico=" + fechaModifico + ", horaModifico=" + horaModifico + ", eliminado=" + eliminado + ", destino=" + destino + ", modfico=" + modfico + ", genero=" + genero + ", sgRutaTerrestre=" + sgRutaTerrestre + ", sgOficina=" + sgOficina + '}';
    }
    
    /**
     * @return the destino
     */
    public boolean isDestino() {
        return destino;
    }

    /**
     * @param destino the destino to set
     */
    public void setDestino(boolean destino) {
        this.destino = destino;
    }
    
}
