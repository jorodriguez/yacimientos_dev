/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import sia.constantes.Constantes;

/**
 *
 * @author ihsa
 */
@Entity
@Getter
@Setter
@Table(name = "GR_INTERSECCION")
@SequenceGenerator(sequenceName = "gr_interseccion_id_seq", name = "gr_interseccion_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "GrInterseccion.findAll", query = "SELECT o FROM GrInterseccion o")})
public class GrInterseccion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "gr_interseccion_seq", strategy = GenerationType.SEQUENCE)
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
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "SG_VIAJE_A", referencedColumnName = "ID")
    @ManyToOne
    private SgViaje sgViajeA;
    @JoinColumn(name = "SG_VIAJE_B", referencedColumnName = "ID")
    @ManyToOne
    private SgViaje sgViajeB;
    @JoinColumn(name = "GR_PUNTO", referencedColumnName = "ID")
    @ManyToOne
    private GrPunto grPunto;
    

    public GrInterseccion() {
    }

    public GrInterseccion(Integer id) {
        this.id = id;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OcOrdenEts)) {
            return false;
        }
        GrInterseccion other = (GrInterseccion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName());
        sb.append("{");
        sb.append("id=").append(this.id);
        sb.append(", genero=").append(this.genero != null ? this.genero.getId() : null);        
        sb.append(", sgViajeA=").append(this.sgViajeA != null ? this.sgViajeA.getId() : null);
        sb.append(", sgViajeB=").append(this.sgViajeB != null ? this.sgViajeB.getId() : null);        
        sb.append(", fechaGenero=").append(this.fechaGenero != null ? (Constantes.FMT_ddMMyyy.format(this.fechaGenero)) : null);
        sb.append(", horaGenero=").append(this.horaGenero != null ? (Constantes.FMT_HHmmss.format(this.horaGenero)) : null);
        sb.append(", modifico=").append(this.modifico != null ? this.modifico.getId() : null);
        sb.append(", fechaModifico=").append(this.fechaModifico != null ? (Constantes.FMT_ddMMyyy.format(this.fechaModifico)) : null);
        sb.append(", horaModifico=").append(this.horaModifico != null ? (Constantes.FMT_HHmmss.format(this.horaModifico)) : null);        
        sb.append(", grPunto=").append(this.grPunto != null ? this.grPunto.getId() : null);
        sb.append(", eliminado=").append(this.eliminado);        
        sb.append("}");

        return sb.toString();
    }

}



