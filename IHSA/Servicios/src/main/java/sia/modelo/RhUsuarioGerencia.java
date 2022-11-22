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
import lombok.Data;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "RH_USUARIO_GERENCIA")
@SequenceGenerator(sequenceName = "rh_usuario_gerencia_id_seq", name = "rh_usuario_gerencia_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "RhUsuarioGerencia.findAll", query = "SELECT r FROM RhUsuarioGerencia r")})
@Data
public class RhUsuarioGerencia implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "rh_usuario_gerencia_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    
    @Column(name = "LIBERADO")
    private boolean liberado;
    @JoinColumn(name = "USUARIO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario usuario;
    @JoinColumn(name = "USUARIO_LIBERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario usuarioLibero;
    @Column(name = "FECHA_LIBERO")
    @Temporal(TemporalType.DATE)
    private Date fechaLibero;
    @Column(name = "HORA_LIBERO")
    @Temporal(TemporalType.TIME)
    private Date horaLibero;
    @JoinColumn(name = "RH_CAMPO_GERENCIA", referencedColumnName = "ID")
    @ManyToOne
    private RhCampoGerencia rhCampoGerencia;    
    @NotNull
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
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

    public RhUsuarioGerencia() {
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RhUsuarioGerencia)) {
            return false;
        }
        RhUsuarioGerencia other = (RhUsuarioGerencia) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
