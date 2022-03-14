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
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author jevazquez
 */
@Entity
@Table(name = "SG_UBICACION")
@SequenceGenerator(sequenceName = "sg_ubicacion_id_seq", name = "sg_ubicacion_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgUbicacion.findAll", query = "SELECT s FROM SgUbicacion s")})
@Setter
@Getter
public class SgUbicacion implements Serializable {

    private static final long serialVersionUID = 1L;
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

    @Id
@GeneratedValue(generator =  "sg_ubicacion_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @Size(max = 70)
    @Column(name = "NOMBRE")
    private String nombre;
    @Size(max = 1024)
    @Column(name = "DESCRIPCION")
    private String descripcion;

    
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgUbicacion)) {
            return false;
        }
        SgUbicacion other = (SgUbicacion) object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.SgUbicacionCercana[ id=" + id + " ]";
    }

}
