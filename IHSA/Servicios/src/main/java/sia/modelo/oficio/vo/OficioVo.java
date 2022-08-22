
package sia.modelo.oficio.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import sia.constantes.Constantes;
import sia.modelo.sgl.vo.Vo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.util.UtilSia;

/**
 * Value Object base para el transporte de la informacion de oficios entre las 
 * capas de web y de negocio.
 * 
 * @author esapien
 * 
 */
public abstract class OficioVo extends Vo {
    
    private Integer oficioId;
    private String oficioNumero;
    
    private Date oficioFecha;
    private Date oficioFechaDesde;
    private Date oficioFechaHasta;
    private String oficioFechaDesdeString;
    private String oficioFechaHastaString;
    
    private Date fechaAltaDesde;
    private Date fechaAltaHasta;
    private String fechaAltaDesdeString;
    private String fechaAltaHastaString;
    
    private String oficioAsunto;
    //private String oficioDescripcion;
    private String observaciones;
    
    private Integer companiaId;
    private String companiaRfc;
    private String companiaNombre;
    private String companiaSiglas;
    
    private Integer bloqueId;
    private String bloqueNombre;
    
    private Integer gerenciaId;
    private String gerenciaNombre;
    
    private Integer tipoOficioId;
    private String tipoOficioNombre;
    
    private Integer estatusId;
    private String estatusNombre;
    
    private boolean requiereSeguimiento;
    
    private byte seguimientoConsulta;
    
    private String usuarioGeneroNombre;
    
    private AdjuntoOficioVo archivoAdjunto;
    
    private AdjuntoOficioVo archivoPromocion;
    
    private PrivacidadOficio acceso;
    
    private byte mostrarPublicos;
    private int maxOficios;
    
    /**
     * Para indicar que este oficio es importante. En este caso,
     * se envían las notificaciones de los movimientos a los usuarios por 
     * correo.
     */
    private boolean urgente;
    
    /**
     * Para utilizar en una consulta, ej. en búsqueda de oficios para asociación
     */
    private Integer oficioIdExcluir;
    
    /**
     * Bandera para indicar que se debe mostrar en pantalla el link de acceso
     * para el detalle del oficio, en funcion de los permisos del usuario.
     * 
     */
    //private boolean mostrarLinkDetalle;
    
    
    /**
     * Contiene la lista de oficios asociados en la que se encuentra este oficio.
     * 
     */
    //private List<OficioVo> oficiosAsociados;
    
    
    /**
     * Oficios hacia los que este oficio se encuentra asociado.
     * 
     */
    private List<OficioVo> asociadoHaciaOficios;
    
    /**
     * Oficios desde los que este oficio se encuentra asociado.
     * 
     */
    private List<OficioVo> asociadoDesdeOficios;
    
    /**
     * Contiene la lista de movimientos (cambios de estatus) de 
     * este oficio.
     * 
     */
    private List<MovimientoVo> movimientos;
    
    
    
    /**
     * Contiene la lista de los movimientos que tienen adjuntos.
     * 
     */
    private List<MovimientoVo> movimientosAdjuntos;
    
    
    /**
     * Contiene la lista de los movimientos que tienen adjuntos no eliminados.
     * 
     */
    private List<MovimientoVo> movimientosAdjuntosActivos;
    
    
    
    /**
     * Usuarios con acceso a este oficio. El oficio estará con acceso 
     * restringido (no público).
     * 
     */
    private List<UsuarioVO> restringidoAUsuarios;
    
    
    // <editor-fold defaultstate="collapsed" desc="Constructores">
    
    
    /**
     * 
     */
    public OficioVo() {
        
        this.companiaId = -1;
        
        this.archivoAdjunto = new AdjuntoOficioVo();
        this.archivoPromocion = new AdjuntoOficioVo();
        this.asociadoHaciaOficios = new ArrayList<OficioVo>();
        this.asociadoDesdeOficios = new ArrayList<OficioVo>();
        this.restringidoAUsuarios = new ArrayList<UsuarioVO>();
        
        this.acceso = PrivacidadOficio.PUBLICO;
        
    }
    
    
    // </editor-fold>
    
    
    
    // <editor-fold defaultstate="collapsed" desc="Métodos auxiliares">

    
    public String toString() {
        return "OficioVo{" + "oficioId=" + oficioId + 
                ", oficioNumero=" + oficioNumero + 
                /*", oficioFecha=" + oficioFecha + 
                ", oficioFechaDesde=" + oficioFechaDesde + 
                ", oficioFechaHasta=" + oficioFechaHasta + 
                ", oficioFechaDesdeString=" + oficioFechaDesdeString + 
                ", oficioFechaHastaString=" + oficioFechaHastaString + */
                ", oficioAsunto=" + oficioAsunto + 
                //", oficioDescripcion=" + oficioDescripcion + 
                ", companiaId=" + companiaId + 
                ", companiaRfc=" + companiaRfc + 
                ", companiaNombre=" + companiaNombre + 
                ", companiaSiglas=" + companiaSiglas + 
                ", bloqueId=" + bloqueId + 
                ", bloqueNombre=" + bloqueNombre + 
                ", gerenciaId=" + gerenciaId + 
                ", gerenciaNombre=" + gerenciaNombre + 
                ", tipoOficioId=" + tipoOficioId + 
                ", tipoOficioNombre=" + tipoOficioNombre + 
                ", estatusId=" + estatusId + 
                ", estatusNombre=" + estatusNombre + 
                ", usuarioGeneroNombre=" + usuarioGeneroNombre + 
                ", seguimientoConsulta=" + seguimientoConsulta + 
                ", acceso=" + acceso + 
                ", restringidoAUsuarios=" + getRestringidoAUsuariosIds() + 
                /*", archivoAdjunto=" + archivoAdjunto + 
                ", archivoPromocion=" + archivoPromocion + 
                ", oficioIdExcluir=" + oficioIdExcluir + 
                ", oficiosAsociados=" + oficiosAsociados + 
                ", movimientos=" + movimientos + 
                ", movimientosAdjuntos=" + movimientosAdjuntos + */
                '}';
    }
    
    
    
    
    public String getOficioFechaFormato() {
        return Constantes.FMT_ddMMyyy.format(oficioFecha);
        
    }
    
    public String getFechaGeneroFormato() {
        return Constantes.FMT_ddMMyyy.format(this.getFechaGenero());
        
    }
    

    /**
     * 
     * @return 
     */
    public String getOficioNumero() {
        
        // en caso que el registro esté eliminado, el numero de oficio
        // tendrá un sufijo "-<ID>". Remover para despliegue
        
        String resultado;
        
        if (this.isEliminado()) {
            resultado = this.oficioNumero.substring(
                    Constantes.CERO, 
                    this.oficioNumero.lastIndexOf(Constantes.GUION));
        } else {
            resultado = this.oficioNumero;
        }
        
        return resultado;
    }
    
    
    /**
     * Indica si este oficio y el parámetro pertenecen a la misma compañía,
     * bloque y gerencia.
     * 
     * @param vo
     * @return 
     */
    public boolean isMismaGerencia(OficioVo vo) {
        
        boolean mismoCompaniaId = 
                this.getCompaniaRfc() != null 
                && vo.getCompaniaRfc() != null 
                && this.getCompaniaRfc().equals(vo.getCompaniaRfc());
        boolean mismoBloqueId = 
                this.getBloqueId() != null 
                && vo.getBloqueId() != null 
                && this.getBloqueId().intValue() == vo.getBloqueId().intValue();
        boolean mismoGerenciaId = 
                this.getGerenciaId() != null 
                && vo.getGerenciaId() != null 
                && this.getGerenciaId().intValue() == vo.getGerenciaId().intValue();
        
        //System.out.println("num oficio = '" + vo.getOficioNumero() + "', mismoCompaniaId = '" + mismoCompaniaId + "', mismoBloqueId = '" + mismoBloqueId + "', mismoGerenciaId = '" + mismoGerenciaId + "'");
        //System.out.println("this compania RFC = '" + this.getCompaniaRfc() + "', vo compania RFC = '" + vo.getCompaniaRfc() + "'");
        
        return mismoCompaniaId && mismoBloqueId && mismoGerenciaId;
    }
    
    
    /**
     * 
     * @param usuarioId
     * @return 
     */
    public final boolean tieneAccesoOficioRestringido(String usuarioId) {
        
        boolean resultado;
        
        if (this.isRestringido()) {
            //revisa que sea restringido a ciertos usuarios
            resultado = this.getRestringidoAUsuariosIds().contains(usuarioId);
                    
        } else {
            
            resultado = true;
        }
        
        return resultado;
        
    }
    
    
    /**
     * 
     * @return 
     */
    /*public boolean isAsociado() {
        // la lista trae el oficio actual
        return this.oficiosAsociados.size() > 1;
    }*/
    
    // </editor-fold>
    
    
    
    
    // <editor-fold defaultstate="collapsed" desc="Getters / Setters">

    public void setOficioNumero(String oficioNumero) {
        this.oficioNumero = oficioNumero;
    }
    
    public AdjuntoOficioVo getArchivoAdjunto() {
        return archivoAdjunto;
    }

    public void setArchivoAdjunto(AdjuntoOficioVo archivoAdjunto) {
        this.archivoAdjunto = archivoAdjunto;
    }

    public AdjuntoOficioVo getArchivoPromocion() {
        return archivoPromocion;
    }

    public void setArchivoPromocion(AdjuntoOficioVo archivoPromocion) {
        this.archivoPromocion = archivoPromocion;
    }

    public Integer getBloqueId() {
        return bloqueId;
    }

    public void setBloqueId(Integer bloqueId) {
        this.bloqueId = bloqueId;
    }

    public String getBloqueNombre() {
        return bloqueNombre;
    }

    public void setBloqueNombre(String bloqueNombre) {
        this.bloqueNombre = bloqueNombre;
    }

    public Integer getCompaniaId() {
        return companiaId;
    }

    public void setCompaniaId(Integer companiaId) {
        this.companiaId = companiaId;
    }

    public String getCompaniaNombre() {
        return companiaNombre;
    }

    public void setCompaniaNombre(String companiaNombre) {
        this.companiaNombre = companiaNombre;
    }

    public String getCompaniaRfc() {
        return companiaRfc;
    }

    public void setCompaniaRfc(String companiaRfc) {
        this.companiaRfc = companiaRfc;
    }

    public String getCompaniaSiglas() {
        return companiaSiglas;
    }

    public void setCompaniaSiglas(String companiaSiglas) {
        this.companiaSiglas = companiaSiglas;
    }
    
    

    public Integer getEstatusId() {
        return estatusId;
    }

    public void setEstatusId(Integer estatusId) {
        this.estatusId = estatusId;
    }

    public String getEstatusNombre() {
        return estatusNombre;
    }

    public void setEstatusNombre(String estatusNombre) {
        this.estatusNombre = estatusNombre;
    }

    public boolean isUrgente() {
        return urgente;
    }

    public void setUrgente(boolean urgente) {
        this.urgente = urgente;
    }
    
    public Integer getGerenciaId() {
        return gerenciaId;
    }

    public void setGerenciaId(Integer gerenciaId) {
        this.gerenciaId = gerenciaId;
    }

    public String getGerenciaNombre() {
        return gerenciaNombre;
    }

    public void setGerenciaNombre(String gerenciaNombre) {
        this.gerenciaNombre = gerenciaNombre;
    }

    public List<MovimientoVo> getMovimientos() {
        return movimientos;
    }
    
    
    /**
     * 
     * @param id
     * @return 
     */
    public MovimientoVo getMovimiento(int id) {
        
        MovimientoVo movimientoVo = null;
        
        for (MovimientoVo mov : getMovimientos()) {
            
            if (mov.getId() == id) {
                movimientoVo = mov;
                break;
            }
        }
        
        return movimientoVo;
        
    }
    

    public List<MovimientoVo> getMovimientosAdjuntos() {
        return movimientosAdjuntos;
    }

    public List<MovimientoVo> getMovimientosAdjuntosActivos() {
        return movimientosAdjuntosActivos;
    }
    
    
    
    /**
     * 
     * @param movimientos 
     */
    public void setMovimientos(List<MovimientoVo> movimientos) {

        // lista de todos los movimientos

        this.movimientos = movimientos;

        // establecer lista de movimientos con adjuntos

        List<MovimientoVo> movAdjuntos = new ArrayList<MovimientoVo>();
        List<MovimientoVo> movAdjuntosActivos = new ArrayList<MovimientoVo>();

        for (MovimientoVo vo : movimientos) {
            if (vo.tieneAdjunto()) {
                movAdjuntos.add(vo);
                
                if (!vo.isAdjuntoEliminado()) {
                    movAdjuntosActivos.add(vo);
                }
            }
        }
        
        this.movimientosAdjuntos = movAdjuntos;
        this.movimientosAdjuntosActivos = movAdjuntosActivos;
        
    }

    public String getOficioAsunto() {
        return oficioAsunto;
    }

    public void setOficioAsunto(String oficioAsunto) {
        this.oficioAsunto = oficioAsunto;
    }

    /*public String getOficioDescripcion() {
        return oficioDescripcion;
    }

    public void setOficioDescripcion(String oficioDescripcion) {
        this.oficioDescripcion = oficioDescripcion;
    }*/

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    

    public Date getOficioFecha() {
        return oficioFecha;
    }

    public void setOficioFecha(Date oficioFecha) {
        this.oficioFecha = oficioFecha;
    }

    public Date getOficioFechaDesde() {
        return oficioFechaDesde;
    }

    public void setOficioFechaDesde(Date oficioFechaDesde) {
        this.oficioFechaDesde = oficioFechaDesde;
    }

    public String getOficioFechaDesdeString() {
        return oficioFechaDesdeString;
    }

    public void setOficioFechaDesdeString(String oficioFechaDesdeString) {
        this.oficioFechaDesdeString = oficioFechaDesdeString;
    }

    public Date getOficioFechaHasta() {
        return oficioFechaHasta;
    }

    public void setOficioFechaHasta(Date oficioFechaHasta) {
        this.oficioFechaHasta = oficioFechaHasta;
    }

    public String getOficioFechaHastaString() {
        return oficioFechaHastaString;
    }

    public void setOficioFechaHastaString(String oficioFechaHastaString) {
        this.oficioFechaHastaString = oficioFechaHastaString;
    }

    public Date getFechaAltaDesde() {
        return fechaAltaDesde;
    }

    public void setFechaAltaDesde(Date fechaAltaDesde) {
        this.fechaAltaDesde = fechaAltaDesde;
    }

    public String getFechaAltaDesdeString() {
        return fechaAltaDesdeString;
    }

    public void setFechaAltaDesdeString(String fechaAltaDesdeString) {
        this.fechaAltaDesdeString = fechaAltaDesdeString;
    }

    public Date getFechaAltaHasta() {
        return fechaAltaHasta;
    }

    public void setFechaAltaHasta(Date fechaAltaHasta) {
        this.fechaAltaHasta = fechaAltaHasta;
    }

    public String getFechaAltaHastaString() {
        return fechaAltaHastaString;
    }

    public void setFechaAltaHastaString(String fechaAltaHastaString) {
        this.fechaAltaHastaString = fechaAltaHastaString;
    }
    

    public Integer getOficioId() {
        return oficioId;
    }

    public void setOficioId(Integer oficioId) {
        this.oficioId = oficioId;
    }

    public Integer getOficioIdExcluir() {
        return oficioIdExcluir;
    }

    public void setOficioIdExcluir(Integer oficioIdExcluir) {
        this.oficioIdExcluir = oficioIdExcluir;
    }

    /*public List<OficioVo> getOficiosAsociados() {
        return oficiosAsociados;
    }

    public void setOficiosAsociados(List<OficioVo> oficiosAsociados) {
        this.oficiosAsociados = oficiosAsociados;
    }*/

    public String getUsuarioGeneroNombre() {
        return usuarioGeneroNombre;
    }

    public void setUsuarioGeneroNombre(String usuarioGeneroNombre) {
        this.usuarioGeneroNombre = usuarioGeneroNombre;
    }
    
    public Integer getTipoOficioId() {
        return tipoOficioId;
    }

    public void setTipoOficioId(Integer tipoOficioId) {
        this.tipoOficioId = tipoOficioId;
    }

    public String getTipoOficioNombre() {
        return tipoOficioNombre;
    }

    public void setTipoOficioNombre(String tipoOficioNombre) {
        this.tipoOficioNombre = tipoOficioNombre;
    }

    /*public boolean isMostrarLinkDetalle() {
        return mostrarLinkDetalle;
    }

    public void setMostrarLinkDetalle(boolean mostrarLinkDetalle) {
        this.mostrarLinkDetalle = mostrarLinkDetalle;
    }*/
    
    public boolean isRequiereSeguimiento() {
        return requiereSeguimiento;
    }

    public void setRequiereSeguimiento(boolean requiereSeguimiento) {
        this.requiereSeguimiento = requiereSeguimiento;
    }

    public byte getSeguimientoConsulta() {
        return seguimientoConsulta;
    }

    public void setSeguimientoConsulta(byte seguimientoConsulta) {
        this.seguimientoConsulta = seguimientoConsulta;
    }

    public List<OficioVo> getAsociadoDesdeOficios() {
        return asociadoDesdeOficios;
    }

    public void setAsociadoDesdeOficios(List<OficioVo> asociadoDesdeOficios) {
        this.asociadoDesdeOficios = asociadoDesdeOficios;
    }

    public List<OficioVo> getAsociadoHaciaOficios() {
        return asociadoHaciaOficios;
    }

    public void setAsociadoHaciaOficios(List<OficioVo> asociadoHaciaOficios) {
        this.asociadoHaciaOficios = asociadoHaciaOficios;
    }
    
    public final List<UsuarioVO> getRestringidoAUsuarios() {
        return restringidoAUsuarios;
    }
    
    /**
     * Método de conveniencia para retornar solo los IDS de la lista 
     * de usuarios a los que este oficio está restringido.
     * 
     * @return 
     */
    public final List<String> getRestringidoAUsuariosIds() {
        
        List<String> ids = new ArrayList<String>();

        for (UsuarioVO vo : restringidoAUsuarios) {

            ids.add(vo.getId());

        }
        
        return ids;
        
    }
    
    /**
     * Método alterno para establecer la lista de usuarios a los que este oficio
     * está restringido.
     * 
     * @param usuarioIds Cadena de ids de usuario separados por comas.
     */
    public final void setRestringidoAUsuariosIds(final String usuarioIds) {
        
        List<String> ids = UtilSia.toList(usuarioIds);
        
        List<UsuarioVO> usuarios = new ArrayList<UsuarioVO>();
        
        for (String usuarioId : ids) {
            usuarios.add(new UsuarioVO(usuarioId));
        }
        
        this.restringidoAUsuarios = usuarios;
        
    }
    

    /**
     * 
     * @param restringidoAUsuarios 
     */
    public final void setRestringidoAUsuarios(List<UsuarioVO> restringidoAUsuarios) {
        
        this.restringidoAUsuarios = restringidoAUsuarios;
        
    }
    
    public PrivacidadOficio getAcceso() {
        return acceso;
    }

    public void setAcceso(PrivacidadOficio acceso) {
        this.acceso = acceso;
    }
    
    public final boolean isRestringido() {
        return this.acceso.isRestringido();
    }
    
      public final boolean isPublico() {
        return this.acceso.isPublico();
    }
    
    /**
     * 
     * Regresa una lista con los ID de los oficios hacia los que este 
     * oficio está asociado.
     * 
     * @return 
     */
    public final List<Integer> getAsociadoHaciaOficiosListaIds() {
        
        List<Integer> ids = new ArrayList<Integer>();
        
        for (OficioVo vo : getAsociadoHaciaOficios()) {
            
            ids.add(vo.getOficioId());
            
        }
        
        return ids;
        
    }
    
    
    
    // </editor-fold>

    /**
     * 
     * @param obj
     * @return 
     */
    
    public final boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OficioVo other = (OficioVo) obj;
        if (this.oficioId != other.oficioId && (this.oficioId == null || !this.oficioId.equals(other.oficioId))) {
            return false;
        }
        return true;
    }

    
    public final int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.oficioId != null ? this.oficioId.hashCode() : 0);
        return hash;
    }

    /**
     * @return the mostrarPublicos
     */
    public byte getMostrarPublicos() {
        return mostrarPublicos;
    }

    /**
     * @param mostrarPublicos the mostrarPublicos to set
     */
    public void setMostrarPublicos(byte mostrarPublicos) {
        this.mostrarPublicos = mostrarPublicos;
    }

    /**
     * @return the maxOficios
     */
    public int getMaxOficios() {
        return maxOficios;
    }

    /**
     * @param maxOficios the maxOficios to set
     */
    public void setMaxOficios(int maxOficios) {
        this.maxOficios = maxOficios;
    }

}
