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
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ihsa
 */
@Entity
@Getter
@Setter
@Table(name = "INV_DETALLE_SOLICITUD_MATERIAL")
@SequenceGenerator(sequenceName = "inv_detalle_solicitud_material_id_seq", name = "detalle_solicitud_material_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "InvDetalleSolicitudMaterial.findAll", query = "SELECT o FROM InvDetalleSolicitudMaterial o")})
public class InvDetalleSolicitudMaterial implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "detalle_solicitud_material_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;

    @NotNull
    @JoinColumn(name = "INV_SOLICITUD_MATERIAL", referencedColumnName = "ID")
    @ManyToOne
    private InvSolicitudMaterial invSolicitudMaterial;
    @JoinColumn(name = "INv_ARTICULO", referencedColumnName = "ID")
    @ManyToOne
    private InvArticulo invArticulo;
    @Column(name = "REFERENCIA")
    private String referencia;
    @Column(name = "CANTIDAD")
    private double cantidad;
    @Column(name = "CANTIDAD_RECIBIDA")
    private double cantidadRecibida;
    
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    //
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

    public InvDetalleSolicitudMaterial() {
    }

    public InvDetalleSolicitudMaterial(Integer id) {
        this.id = id;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        if (!(object instanceof InvDetalleSolicitudMaterial)) {
            return false;
        }
        InvDetalleSolicitudMaterial other = (InvDetalleSolicitudMaterial) object;
        if ((this.id == null && other.getId() != null) || (this.id != null && !this.id.equals(other.getId()))) {
            return false;
        }
        return true;
    }

}
