/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import javax.persistence.SequenceGenerator;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * GEN_CONTACTO_PROVEEDOR_ID
 *
 * @author hacosta
 */
@Entity
@Table(name = "CONTACTO_PROVEEDOR")
@SequenceGenerator(sequenceName = "contacto_proveedor_id_seq", name = "contacto_proveedor_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ContactoProveedor.findAll", query = "SELECT c FROM ContactoProveedor c")})
@Getter
@Setter
@ToString
public class ContactoProveedor implements Serializable {

    @OneToMany(mappedBy = "contactoProveedor")
    private Collection<PvLogNotifica> pvLogNotificaCollection;
    @JoinColumn(name = "PV_AREA", referencedColumnName = "ID")
    @ManyToOne
    private PvArea pvArea;
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "contacto_proveedor_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 70)
    @Column(name = "NOMBRE")
    private String nombre;
    @Size(max = 50)
    @Column(name = "PUESTO")
    private String puesto;
    @Size(max = 50)
    @Column(name = "TELEFONO")
    private String telefono;
    @Size(max = 50)
    @Column(name = "CELULAR")
    private String celular;
    @Size(max = 200)
    @Column(name = "CORREO")
    private String correo;
    @Size(max = 20)
    @Column(name = "EXTENSION")
    private String extension;
    
    @Column(name = "ACTIVO")
    private boolean activo;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "PROVEEDOR", referencedColumnName = "ID")
    @ManyToOne
    private Proveedor proveedor;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    @OneToMany(mappedBy = "contactoProveedor")
    private Collection<ContactosOrden> contactosOrdenCollection;

    @Size(max = 13)
    @Column(name = "RFC")
    private String rfc;
    @Size(max = 18)
    @Column(name = "CURP")
    private String curp;
    @Size(max = 256)
    @Column(name = "REFERENCIA")
    private String referencia;
    @Size(max = 256)
    @Column(name = "NOTARIO")
    private String notario;
    @Size(max = 100)
    @Column(name = "PODERNOTARIAL")
    private String poderNotarial;
    @Size(max = 256)
    @Column(name = "NONOTARIA")
    private String noNotaria;
    @Column(name = "EMISION")
    @Temporal(TemporalType.DATE)
    private Date emision;
    @Size(max = 64)
    @Column(name = "IDTIPO")
    private String idTipo;
    @Column(name = "IDVIGENCIA")
    @Temporal(TemporalType.DATE)
    private Date idVigencia;
    @JoinColumn(name = "SI_LISTA_ELEMENTO", referencedColumnName = "ID")
    @ManyToOne
    private SiListaElemento siListaElemento;
    //
    @Column(name = "NOTIFICA")
    private boolean notifica;

    public ContactoProveedor() {
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ContactoProveedor)) {
            return false;
        }
        ContactoProveedor other = (ContactoProveedor) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
