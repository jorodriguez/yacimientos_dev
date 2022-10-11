/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
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
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author hacosta
 */
@Entity
@Table(name = "COMPANIA")
@SequenceGenerator(sequenceName = "compania_id_seq", name = "compania_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Compania.findAll", query = "SELECT c FROM Compania c")})
@Getter
@Setter
@ToString
public class Compania implements Serializable {

    @Lob
    @Column(name = "LOGO")
    private byte[] logo;
    @Lob()
    @Column(name = "LOGO_ESR")
    private byte[] logoEsr;
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
    @OneToMany(mappedBy = "compania")
    private Collection<Orden> ordenCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "compania")
    private Collection<ProyectoOt> proyectoOtCollection;
    @OneToMany(mappedBy = "compania")
    private Collection<Requisicion> requisicionCollection;
    @OneToMany(mappedBy = "compania")
    private Collection<ApCampo> apCampoCollection;

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    
    @Size(min = 1, max = 13)
    @Column(name = "RFC")
    private String rfc;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @Column(name = "MONTO_REVISA")
    private Double montoRevisa;
    @Column(name = "MONTO_AUTORIZA")
    private Double montoAutoriza;
    @Column(name = "MONTO_AUTORIZA_ALFA")
    private Double montoAutorizaAlfa;
    @Size(max = 250)
    @Column(name = "NOTIFICA_ANTICIPO")
    private String notificaAnticipo;
    @Size(max = 100)
    @Column(name = "NOMBRE")
    private String nombre;
    @Size(max = 75)
    @Column(name = "FACTURA")
    private String factura;
    @Lob
    @Column(name = "DOMICILIO_FISCAL")
    private String domicilioFiscal;
    
    @Column(name = "SIGLAS")
    private String siglas;
    @Column(name = "CENTRO_COSTOS")
    private Integer centroCostos;
    @Size(max = 70)
    @Column(name = "CALLE")
    private String calle;
    @Size(max = 50)
    @Column(name = "COLONIA")
    private String colonia;
    @Size(max = 70)
    @Column(name = "CIUDAD")
    private String ciudad;
    @Size(max = 30)
    @Column(name = "ESTADO")
    private String estado;
    @Column(name = "CP")
    private Integer cp;
    @Size(max = 100)
    @Column(name = "TELEFONO")
    private String telefono;
    @Size(max = 60)
    @Column(name = "FAX")
    private String fax;
    @Size(max = 2500)
    @Column(name = "REQUISITO_FACTURA")
    private String requisitoFactura;
    @JoinColumn(name = "RECEPCION_FACTURA", referencedColumnName = "ID")
    @ManyToOne
    private Usuario recepcionFactura;
    @Size(max = 10)
    @Column(name = "NAV_PREFIJO")
    private String navPrefijo;
    @Column(name = "SOCIO")
    private boolean socio;
//    @OneToMany(mappedBy = "compania")
//    private Collection<Orden> ordenCollection;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "compania")
//    private Collection<ProyectoOt> proyectoOtCollection;
//    @OneToMany(mappedBy = "compania")
//    private Collection<Gerencia> gerenciaCollection;
//    @OneToMany(mappedBy = "compania")
//    private Collection<Requisicion> requisicionCollection;
//    @OneToMany(mappedBy = "companiaPropietaria")
//    private Collection<ServiciosAdquiridos> serviciosAdquiridosCollection;
//    @OneToMany(mappedBy = "compania")
//    private Collection<ApCampo> apCampoCollection;

    @JoinColumn(name = "MONEDA", referencedColumnName = "ID")
    @ManyToOne
    private Moneda moneda;
    @Column(name = "MONTO_LICITACION")
    private Double montoLicitacion;
    @JoinColumn(name = "SI_PAIS", referencedColumnName = "ID")
    @ManyToOne
    private SiPais siPais;
    
    @Column(name = "viaje_aereo")
    private boolean viajeAereo;
    
    public Compania() {
    }

    public Compania(String RFC_IHSA) {
	this.rfc = RFC_IHSA;
    }

    
    public int hashCode() {
	int hash = 0;
	hash += (getRfc() != null ? getRfc().hashCode() : 0);
	return hash;
    }

    
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof Compania)) {
	    return false;
	}
	Compania other = (Compania) object;
	if ((this.getRfc() == null && other.getRfc() != null) || (this.getRfc() != null && !this.rfc.equals(other.rfc))) {
	    return false;
	}
	return true;
    }
}
