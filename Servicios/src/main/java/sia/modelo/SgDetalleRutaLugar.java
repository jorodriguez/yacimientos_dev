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
import lombok.Data;

   
/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SG_DETALLE_RUTA_LUGAR")
@SequenceGenerator(sequenceName = "sg_detalle_ruta_lugar_id_seq", name = "sg_detalle_ruta_lugar_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgDetalleRutaLugar.findAll", query = "SELECT s FROM SgDetalleRutaLugar s")})
@Data
public class SgDetalleRutaLugar implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_detalle_ruta_lugar_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    
    @Column(name = "DESTINO")
    private boolean destino;
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
    @JoinColumn(name = "SG_RUTA_TERRESTRE", referencedColumnName = "ID")
    @ManyToOne
    private SgRutaTerrestre sgRutaTerrestre;
    @JoinColumn(name = "SG_LUGAR", referencedColumnName = "ID")
    @ManyToOne
    private SgLugar sgLugar;

    public SgDetalleRutaLugar() {
    }
    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgDetalleRutaLugar)) {
            return false;
        }
        SgDetalleRutaLugar other = (SgDetalleRutaLugar) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }


    
}
