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
@Table(name = "INV_SOLICITUD_MATERIAL")
@SequenceGenerator(sequenceName = "inv_solicitud_material_id_seq", name = "solicitud_material_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "InvSolicitudMaterial.findAll", query = "SELECT o FROM InvSolicitudMaterial o")})
public class InvSolicitudMaterial implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "solicitud_material_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)

    @Column(name = "ID")
    private Integer id;

    @JoinColumn(name = "INV_ALMACEN", referencedColumnName = "ID")
    @ManyToOne
    private InvAlmacen invAlmacen;
    @JoinColumn(name = "GERENCIA", referencedColumnName = "ID")
    @ManyToOne
    private Gerencia gerencia;
    @JoinColumn(name = "AP_CAMPO", referencedColumnName = "ID")
    @ManyToOne
    private ApCampo apCampo;
    @JoinColumn(name = "ESTATUS", referencedColumnName = "ID")
    @ManyToOne
    private Estatus estatus;
    @Column(name = "FECHA_SOLICITA")
    @Temporal(TemporalType.DATE)
    private Date fechaSolicita;
    @Column(name = "FECHA_REQUERIDA")
    @Temporal(TemporalType.DATE)
    private Date fechaRequerida;
    
    @Column(name = "FECHA_ENTREGA")
    @Temporal(TemporalType.DATE)
    private Date fechaEntrega;
    @Column(name = "HORA_ENTREGA")
    @Temporal(TemporalType.TIME)
    private Date horaEntrega;
    
    @Column(name = "FOLIO")
    private String folio;
    @Column(name = "OBSERVACIONES")
    private String observacion;
    @Column(name = "CANTIDAD_SOLICITADA")
    private double cantidadSolicitada;
    @Column(name = "CANTIDAD_RECIBIDA")
    private double cantidadRecibida;
    @Column(name = "TELEFONO")
    private String telefono;
    @Column(name = "USUARIO_RECOGE")
    private String usuarioRecoge;
    @Column(name = "USUARIO_RECIBE_MATERIAL")
    private String usuarioRecibeMaterial;
    
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

    public InvSolicitudMaterial() {
    }

    public InvSolicitudMaterial(Integer id) {
        this.id = id;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        if (!(object instanceof InvSolicitudMaterial)) {
            return false;
        }
        InvSolicitudMaterial other = (InvSolicitudMaterial) object;
        if ((this.id == null && other.getId() != null) || (this.id != null && !this.id.equals(other.getId()))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.InvSolicitudMaterial[ id=" + id + " ]";
    }
}
