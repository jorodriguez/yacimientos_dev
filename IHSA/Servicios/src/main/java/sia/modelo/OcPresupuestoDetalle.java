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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import sia.constantes.Constantes;

/**
 *
 * @author jcarranza
 */
@Entity
@Table(name = "OC_PRESUPUESTO_DETALLE")
@SequenceGenerator(sequenceName = "oc_presupuesto_detalle_id_seq", name = "oc_presupuesto_detalle_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "OcPresupuestoDetalle.findAll", query = "SELECT o FROM OcPresupuestoDetalle o")})
@Setter
@Getter
public class OcPresupuestoDetalle implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "oc_presupuesto_detalle_seq", strategy = GenerationType.SEQUENCE)
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
    @JoinColumn(name = "OC_PRESUPUESTO", referencedColumnName = "ID")
    @ManyToOne
    private OcPresupuesto ocPresupuesto;
    @JoinColumn(name = "OC_ACTIVIDADPETROLERA", referencedColumnName = "ID")
    @ManyToOne
    private OcActividadPetrolera ocActividadPetrolera;
    @JoinColumn(name = "OC_UNIDAD_COSTO", referencedColumnName = "ID")
    @ManyToOne
    private OcUnidadCosto ocUnidadCosto;
    @JoinColumn(name = "OC_CODIGO_TAREA", referencedColumnName = "ID")
    @ManyToOne
    private OcCodigoTarea ocCodigoTarea;
    @JoinColumn(name = "OC_CODIGO_SUBTAREA", referencedColumnName = "ID")
    @ManyToOne
    private OcCodigoSubtarea ocCodigoSubtarea;

    @Column(name = "anio_presupuesto")
    private Integer anio;
    
    @Column(name = "MES")
    private Integer mes;

    @Column(name = "mano_obra_CN")
    private Double manoObraCN;

    @Column(name = "mano_obra_EX")
    private Double manoObraEX;

    @Column(name = "bienes_CN")
    private Double bienesCN;

    @Column(name = "bienes_EX")
    private Double bienesEX;

    @Column(name = "servicios_CN")
    private Double serviciosCN;

    @Column(name = "servicios_EX")
    private Double serviciosEX;

    @Column(name = "capacitacion_CN")
    private Double capacitacionCN;

    @Column(name = "capacitacion_EX")
    private Double capacitacionEX;

    @Column(name = "trans_Tecnologia")
    private Double transTecnologia;

    @Column(name = "infraestructura")
    private Double infraestructura;
    
//    @JoinColumn(name = "PROYECTO_OT", referencedColumnName = "ID")
//    @ManyToOne
//    private ProyectoOt proyectoOt;

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OcPresupuesto)) {
            return false;
        }
        OcPresupuestoDetalle other = (OcPresupuestoDetalle) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName())
                .append("{")
                .append("id=").append(this.id)
                .append(", genero=").append(this.genero != null ? this.genero.getId() : null)
                .append(", fechaGenero=").append(this.fechaGenero != null ? (Constantes.FMT_ddMMyyy.format(this.fechaGenero)) : null)
                .append(", horaGenero=").append(this.horaGenero != null ? (Constantes.FMT_HHmmss.format(this.horaGenero)) : null)
                .append(", modifico=").append(this.modifico != null ? this.modifico.getId() : null)
                .append(", fechaModifico=").append(this.fechaModifico != null ? (Constantes.FMT_ddMMyyy.format(this.fechaModifico)) : null)
                .append(", horaModifico=").append(this.horaModifico != null ? (Constantes.FMT_HHmmss.format(this.horaModifico)) : null)
                .append(", eliminado=").append(this.eliminado)
                .append("}");

        return sb.toString();
    }
}
