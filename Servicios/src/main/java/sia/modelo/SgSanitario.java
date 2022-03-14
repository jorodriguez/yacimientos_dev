/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SG_SANITARIO")
@SequenceGenerator(sequenceName = "sg_sanitario_id_seq", name = "sg_sanitario_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgSanitario.findAll", query = "SELECT s FROM SgSanitario s")})
public class SgSanitario implements Serializable {
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sgOficnaSanitario")
    private Collection<SgCtrlMantenimientoSanitario> sgCtrlMantenimientoSanitarioCollection;
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_sanitario_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 128)
    @Column(name = "NOMBRE")
    private String nombre;
    @Column(name = "SEXO")
    private Character sexo;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sgSanitario")
    private Collection<SgCaracteristicaSanitario> sgCaracteristicaSanitarioCollection;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "SG_OFICINA", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SgOficina sgOficina;

    public SgSanitario() {
    }

    public SgSanitario(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Character getSexo() {
        return sexo;
    }

    public void setSexo(Character sexo) {
        this.sexo = sexo;
    }

    public Date getFechaGenero() {
        return fechaGenero;
    }

    public void setFechaGenero(Date fechaGenero) {
        this.fechaGenero = fechaGenero;
    }

    public Date getHoraGenero() {
        return horaGenero;
    }

    public void setHoraGenero(Date horaGenero) {
        this.horaGenero = horaGenero;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public Collection<SgCaracteristicaSanitario> getSgCaracteristicaSanitarioCollection() {
        return sgCaracteristicaSanitarioCollection;
    }

    public void setSgCaracteristicaSanitarioCollection(Collection<SgCaracteristicaSanitario> sgCaracteristicaSanitarioCollection) {
        this.sgCaracteristicaSanitarioCollection = sgCaracteristicaSanitarioCollection;
    }

    public Usuario getGenero() {
        return genero;
    }

    public void setGenero(Usuario genero) {
        this.genero = genero;
    }

    public SgOficina getSgOficina() {
        return sgOficina;
    }

    public void setSgOficina(SgOficina sgOficina) {
        this.sgOficina = sgOficina;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgSanitario)) {
            return false;
        }
        SgSanitario other = (SgSanitario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.SgSanitario[ id=" + id + " ]";
    }
    public Collection<SgCtrlMantenimientoSanitario> getSgCtrlMantenimientoSanitarioCollection() {
        return sgCtrlMantenimientoSanitarioCollection;
    }

    public void setSgCtrlMantenimientoSanitarioCollection(Collection<SgCtrlMantenimientoSanitario> sgCtrlMantenimientoSanitarioCollection) {
        this.sgCtrlMantenimientoSanitarioCollection = sgCtrlMantenimientoSanitarioCollection;
    }
}
