/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Entity
@Getter
@Setter
@Table(name = "AP_CAMPO")
@SequenceGenerator(sequenceName = "ap_campo_id_seq", name = "ap_campo_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "ApCampo.findAll", query = "SELECT a FROM ApCampo a")})
public class ApCampo implements Serializable {

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
    @OneToMany(mappedBy = "apCampo")
    private Collection<ApCampoGerencia> apCampoGerenciaCollection;
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)

    @Column(name = "ID")
    private Integer id;
    @Size(max = 100)
    @Column(name = "NOMBRE")
    private String nombre;
    @Size(max = 256)
    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @OneToMany(mappedBy = "apCampo")
    private Collection<Usuario> usuarioCollection;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "COMPANIA", referencedColumnName = "RFC")
    @ManyToOne
    private Compania compania;

    @Column(name = "CODIGO")
    private String codigo;
    @Size(max = 1)
    @Column(name = "TIPO")
    private String tipo;
    @Size(max = 8)
    @Column(name = "ALMACEN")
    private String almacen;

    @Size(max = 10)
    @Column(name = "CODEPROY")
    private String codeproy;
    
    @JoinColumn(name = "inv_almacen", referencedColumnName = "ID")
    @ManyToOne
    private InvAlmacen invAlmacen;
    
    @Column(name = "CARTA_INTENCION")
    private boolean cartaIntencion;
    
    @Column(name = "FOTO")
    private String foto;

    public ApCampo() {
    }

    public ApCampo(Integer id) {
        this.id = id;
    }


    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ApCampo)) {
            return false;
        }
        ApCampo other = (ApCampo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.ApCampo[ id=" + id + " ]";
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

}
