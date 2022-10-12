package sia.sgl.catalogo.bean.model;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;

import sia.constantes.Constantes;
import sia.excepciones.ExistingItemException;
import sia.excepciones.ItemUsedBySystemException;
import sia.excepciones.SIAException;
import sia.modelo.SgAerolinea;
import sia.modelo.SgCaracteristica;
import sia.modelo.SgEmpresa;
import sia.modelo.SgInvitado;
import sia.modelo.SgLugar;
import sia.modelo.SgMarca;
import sia.modelo.SgModelo;
import sia.modelo.SgMotivo;
import sia.modelo.SgOficina;
import sia.modelo.SgPagoServicio;
import sia.modelo.SgTipo;
import sia.modelo.SgTipoEspecifico;
import sia.modelo.SgTipoTipoEspecifico;
import sia.modelo.SiCondicion;
import sia.modelo.SiOperacion;
import sia.modelo.SiPais;
import sia.modelo.Usuario;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.rol.vo.RolVO;
import sia.modelo.sgl.oficina.vo.OficinaVO;
import sia.modelo.sgl.semaforo.vo.SemaforoVo;
import sia.modelo.sgl.viaje.vo.InvitadoVO;
import sia.modelo.sgl.viaje.vo.RolTipoSolicitudVo;
import sia.modelo.sgl.viaje.vo.RutaTerrestreVo;
import sia.modelo.sgl.viaje.vo.TipoSolicitudViajeVO;
import sia.modelo.sgl.viaje.vo.UsuarioRolGerenciaVo;
import sia.modelo.sgl.vo.SgDetalleRutaTerrestreVo;
import sia.modelo.usuario.vo.UsuarioTipoVo;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.huesped.impl.SiUsuarioTipoImpl;
import sia.servicios.sgl.impl.SgCaracteristicaImpl;
import sia.servicios.sgl.impl.SgEmpresaImpl;
import sia.servicios.sgl.impl.SgInvitadoImpl;
import sia.servicios.sgl.impl.SgMarcaImpl;
import sia.servicios.sgl.impl.SgModeloImpl;
import sia.servicios.sgl.impl.SgMotivoImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sgl.impl.SgPagoServicioImpl;
import sia.servicios.sgl.impl.SgSolicitudEstanciaImpl;
import sia.servicios.sgl.impl.SgTipoEspecificoImpl;
import sia.servicios.sgl.impl.SgTipoImpl;
import sia.servicios.sgl.impl.SgTipoSolicitudViajeImpl;
import sia.servicios.sgl.impl.SgTipoTipoEspecificoImpl;
import sia.servicios.sgl.semaforo.impl.SgEstadoSemaforoImpl;
import sia.servicios.sgl.semaforo.impl.SgSemaforoImpl;
import sia.servicios.sgl.vehiculo.impl.SiOperacionImpl;
import sia.servicios.sgl.viaje.impl.SgAerolineaImpl;
import sia.servicios.sgl.viaje.impl.SgDetalleRutaCiudadImpl;
import sia.servicios.sgl.viaje.impl.SgDetalleRutaTerrestreImpl;
import sia.servicios.sgl.viaje.impl.SgLugarImpl;
import sia.servicios.sgl.viaje.impl.SgRolTipoSolicitudViajeImpl;
import sia.servicios.sgl.viaje.impl.SgRutaTerrestreImpl;
import sia.servicios.sgl.viaje.impl.SgUsuarioRolGerenciaImpl;
import sia.servicios.sgl.viaje.impl.SgViajeImpl;
import sia.servicios.sistema.impl.SiCiudadImpl;
import sia.servicios.sistema.impl.SiCondicionImpl;
import sia.servicios.sistema.impl.SiRolImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.SoporteProveedor;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 *//*
 */

@Named(value = "catalogoBeanModel")
@ViewScoped
public class CatalogoBeanModel implements Serializable {

    @Inject
    private Sesion sesion;
    @ManagedProperty(value = "#{soporteProveedor}")
    private SoporteProveedor soporteProveedor;
    //Servicios
    @Inject
    private SgTipoImpl tipoService;
    @Inject
    private SgTipoTipoEspecificoImpl sgTipoTipoEspecificoImpl;
    @Inject
    private SgTipoEspecificoImpl tipoEspecificoService;
    @Inject
    private SgCaracteristicaImpl caracteristicaService;
    @Inject
    private SgPagoServicioImpl sgPagoServicioImpl;
    @Inject
    private SgModeloImpl modeloService;
    @Inject
    private SgMarcaImpl marcaService;
    @Inject
    private SgMotivoImpl sgMotivoImpl;
    @Inject
    private SgSolicitudEstanciaImpl sgSolicitudEstanciaImpl;
    @Inject
    private SiOperacionImpl siOperacionImpl;
    @Inject
    private SiCondicionImpl siCondicionImpl;
    @Inject
    private SgAerolineaImpl aerolineaService;
    @Inject
    private SgRutaTerrestreImpl sgRutaTerrestreImpl;
    @Inject
    private SgDetalleRutaTerrestreImpl sgDetalleRutaTerrestreImpl;
    @Inject
    private SgViajeImpl sgViajeImpl;
    @Inject
    private SgOficinaImpl sgOficinaImpl;
    @Inject
    private SgEmpresaImpl sgEmpresaImpl;
    @Inject
    private SgInvitadoImpl sgInvitadoImpl;
    @Inject
    private SgLugarImpl sgLugarImpl;
    @Inject
    private SiUsuarioTipoImpl siUsuarioTipoImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private SgDetalleRutaCiudadImpl sgDetalleRutaCiudadImpl;
    @Inject
    private SiCiudadImpl siCiudadImpl;
    @Inject
    private SgRolTipoSolicitudViajeImpl sgRolTipoSolicitudViajeImpl;
    @Inject
    private SiRolImpl siRolImpl;
    @Inject
    private SgTipoSolicitudViajeImpl sgTipoSolicitudViajeImpl;
    @Inject
    private SgUsuarioRolGerenciaImpl sgUsuarioRolGerenciaImpl;
    @Inject
    private GerenciaImpl gerenciaImpl;
    @Inject
    private SgSemaforoImpl sgSemaforoImpl;
    //
    @Inject
    private SgEstadoSemaforoImpl sgEstadoSemaforoImpl;
    //Entidades
    private SgTipo tipo;
    private SgTipoEspecifico sgTipoEspecifico;
    private SgTipoTipoEspecifico sgTipoTipoEspecifico;
    private SgCaracteristica caracteristica;
    private SgModelo modelo;
    private SgMarca marca;
    private SgMotivo sgMotivo;
    private SiPais siPais;
    private SgEmpresa sgEmpresa;
    private InvitadoVO invitadoVo;
    private SiOperacion siOperacion;
    private SiCondicion siCondicion;
    private SgAerolinea aerolinea;
    private SgOficina sgOficina;
    private SgLugar sgLugar;
    //VO
    private UsuarioTipoVo usuarioTipoVo;
    private OficinaVO oficinaVO;
    private RutaTerrestreVo rutaTerrestreVo;
    private SgDetalleRutaTerrestreVo sgDetalleRutaTerrestreVo;
    private RolTipoSolicitudVo rolTipoSolicitudVo;
    private UsuarioRolGerenciaVo usuarioRolGerenciaVo;
    private SemaforoVo semaforoVo;
    private SemaforoVo semaforoVoActual;
    //Clases
    private String cadena = ""; //Se ocupó en motivo, pais, ruta y en Invitado // y usuario copiado
    private String mensaje = "";
    private String nombreTipo = "";  //Tambien utilizado para nombre de la oficina
    private String descripcionTipo = "";
    private String nombreCaracteristica = "";
    private String opcionDestino;
    private String user;
//    private Date horaMinima;
//    private Date horaMaxima;
    //Colecciones
    private DataModel dataModelGeneric; //Ocúpalo si solo ocupas un solo dataModel ;)
    private DataModel listaTipo = new ArrayDataModel();
    private DataModel listaTipoEspecifico = new ArrayDataModel();
    private DataModel listaTipoTipo = new ArrayDataModel();
    private DataModel lista;
    private DataModel listaUsuarioCopiado;
    private List listaFilasSeleccionadas;
    private Map<Integer, Boolean> filasSeleccionadas = new HashMap<Integer, Boolean>();
    private List<SelectItem> listItem;
    private DataModel listaMarca; 
    //private List<SelectItem> listItem;
    //Primitivos
    private int idTipoEspecifico = -1;
    private int idTipo;  //tambie lo ocupo para comparar in id de empresa al momento de modificar
    private int idMarca; //tambien ocupado para idEmpresa en el catalogo de invitado, los get y set se llaman getIdEmpresa pero referencia a idMarca
    private int idRuta;   //Tambien ocupado para idoficina
    private int accion = 0; // 0 = crear 1 = actualizar
    private int idCiudad;
    private int idRol;
    private int idGerencia;
    private int idLugar;
    private int idSemaforo;
    //Booleanos
    private boolean crearPopUp = false;
    private boolean modificarPopUp = false;
    private boolean cambiarPopUp = false;
    private boolean popUp = false;
    private boolean mostarPanel = false; //Muestra la opcion de agregar detalle de ruta
    private boolean mrPopupCrearCaracteristica = false;
    private boolean mrPopupModificarCaracteristica = false;
    private boolean mrPopupEliminarCaracteristica = false;
    private boolean mrPopupCrearTipo = false;
    private boolean detallePop = false;
    private boolean pago;

    /**
     * Creates a new instance of CatalogoBeanModel
     */
    public CatalogoBeanModel() {
    }

    public void iniciaControladorPopPupFalse(String llave) {
	sesion.getControladorPopups().put(llave, Boolean.FALSE);
    }

    public void inicializaPopUpTrue(String llave) {
	sesion.getControladorPopups().put(llave, Boolean.TRUE);

    }

    public void clear() {
	setDataModelGeneric(null);
	setLista(null);
	setListaTipo(null);
	setListaTipoEspecifico(null);
	setListaTipoTipo(null);
	setListaFilasSeleccionadas(null);
	setListItem(null);
	setFilasSeleccionadas(null);

	setCadena(null);

	this.tipo = null;
	this.sgTipoEspecifico = null;
	this.sgTipoTipoEspecifico = null;
	this.modelo = null;
	this.marca = null;
	this.cadena = "";
	this.mensaje = "";
	this.dataModelGeneric = null;
	this.listaTipo = null;
	this.listaTipoEspecifico = null;
	this.listaTipoTipo = null;
	this.idTipo = -1;
	this.idTipoEspecifico = -1;
	this.idMarca = -1;
    }

    @PostConstruct
    public void iniciar() {
        try {
            asignarTipoVehiculo();
            setCadena("Vehículo");
            oficinaVO = new OficinaVO();
            listaMarca = new ListDataModel(marcaService.findAllByTipo(this.tipo, "nombre", Constantes.ORDER_BY_ASC, false));
            //Metiendo popups a Map de Popups
            iniciaControladorPopPupFalse("popupEliminarMarca");
            iniciaControladorPopPupFalse("popupEliminarModelo");
            iniciaControladorPopPupFalse("popupCrearTipoEspecifico");
            iniciaControladorPopPupFalse("popupCrearMarca");
            iniciaControladorPopPupFalse("popupCreateAerolinea");
            iniciaControladorPopPupFalse("popupUpdateAerolinea");
            iniciaControladorPopPupFalse("popupDeleteAerolinea");
        } catch (Exception ex) {
            Logger.getLogger(CatalogoBeanModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void guardarTipo() throws SIAException, Exception {
	tipoService.guardarTipo(getSgTipo(), sesion.getUsuario(), Constantes.BOOLEAN_FALSE);
    }

    public void modificarTipo() throws SIAException, Exception {
	tipoService.modificarTipo(getSgTipo(), sesion.getUsuario());
    }

    public boolean buscarTipoEnTipoTipoEspecifico() {
	boolean v = false;
	List<SgTipoTipoEspecifico> lista = sgTipoTipoEspecificoImpl.traerPorTipo(getSgTipo(), Constantes.BOOLEAN_FALSE);
	if (lista.size() > 0) {
	    v = true;
	}
	return v;
    }

    public void eliminarTipo() throws SIAException, Exception {
	tipoService.eliminarTipo(getSgTipo(), sesion.getUsuario(), Constantes.BOOLEAN_TRUE);
    }

    public DataModel traerTipo() {
	setListaTipo(new ListDataModel(tipoService.traerTipo(sesion.getUsuario(), Constantes.BOOLEAN_FALSE)));
	return getListaTipo();
    }

    public List<SelectItem> traerTipoGeneral() {
	List<SgTipo> lc;
	try {
	    List<SelectItem> l = new ArrayList<>();
	    lc = tipoService.traerTipo(sesion.getUsuario(), Constantes.BOOLEAN_FALSE);
	    for (SgTipo t : lc) {
		SelectItem item = new SelectItem(t.getId(), t.getNombre());
		l.add(item);
	    }
	    return l;
	} catch (Exception e) {
	    return null;
	}
    }

    public void traerTipoEspecificoPorTipo() {
	setSgTipo(tipoService.find(getIdTipo()));
	setListaTipoTipo(new ListDataModel(sgTipoTipoEspecificoImpl.traerPorTipo(getSgTipo(), Constantes.BOOLEAN_FALSE)));
    }

    public List<SgTipoEspecifico> getTipoEspecificoByTipo() throws SIAException, Exception {
	UtilLog4j.log.info(this, "CatalogoBeanModel.getTipoEspecificoByTipo()");

	List<SgTipoEspecifico> tipoEspecificoList = new ArrayList<SgTipoEspecifico>();

	if (this.tipo != null) {
	    List<SgTipoTipoEspecifico> tipoTipoEspecificoList = sgTipoTipoEspecificoImpl.traerPorTipoPago(this.tipo, Constantes.NO_ELIMINADO, Constantes.BOOLEAN_FALSE);

	    for (SgTipoTipoEspecifico t : tipoTipoEspecificoList) {
		tipoEspecificoList.add(t.getSgTipoEspecifico());
	    }
	} else {
	    throw new SIAException("No se pudieron obtener los tipos específicos porque falta el Tipo");
	}
	return tipoEspecificoList;
    }

    public void guardarTipoEspecifico() {
	getSgTipoEspecifico().setPago(isPago());
	tipoEspecificoService.guardarTipoEspecifico(getSgTipo().getId(), getSgTipoEspecifico(), sesion.getUsuario(), Constantes.BOOLEAN_FALSE);
    }

    public void saveTipoEspecifico() throws SIAException, Exception {
	this.sgTipoEspecifico.setPago(isPago());
	Object[] result = tipoEspecificoService.save(this.tipo.getId(), this.sgTipoEspecifico, sesion.getUsuario().getId());
	this.setMensaje((result[1] != null) ? result[1].toString() : "");
	//Recargar la lista de Tipos Específicos
	getTipoEspecificoByTipo();
    }

    public SgTipo buscarTipoPorId() {
	setSgTipo(tipoService.find(getIdTipo()));
	return getSgTipo();
    }

    public void modificarTipoEspecifico() {
	getSgTipoTipoEspecifico().getSgTipoEspecifico().setPago(isPago());
	tipoEspecificoService.modificarTipoEspecifico(getSgTipoTipoEspecifico().getSgTipoEspecifico(), sesion.getUsuario());
    }

    public void eliminarTipoTipoEspecifico() {
	sgTipoTipoEspecificoImpl.eliminarRelacionTipoEspecifico(getSgTipoTipoEspecifico(), sesion.getUsuario(), Constantes.BOOLEAN_TRUE);

    }

    public DataModel traerTipoEspecificoSinTipo() {
	setListaTipoEspecifico(new ListDataModel(tipoEspecificoService.traerTipoEspecificoSinTipo(getSgTipo(), getSgTipoEspecifico(), Constantes.BOOLEAN_FALSE)));
	return getListaTipoEspecifico();
    }

    public void asignarTipoEspecifico() {
	tipoEspecificoService.asignarTipoEspecifico(getListaFilasSeleccionadas(), getSgTipo().getId(), sesion.getUsuario(), Constantes.BOOLEAN_FALSE);
    }

    public List<SgPagoServicio> buscarPorTipoEspecifico() {
	try {
	    if (getSgTipoEspecifico().isPago()) {
		List<SgPagoServicio> lp = sgPagoServicioImpl.buscarPorTipoEspecifico(getSgTipoEspecifico(), Constantes.BOOLEAN_FALSE);
		return lp;
	    } else {
		return null;
	    }
	} catch (Exception e) {
	    return null;
	}
    }

    public List<SgTipoEspecifico> filtarFilasSeleccionadas() {
	DataModel<SgTipoEspecifico> lt = getListaTipoEspecifico();
	List<SgTipoEspecifico> l = new ArrayList<SgTipoEspecifico>();
	setListaFilasSeleccionadas(new ArrayList<SgTipoEspecifico>());
	UtilLog4j.log.info(this, "Filas seleccionadas :" + filasSeleccionadas.size());
	for (SgTipoEspecifico sgC : lt) {
	    if (filasSeleccionadas.get(sgC.getId()).booleanValue()) {
		l.add(sgC);
		filasSeleccionadas.remove(sgC.getId());
	    }
	}
	setListaFilasSeleccionadas(l);
	return getListaFilasSeleccionadas();
    }

    public void eliminarTipoEspecifico() throws SIAException, Exception {
	tipoEspecificoService.deleteTipoEspecifico(getSgTipoEspecifico(), sesion.getUsuario().getId());
    }

    public SgTipoEspecifico getTipoEspecificoById() {
	return ((this.idTipoEspecifico != -1) ? tipoEspecificoService.find(this.idTipoEspecifico) : null);
    }

    public SgMarca getMarcaById() {
	return ((this.idMarca != -1) ? marcaService.find(this.idMarca) : null);
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Catálogo Características - START <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public void getAllCaracteristicas() throws Exception {
	if (this.dataModelGeneric == null) {
	    this.dataModelGeneric = new ListDataModel(caracteristicaService.findAll(false, false));
	}
    }

    public void reloadAllCaracteristicas() throws Exception {
	this.dataModelGeneric = new ListDataModel(caracteristicaService.findAll(false, false));
    }

    public void createCaracteristica() throws SIAException, Exception {
	UtilLog4j.log.info(this, "CatalogoBeanModel.createCaracteristica()");
	this.caracteristica.setNombre(this.nombreCaracteristica);
	caracteristicaService.create(this.nombreCaracteristica, false, (isPago() ? Constantes.TIPO_PAGO_VEHICULO : 0), sesion.getUsuario().getId());
    }

    public void updateCaracteristica() throws SIAException, Exception {
	UtilLog4j.log.info(this, "CatalogoBeanModel.updateCaracteristica()");
	this.caracteristica.setNombre(this.nombreCaracteristica);
	caracteristicaService.update(this.caracteristica, sesion.getUsuario().getId());
    }

    public void deleteCaracteristica() throws SIAException, Exception {
	UtilLog4j.log.info(this, "CatalogoBeanModel.deleteCaracteristica()");
	caracteristicaService.delete(this.caracteristica, sesion.getUsuario().getId());
    }

    public void createTipo() throws Exception {
	UtilLog4j.log.info(this, "CatalogoBeanModel.createTipo()");
	SgTipo tipo = new SgTipo();
	tipo.setNombre(this.nombreTipo);
	tipo.setDescripcion(this.descripcionTipo);
	tipoService.guardarTipo(tipo, sesion.getUsuario(), Constantes.NO_ELIMINADO);
    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Catálogo Características - END <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Catálogo Modelo - START <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public void getModeloList() throws Exception {
	if (this.dataModelGeneric == null) {
	    this.dataModelGeneric = new ListDataModel(modeloService.findAll(tipoEspecificoService.find(this.idTipoEspecifico), this.marcaService.find(this.idMarca), "id", Constantes.ORDER_BY_ASC, false));
	}
    }

    public void reloadModeloList() throws Exception {
	this.dataModelGeneric = new ListDataModel(modeloService.findAll(tipoEspecificoService.find(this.idTipoEspecifico), this.marcaService.find(this.idMarca), "id", Constantes.ORDER_BY_ASC, false));
    }

    public void saveModelo() throws SIAException, Exception {
	UtilLog4j.log.info(this, "Marca antes de guardar el modelo: " + this.marca);
	UtilLog4j.log.info(this, "Tipo específico antes de guardar el modelo: " + this.sgTipoEspecifico);
	if (this.idMarca > 0) {
	    this.modelo.setSgMarca(marcaService.find(this.idMarca));
	} else {
	    this.modelo.setSgMarca(this.marca);
	}
	this.modelo.setSgTipoEspecifico(this.sgTipoEspecifico);
	modeloService.save(sesion.getUsuario().getId(), modelo.getNombre(), modelo.getSgMarca().getId(), modelo.getSgTipoEspecifico().getId());
    }

    public void updateModelo() throws SIAException, Exception {
	modeloService.update(this.modelo, sesion.getUsuario().getId());
    }

    public void deleteModelo() throws SIAException, Exception {
	modeloService.delete(this.modelo, sesion.getUsuario().getId());
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Catálogo Modelo - END <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Catálogo Marca - START <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public List<SgMarca> getMarcasByTipo() throws SIAException, Exception {
	return marcaService.findAllByTipo(this.tipo, "nombre", Constantes.ORDER_BY_ASC, false);
    }

    public void marcaList() throws Exception {
	if (listaMarca == null) {
	    listaMarca = new ListDataModel(marcaService.findAllByTipo(this.tipo, "nombre", Constantes.ORDER_BY_ASC, false));
	}
    }

    public void reloadMarcaList() throws Exception {
	listaMarca = new ListDataModel(marcaService.findAllByTipo(this.tipo, "nombre", Constantes.ORDER_BY_ASC, false));
	UtilLog4j.log.info(this, "reloadMarcaList ok");
    }

    public void saveMarca() throws SIAException, Exception {
	this.marca.setSgTipo(this.tipo);
	UtilLog4j.log.info(this, "Tipo a guardar " + this.tipo.getDescripcion());
	marcaService.save(sesion.getUsuario().getId(), this.marca.getNombre());
        reloadMarcaList();
    }

    public void updateMarca() throws SIAException, Exception {
	marcaService.update(this.marca, sesion.getUsuario().getId());
    }

    public void deleteMarca() throws SIAException, Exception {
	marcaService.delete(this.marca, sesion.getUsuario().getId());
	reloadMarcaList();
    }

    public void asignarTipoVehiculo() {
	this.tipo = tipoService.find(1);
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Catálogo Marca - END <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Catálogo Motivo - INICIO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public DataModel traerMotivo() {
	try {
	    setLista(new ListDataModel(sgMotivoImpl.getAllMotivos(Constantes.NO_ELIMINADO)));
	    return getLista();
	} catch (Exception e) {
	    return null;
	}
    }

    public boolean buscarMotivoUsado() {
	boolean v = false;
	v = sgSolicitudEstanciaImpl.buscarMotivoUsado(getSgMotivo());
	return v;
    }

    public void eliminarMotivo() {
	sgMotivoImpl.modificarMotivo(sesion.getUsuario(), getSgMotivo(), Constantes.ELIMINADO);
    }

    public SgMotivo buscarMotivoPorNombre() {
	if (getCadena().isEmpty()) {
	    return sgMotivoImpl.buscarPorNombre(getSgMotivo().getNombre());
	} else {
	    if (getCadena().equals(getSgMotivo().getNombre())) {
		return null;
	    } else {
		return sgMotivoImpl.buscarPorNombre(getSgMotivo().getNombre());
	    }
	}
    }

    public void completarMotivo() {
	UtilLog4j.log.info(this, "Motivo; " + getSgMotivo().getNombre());
	sgMotivoImpl.guardarMotivo(sesion.getUsuario(), getSgMotivo());
    }

    public void modificarMotivo() {
	sgMotivoImpl.modificarMotivo(sesion.getUsuario(), getSgMotivo(), Constantes.NO_ELIMINADO);
    }
//*************************************/
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Catálogo Pais - INICIO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

//*************  FIN PAIS************************/
    public DataModel traerOperacion() {
	try {
	    setLista(new ListDataModel(siOperacionImpl.traerOperacion()));
	    return getLista();
	} catch (Exception e) {
	    return null;
	}
    }

    public SiOperacion buscarOperacionPorNombre() {
	try {
	    return siOperacionImpl.buscarPorNombre(getSiOperacion());
	} catch (Exception e) {
	    return null;
	}
    }

    public void guardarOperacion() {
	siOperacionImpl.guardarOperacion(sesion.getUsuario(), getSiOperacion());
    }

    public void completarModificarOperacion() {
	siOperacionImpl.modificarOperacion(sesion.getUsuario(), getSiOperacion());
    }

    public void eliminarOperacion() {
	siOperacionImpl.eliminarOperacion(sesion.getUsuario(), getSiOperacion());
    }
//COndicion

    public DataModel traerCondicion() {
	try {
	    setLista(new ListDataModel(siCondicionImpl.traerCondicion(Constantes.NO_ELIMINADO)));
	    return getLista();
	} catch (Exception e) {
	    return null;
	}
    }

    public SiCondicion buscarCondicionPorNombre() {
	try {
	    return siCondicionImpl.buscarPorNombre(getSiCondicion());
	} catch (Exception e) {
	    return null;
	}
    }

    public void guardarCondicion() {
	siCondicionImpl.guardarCondicion(sesion.getUsuario(), getSiCondicion());
    }

    public void completarModificarCondicion() {
	siCondicionImpl.modificarCondicion(sesion.getUsuario(), getSiCondicion());
    }

    public void eliminarCondicion() {
	siCondicionImpl.eliminarCondicion(sesion.getUsuario(), getSiCondicion());
    }

    public List<SelectItem> listaTipoEspecifico() {
	List<SelectItem> l = new ArrayList<SelectItem>();
	List<SgTipoTipoEspecifico> lc;
	try {
	    lc = sgTipoTipoEspecificoImpl.traerPorTipo(getSgTipo(), Constantes.NO_ELIMINADO);
	    for (SgTipoTipoEspecifico tipoEsp : lc) {
		SelectItem item = new SelectItem(tipoEsp.getSgTipoEspecifico().getId(), tipoEsp.getSgTipoEspecifico().getNombre());
		l.add(item);
	    }
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Aquí en la excepción");
	}
	return l;
    }

    public List<SgAerolinea> getAllAerolineas() throws SIAException, Exception {
	List<SgAerolinea> aerolineasList = null;
	if (this.dataModelGeneric == null) {
	    aerolineasList = aerolineaService.findAll("nombre", true, false);
	    this.dataModelGeneric = new ListDataModel(aerolineasList);
	}
	return aerolineasList;
    }

    public void reloadAllAerolineas() {
	this.dataModelGeneric = new ListDataModel(aerolineaService.findAll("nombre", true, false));
    }

    public void saveAerolinea() throws ExistingItemException {
	this.aerolineaService.save(this.aerolinea, this.sesion.getUsuario().getId());
	this.dataModelGeneric = new ListDataModel(aerolineaService.findAll("nombre", true, false));
    }

    public void updateAerolinea() throws ExistingItemException {
	this.aerolineaService.update(this.aerolinea, this.sesion.getUsuario().getId());
	this.dataModelGeneric = new ListDataModel(aerolineaService.findAll("nombre", true, false));
    }

    public void deleteAerolinea() throws ItemUsedBySystemException {
	this.aerolineaService.delete(this.aerolinea, this.sesion.getUsuario().getId());
	this.dataModelGeneric = new ListDataModel(aerolineaService.findAll("nombre", true, false));
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Propiedades (getters y setters) <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Catálogo empresa - INICIO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public void traerEmpresaItems() {
	List<SgEmpresa> EmpresaList = null;
	try {
	    EmpresaList = sgEmpresaImpl.getAllEmpresa(Constantes.BOOLEAN_FALSE);
	    this.setListItem(new ArrayList<SelectItem>());
	    for (SgEmpresa c : EmpresaList) {
		SelectItem item = new SelectItem(c.getId(), c.getNombre());
		this.listItem.add(item);
	    }
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Excepcion en traer empresa " + e.getMessage());
	}
    }

    public DataModel traerEmpresas() {
	try {
	    setLista(new ListDataModel(sgEmpresaImpl.getAllEmpresa(Constantes.BOOLEAN_FALSE)));
	    return getLista();
	} catch (Exception e) {
	    return null;
	}
    }

    public boolean buscarEmpresaOcupada() {
	boolean v = false;
	v = sgEmpresaImpl.buscarEmpresaOcupado(getSgEmpresa().getId());
	return v;
    }

    public void eliminarEmpresa() {
	sgEmpresaImpl.eliminarEmpresa(sesion.getUsuario(), getSgEmpresa(), Constantes.ELIMINADO);
    }

    public SgEmpresa buscarEmpresaPorNombre() {
	return sgEmpresaImpl.buscarPorNombre(getSgEmpresa().getNombre());
    }

    public void guardarEmpresa() {
	UtilLog4j.log.info(this, "Empresa; " + getSgEmpresa().getNombre());
	sgEmpresaImpl.guardarEmpresa(sesion.getUsuario(), getSgEmpresa());
    }

    public void modificarEmpresa() {
	sgEmpresaImpl.modificarEmpresa(sesion.getUsuario(), getSgEmpresa());
    }
//*********************** FIN CATALOGO DE EMPRESA       * * ************/

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> CATALOGO INVITADO - INICIO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public void traerInvitadoItems() {
	List<InvitadoVO> invitadoList = null;
	try {
	    invitadoList = sgInvitadoImpl.traerInvitado();
	    this.setListItem(new ArrayList<SelectItem>());
	    for (InvitadoVO i : invitadoList) {
		SelectItem item = new SelectItem(i.getIdInvitado(), i.getNombre());
		this.listItem.add(item);
	    }
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Excepcion en traer invitado " + e.getMessage());
	}
    }

    public DataModel traerInvitados() {
	try {
	    setLista(new ListDataModel(sgInvitadoImpl.traerInvitado()));
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "excepcion al traer invitados " + e.getMessage());
	}
	return getLista();
    }

    public boolean buscarInvitadoOcupado() {
	boolean v = false;
	v = sgInvitadoImpl.buscarInvitadoOcupado(getInvitadoVo().getIdInvitado());
	return v;
    }

    public void eliminarInvitado() {
	sgInvitadoImpl.eliminarInvitado(sesion.getUsuario(), getInvitadoVo(), Constantes.ELIMINADO);
    }

    public boolean buscarInvitado() {
	try {
	    return sgInvitadoImpl.buscarInvitado(getInvitadoVo().getNombre(), getIdEmpresa());
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Excepcion en buscar invitado " + e.getMessage());
	    return true;
	}
    }

    public void guardarInvitado() {
	SgInvitado inv = sgInvitadoImpl.guardarInvitado(sesion.getUsuario(), getInvitadoVo(), getIdEmpresa());
	soporteProveedor.setListInvitado(null);
    }

    public void modificarInvitado() {
	sgInvitadoImpl.modificarInvitado(sesion.getUsuario(), getInvitadoVo(), getIdEmpresa());
    }

    /**
     * *************************INICIO USUARIO COPIADO
     * ***************************************
     */
    public DataModel traerUsuarioCopiado() {
	UtilLog4j.log.info(this, "Id ofi: " + getOficinaVO().getId());
	UtilLog4j.log.info(this, "Id tipo: " + getIdTipo());
	setListaUsuarioCopiado(new ListDataModel(siUsuarioTipoImpl.getListUser(getIdTipo(), getOficinaVO().getId())));
	return getListaUsuarioCopiado();
    }

    public List<SelectItem> regresaUsuarioActivo(String cadenaDigitada) {
	try {
	    return soporteProveedor.regresaUsuarioActivo(cadenaDigitada, 1, "nombre", true, true, false);
	} catch (Exception e) {
	    return null;
	}
    }

    public boolean buscarUsuario() {
	Usuario u = usuarioImpl.buscarPorNombre(getCadena());
	if (u != null) {
	    return true;
	} else {
	    return false;
	}
    }

    public String buscarOficinaVO() {
	return sgOficinaImpl.buscarPorId(getOficinaVO().getId()).getNombre();
    }

    public void guardarUsurioCopiado() {
	siUsuarioTipoImpl.guardarUsurioCopiado(sesion.getUsuario().getId(), getIdTipo(), getCadena(), getUsuarioTipoVo().getDescripcion(), getOficinaVO().getId());
    }

    public void quitarUsuario() {
	siUsuarioTipoImpl.quitarUsuario(sesion.getUsuario().getId(), usuarioTipoVo.getId());
    }

    public List<SelectItem> listaOficina() {
	List<OficinaVO> lo;
	try {
	    lo = sgOficinaImpl.getIdOffices();
	    List<SelectItem> li = new ArrayList<SelectItem>();
	    for (OficinaVO i : lo) {
		SelectItem item = new SelectItem(i.getId(), i.getNombre());
		li.add(item);
	    }
	    return li;
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Excepcion en traer oficina " + e.getMessage());
	    return null;
	}
    }

//*********************** FIN CATALOGO DE INVITADO       * * ************/
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ROL TIPO SOLI - INICIO<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public List<SelectItem> listaRol() {
	try {
	    List<SelectItem> l = new ArrayList<SelectItem>();
	    List<RolVO> rts = siRolImpl.traerRol(Constantes.MODULO_SGYL);
	    for (RolVO rol : rts) {
		l.add(new SelectItem(rol.getId(), rol.getNombre()));
	    }
	    return l;
	} catch (Exception e) {
	    return null;
	}
    }

    public List<SelectItem> listaGerecia() {
	try {
	    List<SelectItem> l = new ArrayList<SelectItem>();
	    List<GerenciaVo> rts = gerenciaImpl.findAll("nombre", true, false);
	    for (GerenciaVo g : rts) {
		l.add(new SelectItem(g.getId(), g.getNombre()));
	    }
	    return l;
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Error : : ::  : " + e.getMessage());
	    return null;
	}
    }

    public List<SelectItem> listaTipoSolicitud() {
	try {
	    List<SelectItem> l = new ArrayList<SelectItem>();
	    List<TipoSolicitudViajeVO> rts = sgTipoSolicitudViajeImpl.findAllTipoSolicitud();
	    for (TipoSolicitudViajeVO tsol : rts) {
		l.add(new SelectItem(tsol.getId(), tsol.getNombreSolicitud()));
	    }
	    return l;
	} catch (Exception e) {
	    return null;
	}
    }

    public void agregarRolTipoSolicitud() {
	sgRolTipoSolicitudViajeImpl.guardarRelacion(sesion.getUsuario().getId(), getIdRol(), getIdTipo());
    }

    public boolean validaTipoRelacion() {
	List<RolTipoSolicitudVo> rts = sgRolTipoSolicitudViajeImpl.traerTipoSolicitudPorRol(getIdRol(), -1);
	for (RolTipoSolicitudVo rolTipoSolicitudVo1 : rts) {
	    if (rolTipoSolicitudVo1.getIdTipoSolicitud() == getIdTipo()) {
		return false; //existe
	    }
	}
	return true; // No existe
    }

    public DataModel traerTipoSolicitud() {
	try {
	    List<RolTipoSolicitudVo> rts = sgRolTipoSolicitudViajeImpl.traerTipoSolicitudPorRol(getIdRol(), -1);
	    setLista(new ListDataModel(rts));
	    return getLista();
	} catch (Exception e) {
	    return null;
	}
    }

    public void quitarRelacion() {
	sgRolTipoSolicitudViajeImpl.quitarRelacion(sesion.getUsuario().getId(), getRolTipoSolicitudVo().getIdRolTipoSolicitud());

    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ROL TIPO SOLI - FIN <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ROL U-G - INICIO    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public List<SelectItem> traerUsuarioActivo(String cadena) {
	return soporteProveedor.regresaUsuarioActivoVO(cadena);
    }

    public DataModel traerRolGerencia() {
	try {
	    setLista(new ListDataModel(sgUsuarioRolGerenciaImpl.traerGerenciaPorRol(usuarioImpl.buscarPorNombre(getCadena()).getId(), -1)));
	    return getLista();
	} catch (Exception e) {
	    return null;
	}
    }

    public void agregarUsuarioRolGerencia() {
	sgUsuarioRolGerenciaImpl.guardarUsuarioRolGerencia(sesion.getUsuario().getId(), getCadena(), getIdRol(), getIdGerencia());
    }

    public void quitarRelacionUsuarioRolGerencia() {
	sgUsuarioRolGerenciaImpl.quitarRelacionUsuarioRolGerencia(sesion.getUsuario().getId(), getUsuarioRolGerenciaVo().getIdUsuarioRolGerencia());
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ROL U-G - FIN <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> SEM U-G - INICIO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public void completarModificarHorario() {
	sgSemaforoImpl.modificar(sesion.getUsuario().getId(), getSemaforoVo());
    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> SEM U-G - FIN <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    //SEMAFORO
    public SemaforoVo traerEstadoSemaforo() {
	List<SemaforoVo> lofi = new ArrayList<SemaforoVo>();
	List<SemaforoVo> lciu = new ArrayList<SemaforoVo>();
	List<SemaforoVo> lLug = new ArrayList<SemaforoVo>();
	try {
	    UtilLog4j.log.info(this, "Aqui antes de la consulta ");
	    for (SemaforoVo semaforo : sgEstadoSemaforoImpl.traerEstadoRuta()) {
		UtilLog4j.log.info(this, "semaforo: " + semaforo.getColor());
		if (semaforo.getRutaTipoEspecifico() == Constantes.RUTA_TIPO_OFICINA) {
		    lofi.add(semaforo);
		} else if (semaforo.getRutaTipoEspecifico() == Constantes.RUTA_TIPO_CIUDAD) {
		    lciu.add(semaforo);
		} else if (semaforo.getRutaTipoEspecifico() == Constantes.RUTA_TIPO_LUGAR) {
		    lLug.add(semaforo);
		}
	    }
	    setSemaforoVoActual(new SemaforoVo());
	    getSemaforoVoActual().setListaRutaOficina(new ArrayList<SemaforoVo>(lofi));
	    getSemaforoVoActual().setListaRutaCiudad(new ArrayList<SemaforoVo>(lciu));
	    getSemaforoVoActual().setListaRutaLugar(new ArrayList<SemaforoVo>(lLug));
	    return getSemaforoVoActual();

	} catch (Exception e) {
	    UtilLog4j.log.info(this, "e model :  " + e.getMessage() + " - - - - - +  +  +  + " + e.getCause());
	    return null;
	}
    }

    public SemaforoVo traerEstadoSemaforoActual() {
	semaforoVo = sgEstadoSemaforoImpl.buscarEstadoSemaforoPorId(getIdSemaforo());
	return getSemaforoVo();
    }

    public DataModel traerSemaforo() {
	listaFilasSeleccionadas = new ArrayList<SemaforoVo>();
	setLista(new ListDataModel(sgSemaforoImpl.traerSemaforo()));
	return getLista();
    }

    public void actualizarRangoHorario() {
	sgEstadoSemaforoImpl.actualizar(getIdSemaforo(), sesion.getUsuario().getId(), Constantes.NO_ELIMINADO);
    }
// * * * * * * * * * * CATÁLOGO SG_LUGAR - START * * * * * * * * * * * *


    public void saveSgLugar() throws SIAException {
	this.sgLugarImpl.save(getSgLugar(), this.sesion.getUsuario().getId());
	setDataModelGeneric(new ListDataModel(this.sgLugarImpl.findAll("nombre", true, false)));
    }

    public void updateSgLugar() throws SIAException {
	this.sgLugarImpl.update(getSgLugar(), this.cadena, this.sesion.getUsuario().getId());
	setDataModelGeneric(new ListDataModel(this.sgLugarImpl.findAll("nombre", true, false)));
    }

    public void deleteSgLugar() throws SIAException {
	this.sgLugarImpl.delete(getSgLugar(), this.sesion.getUsuario().getId());
	setDataModelGeneric(new ListDataModel(this.sgLugarImpl.findAll("nombre", true, false)));
    }

    // * * * * * * * * * * CATÁLOGO SG_LUGAR - END * * * * * * * * * * * *
    /**
     * @return the caracteristica
     */
    public SgCaracteristica getCaracteristica() {
	return caracteristica;
    }

    /**
     * @param caracteristica the caracteristica to set
     */
    public void setCaracteristica(SgCaracteristica caracteristica) {
	this.caracteristica = caracteristica;
    }

    /**
     * @return the dataModelGeneric
     */
    public DataModel getDataModelGeneric() {
	return dataModelGeneric;
    }

    /**
     * @param dataModelGeneric the dataModelGeneric to set
     */
    public void setDataModelGeneric(DataModel dataModelGeneric) {
	this.dataModelGeneric = dataModelGeneric;
    }

    /**
     * @return the nombreTipo
     */
    public String getNombreTipo() {
	return nombreTipo;
    }

    /**
     * @param nombreTipo the nombreTipo to set
     */
    public void setNombreTipo(String nombreTipo) {
	this.nombreTipo = nombreTipo;
    }

    /**
     * @return the descripcionTipo
     */
    public String getDescripcionTipo() {
	return descripcionTipo;
    }

    /**
     * @param descripcionTipo the descripcionTipo to set
     */
    public void setDescripcionTipo(String descripcionTipo) {
	this.descripcionTipo = descripcionTipo;
    }

    /**
     * @return the nombreCaracteristica
     */
    public String getNombreCaracteristica() {
	return nombreCaracteristica;
    }

    /**
     * @param nombreCaracteristica the nombreCaracteristica to set
     */
    public void setNombreCaracteristica(String nombreCaracteristica) {
	this.nombreCaracteristica = nombreCaracteristica;
    }

    //Popups
    /**
     * @return the mrPopupCrearCaracteristica
     */
    public boolean isMrPopupCrearCaracteristica() {
	return mrPopupCrearCaracteristica;
    }

    /**
     * @param mrPopupCrearCaracteristica the mrPopupCrearCaracteristica to set
     */
    public void setMrPopupCrearCaracteristica(boolean mrPopupCrearCaracteristica) {
	this.mrPopupCrearCaracteristica = mrPopupCrearCaracteristica;
    }

    /**
     * @return the mrPopupModificarCaracteristica
     */
    public boolean isMrPopupModificarCaracteristica() {
	return mrPopupModificarCaracteristica;
    }

    /**
     * @param mrPopupModificarCaracteristica the mrPopupModificarCaracteristica
     * to set
     */
    public void setMrPopupModificarCaracteristica(boolean mrPopupModificarCaracteristica) {
	this.mrPopupModificarCaracteristica = mrPopupModificarCaracteristica;
    }

    /**
     * @return the mrPopupEliminarCaracteristica
     */
    public boolean isMrPopupEliminarCaracteristica() {
	return mrPopupEliminarCaracteristica;
    }

    /**
     * @param mrPopupEliminarCaracteristica the mrPopupEliminarCaracteristica to
     * set
     */
    public void setMrPopupEliminarCaracteristica(boolean mrPopupEliminarCaracteristica) {
	this.mrPopupEliminarCaracteristica = mrPopupEliminarCaracteristica;
    }

    /**
     * @return the mrPopupCrearTipo
     */
    public boolean isMrPopupCrearTipo() {
	return mrPopupCrearTipo;
    }

    /**
     * @param mrPopupCrearTipo the mrPopupCrearTipo to set
     */
    public void setMrPopupCrearTipo(boolean mrPopupCrearTipo) {
	this.mrPopupCrearTipo = mrPopupCrearTipo;
    }

    /**
     * @param sgTipo the sgTipo to set
     */
    public void setSgTipo(SgTipo sgTipo) {
	this.tipo = sgTipo;
    }

    /**
     * @return the sgTipoEspecifico
     */
    public SgTipoEspecifico getSgTipoEspecifico() {
	return sgTipoEspecifico;
    }

    /**
     * @param sgTipoEspecifico the sgTipoEspecifico to set
     */
    public void setSgTipoEspecifico(SgTipoEspecifico sgTipoEspecifico) {
	this.sgTipoEspecifico = sgTipoEspecifico;
    }

    /**
     * @return the crearPopUp
     */
    public boolean isCrearPopUp() {
	return crearPopUp;
    }

    /**
     * @param crearPopUp the crearPopUp to set
     */
    public void setCrearPopUp(boolean crearPopUp) {
	this.crearPopUp = crearPopUp;
    }

    /**
     * @return the modificarPopUp
     */
    public boolean isModificarPopUp() {
	return modificarPopUp;
    }

    /**
     * @param modificarPopUp the modificarPopUp to set
     */
    public void setModificarPopUp(boolean modificarPopUp) {
	this.modificarPopUp = modificarPopUp;
    }

    /**
     * @return the cambiarPopUp
     */
    public boolean isCambiarPopUp() {
	return cambiarPopUp;
    }

    /**
     * @param cambiarPopUp the cambiarPopUp to set
     */
    public void setCambiarPopUp(boolean cambiarPopUp) {
	this.cambiarPopUp = cambiarPopUp;
    }

    /**
     * @return the popUp
     */
    public boolean isPopUp() {
	return popUp;
    }

    /**
     * @param popUp the popUp to set
     */
    public void setPopUp(boolean popUp) {
	this.popUp = popUp;
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
     * @return the listaTipoEspecifico
     */
    public DataModel getListaTipoEspecifico() {
	return listaTipoEspecifico;
    }

    /**
     * @param listaTipoEspecifico the listaTipoEspecifico to set
     */
    public void setListaTipoEspecifico(DataModel listaTipoEspecifico) {
	this.listaTipoEspecifico = listaTipoEspecifico;
    }

    /**
     * @return the listaTipoTipo
     */
    public DataModel getListaTipoTipo() {
	return listaTipoTipo;
    }

    /**
     * @param listaTipoTipo the listaTipoTipo to set
     */
    public void setListaTipoTipo(DataModel listaTipoTipo) {
	this.listaTipoTipo = listaTipoTipo;
    }

    /**
     * @return the sgTipoTipoEspecifico
     */
    public SgTipoTipoEspecifico getSgTipoTipoEspecifico() {
	return sgTipoTipoEspecifico;
    }

    /**
     * @param sgTipoTipoEspecifico the sgTipoTipoEspecifico to set
     */
    public void setSgTipoTipoEspecifico(SgTipoTipoEspecifico sgTipoTipoEspecifico) {
	this.sgTipoTipoEspecifico = sgTipoTipoEspecifico;
    }

    /**
     * @return the idTipo
     */
    public int getIdTipo() {
	return idTipo;
    }

    /**
     * @param idTipo the idTipo to set
     */
    public void setIdTipo(int idTipo) {
	this.idTipo = idTipo;
    }

    /**
     * @return the mostarPanel
     */
    public boolean isMostarPanel() {
	return mostarPanel;
    }

    /**
     * @param mostarPanel the mostarPanel to set
     */
    public void setMostarPanel(boolean mostarPanel) {
	this.mostarPanel = mostarPanel;
    }

    /**
     * @return the filasSeleccionadas
     */
    public Map<Integer, Boolean> getFilasSeleccionadas() {
	return filasSeleccionadas;
    }

    /**
     * @param filasSeleccionadas the filasSeleccionadas to set
     */
    public void setFilasSeleccionadas(Map<Integer, Boolean> filasSeleccionadas) {
	this.filasSeleccionadas = filasSeleccionadas;
    }

    /**
     * @return the listaFilasSeleccionadas
     */
    public List getListaFilasSeleccionadas() {
	return listaFilasSeleccionadas;
    }

    /**
     * @param listaFilasSeleccionadas the listaFilasSeleccionadas to set
     */
    public void setListaFilasSeleccionadas(List listaFilasSeleccionadas) {
	this.listaFilasSeleccionadas = listaFilasSeleccionadas;
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
     * @return the pago
     */
    public boolean isPago() {
	return pago;
    }

    /**
     * @param pago the pago to set
     */
    public void setPago(boolean pago) {
	this.pago = pago;
    }

    public SgTipo getSgTipo() {
	return tipo;
    }

    /**
     * @return the modelo
     */
    public SgModelo getModelo() {
	return modelo;
    }

    /**
     * @param modelo the modelo to set
     */
    public void setModelo(SgModelo modelo) {
	this.modelo = modelo;
    }

    /**
     * @return the marca
     */
    public SgMarca getMarca() {
	return marca;
    }

    /**
     * @param marca the marca to set
     */
    public void setMarca(SgMarca marca) {
	UtilLog4j.log.info(this, "Poniendo la marca: " + marca);
	this.marca = marca;
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
     * @return the sgMotivo
     */
    public SgMotivo getSgMotivo() {
	return sgMotivo;
    }

    /**
     * @param sgMotivo the sgMotivo to set
     */
    public void setSgMotivo(SgMotivo sgMotivo) {
	this.sgMotivo = sgMotivo;
    }

    /*
     * @return the idTipoEspecifico
     */
    public int getIdTipoEspecifico() {
	return idTipoEspecifico;
    }

    /**
     * @param idTipoEspecifico the idTipoEspecifico to set
     */
    public void setIdTipoEspecifico(int idTipoEspecifico) {
	this.idTipoEspecifico = idTipoEspecifico;
    }

    /**
     * @return the idMarca
     */
    public int getIdMarca() {
	return idMarca;
    }

    /**
     * @param idMarca the idMarca to set
     */
    public void setIdMarca(int idMarca) {
	this.idMarca = idMarca;
    }

    /**
     * @return the mensaje
     */
    public String getMensaje() {
	return mensaje;
    }

    /**
     * @param mensaje the mensaje to set
     */
    public void setMensaje(String mensaje) {
	this.mensaje = mensaje;
    }

    public SiOperacion getSiOperacion() {
	return siOperacion;
    }

    public void setSiOperacion(SiOperacion siOperacion) {
	this.siOperacion = siOperacion;
    }

    /**
     * @return the siCondicion
     */
    public SiCondicion getSiCondicion() {
	return siCondicion;
    }

    /**
     * @param siCondicion the siCondicion to set
     */
    public void setSiCondicion(SiCondicion siCondicion) {
	this.siCondicion = siCondicion;
    }

    /**
     * @return the aerolinea
     */
    public SgAerolinea getAerolinea() {
	return aerolinea;
    }

    /**
     * @param aerolinea the aerolinea to set
     */
    public void setAerolinea(SgAerolinea aerolinea) {
	this.aerolinea = aerolinea;
    }

    /**
     * @return the siPais
     */
    public SiPais getSiPais() {
	return siPais;
    }

    /**
     * @param siPais the siPais to set
     */
    public void setSiPais(SiPais siPais) {
	this.siPais = siPais;
    }

    /**
     * @return the idRuta
     */
    public int getIdRuta() {
	return idRuta;
    }

    /**
     * @param idRuta the idRuta to set
     */
    public void setIdRuta(int idRuta) {
	this.idRuta = idRuta;
    }

    /**
     * @return the listItem
     */
    public List<SelectItem> getListItem() {
	return listItem;
    }

    /**
     * @param listItem the listItem to set
     */
    public void setListItem(List<SelectItem> empresaListItem) {
	this.listItem = empresaListItem;
    }

    /**
     * @return the sgEmpresa
     */
    public SgEmpresa getSgEmpresa() {
	return sgEmpresa;
    }

    /**
     * @param sgEmpresa the sgEmpresa to set
     */
    public void setSgEmpresa(SgEmpresa sgEmpresa) {
	this.sgEmpresa = sgEmpresa;
    }

    /*
     * @return the opcionDestino
     */
    public String getOpcionDestino() {
	return opcionDestino;
    }

    /**
     * @param opcionDestino the opcionDestino to set
     */
    public void setOpcionDestino(String opcionDestino) {
	this.opcionDestino = opcionDestino;
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

    /**
     * Estos metodos ocupan el atributo idMarca para no declarar una nueva
     *
     * @return the idMarca
     */
    public int getIdEmpresa() {
	return idMarca;
    }

    /**
     * @param idMarca the idMarca to set
     */
    public void setIdEmpresa(int idMarca) {
	this.idMarca = idMarca;
    }

    /**
     * @return the sgLugar
     */
    public SgLugar getSgLugar() {
	return sgLugar;
    }

    /**
     * @param sgLugar the sgLugar to set
     */
    public void setSgLugar(SgLugar sgLugar) {
	this.sgLugar = sgLugar;
    }

    /**
     * @return the detallePop
     */
    public boolean isDetallePop() {
	return detallePop;
    }

    /**
     * @param detallePop the detallePop to set
     */
    public void setDetallePop(boolean detallePop) {
	this.detallePop = detallePop;
    }

    /**
     * @return the usuarioCopiadoVo
     */
    public UsuarioTipoVo getUsuarioTipoVo() {
	return usuarioTipoVo;
    }

    /**
     * @param usuarioCopiadoVo the usuarioCopiadoVo to set
     */
    public void setUsuarioTipoVo(UsuarioTipoVo usuarioTipoVo) {
	this.usuarioTipoVo = usuarioTipoVo;
    }

    /**
     * @return the accion
     */
    public int getAccion() {
	return accion;
    }

    /**
     * @param accion the accion to set
     */
    public void setAccion(int accion) {
	this.accion = accion;
    }

    /**
     * @return the oficinaVO
     */
    public OficinaVO getOficinaVO() {
	return oficinaVO;
    }

    /**
     * @param oficinaVO the oficinaVO to set
     */
    public void setOficinaVO(OficinaVO oficinaVO) {
	this.oficinaVO = oficinaVO;
    }

    /**
     * @return the rutaTerrestreVo
     */
    public RutaTerrestreVo getRutaTerrestreVo() {
	return rutaTerrestreVo;
    }

    /**
     * @param rutaTerrestreVo the rutaTerrestreVo to set
     */
    public void setRutaTerrestreVo(RutaTerrestreVo rutaTerrestreVo) {
	this.rutaTerrestreVo = rutaTerrestreVo;
    }

    /**
     * @param soporteProveedor the soporteProveedor to set
     */
    public void setSoporteProveedor(SoporteProveedor soporteProveedor) {
	this.soporteProveedor = soporteProveedor;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
	this.sesion = sesion;
    }

    /**
     * @return the sgDetalleRutaTerrestreVo
     */
    public SgDetalleRutaTerrestreVo getSgDetalleRutaTerrestreVo() {
	return sgDetalleRutaTerrestreVo;
    }

    /**
     * @param sgDetalleRutaTerrestreVo the sgDetalleRutaTerrestreVo to set
     */
    public void setSgDetalleRutaTerrestreVo(SgDetalleRutaTerrestreVo sgDetalleRutaTerrestreVo) {
	this.sgDetalleRutaTerrestreVo = sgDetalleRutaTerrestreVo;
    }

    /**
     * @return the idCiudad
     */
    public int getIdCiudad() {
	return idCiudad;
    }

    /**
     * @param idCiudad the idCiudad to set
     */
    public void setIdCiudad(int idCiudad) {
	this.idCiudad = idCiudad;
    }

    /**
     * @return the idRol
     */
    public int getIdRol() {
	return idRol;
    }

    /**
     * @param idRol the idRol to set
     */
    public void setIdRol(int idRol) {
	this.idRol = idRol;
    }

    /**
     * @return the rolTipoSolicitudVo
     */
    public RolTipoSolicitudVo getRolTipoSolicitudVo() {
	return rolTipoSolicitudVo;
    }

    /**
     * @param rolTipoSolicitudVo the rolTipoSolicitudVo to set
     */
    public void setRolTipoSolicitudVo(RolTipoSolicitudVo rolTipoSolicitudVo) {
	this.rolTipoSolicitudVo = rolTipoSolicitudVo;
    }

    /**
     * @return the usuarioRolGerenciaVo
     */
    public UsuarioRolGerenciaVo getUsuarioRolGerenciaVo() {
	return usuarioRolGerenciaVo;
    }

    /**
     * @param usuarioRolGerenciaVo the usuarioRolGerenciaVo to set
     */
    public void setUsuarioRolGerenciaVo(UsuarioRolGerenciaVo usuarioRolGerenciaVo) {
	this.usuarioRolGerenciaVo = usuarioRolGerenciaVo;
    }

    /**
     * @return the user
     */
    public String getUser() {
	return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
	this.user = user;
    }

    /**
     * @return the idGerencia
     */
    public int getIdGerencia() {
	return idGerencia;
    }

    /**
     * @param idGerencia the idGerencia to set
     */
    public void setIdGerencia(int idGerencia) {
	this.idGerencia = idGerencia;
    }

    /**
     * @return the idLugar
     */
    public int getIdLugar() {
	return idLugar;
    }

    /**
     * @param idLugar the idLugar to set
     */
    public void setIdLugar(int idLugar) {
	this.idLugar = idLugar;
    }

    /**
     * @return the semaforoVo
     */
    public SemaforoVo getSemaforoVo() {
	return semaforoVo;
    }

    /**
     * @param semaforoVo the semaforoVo to set
     */
    public void setSemaforoVo(SemaforoVo semaforoVo) {
	this.semaforoVo = semaforoVo;
    }

    /**
     * @return the idSemaforo
     */
    public int getIdSemaforo() {
	return idSemaforo;
    }

    /**
     * @param idSemaforo the idSemaforo to set
     */
    public void setIdSemaforo(int idSemaforo) {
	this.idSemaforo = idSemaforo;
    }

    /**
     * @return the semaforoVoActual
     */
    public SemaforoVo getSemaforoVoActual() {
	return semaforoVoActual;
    }

    /**
     * @param semaforoVoActual the semaforoVoActual to set
     */
    public void setSemaforoVoActual(SemaforoVo semaforoVoActual) {
	this.semaforoVoActual = semaforoVoActual;
    }

    /**
     * @return the listaUsuarioCopiado
     */
    public DataModel getListaUsuarioCopiado() {
	return listaUsuarioCopiado;
    }

    /**
     * @param listaUsuarioCopiado the listaUsuarioCopiado to set
     */
    public void setListaUsuarioCopiado(DataModel listaUsuarioCopiado) {
	this.listaUsuarioCopiado = listaUsuarioCopiado;
    }

    /**
     * @param sgInvitado the invitadoVo to set
     */
    public void setInvitadoVo(InvitadoVO sgInvitado) {
	this.invitadoVo = sgInvitado;
    }

    /**
     * @return the invitadoVo
     */
    public InvitadoVO getInvitadoVo() {
	return invitadoVo;
    }

    /**
     * @return the listaMarca
     */
    public DataModel getListaMarca() {
        return listaMarca;
    }

    /**
     * @param listaMarca the listaMarca to set
     */
    public void setListaMarca(DataModel listaMarca) {
        this.listaMarca = listaMarca;
    }
}
