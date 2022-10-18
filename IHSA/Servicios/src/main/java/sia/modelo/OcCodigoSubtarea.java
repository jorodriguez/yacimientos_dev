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

/**
 *
 * @author jcarranza
 */
@Entity
@Table(name = "OC_CODIGO_SUBTAREA")
@SequenceGenerator(sequenceName = "oc_codigo_subtarea_id_seq", name = "oc_codigo_subtarea_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "OcCodigoSubtarea.findAll", query = "SELECT u FROM OcCodigoSubtarea u")})
@Getter
@Setter
public class OcCodigoSubtarea implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "oc_codigo_subtarea_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 40)
    @Column(name = "CODIGO")
    private String codigo;
    @Size(max = 256)
    @Column(name = "NOMBRE")
    private String nombre;
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;

    public OcCodigoSubtarea() {    
    }
    
    public OcCodigoSubtarea(Integer id) {
        this.id = id;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof sia.modelo.OcCodigoTarea)) {
            return false;
        }
        sia.modelo.OcCodigoSubtarea other = (sia.modelo.OcCodigoSubtarea) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.OcCodigoTarea[ id=" + id + " ]";
    }
}