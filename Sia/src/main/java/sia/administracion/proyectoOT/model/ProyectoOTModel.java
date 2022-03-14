/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.proyectoOT.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import sia.modelo.ProyectoOt;
import sia.modelo.RelProyectoTipoObra;
import sia.modelo.TipoObra;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.proyectoOT.vo.ProyectoOtVo;
import sia.modelo.requisicion.vo.OcTareaVo;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.sgl.vo.Vo;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.ProyectoOtImpl;
import sia.servicios.catalogos.impl.TipoObraImpl;
import sia.servicios.requisicion.impl.OcTareaImpl;
import sia.servicios.requisicion.impl.RelGerenciaProyectoImpl;
import sia.servicios.requisicion.impl.RelProyectoTipoObraImpl;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.SoporteProveedor;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@ManagedBean
@SessionScoped
public class ProyectoOTModel implements Serializable{

    @EJB
    private ProyectoOtImpl proyectoOtServicioRemoto;
    @EJB
    private RelGerenciaProyectoImpl relGerenciaProyectoServicioRemoto;
    @EJB
    private GerenciaImpl gerenciaServicioRemoto;
    @EJB
    private RelProyectoTipoObraImpl relProyectoTipoObraServicioRemoto;
    @EJB
    private TipoObraImpl tipoObraServicioRemoto;
    @EJB
    private ApCampoImpl apCampoImpl;
    @EJB
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @EJB
    private OcTareaImpl ocTareaImpl;
    //
    @ManagedProperty(value = "#{soporteProveedor}")
    private SoporteProveedor soporteProveedor;
    @ManagedProperty(value = "#{sesion}")
    private Sesion sesion;
    private boolean proyectoAbierto = false;
    private List<SelectItem> listaProyectosOtItems;
    private int idApCampoActivo;
    private String nombreCampo;
    //
    private ProyectoOtVo proyectoOtVo;
    private TipoObra tipoObra;
    //private RelGerenciaProyectoVO relGerenciaProyectoVO;
    private OcTareaVo gerenciaTarea;
    //private RelProyectoTipoObraVO relProyectoTipoObraVO;
    private OcTareaVo unidadCostoTarea;
    private OcTareaVo tareaVo;
    private String cuentaContable;
    private DataModel lista;
    private DataModel listaG;
    private DataModel listaOrden;
    private boolean mostrar;
    private String nombreProyectoOT = "";
    private boolean mostrarPanelRelacion = false;
    private int gerencia;
    //Tipo de obra
    private boolean mostrarPanelTipoObra = false;
    private int tipoO;
    private boolean mostrarPanelRelTipoObra = false;
    private boolean panelBuscar = true;
    private String obra;
    private String nombreTipoObra;
    private DataModel listaTipoObra;
    private DataModel listaTipo;
    private boolean mostrarPop = false;
    private String cadena = "";

    /**
     * Creates a new instance of ProyectoOTModel
     */
    public ProyectoOTModel() {
    }

    public void inicio() {

        setProyectoOtVo(null);
        setListaG(null);
        setPanelBuscar(true);
        setCuentaContable("");
        setNombreProyectoOT("");
        setMostrar(false);
        setIdApCampoActivo(sesion.getUsuario().getApCampo().getId());
        lista = new ListDataModel(proyectoOtServicioRemoto.getListaProyectosOtPorCampo(getIdApCampoActivo(), sesion.getUsuario().getApCampo().getCompania().getRfc(), null, false));

    }

    public void llenarLista() {
        lista = new ListDataModel(proyectoOtServicioRemoto.getListaProyectosOtPorCampo(getIdApCampoActivo(), sesion.getUsuario().getApCampo().getCompania().getRfc(), null, false));
    }

    public List<SelectItem> getApCamposItems() {
        UtilLog4j.log.info(this, "getApCamposItems");
        List<SelectItem> l = new ArrayList<SelectItem>();
        try {
            for (CampoUsuarioPuestoVo apCampoVo : this.apCampoUsuarioRhPuestoImpl.getAllPorUsurio(sesion.getUsuario().getId())) {
                SelectItem item = new SelectItem(apCampoVo.getIdCampo(), apCampoVo.getCampo());
                l.add(item);
            }
            UtilLog4j.log.info(this, " size" + l.size());
            return l;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al traer la lista de campos " + e.getMessage());
            return null;
        }
    }

    public void mostrarNuevoProyecto() {
        this.proyectoOtVo = null;
        this.mostrar = true;
        //    this.listaG = null;
        this.panelBuscar = false;
        this.cuentaContable = "";
        this.nombreProyectoOT = "";
    }

    public List<ProyectoOtVo> buscarProyectoOT(String cuenta) {
        return this.proyectoOtServicioRemoto.getPorCuentaContable(cuenta, getIdApCampoActivo());

    }

    public List<OrdenVO> traerDetalleProyectoOt(Integer idProyectoOt) {
        UtilLog4j.log.info(this, "Id proyecto ot " + idProyectoOt);
        return this.proyectoOtServicioRemoto.getDetalleProyectoOt(idProyectoOt);
    }

    public void traerNombreCampoSeleccionado() {
        nombreCampo = apCampoImpl.find(idApCampoActivo).getNombre();
    }

    /*
     * Busca un objeto proyectoOT deacuerdo a una cuenta contable Retorna un
     * Objeto;
     */
    public ProyectoOt buscarProyectoOT(Integer idProyecto) {
        try {
            return proyectoOtServicioRemoto.find(idProyecto);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al buscar proyecto ot por id" + e.getMessage());
            return null;
        }
    }

    public boolean elminarProyectoOT() {
        List<ProyectoOtVo> lp = new ArrayList<ProyectoOtVo>();
        lp.add(proyectoOtVo);
        return this.proyectoOtServicioRemoto.eliminarProyectoOt(lp, sesion.getUsuario().getId());
    }

    public boolean validarEliminacionProyectoOt() {
        return this.proyectoOtServicioRemoto.buscarExistenciaRelacionConGerenciaYTipoObra(getProyectoOtVo().getId());
    }

    public boolean cerrarProyectoOT() {
        List<ProyectoOtVo> lp = new ArrayList<ProyectoOtVo>();
        lp.add(proyectoOtVo);
        return this.proyectoOtServicioRemoto.cerrarProyectoOt(lp, sesion.getUsuario().getId());
    }

    public boolean abrirProyectoOT() {
        return this.proyectoOtServicioRemoto.abrirProyectoOt(getProyectoOtVo().getId(), sesion.getUsuario().getId());

    }

    public void guardarProyectoOT() {
        this.proyectoOtServicioRemoto.guardarProyectoOT(getNombreProyectoOT(), getCuentaContable(), getIdApCampoActivo(), sesion.getUsuario().getId());
    }

    public List<ProyectoOtVo> traerProyectoOTPorCadena(String cad) {
        setCadena(cad);
        return proyectoOtServicioRemoto.traerProyectoOTPorCadena(getCadena(), getIdApCampoActivo());
    }

    public List<OcTareaVo> traerRelacionProyectoGerencia() {
        return ocTareaImpl.traerGerenciaPorProyectoOT(getProyectoOtVo().getId());
    }

    public List<GerenciaVo> traerGerencia() {
        //return this.gerenciaServicioRemoto.getAllGerenciaByApCampo(sesion.getUsuario().getIdCampo(), "nombre", true, true, false);
        return this.gerenciaServicioRemoto.getAllGerenciaByApCampo(getIdApCampoActivo(), "nombre", true, true, false);
    }

    public void guardarRelacionGerencia() {
        this.relGerenciaProyectoServicioRemoto.guardarRelacionGerencia(getProyectoOtVo().getId(), getGerencia(), this.sesion.getUsuario().getId());
    }

    public boolean buscarRelacionProyectoOtGerencia() {
        return this.relGerenciaProyectoServicioRemoto.buscarProyectoOtGerenciaRepetidos(getProyectoOtVo().getId(), getGerencia());
    }

    public List<OcTareaVo> traerRelacionProyectoUnidadCosto() {
        int idActividadPetrolera = 0;
        return ocTareaImpl.traerUnidadCostoPorGerenciaProyectoOT(getGerenciaTarea().getIdGerencia(), getProyectoOtVo().getId(), idActividadPetrolera);
    }

    public List<OcTareaVo> traerTareas() {
        return ocTareaImpl.traerTarea(getGerenciaTarea().getIdGerencia(), getProyectoOtVo().getId(), getUnidadCostoTarea().getIdUnidadCosto());
    }

    public boolean buscarRelacionProyectoTipoObra() {
        return this.relProyectoTipoObraServicioRemoto.buscarProyectoOtTipoObreRepetidos(getProyectoOtVo().getId(), getTipoO());
    }

    public List<Vo> traerTipoObraVo() {
        return this.tipoObraServicioRemoto.traerTipoObraActiva();
    }

    public void guardarRelacionTipoObra() {
        this.relProyectoTipoObraServicioRemoto.guardarRelProyectoTipoObra(getProyectoOtVo().getId(), getTipoO(), sesion.getUsuario().getId());
    }

    public TipoObra buscarTipoObra() {
        return this.tipoObraServicioRemoto.buscarPorNombre(getNombreTipoObra());
    }

    public RelProyectoTipoObra findRelProyectoOtTipoObra(Integer idRelProyectoOtTipoObra) {
        return this.relProyectoTipoObraServicioRemoto.find(idRelProyectoOtTipoObra);
    }

    public List<TipoObra> traerTipoObra() {
        return this.tipoObraServicioRemoto.traerTipoObraPorNombre(getNombreTipoObra());
    }

    public void guardarTipoObra() {
        this.tipoObraServicioRemoto.guardarTipoObra(getNombreTipoObra());
    }

    public void actualizarTipoObra(TipoObra tObra) {
        this.tipoObraServicioRemoto.edit(tObra);
    }

    public void eliminarRelacionGerencia() {
        ocTareaImpl.eliminarRelacionGerenciaProyectoOt(getProyectoOtVo().getId(), getGerenciaTarea().getIdGerencia(), sesion.getUsuario().getId());
    }

    public boolean eliminarRelacionUnidadCosto() {
        //HAcer que se eliminen logicamente...
        return ocTareaImpl.eliminarRelacionUnidadCosto(getProyectoOtVo().getId(), getGerenciaTarea().getIdGerencia(), getUnidadCostoTarea().getIdUnidadCosto(), sesion.getUsuario().getId());
    }
    
    public boolean eliminarTarea() {
        return ocTareaImpl.eliminarTarea(getTareaVo().getIdTarea(), sesion.getUsuario().getId());
    }

    public void completarModificacion() {
        this.proyectoOtServicioRemoto.modificarProyectoOt(getProyectoOtVo().getId(), sesion.getUsuario().getId());
    }

    // AUTOCOMPLETe 
    public List<SelectItem> cargarListaProyectosOtItems(String cadena) {
        UtilLog4j.log.info(this, "cargarListaProyectosOtItems " + cadena + "   " + idApCampoActivo);
        try {
            setListaProyectosOtItems(soporteProveedor.regresaProyectosOtCompletados(cadena, idApCampoActivo, sesion.getUsuario().getApCampo().getCompania().getRfc()));
            UtilLog4j.log.info(this, " " + getListaProyectosOtItems().size());
            return this.getListaProyectosOtItems();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al traer items de proyectos ot " + e.getMessage());
            return null;
        }
    }

    /**
     * @return the proyectoAbierto
     */
    public boolean isProyectoAbierto() {
        return proyectoAbierto;
    }

    /**
     * @param proyectoAbierto the proyectoAbierto to set
     */
    public void setProyectoAbierto(boolean proyectoAbierto) {
        this.proyectoAbierto = proyectoAbierto;
    }

    /**
     * @return the listaProyectosOtItems
     */
    public List<SelectItem> getListaProyectosOtItems() {
        return listaProyectosOtItems;
    }

    /**
     * @param listaProyectosOtItems the listaProyectosOtItems to set
     */
    public void setListaProyectosOtItems(List<SelectItem> listaProyectosOtItems) {
        this.listaProyectosOtItems = listaProyectosOtItems;
    }

    /**
     * @param soporteProveedor the soporteProveedor to set
     */
    public void setSoporteProveedor(SoporteProveedor soporteProveedor) {
        this.soporteProveedor = soporteProveedor;
    }

    /**
     * @return the idApCampoActivo
     */
    public int getIdApCampoActivo() {
        return idApCampoActivo;
    }

    /**
     * @param idApCampoActivo the idApCampoActivo to set
     */
    public void setIdApCampoActivo(int idApCampoActivo) {
        this.idApCampoActivo = idApCampoActivo;
    }

    /**
     * @return the nombreCampo
     */
    public String getNombreCampo() {
        return nombreCampo;
    }

    /**
     * @param nombreCampo the nombreCampo to set
     */
    public void setNombreCampo(String nombreCampo) {
        this.nombreCampo = nombreCampo;
    }

    /**
     * @return the tipoObra
     */
    public TipoObra getTipoObra() {
        return tipoObra;
    }

    /**
     * @param tipoObra the tipoObra to set
     */
    public void setTipoObra(TipoObra tipoObra) {
        this.tipoObra = tipoObra;
    }

    /**
     * @return the cuentaContable
     */
    public String getCuentaContable() {
        return cuentaContable;
    }

    /**
     * @param cuentaContable the cuentaContable to set
     */
    public void setCuentaContable(String cuentaContable) {
        this.cuentaContable = cuentaContable;
    }

    /**
     * @return the lista
     */
    public DataModel getLista() {
        return lista;
    }

    /**
     * @param lista the lista to set
     */
    public void setLista(DataModel lista) {
        this.lista = lista;
    }

    /**
     * @return the listaG
     */
    public DataModel getListaG() {
        return listaG;
    }

    /**
     * @param listaG the listaG to set
     */
    public void setListaG(DataModel listaG) {
        this.listaG = listaG;
    }

    /**
     * @return the listaOrden
     */
    public DataModel getListaOrden() {
        return listaOrden;
    }

    /**
     * @param listaOrden the listaOrden to set
     */
    public void setListaOrden(DataModel listaOrden) {
        this.listaOrden = listaOrden;
    }

    /**
     * @return the mostrar
     */
    public boolean isMostrar() {
        return mostrar;
    }

    /**
     * @param mostrar the mostrar to set
     */
    public void setMostrar(boolean mostrar) {
        this.mostrar = mostrar;
    }

    /**
     * @return the nombreProyectoOT
     */
    public String getNombreProyectoOT() {
        return nombreProyectoOT;
    }

    /**
     * @param nombreProyectoOT the nombreProyectoOT to set
     */
    public void setNombreProyectoOT(String nombreProyectoOT) {
        this.nombreProyectoOT = nombreProyectoOT;
    }

    /**
     * @return the mostrarPanelRelacion
     */
    public boolean isMostrarPanelRelacion() {
        return mostrarPanelRelacion;
    }

    /**
     * @param mostrarPanelRelacion the mostrarPanelRelacion to set
     */
    public void setMostrarPanelRelacion(boolean mostrarPanelRelacion) {
        this.mostrarPanelRelacion = mostrarPanelRelacion;
    }

    /**
     * @return the gerencia
     */
    public int getGerencia() {
        return gerencia;
    }

    /**
     * @param gerencia the gerencia to set
     */
    public void setGerencia(int gerencia) {
        this.gerencia = gerencia;
    }

    /**
     * @return the mostrarPanelTipoObra
     */
    public boolean isMostrarPanelTipoObra() {
        return mostrarPanelTipoObra;
    }

    /**
     * @param mostrarPanelTipoObra the mostrarPanelTipoObra to set
     */
    public void setMostrarPanelTipoObra(boolean mostrarPanelTipoObra) {
        this.mostrarPanelTipoObra = mostrarPanelTipoObra;
    }

    /**
     * @return the tipoO
     */
    public int getTipoO() {
        return tipoO;
    }

    /**
     * @param tipoO the tipoO to set
     */
    public void setTipoO(int tipoO) {
        this.tipoO = tipoO;
    }

    /**
     * @return the mostrarPanelRelTipoObra
     */
    public boolean isMostrarPanelRelTipoObra() {
        return mostrarPanelRelTipoObra;
    }

    /**
     * @param mostrarPanelRelTipoObra the mostrarPanelRelTipoObra to set
     */
    public void setMostrarPanelRelTipoObra(boolean mostrarPanelRelTipoObra) {
        this.mostrarPanelRelTipoObra = mostrarPanelRelTipoObra;
    }

    /**
     * @return the panelBuscar
     */
    public boolean isPanelBuscar() {
        return panelBuscar;
    }

    /**
     * @param panelBuscar the panelBuscar to set
     */
    public void setPanelBuscar(boolean panelBuscar) {
        this.panelBuscar = panelBuscar;
    }

    /**
     * @return the obra
     */
    public String getObra() {
        return obra;
    }

    /**
     * @param obra the obra to set
     */
    public void setObra(String obra) {
        this.obra = obra;
    }

    /**
     * @return the nombreTipoObra
     */
    public String getNombreTipoObra() {
        return nombreTipoObra;
    }

    /**
     * @param nombreTipoObra the nombreTipoObra to set
     */
    public void setNombreTipoObra(String nombreTipoObra) {
        this.nombreTipoObra = nombreTipoObra;
    }

    /**
     * @return the listaTipoObra
     */
    public DataModel getListaTipoObra() {
        return listaTipoObra;
    }

    /**
     * @param listaTipoObra the listaTipoObra to set
     */
    public void setListaTipoObra(DataModel listaTipoObra) {
        this.listaTipoObra = listaTipoObra;
    }

    /**
     * @return the listaTipo
     */
    public DataModel getListaTipo() {
        return listaTipo;
    }

    /**
     * @param listaTipo the listaTipo to set
     */
    public void setListaTipo(DataModel listaTipo) {
        this.listaTipo = listaTipo;
    }

    /**
     * @return the mostrarPop
     */
    public boolean isMostrarPop() {
        return mostrarPop;
    }

    /**
     * @param mostrarPop the mostrarPop to set
     */
    public void setMostrarPop(boolean mostrarPop) {
        this.mostrarPop = mostrarPop;
    }

    /**
     * @return the proyectoOtVo
     */
    public ProyectoOtVo getProyectoOtVo() {
        return proyectoOtVo;
    }

    /**
     * @param proyectoOtVo the proyectoOtVo to set
     */
    public void setProyectoOtVo(ProyectoOtVo proyectoOtVo) {
        this.proyectoOtVo = proyectoOtVo;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    /**
     * @return the cadena
     */
    public String getCadena() {
        return cadena;
    }

    /**
     * @param cadena the cadena to set
     */
    public void setCadena(String cadena) {
        this.cadena = cadena;
    }

    /**
     * @return the gerenciaTarea
     */
    public OcTareaVo getGerenciaTarea() {
        return gerenciaTarea;
    }

    /**
     * @param gerenciaTarea the gerenciaTarea to set
     */
    public void setGerenciaTarea(OcTareaVo gerenciaTarea) {
        this.gerenciaTarea = gerenciaTarea;
    }

    /**
     * @return the unidadCostoTarea
     */
    public OcTareaVo getUnidadCostoTarea() {
        return unidadCostoTarea;
    }

    /**
     * @param unidadCostoTarea the unidadCostoTarea to set
     */
    public void setUnidadCostoTarea(OcTareaVo unidadCostoTarea) {
        this.unidadCostoTarea = unidadCostoTarea;
    }

    /**
     * @return the tareaVo
     */
    public OcTareaVo getTareaVo() {
        return tareaVo;
    }

    /**
     * @param tareaVo the tareaVo to set
     */
    public void setTareaVo(OcTareaVo tareaVo) {
        this.tareaVo = tareaVo;
    }
}
