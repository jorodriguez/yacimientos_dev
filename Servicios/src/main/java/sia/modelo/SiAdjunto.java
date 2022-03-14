package sia.modelo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import sia.constantes.Constantes;

/**
 *
 * @author sluis
 */
@Entity
@Table(name = "SI_ADJUNTO")
@SequenceGenerator(sequenceName = "si_adjunto_id_seq", name = "si_adjunto_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SiAdjunto.findAll", query = "SELECT s FROM SiAdjunto s")})
public class SiAdjunto implements Serializable {

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

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "si_adjunto_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Column(name = "ID_ELEMENTO")
    private Integer idElemento;
    @Size(max = 15)
    @Column(name = "TIPO_ELEMENTO")
    private String tipoElemento;
    @Size(max = 1000)
    @Column(name = "URL")
    private String url;
    @Size(max = 200)
    @Column(name = "NOMBRE")
    private String nombre;
    @Size(max = 250)
    @Column(name = "DESCRIPCION")
    private String descripcion;
    @Size(max = 75)
    @Column(name = "TIPO_ARCHIVO")
    private String tipoArchivo;
    @Size(max = 10)
    @Column(name = "PESO")
    private String peso;
    @JoinColumn(name = "SI_MODULO", referencedColumnName = "ID")
    @ManyToOne
    private SiModulo siModulo;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @Size(max = 64)
    @Column(name = "UUID")
    private String uuid;

    public SiAdjunto() {
    }

    public SiAdjunto(Integer id) {
        this.id = id;
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
     * @return the idElemento
     */
    public Integer getIdElemento() {
        return idElemento;
    }

    /**
     * @param idElemento the idElemento to set
     */
    public void setIdElemento(Integer idElemento) {
        this.idElemento = idElemento;
    }

    /**
     * @return the tipoElemento
     */
    public String getTipoElemento() {
        return tipoElemento;
    }

    /**
     * @param tipoElemento the tipoElemento to set
     */
    public void setTipoElemento(String tipoElemento) {
        this.tipoElemento = tipoElemento;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
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
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the tipoArchivo
     */
    public String getTipoArchivo() {
        return tipoArchivo;
    }

    /**
     * @param tipoArchivo the tipoArchivo to set
     */
    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }

    /**
     * @return the peso
     */
    public String getPeso() {
        return peso;
    }

    /**
     * @param peso the peso to set
     */
    public void setPeso(String peso) {
        this.peso = peso;
    }

    /**
     *
     * @param peso
     */
    public void setPeso(long peso) {

        this.peso = getSizeFormatted(peso);

    }

    /**
     * @return the siModulo
     */
    public SiModulo getSiModulo() {
        return siModulo;
    }

    /**
     * @param siModulo the siModulo to set
     */
    public void setSiModulo(SiModulo siModulo) {
        this.siModulo = siModulo;
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
     * @return the modifico
     */
    public Usuario getModifico() {
        return modifico;
    }

    /**
     * @param modifico the modifico to set
     */
    public void setModifico(Usuario modifico) {
        this.modifico = modifico;
    }

    /**
     * @return the fechaModifico
     */
    public Date getFechaModifico() {
        return fechaModifico;
    }

    /**
     * @param fechaModifico the fechaModifico to set
     */
    public void setFechaModifico(Date fechaModifico) {
        this.fechaModifico = fechaModifico;
    }

    /**
     * @return the horaModifico
     */
    public Date getHoraModifico() {
        return horaModifico;
    }

    /**
     * @param horaModifico the horaModifico to set
     */
    public void setHoraModifico(Date horaModifico) {
        this.horaModifico = horaModifico;
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
     *
     * @param size
     * @return
     */
    private String getSizeFormatted(long size) {
        if (size >= Constantes.MEGABYTE_LENGTH_BYTES) {
            return size / Constantes.MEGABYTE_LENGTH_BYTES + " MB";
        } else if (size >= Constantes.KILOBYTE_LENGTH_BYTES) {
            return size / Constantes.KILOBYTE_LENGTH_BYTES + " KB";
        } else if (size == 0) {
            return "0";
        } else if (size < Constantes.KILOBYTE_LENGTH_BYTES) {
            return size + " B";
        }

        return Long.toString(size);
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SiAdjunto)) {
            return false;
        }
        SiAdjunto other = (SiAdjunto) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        SimpleDateFormat sdfd = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdft = new SimpleDateFormat("HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName())
                .append("{")
                .append("id=").append(this.id)
                .append(", idElemento=").append(this.idElemento)
                .append(", tipoElemento=").append(this.tipoElemento)
                .append(", tipoArchivo=").append(this.tipoArchivo)
                .append(", nombre=").append(this.nombre)
                .append(", peso=").append(this.peso)
                .append(", url=").append(this.url)
                .append(", descripcion=").append(this.descripcion)
                .append(", siModulo=").append(this.siModulo != null ? this.siModulo.getId() : null)
                .append(", genero=").append(this.genero != null ? this.genero.getId() : null)
                .append(", fechaGenero=").append(this.fechaGenero != null ? (sdfd.format(this.fechaGenero)) : null)
                .append(", horaGenero=").append(this.horaGenero != null ? (sdft.format(this.horaGenero)) : null)
                .append(", modifico=").append(this.modifico != null ? this.modifico.getId() : null)
                .append(", fechaModifico=").append(this.fechaModifico != null ? (sdfd.format(this.fechaModifico)) : null)
                .append(", horaModifico=").append(this.horaModifico != null ? (sdft.format(this.horaModifico)) : null)
                .append(", eliminado=").append(this.eliminado)
                .append("}");

        return sb.toString();
    }

    /**
     * @return the UUID
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param UUID the UUID to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNombreSinUUID() {
        String nombre = null;
        String extension = null;
        String nombreREal = "";
        // validar el nombre de archivo contenga un punto y que no sea
        // el último caracter
        if (this.getNombre() != null
                && this.getNombre().trim().length() > 1
                && this.getNombre().contains(Constantes.PUNTO)
                && this.getNombre().contains("UUID")
                && this.getNombre().lastIndexOf(Constantes.PUNTO) != 0
                && this.getNombre().lastIndexOf(Constantes.PUNTO) != this.getNombre().length() - 1) {

            nombre = this.getNombre().substring(0, this.getNombre().indexOf("UUID"));
            extension = this.getNombre().substring(this.getNombre().lastIndexOf(Constantes.PUNTO)).toLowerCase();
            nombreREal = new StringBuilder().append(nombre).append(extension).toString();
        } else if (this.getNombre() != null) {
            nombreREal = this.getNombre();
        }
        return nombreREal;
    }
}
