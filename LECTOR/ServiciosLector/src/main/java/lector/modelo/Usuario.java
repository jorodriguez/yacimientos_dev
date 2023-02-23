/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lector.modelo;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jorodriguez
 */
@Entity
@Getter
@Setter
@Table(name = "usuario")
@NamedQueries({
    @NamedQuery(name = "Usuario.findByCorreo", query = "SELECT u FROM Usuario u where u.email = ?1 and u.eliminado = false"),    
    @NamedQuery(name = "Usuario.findById", query = "SELECT u FROM Usuario u where u.id = ?1 and u.eliminado = false"),    
    @NamedQuery(name = "Usuario.findAll", query = "SELECT u FROM Usuario u where u.eliminado = false")
})
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;
    @Basic(optional = false)
    @Column(name = "email")
    private String email;
    @Basic(optional = false)
    @Column(name = "clave")
    private String clave;
    @Basic(optional = false)
    @Column(name = "telefono")
    private String telefono;
    @Basic(optional = false)
    @Column(name = "fecha_nacimiento")
    @Temporal(TemporalType.DATE)
    private Date fechaNacimiento;
    @Column(name = "domicilio")
    private String domicilio;
    @Column(name = "curp")
    private String curp;
    @Column(name = "foto")
    private String foto;
    @Column(name = "anio_registro")
    private String anioRegistro;
    @Column(name = "anio_emision")
    private Integer anioEmision;
    @Basic(optional = false)
    @Column(name = "estado")
    private int estado;
    @Basic(optional = false)
    @Column(name = "municipio")
    private String municipio;
    @Basic(optional = false)
    @Column(name = "seccion")
    private String seccion;
    @Basic(optional = false)
    @Column(name = "localidad")
    private String localidad;
    @Basic(optional = false)
    @Column(name = "emision")
    private int emision;
    @Basic(optional = false)
    @Column(name = "vigencia")
    private int vigencia;
    @Basic(optional = false)
    @Column(name = "sexo")
    private String sexo;
    @Basic(optional = false)
    @Column(name = "fecha_genero")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaGenero;
    @Column(name = "fecha_modifico")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModifico;
    @Column(name = "eliminado")
    private Boolean eliminado;
    @JoinColumn(name = "c_cuenta", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private CCuenta cCuenta;
    @JoinColumn(name = "c_tipo_contacto", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private CTipoContacto cTipoContacto;
    @JoinColumn(name = "si_adjunto", referencedColumnName = "id")
    @ManyToOne
    private SiAdjunto siAdjunto;
    @JoinColumn(name = "genero", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario genero;
    @JoinColumn(name = "modifico", referencedColumnName = "id")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "registro", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario registro;

    public Usuario() {
    }

    public Usuario(Integer id) {
        this.id = id;
    }

    public Usuario(Integer id, String nombre, String email, String clave, String telefono, Date fechaNacimiento, int estado, String municipio, String seccion, String localidad, int emision, int vigencia, String sexo, Date fechaGenero) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.clave = clave;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.estado = estado;
        this.municipio = municipio;
        this.seccion = seccion;
        this.localidad = localidad;
        this.emision = emision;
        this.vigencia = vigencia;
        this.sexo = sexo;
        this.fechaGenero = fechaGenero;
    }

  


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

     public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append(getClass().getSimpleName());
	sb.append("{");
	sb.append("id=").append(id);
	sb.append(", nombre").append(nombre != null ? nombre : null);	
	sb.append(", genero=").append(this.genero != null ? this.genero.getId() : null);
	sb.append(", modifico=").append(this.modifico != null ? this.modifico.getId() : null);
	sb.append(", eliminado=").append(this.eliminado);
	sb.append("}");

	return sb.toString();
    }
    
}
