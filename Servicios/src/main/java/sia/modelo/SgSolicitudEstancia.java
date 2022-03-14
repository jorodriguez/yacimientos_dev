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
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author b75ckd35th
 */
@Entity
@Table(name = "SG_SOLICITUD_ESTANCIA")
@SequenceGenerator(sequenceName = "sg_solicitud_estancia_id_seq", name = "sg_solicitud_estancia_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgSolicitudEstancia.findAll", query = "SELECT s FROM SgSolicitudEstancia s")})
@Setter
@Getter
@ToString
public class SgSolicitudEstancia implements Serializable {

    @Column(name = "INICIO_ESTANCIA")
    @Temporal(TemporalType.DATE)
    private Date inicioEstancia;
    @Column(name = "FIN_ESTANCIA")
    @Temporal(TemporalType.DATE)
    private Date finEstancia;
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
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_solicitud_estancia_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Column(name = "DIAS_ESTANCIA")
    private Integer diasEstancia;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    
    @Column(name = "CANCELADO")
    private boolean cancelado;
    @Size(max = 12)
    @Column(name = "CODIGO")
    private String codigo;
    @OneToMany(mappedBy = "sgSolicitudEstancia")
    private Collection<SgHuespedHotel> sgHuespedHotelCollection;
    @OneToMany(mappedBy = "sgSolicitudEstancia")
    private Collection<SgHuespedStaff> sgHuespedStaffCollection;
    @OneToMany(mappedBy = "sgSolicitudEstancia")
    private Collection<SgDetalleSolicitudEstancia> sgDetalleSolicitudEstanciaCollection;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "SG_OFICINA", referencedColumnName = "ID")
    @ManyToOne
    private SgOficina sgOficina;
    @JoinColumn(name = "GERENCIA", referencedColumnName = "ID")
    @ManyToOne
    private Gerencia gerencia;
    @JoinColumn(name = "SG_MOTIVO", referencedColumnName = "ID")
    @ManyToOne
    private SgMotivo sgMotivo;
    @JoinColumn(name = "ESTATUS", referencedColumnName = "ID")
    @ManyToOne
    private Estatus estatus;
    @JoinColumn(name = "USUARIO_HOSPEDA", referencedColumnName = "ID")
    @ManyToOne
    private Usuario usuarioHospeda;
    @Column(name = "OBSERVACION")
    private String observacion;
    @JoinColumn(name = "SG_UBICACION", referencedColumnName = "ID")
    @ManyToOne
    private SgUbicacion sgUbicacion;
    @JoinColumn(name = "AP_CAMPO", referencedColumnName = "ID")
    @ManyToOne
    private ApCampo apCampo;

    public SgSolicitudEstancia() {
    }

    public SgSolicitudEstancia(Integer id) {
	this.id = id;
    }

    
    public int hashCode() {
	int hash = 0;
	hash += (id != null ? id.hashCode() : 0);
	return hash;
    }

    
    public boolean equals(Object object) {
	if (!(object instanceof SgSolicitudEstancia)) {
	    return false;
	}
	SgSolicitudEstancia other = (SgSolicitudEstancia) object;
	if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
	    return false;
	}
	return true;
    }
}
