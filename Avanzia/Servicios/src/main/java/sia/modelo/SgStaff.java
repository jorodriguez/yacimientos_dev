/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author b75ckd35th
 */
@Entity
@Table(name = "SG_STAFF")
@SequenceGenerator(sequenceName = "sg_staff_id_seq", name = "sg_staff_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgStaff.findAll", query = "SELECT s FROM SgStaff s")})
public class SgStaff implements Serializable {

    @Id
@GeneratedValue(generator =  "sg_staff_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "NOMBRE")
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 8)
    @Column(name = "NUMERO_STAFF")
    private String numeroStaff;
    @Column(name = "NUMERO_CUARTOS")
    private Integer numeroCuartos;
    @Size(max = 15)
    @Column(name = "NUMERO_TELEFONO")
    private String numeroTelefono;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Basic(optional = false)
    @NotNull
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    @Basic(optional = false)
    @NotNull
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Usuario genero;
    @JoinColumn(name = "SG_DIRECCION", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SgDireccion sgDireccion;
    @JoinColumn(name = "SG_OFICINA", referencedColumnName = "ID")
    @ManyToOne
    private SgOficina sgOficina;

    public SgStaff() {
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the numeroStaff
     */
    public String getNumeroStaff() {
        return numeroStaff;
    }

    /**
     * @param numeroStaff the numeroStaff to set
     */
    public void setNumeroStaff(String numeroStaff) {
        this.numeroStaff = numeroStaff;
    }

    /**
     * @return the numeroCuartos
     */
    public Integer getNumeroCuartos() {
        return numeroCuartos;
    }

    /**
     * @param numeroCuartos the numeroCuartos to set
     */
    public void setNumeroCuartos(Integer numeroCuartos) {
        this.numeroCuartos = numeroCuartos;
    }

    /**
     * @return the numeroTelefono
     */
    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    /**
     * @param numeroTelefono the numeroTelefono to set
     */
    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    /**
     * @return the eliminado
     */
    public boolean isEliminado() {
        return eliminado;
    }

    /**
     * @param eliminado the eliminado to set
     */
    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    /**
     * @return the genero
     */
    public Usuario getGenero() {
        return genero;
    }

    /**
     * @param genero the genero to set
     */
    public void setGenero(Usuario genero) {
        this.genero = genero;
    }

    /**
     * @return the fechaGenero
     */
    public Date getFechaGenero() {
        return fechaGenero;
    }

    /**
     * @param fechaGenero the fechaGenero to set
     */
    public void setFechaGenero(Date fechaGenero) {
        this.fechaGenero = fechaGenero;
    }

    /**
     * @return the horaGenero
     */
    public Date getHoraGenero() {
        return horaGenero;
    }

    /**
     * @param horaGenero the horaGenero to set
     */
    public void setHoraGenero(Date horaGenero) {
        this.horaGenero = horaGenero;
    }

    /**
     * @return the sgDireccion
     */
    public SgDireccion getSgDireccion() {
        return sgDireccion;
    }

    /**
     * @param sgDireccion the sgDireccion to set
     */
    public void setSgDireccion(SgDireccion sgDireccion) {
        this.sgDireccion = sgDireccion;
    }

    /**
     * @return the sgOficina
     */
    public SgOficina getSgOficina() {
        return sgOficina;
    }

    /**
     * @param sgOficina the sgOficina to set
     */
    public void setSgOficina(SgOficina sgOficina) {
        this.sgOficina = sgOficina;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgStaff)) {
            return false;
        }
        SgStaff other = (SgStaff) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.SgStaff[ id=" + getId() + " ]";
    }
}
