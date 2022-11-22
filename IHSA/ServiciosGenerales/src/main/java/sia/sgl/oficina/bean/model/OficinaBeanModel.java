    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.oficina.bean.model;

import java.io.File;
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

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.constantes.Constantes;
import sia.excepciones.ExistingItemException;
import sia.excepciones.ItemUsedBySystemException;
import sia.excepciones.SIAException;
import sia.modelo.Convenio;
import sia.modelo.Moneda;
import sia.modelo.Proveedor;
import sia.modelo.SgCaracteristica;
import sia.modelo.SgCaracteristicaComedor;
import sia.modelo.SgCaracteristicaSalaJunta;
import sia.modelo.SgCaracteristicaSanitario;
import sia.modelo.SgComedor;
import sia.modelo.SgCtrlMantenimientoSanitario;
import sia.modelo.SgDireccion;
import sia.modelo.SgHistorialConvenioOficina;
import sia.modelo.SgOficina;
import sia.modelo.SgOficinaAnalista;
import sia.modelo.SgOficinaPlano;
import sia.modelo.SgPagoServicio;
import sia.modelo.SgPagoServicioOficina;
import sia.modelo.SgSalaJunta;
import sia.modelo.SgSanitario;
import sia.modelo.SgStaff;
import sia.modelo.SgTipo;
import sia.modelo.SgTipoEspecifico;
import sia.modelo.SgTipoTipoEspecifico;
import sia.modelo.SiAdjunto;
import sia.modelo.SiCiudad;
import sia.modelo.SiEstado;
import sia.modelo.SiPais;
import sia.modelo.Usuario;
import sia.modelo.sgl.oficina.vo.OficinaVO;
import sia.modelo.sgl.oficina.vo.SgOficinaAnalistaVo;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.modelo.sgl.vo.CaracteristicaVo;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.convenio.impl.ConvenioImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.sgl.impl.SgCaracteristicaComedorImpl;
import sia.servicios.sgl.impl.SgCaracteristicaImpl;
import sia.servicios.sgl.impl.SgCaracteristicaSalaJuntaImpl;
import sia.servicios.sgl.impl.SgCaracteristicaSanitarioImpl;
import sia.servicios.sgl.impl.SgComedorImpl;
import sia.servicios.sgl.impl.SgCtrlMantenimientoSanitarioImpl;
import sia.servicios.sgl.impl.SgHistorialConvenioOficinaImpl;
import sia.servicios.sgl.impl.SgOficinaAnalistaImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sgl.impl.SgOficinaPlanoIpml;
import sia.servicios.sgl.impl.SgPagoServicioImpl;
import sia.servicios.sgl.impl.SgPagoServicioOficinaImpl;
import sia.servicios.sgl.impl.SgSalaJuntaImpl;
import sia.servicios.sgl.impl.SgSanitarioImpl;
import sia.servicios.sgl.impl.SgStaffImpl;
import sia.servicios.sgl.impl.SgTipoEspecificoImpl;
import sia.servicios.sgl.impl.SgTipoImpl;
import sia.servicios.sgl.impl.SgTipoTipoEspecificoImpl;
import sia.servicios.sgl.vehiculo.impl.SgVehiculoImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiCiudadImpl;
import sia.servicios.sistema.impl.SiEstadoImpl;
import sia.servicios.sistema.impl.SiPaisImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.servicios.sistema.vo.MonedaVO;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.SoporteProveedor;
import sia.util.ProveedorEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 *
 */
@Named(value = "oficinaBeanModel")
@ViewScoped
public class OficinaBeanModel implements Serializable {

    private static final UtilLog4j LOGGER = UtilLog4j.log;

    @Inject
    private Sesion sesion;
    @ManagedProperty(value = "#{soporteProveedor}")
    private SoporteProveedor soporteProveedor;
    //
    //
    @Inject
    private SgOficinaImpl sgOficinaImpl;
    @Inject
    private SgComedorImpl sgComedorImpl;
    @Inject
    private SgOficinaAnalistaImpl sgOficinaAnalistaImpl;
    @Inject
    private SgSalaJuntaImpl sgSalaJuntaImpl;
    @Inject
    private SgSanitarioImpl sgSanitarioImpl;
    @Inject
    private SgCaracteristicaImpl sgCaracteristicaImpl;
    @Inject
    private SgCaracteristicaComedorImpl sgCaracteristicaComedorImpl;
    @Inject
    private SgCaracteristicaSanitarioImpl sgCaracteristicaSanitarioImpl;
    @Inject
    private SgCaracteristicaSalaJuntaImpl sgCaracteristicaSalaJuntaImpl;
    @Inject
    private SgTipoImpl sgTipoImpl;
    @Inject
    private SgTipoTipoEspecificoImpl sgTipoTipoEspecificoImpl;
    @Inject
    private SgTipoEspecificoImpl sgTipoEspecificoImpl;
    @Inject
    private SgPagoServicioImpl sgPagoServicioImpl;
    @Inject
    private SgPagoServicioOficinaImpl sgPagoServicioOficinaImpl;
    @Inject
    private MonedaImpl monedaImpl;
    @Inject
    private SgStaffImpl sgStaffImpl;
    @Inject
    private SgVehiculoImpl sgVehiculoImpl;
    @Inject
    private SiAdjuntoImpl siAdjuntoImpl;
    @Inject
    private SiParametroImpl siParametroImpl;
    @Inject
    private SgCtrlMantenimientoSanitarioImpl sgCtrlMantenimientoSanitarioImpl;
    @Inject
    private SgHistorialConvenioOficinaImpl sgHistorialConvenioOficinaImpl;
    @Inject
    private SgOficinaPlanoIpml sgOficinaPlanoImpl;
    @Inject
    private ConvenioImpl convenioImpl;
    @Inject
    private ProveedorServicioImpl proveedorImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private SiPaisImpl siPaisImpl;
    @Inject
    private SiEstadoImpl siEstadoImpl;
    @Inject
    private SiCiudadImpl siCiudadImpl;
    //
    private DataModel dataModelAnlistas;
    private DataModel dataModelAux;
    //  private DataModel lista;
    private Map<String, DataModel> mapaLista = new HashMap<String, DataModel>();
    private DataModel listaContrato;
    private DataModel listaRegistro;  //Lista que ocupo para historial de convenios
    private DataModel listaVistoBueno; //Lista tambien para archivos del convenio
    private DataModel listaContratoProveedor;
    private DataModel listaPlano;
    private Map<Integer, Boolean> filasSeleccionadas = new HashMap<Integer, Boolean>();
    private boolean crearPopUp = false;
    private boolean modificarPopUp = false;
    private boolean cambiarPopUp = false;
    private boolean popUp = false;
    private boolean mostrarPanel = false;
    private boolean agregarCaracteristicaOficia = false;
    private boolean subirArchivo = false;
    private boolean verDetallePop = false;
    private boolean eliminarPop = false;
    private boolean abrirArchivHistorial = false;
    private boolean agregarPopContrato = false;
    private SgOficina sgOficina;
    private SgDireccion sgDireccion;
    private SgOficinaAnalista sgOficinaAnalista;
    private SgSalaJunta sgSalaJunta;
    private SgComedor sgComedor;
    private SgCaracteristica sgCaracteristica;
    private CaracteristicaVo sgCaracteristicaOficina;
    private SgSanitario sgSanitario;
    private Usuario usuario;
    private SgPagoServicio sgPagoServicio;
    private Moneda moneda;
    private SgTipo sgTipo;
    private SgTipoEspecifico sgTipoEspecifico;
    private SgPagoServicioOficina sgPagoServicioOficina;
    private SgCtrlMantenimientoSanitario sgCtrlMantenimientoSanitario;
    private Convenio convenio;
    private SgHistorialConvenioOficina sgHistorialConvenioOficina;
    private SgOficinaPlano sgOficinaPlano;
    private Integer cantidadCaracteristica = 1;
    private Object object;
    private OficinaVO oficinaVO;
    private String user;
    private String prefijo = "";
    private String numeroFactura;
    private String nombre;
    private String latitud;
    private String longitud;
    private String telefono;
    private String municipio;
    private String colonia;
    private String calle;
    private String numExterior;
    private String numInterior;
    private String numPiso;
    private String codigoPostal;
    private List<SelectItem> listaUsuariosAlta;
    private List<SelectItem> listaTipoEspecifico;
    private List<SelectItem> listaProveedor; //Usado: a)SiEstado en alta de Oficina
    private List<SelectItem> listaPagos; //Usado: a)SiPais en alta Oficina
    private List<SelectItem> listaPais; //Usado: a)SiEstado en alta de Oficina
    private List<SelectItem> listaEstado; //Usado: a)SiPais en alta Oficina
    private List<SelectItem> listaCiudad; //Usado: a)SiPais en alta Oficina
    private List<String> listaProveedorBuscar;
    private List<SelectItem> caracteristicas; //Usado: b)SiCiudad en alta de Oficina
    private List<SelectItem> matchesList;
    private List listaFilasSeleccionadas;
    private int opcionSeleccionada = 1;
    private int idTipo;
    private int idTipoEspecifico = -1;
    private int idMoneda; //Usado: a)idSiPais
    private int idStaff; //Usado: a)idSiCiudad
    private int idVehiculo;
    private int idSanitario;
    private int idAdjunto = 0;
    private String uuid;
    private int tamanioLista;
    private int idPais;
    private int idEstado;
    private int idCiudad;
    private String opcionPagar;
    private String pro;
    private String principal;
    private String url = Constantes.URL;

    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;

    /**
     * Creates a new instance of oficinaModel
     */
    public OficinaBeanModel() {
    }

    @PostConstruct
    public void iniciarConversacion() {
	try {
	    sgCaracteristicaOficina = new CaracteristicaVo();
	    //Limpiando Variables
	    setCrearPopUp(false);
	    this.listaPlano = null;
	    this.listaContrato = null;
	    controlaPopUpFalso("popupCreateSgOficina");
	    setOpcionSeleccionada(1);
	    setDataModelAnlistas(new ListDataModel(sgOficinaImpl.traerListaOficina()));
	    mapaLista.put("caracteristica_oficina", new ListDataModel(sgOficinaImpl.getAllCaracteristicasOficinaList(sesion.getOficinaActual().getId())));
	    controlaPopUpFalso("popupAsignarAnalistaOficina");
	    // pagos
	    mapaLista.put("oficina", new ListDataModel(sgOficinaImpl.traerListaOficina()));
	    mapaLista.put("vistoBueno", new ListDataModel(sgOficinaAnalistaImpl.traerOficina(sesion.getUsuario().getId())));
	    setIdTipoEspecifico(-1);
	    setOpcionPagar("Oficina");
	    buscarTipoGeneral();
	    traerTipoEspecificoPorTipoOficina();
	    setListaProveedorBuscar(traerProveedor());
	    //
	    mapaLista.put("comedorOficina", new ListDataModel(sgComedorImpl.traerComedorPorOficina(sesion.getOficinaActual().getId(), Constantes.BOOLEAN_FALSE)));
	    traerSalaJuntaOficina();
	    traerSanitario();
	    traerBitacoraSanitario();

	} catch (Exception ex) {
	    Logger.getLogger(OficinaBeanModel.class.getName()).log(Level.SEVERE, null, ex);
	}

    }

    public OficinaVO buscarOficinaPorId() {
	return sgOficinaImpl.buscarPorId(getOficinaVO().getId());
    }

    public List<SelectItem> traerVehiculo() {
	List<VehiculoVO> lc;
	try {
	    List<SelectItem> l = new ArrayList<SelectItem>();
	    lc = sgVehiculoImpl.getAllVehiculoByOficinaList(sesion.getOficinaActual().getId());
	    for (VehiculoVO sgV : lc) {
		SelectItem item = new SelectItem(sgV.getId(), sgV.getModelo() + " || " + sgV.getSerie());
		l.add(item);
	    }
	    setListaPagos(l);
	    return getListaPagos();
	} catch (Exception e) {
	    return null;
	}
    }

    public void traerPagoPorOficina() {
	setSgTipoEspecifico(sgTipoEspecificoImpl.find(getIdTipoEspecifico()));
	mapaLista.put("pago", new ListDataModel(
		sgPagoServicioOficinaImpl.traerPagoPorTipoEspecifico(
			getSgTipo(),
			getSgTipoEspecifico(),
			sesion.getOficinaActual().getId(),
			Constantes.BOOLEAN_FALSE)));
	setSgPagoServicio(null);
    }

    public void controlaPopUpFalso(String llave) {
	sesion.getControladorPopups().put(llave, Boolean.FALSE);
    }

    public void iniciaControlPopUp(String llave, boolean valor) {
	sesion.getControladorPopups().put(llave, valor);
    }

    public void controlaPopUpTrue(String llave) {
	sesion.getControladorPopups().put(llave, Boolean.TRUE);
    }

    public boolean devolverEstadoPop(String llave) {
	return sesion.getControladorPopups().get(llave);
    }

    public int buscarOficinaEnSesion() {
	return sesion.getOficinaActual().getId();
    }

    public OficinaVO buscarOficinaActualEnSesion() {
	return sesion.getOficinaActual();
    }

    public void clearVariables() {
	setListaPais(null); //siPaisSelectItem
	setListaEstado(null); //siEstadoSelectItem
	setListaCiudad(null); //siCiudadSelectItem
	setOficinaVO(null);
	setOpcionSeleccionada(-1);
	setIdPais(-1);
	setIdMoneda(-1);
	setIdStaff(-1);
	setUser("");
	setNombre("");
	setTelefono("");
	setMunicipio("");
	setColonia("");
	setCalle("");
	setNumExterior("");
	setNumInterior("");
	setNumPiso("");
	setCodigoPostal("");
    }

    public void traerOficinaRegistro() {
	switch (getOpcionSeleccionada()) {
	case 1:
	    mapaLista.put("oficina", new ListDataModel(sgOficinaImpl.findByVistoBuenoList(true, false)));
	    break;
	case 2:
	    mapaLista.put("oficina", new ListDataModel(sgOficinaImpl.findByVistoBuenoList(false, false)));
	    break;
	default:
	    break;
	}
    }

    public void vistoBueno(int idSgOficina) {
	this.sgOficinaImpl.setVistoBuenoSgOficina(idSgOficina, this.sesion.getUsuario().getId());
	//mapaLista.put("vistoBueno", new ListDataModel(sgOficinaImpl.traerOficina(sesion.getUsuario(), Constantes.NO_ELIMINADO, Constantes.BOOLEAN_FALSE)));
        mapaLista.put("vistoBueno", new ListDataModel(sgOficinaAnalistaImpl.traerOficina(sesion.getUsuario().getId())));
    }

    public void saveSgOficina(OficinaVO vo, String nombreAnalista) throws ExistingItemException {
	this.sgOficinaImpl.save(vo, nombreAnalista, this.sesion.getUsuario().getId());
    }

//    public void saveSgOficina(String nombre, String telefono, String municipio, String colonia, String calle, String numExterior, String numInterior, String numPiso, String codigoPostal, int idSiPais, int idSiEstado, int idSiCiudad, String nombreAnalista) throws ExistingItemException {
//        this.sgOficinaImpl.save(nombre, telefono, municipio, colonia, calle, numExterior, numInterior, numPiso, codigoPostal, idSiPais, idSiEstado, idSiCiudad, nombreAnalista, this.sesion.getUsuario().getId());
//    }
    public void updateSgOficina(int idSgOficinaOriginal, OficinaVO vo) throws ExistingItemException {
	this.sgOficinaImpl.update(idSgOficinaOriginal, vo, this.sesion.getUsuario().getId());
    }

    public void deleteSgOficina(int idSgOficina) throws ItemUsedBySystemException {
	this.sgOficinaImpl.delete(idSgOficina, this.sesion.getUsuario().getId());
    }

    public void guardarOficina() throws SIAException, Exception {
	sgOficinaImpl.guardarOficina(getSgOficina(), getSgDireccion(), sesion.getUsuario(), Constantes.BOOLEAN_FALSE, getUser(), getIdPais());
    }

    public void eliminarOficina() throws SIAException, Exception {
	sgOficinaImpl.eliminarOficina(getSgOficinaAnalista().getSgOficina(), getSgOficinaAnalista(), sesion.getUsuario(), Constantes.BOOLEAN_TRUE);
    }

    public void saveSgOficinaAnalista(int idSgOficina, String nombreAnalista) {
	Usuario analista = this.usuarioImpl.buscarPorNombre(nombreAnalista);
	this.sgOficinaAnalistaImpl.save(analista.getId(), idSgOficina, this.sesion.getUsuario().getId());
    }

    public void marcarPrincipal(int idOficinaAnalista, int idOficina) {
	sgOficinaAnalistaImpl.marcarPrincipal(idOficinaAnalista, idOficina, sesion.getUsuario().getId());
    }

    public void deleteSgOficinaAnalista(int idSgOficinaAnalista) {
	this.sgOficinaAnalistaImpl.delete(idSgOficinaAnalista, this.sesion.getUsuario().getId());
    }

    public List<SelectItem> getListaUsuario(String cadenaDigitada) {
	return soporteProveedor.regresaUsuarioActivo(cadenaDigitada, 1, "nombre", true, true, false);
    }

    public boolean existeAnalista() {
	UtilLog4j.log.info(this, "Existe analista: " + (usuarioImpl.buscarPorNombre(this.user) != null));
	if (usuarioImpl.buscarPorNombre(this.user) != null) {
	    this.usuario = usuarioImpl.buscarPorNombre(this.user);
	}
	return (usuarioImpl.buscarPorNombre(this.user) != null);
    }

    public void completarModificacionOficina() throws SIAException, Exception {
	sgOficinaImpl.modificarOficina(getSgOficinaAnalista(), getSgOficinaAnalista().getSgOficina(), sesion.getUsuario(), getUser(), getIdPais());
    }

    public void guardarOficinaComedor() {
	setSgTipo(sgTipoImpl.find(2));
	sgComedorImpl.guardarOficinaComedor(getSgTipo(), sesion.getOficinaActual().getId(), getSgComedor(), sesion.getUsuario(), Constantes.BOOLEAN_FALSE);
    }

    public void completarModificacioOficinaComedor() throws SIAException, Exception {
	sgComedorImpl.modificacioOficinaComedor(getSgComedor(), sesion.getUsuario());
	//Modificar la Característica Principal
	SgCaracteristicaComedor caracteristicaPrincipalComedor = sgCaracteristicaComedorImpl.getCaracteristicaComedorPrincipalByComedor(sgComedor.getId());
	SgCaracteristica caracteristica = caracteristicaPrincipalComedor.getSgCaracteristica();
	caracteristica.setNombre("Comedor " + this.sgComedor.getNombre());
	sgCaracteristicaImpl.update(caracteristica, sesion.getUsuario().getId());
	//Recargar Lista de Comedores
	mapaLista.put("comedorOficina", new ListDataModel(sgComedorImpl.traerComedorPorOficina(sesion.getOficinaActual().getId(), Constantes.BOOLEAN_FALSE)));
    }

    public void eliminarComedorOficina() throws SIAException, Exception {
	sgComedorImpl.eliminarComedorOficina(getSgComedor(), sesion.getUsuario(), Constantes.BOOLEAN_TRUE);
	SgCaracteristicaComedor caracteristicaPrincipalComedor = sgCaracteristicaComedorImpl.getCaracteristicaComedorPrincipalByComedor(sgComedor.getId());
	sgCaracteristicaImpl.delete(caracteristicaPrincipalComedor.getSgCaracteristica(), sesion.getUsuario().getId());
	//Recargar Lista de Comedores
	mapaLista.put("comedorOficina", new ListDataModel(sgComedorImpl.traerComedorPorOficina(sesion.getOficinaActual().getId(), Constantes.BOOLEAN_FALSE)));
    }

    //SALA JUNTA
    public void guardarOficinaSalaJunta() {
	setSgTipo(sgTipoImpl.find(2));
	sgSalaJuntaImpl.guardarOficinaSalaJunta(getSgTipo(), sesion.getOficinaActual().getId(), getSgSalaJunta(), sesion.getUsuario(), Constantes.BOOLEAN_FALSE);

    }

    public void traerSalaJuntaOficina() {
	mapaLista.put("salaOficina", new ListDataModel(sgSalaJuntaImpl.traerSalaJuntaOficina(sesion.getOficinaActual().getId(), Constantes.BOOLEAN_FALSE)));

    }

    public void modificarSalaJuntaOficina() throws SIAException, Exception {
	sgSalaJuntaImpl.modificarSalaJuntaOficina(getSgSalaJunta(), sesion.getUsuario());
	//Modificar la Característica Principal
	SgCaracteristicaSalaJunta caracteristicaPrincipalSalaJunta = sgCaracteristicaSalaJuntaImpl.getCaracteristicaSalaJuntaPrincipalBySalaJunta(sgSalaJunta);
	SgCaracteristica caracteristica = caracteristicaPrincipalSalaJunta.getSgCaracteristica();
	caracteristica.setNombre("Sala de Juntas " + this.sgSalaJunta.getNombre());
	sgCaracteristicaImpl.update(caracteristica, sesion.getUsuario().getId());
	//Recargar Lista de Sala de Juntas
	mapaLista.put("salaOficina", new ListDataModel(sgSalaJuntaImpl.traerSalaJuntaOficina(sesion.getOficinaActual().getId(), Constantes.BOOLEAN_FALSE)));
    }

    public void eliminarSalaJuntaOficina() throws SIAException, Exception {
	sgSalaJuntaImpl.eliminarSalaJuntaOficina(getSgSalaJunta(), sesion.getUsuario(), Constantes.BOOLEAN_TRUE);
	SgCaracteristicaSalaJunta caracteristicaPrincipalSalaJunta = sgCaracteristicaSalaJuntaImpl.getCaracteristicaSalaJuntaPrincipalBySalaJunta(sgSalaJunta);
	sgCaracteristicaImpl.delete(caracteristicaPrincipalSalaJunta.getSgCaracteristica(), sesion.getUsuario().getId());
	//Recargar Lista de Sala de Juntas
	mapaLista.put("salaOficina", new ListDataModel(sgSalaJuntaImpl.traerSalaJuntaOficina(sesion.getOficinaActual().getId(), Constantes.BOOLEAN_FALSE)));

    }

    //SANITARIOS
    public void traerSanitario() {
	mapaLista.put("sanitarioOficina", new ListDataModel(sgSanitarioImpl.traerSanitario(sesion.getOficinaActual().getId(), Constantes.BOOLEAN_FALSE)));
    }

    public void guardarOficinaSanitario() {
	setSgTipo(sgTipoImpl.find(2));
	sgSanitarioImpl.guardarOficinaSanitario(getSgTipo(), sesion.getOficinaActual().getId(), getSgSanitario(), sesion.getUsuario(), Constantes.BOOLEAN_FALSE);
    }

    public void eliminarSanitarioOficina() throws SIAException, Exception {
	sgSanitarioImpl.eliminarOficinaSanitario(getSgSanitario(), sesion.getUsuario(), Constantes.BOOLEAN_TRUE);
	//Modificar la Característica Principal
	SgCaracteristicaSanitario caracteristicaPrincipalSanitario = sgCaracteristicaSanitarioImpl.getCaracteristicaSanitarioPrincipalBySanitario(sgSanitario);
	sgCaracteristicaImpl.delete(caracteristicaPrincipalSanitario.getSgCaracteristica(), sesion.getUsuario().getId());
	//Recargar Lista de Sanitarios
	mapaLista.put("sanitario", new ListDataModel(sgSalaJuntaImpl.traerSalaJuntaOficina(sesion.getOficinaActual().getId(), Constantes.BOOLEAN_FALSE)));
    }

    public void completarModificacionSanitarioOficina() throws SIAException, Exception {
	sgSanitarioImpl.modificarOficinaSanitario(getSgSanitario(), sesion.getUsuario());
	SgCaracteristicaSanitario caracteristicaPrincipalSanitario = sgCaracteristicaSanitarioImpl.getCaracteristicaSanitarioPrincipalBySanitario(sgSanitario);
	SgCaracteristica caracteristica = caracteristicaPrincipalSanitario.getSgCaracteristica();
	caracteristica.setNombre("Sanitario " + this.sgSanitario.getNombre());
	sgCaracteristicaImpl.update(caracteristica, sesion.getUsuario().getId());
	//Recargar Lista de Sanitarios
	mapaLista.put("sanitario", new ListDataModel(sgSalaJuntaImpl.traerSalaJuntaOficina(sesion.getOficinaActual().getId(), Constantes.BOOLEAN_FALSE)));
    }

    public List<SelectItem> traerListaSanitario() {
	List<SgSanitario> lc;
	try {
	    List<SelectItem> l = new ArrayList<SelectItem>();
	    lc = sgSanitarioImpl.traerSanitario(sesion.getOficinaActual().getId(), Constantes.BOOLEAN_FALSE);
	    for (SgSanitario sgS : lc) {
		SelectItem item = new SelectItem(sgS.getId(), sgS.getNombre() + " || " + sgS.getSexo());
		l.add(item);
	    }
	    return l;
	} catch (Exception e) {
	    return null;
	}
    }

    public void buscarSanitarioPorId() {
	setSgSanitario(sgSanitarioImpl.find(getIdSanitario()));
    }

    public void traerBitacoraSanitario() {
	mapaLista.put("bitacoraSanitario", new ListDataModel(sgCtrlMantenimientoSanitarioImpl.traerBitacoraSanitario(getIdSanitario(), Constantes.BOOLEAN_FALSE)));
    }

    public void guardarBitacoraSanitario() {
	sgCtrlMantenimientoSanitarioImpl.guardarBitacoraSanitario(sesion.getUsuario(), getSgSanitario(), getSgCtrlMantenimientoSanitario(), Constantes.BOOLEAN_FALSE);
    }

    public void modificarBitacoraSanitario() {
	sgCtrlMantenimientoSanitarioImpl.modificarBitacoraSanitario(sesion.getUsuario(), getSgCtrlMantenimientoSanitario(), Constantes.BOOLEAN_FALSE);
    }

    public void eliminarBitacoraSanitario() {
	if (getSgCtrlMantenimientoSanitario().getSiAdjunto() != null) {
	    eliminarAdjuntoBitacoraSanitario();
	    sgCtrlMantenimientoSanitarioImpl.modificarBitacoraSanitario(sesion.getUsuario(), getSgCtrlMantenimientoSanitario(), Constantes.BOOLEAN_TRUE);
	} else {
	    sgCtrlMantenimientoSanitarioImpl.modificarBitacoraSanitario(sesion.getUsuario(), getSgCtrlMantenimientoSanitario(), Constantes.BOOLEAN_TRUE);
	}
    }

    public boolean eliminarAdjuntoBitacoraSanitario() {
	boolean valid = false;

	SiAdjunto siAdjunto = getSgCtrlMantenimientoSanitario().getSiAdjunto();

	if (siAdjunto != null) {

	    try {
		proveedorAlmacenDocumentos.getAlmacenDocumentos().borrarDocumento(siAdjunto.getUrl());
		valid
			= sgCtrlMantenimientoSanitarioImpl.eliminarAdjuntoBitacoraSanitario(
				sesion.getUsuario(),
				getSgCtrlMantenimientoSanitario()
			);
		if (valid) {
		    siAdjuntoImpl.eliminarArchivo(
			    siAdjunto,
			    sesion.getUsuario().getId(),
			    Constantes.BOOLEAN_TRUE
		    );
		}
	    } catch (SIAException e) {
		LOGGER.error(e);
	    }
	}

//	//Se eliminan físicamente los archivos
//	//String path = this.siParametroImpl.find(1).getUploadDirectory();
//	try {
//
////            File file = new File(path + getSgCtrlMantenimientoSanitario().getSiAdjunto().getUrl());
//	    Files.delete(Paths.get(path + getSgCtrlMantenimientoSanitario().getSiAdjunto().getUrl()));
//
//	    UtilLog4j.log.info(this, "Archivo: " + path + getSgCtrlMantenimientoSanitario().getSiAdjunto().getUrl());
////            if (file.delete()) {
//	    valid = sgCtrlMantenimientoSanitarioImpl.eliminarAdjuntoBitacoraSanitario(sesion.getUsuario(), getSgCtrlMantenimientoSanitario());
//	    if (valid) {
//		siAdjuntoImpl.eliminarArchivo(getSgCtrlMantenimientoSanitario().getSiAdjunto(), sesion.getUsuario().getId(), Constantes.BOOLEAN_TRUE);
//	    }
////            }
//	} catch (Exception e) {
//	    UtilLog4j.log.info(this, e.getMessage());
//	}
//	//Elimina la carpeta
////         for (PcTipoDocumento pcTipoDocumento : this.pcClasificacionServicioRemoto.traerArchivoActivo()) {
//	String dir = "SGyL/Bitacora/" + getSgSanitario().getNombre() + "/" + getSgCtrlMantenimientoSanitario().getId();
//	UtilLog4j.log.info(this, "Ruta carpeta: " + dir);
//	File sessionfileUploadDirectory = new File(path + dir);
//	if (sessionfileUploadDirectory.isDirectory()) {
//	    try {
//		sessionfileUploadDirectory.delete();
//	    } catch (SecurityException e) {
//		UtilLog4j.log.info(this, e.getMessage());
//	    }
//	}
//        }
	return valid;
    }

    public boolean guardarAdjuntoBitacoraSanitario(String fileName, String contentType, long size) {
	boolean v;
	SiAdjunto siAdjunto = siAdjuntoImpl.guardarArchivoDevolverArchivo(sesion.getUsuario().getId(), 1, "SGyL/Bitacora/" + getSgSanitario().getNombre() + "/" + getSgCtrlMantenimientoSanitario().getId() + "/" + fileName, fileName, contentType, size, 9, "sgl");
	if (siAdjunto != null) {
	    sgCtrlMantenimientoSanitarioImpl.guardarAdjuntoBitacoraSanitario(sesion.getUsuario(), getSgCtrlMantenimientoSanitario(), siAdjunto);
	    v = true;
	    setSubirArchivo(false);
	} else {
	    v = false;
	}
	return v;
    }

    public String getDirBitacora() {
	String retVal = Constantes.VACIO;

	if (getSgCtrlMantenimientoSanitario() != null) {
	    retVal
		    = "SGyL/Bitacora/" + getSgSanitario().getNombre()
		    + "/" + getSgCtrlMantenimientoSanitario().getId();
	}
	return retVal;
    }

    //PLANO
    public String dirPlano() {
	String retVal = Constantes.VACIO;

	if (sesion.getOficinaActual() != null) {
	    retVal = "SGyL/Plano/" + sesion.getOficinaActual().getId() + "/";
	    //return siParametroImpl.find(1).getUploadDirectory() + "SGyL/Plano/" + sesion.getOficinaActual().getId() + "/";
	}
	return retVal;
    }

    public boolean guardarPlanoOficina(String fileName, String path, String contentType, long size) {
	boolean v = false;
	SiAdjunto siAdjunto
		= siAdjuntoImpl.guardarArchivoDevolverArchivo(
			sesion.getUsuario().getId(),
			1,
			path + fileName,
			fileName,
			contentType,
			size,
			9,
			"sgl"
		);
	if (siAdjunto != null) {
	    List<SgOficinaPlano> op = sgOficinaPlanoImpl.buscarPlanoVigente(sesion.getOficinaActual().getId());
	    if (op != null) {
		for (SgOficinaPlano sgOP : op) {
		    sgOficinaPlanoImpl.quitarVigenteOficinaPlano(sesion.getUsuario(), sgOP);
		}
	    }
	    sgOficinaPlanoImpl.guardarOficinaPlano(sesion.getUsuario(), sesion.getOficinaActual().getId(), siAdjunto);

	    v = true;
	}
	return v;
    }

    public DataModel traerPlanoOficina() {
	this.listaPlano = new ListDataModel(sgOficinaPlanoImpl.traerPlanoOficina(sesion.getOficinaActual().getId()));
	return this.listaPlano;
    }

    public void eliminarPlano() {
	boolean v = false;
	//Se eliminan fisicamente los archivos
	String path = this.siParametroImpl.find(1).getUploadDirectory();
	try {
	    File file = new File(path + getSgOficinaPlano().getSiAdjunto().getUrl());
	    UtilLog4j.log.info(this, "Archivo: " + path + getSgOficinaPlano().getSiAdjunto().getUrl());
	    if (file.delete()) {
		v = sgOficinaPlanoImpl.eliminarOficinaPlano(sesion.getUsuario(), getSgOficinaPlano());
		if (v) {
		    siAdjuntoImpl.eliminarArchivo(getSgCtrlMantenimientoSanitario().getSiAdjunto(), sesion.getUsuario().getId(), Constantes.BOOLEAN_TRUE);
		}
	    }
	} catch (Exception e) {
	    UtilLog4j.log.info(this, e.getMessage());
	}
    }

    public List<SgOficinaAnalistaVo> getAllSgOficinaAnalistaBySgOficina(int idSgOficina) {
	return this.sgOficinaAnalistaImpl.getAllSgOficinaAnalista(idSgOficina, "nombre", true, false);
    }

    //CARACTERISTICAS
    /**
     * Devuelve siempre todas las Características que no sean secundarias y no
     * estén eliminadas y las asigna a las lista de Características usada para
     * el autocomplete
     */
    public void getAllCaracteristicas() {
	try {
	    this.caracteristicas = new ArrayList<SelectItem>();
	    this.matchesList = new ArrayList<SelectItem>();
	    List<CaracteristicaVo> cars = this.sgCaracteristicaImpl.getAllSgCaracteristicaStaffAndOficina();
	    if (cars != null && !cars.isEmpty()) {
		for (CaracteristicaVo c : cars) {
		    SelectItem si = new SelectItem(c.getNombre());
		    this.caracteristicas.add(si);
		}
	    }
	} catch (Exception e) {
	}
    }

    public void caracteristicasComedor() throws SIAException, Exception {
	mapaLista.put("comedor", new ListDataModel(sgOficinaImpl.getAllCaracteristicasComedorList(this.sgComedor.getId())));
    }

    public void caracteristicasSalaJunta() throws SIAException, Exception {
	mapaLista.put("sala", new ListDataModel(sgOficinaImpl.getAllCaracteristicasSalaJuntaList(this.sgSalaJunta.getId())));
    }

    public void caracteristicasSanitario() throws SIAException, Exception {
	mapaLista.put("sanitario", new ListDataModel(sgOficinaImpl.getAllCaracteristicasSanitarioList(this.sgSanitario.getId())));
    }

    public void addCaracteristica() throws SIAException, Exception {
//        UtilLog4j.log.info(this, "OficinaBeanModel.addCaracteristica()");
	SgCaracteristica car = sgOficinaImpl.addCaracteristica(this.object, sesion.getOficinaActual(), sgCaracteristicaOficina.getNombre(), this.cantidadCaracteristica, sesion.getUsuario().getId());
	//Si se devolvió una Característica que se agregó nueva y hay que agregarla a la lista 'caracteristicasListSelectItemAll'
	if (car != null) {
	    this.caracteristicas.add(new SelectItem(car.getNombre()));
	}
	//Recargar DataModel que muestra las relaciones
	mapaLista.put("caracteristica_oficina", new ListDataModel(sgOficinaImpl.getAllCaracteristicasOficinaList(sesion.getOficinaActual().getId())));

    }

    public void addCaracteristicaComedor() throws SIAException, Exception {
	SgCaracteristica car = sgOficinaImpl.addCaracteristicaComedor(sesion.getOficinaActual().getId(), sgCaracteristicaOficina.getNombre(), sgComedor.getId(), this.getCantidadCaracteristica(), sesion.getUsuario().getId());
	//Si se devolvió una Característica que se agregó nueva y hay que agregarla a la lista 'caracteristicasListSelectItemAll'
	if (car != null) {
	    this.caracteristicas.add(new SelectItem(car.getNombre()));
	}
	//Recargar DataModel que muestra las relaciones
	mapaLista.put("comedor", new ListDataModel(sgOficinaImpl.getAllCaracteristicasComedorList(this.sgComedor.getId())));

    }

    public void addCaracteristicaSala() throws SIAException, Exception {
	SgCaracteristica car = sgOficinaImpl.addCaracteristicaSala(sesion.getOficinaActual().getId(), sgCaracteristicaOficina.getNombre(), sgSalaJunta.getId(), this.getCantidadCaracteristica(), sesion.getUsuario().getId());
	//Si se devolvió una Característica que se agregó nueva y hay que agregarla a la lista 'caracteristicasListSelectItemAll'
	if (car != null) {
	    this.caracteristicas.add(new SelectItem(car.getNombre()));
	}
	//Recargar DataModel que muestra las relaciones
	mapaLista.put("sala", new ListDataModel(sgOficinaImpl.getAllCaracteristicasSalaJuntaList(this.sgSalaJunta.getId())));

    }

    public void addCaracteristicaSanitario() throws SIAException, Exception {
	SgCaracteristica car = sgOficinaImpl.addCaracteristicaSanitario(sesion.getOficinaActual().getId(), sgCaracteristicaOficina.getNombre(), sgSanitario.getId(), this.getCantidadCaracteristica(), sesion.getUsuario().getId());
	//Si se devolvió una Característica que se agregó nueva y hay que agregarla a la lista 'caracteristicasListSelectItemAll'
	if (car != null) {
	    this.caracteristicas.add(new SelectItem(car.getNombre()));
	}
	//Recargar DataModel que muestra las relaciones
	mapaLista.put("sanitario", new ListDataModel(sgOficinaImpl.getAllCaracteristicasSanitarioList(this.sgSanitario.getId())));

    }

    /**
     * Quita una relación entre una Característica y un área (SgOficina,
     * SgComedor, SgSalaJunta, SgSanitario)
     *
     * @param o
     * @throws SIAException
     * @throws Exception
     */
    public void removeCaracteristica() throws SIAException, Exception {
//        UtilLog4j.log.info(this, "StaffBeanModel.removeCaracteristica()");
	sgOficinaImpl.removeCaracteristica(this.sgCaracteristicaOficina, sesion.getUsuario().getId());
	mapaLista.put("caracteristica_oficina", new ListDataModel(sgOficinaImpl.getAllCaracteristicasOficinaList(this.sesion.getOficinaActual().getId())));

    }

    public void removeCaracteristicaComedor() throws SIAException, Exception {
	sgOficinaImpl.removeCaracteristicaComedor(sgCaracteristicaOficina, sesion.getUsuario().getId());
	mapaLista.put("comedor", new ListDataModel(sgOficinaImpl.getAllCaracteristicasComedorList(this.sgComedor.getId())));
    }

    public void removeCaracteristicaSala() throws SIAException, Exception {
	sgOficinaImpl.removeCaracteristicaSala(this.sgCaracteristicaOficina, sesion.getUsuario().getId());
	mapaLista.put("sala", new ListDataModel(sgOficinaImpl.getAllCaracteristicasSalaJuntaList(this.sgSalaJunta.getId())));
    }

    public void removeCaracteristicaSanitario() throws SIAException, Exception {
	sgOficinaImpl.removeCaracteristicaSanitario(this.sgCaracteristicaOficina, sesion.getUsuario().getId());
	mapaLista.put("sanitario", new ListDataModel(sgOficinaImpl.getAllCaracteristicasSanitarioList(this.sgSanitario.getId())));
    }

    public void buscarTipoGeneral() {
	List<SgTipo> lt = sgTipoImpl.traerTipo(sesion.getUsuario(), Constantes.BOOLEAN_FALSE);
	for (SgTipo sgT : lt) {
	    if (sgT.getNombre().toUpperCase().equals(getOpcionPagar().toUpperCase())) {
		setSgTipo(sgT);
		break;
	    }
	}
    }

    public List<SiPais> getAllSiPais() {
	return this.siPaisImpl.findAll("nombre", true, false);
    }

    public List<SiEstado> getAllSiEstado(int idSiPais) {
	return this.siEstadoImpl.findAll(idSiPais, "nombre", true, false);
    }

    public List<SiCiudad> getAllSiCiudad(int idSiEstado) {
	return this.siCiudadImpl.findAll(idSiEstado, "nombre", true, false);
    }

    public List<SelectItem> listaPais() {
	List<SelectItem> l = new ArrayList<SelectItem>();
	try {
	    List<SiPais> lt = siPaisImpl.findAll("nombre", true, false);
	    for (SiPais siPais : lt) {
		SelectItem item = new SelectItem(siPais.getId(), siPais.getNombre());
		l.add(item);
	    }
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Fui a ver que tenia la excepción de traer paises");
	}
	return l;
    }

    public List<SelectItem> traerCasaStaff() {
	List<SgStaff> lc;
	try {
	    List<SelectItem> l = new ArrayList<SelectItem>();
	    lc = sgStaffImpl.getAllStaffByStatusAndOficina(Constantes.BOOLEAN_FALSE, sesion.getOficinaActual().getId());
	    for (SgStaff st : lc) {
		SelectItem item = new SelectItem(st.getId(), st.getNombre());
		l.add(item);
	    }
	    setListaPagos(l);
	    return getListaPagos();
	} catch (Exception e) {
	    return null;
	}
    }

    public List<SelectItem> traerTipoEspecificoPorTipoOficina() {

	if (sesion.getOficinaActual().getId() > 0) {
	    List<SelectItem> l = new ArrayList<SelectItem>();
	    List<SgTipoTipoEspecifico> lc;
	    try {
		lc = sgTipoTipoEspecificoImpl.traerPorTipoPago(getSgTipo(), Constantes.BOOLEAN_FALSE, Constantes.BOOLEAN_TRUE);
		for (SgTipoTipoEspecifico tipoEsp : lc) {
		    SelectItem item = new SelectItem(tipoEsp.getSgTipoEspecifico().getId(), tipoEsp.getSgTipoEspecifico().getNombre());
		    l.add(item);
		}
		setListaTipoEspecifico(l);
	    } catch (Exception e) {
		UtilLog4j.log.info(this, "Aqui en la excepción");
	    }
	} else {
	    setListaTipoEspecifico(null);
	}
	return getListaTipoEspecifico();
    }

    public List<SelectItem> traerTipoEspecificoPorTipoStaff() {
	if (getIdStaff() > 0) {
	    List<SelectItem> l = new ArrayList<SelectItem>();
	    List<SgTipoTipoEspecifico> lc;
	    try {
		lc = sgTipoTipoEspecificoImpl.traerPorTipoPago(getSgTipo(), Constantes.BOOLEAN_FALSE, Constantes.BOOLEAN_TRUE);
		for (SgTipoTipoEspecifico tipoEsp : lc) {
		    SelectItem item = new SelectItem(tipoEsp.getSgTipoEspecifico().getId(), tipoEsp.getSgTipoEspecifico().getNombre());
		    l.add(item);
		}
		setListaTipoEspecifico(l);
	    } catch (Exception e) {
		UtilLog4j.log.info(this, "Aqui en la excepción");
	    }
	} else {
	    setListaTipoEspecifico(null);
	}
	return getListaTipoEspecifico();
    }

    public List<SelectItem> traerMoneda() {
	List<MonedaVO> lc;
	try {
	    List<SelectItem> l = new ArrayList<SelectItem>();
	    lc = monedaImpl.traerMonedaActiva(Constantes.AP_CAMPO_DEFAULT);
	    UtilLog4j.log.info(this, "LMon: " + lc.size());
	    for (MonedaVO mon : lc) {
		SelectItem item = new SelectItem(mon.getId(), mon.getSiglas());
		l.add(item);
	    }
	    return l;
	} catch (Exception e) {
	    return null;
	}
    }

    public Proveedor buscarProveedorPorNombre() {
	try {
	    return proveedorImpl.getPorNombre(getPro(), sesion.getRfcEmpresa());
	} catch (Exception e) {
	    return null;
	}
    }

    public boolean guardarPagoServicioOficina() {
	boolean retVal = false;

	try {
	    sgPagoServicioImpl.guardarPagoServicio(
		    Constantes.TIPO_PAGO_OFICINA,
		    getSgTipoEspecifico(),
		    getSgPagoServicio(),
		    sesion.getOficinaActual(),
		    sesion.getUsuario(), Constantes.BOOLEAN_FALSE,
		    getIdMoneda(),
		    getOpcionPagar(),
		    getPro(),
		    sesion.getRfcEmpresa()
	    );
	    traerPagoPorOficina();
	    retVal = true;
	} catch (Exception e) {
	    LOGGER.error(e);
	}

	return retVal;
    }

    public void modificarPagoServicio() {
	sgPagoServicioImpl.modificarPagoServicio(getSgPagoServicio(), sesion.getUsuario(), getIdMoneda());
    }

    public void eliminarPagoServicioOficina() {
	sgPagoServicioImpl.eliminarPagoServicio(sesion.getOficinaActual(), getSgPagoServicioOficina(), sesion.getUsuario(), Constantes.BOOLEAN_TRUE);
	if (getSgPagoServicio().getSiAdjunto() != null) {
	    eliminarComprobante();
	}
    }

    public String getDirectorio() {
	return "SGyL/Pago/" + getSgTipo().getNombre() + "/" + getSgPagoServicio().getId() + "/";
    }

    public boolean guardarArchivo(String fileName, String contentType, long size) {
	UtilLog4j.log.info(this, "Absolute path " + getDirectorio());
	boolean v = false;
	SiAdjunto siAdjunto
		= siAdjuntoImpl.guardarArchivoDevolverArchivo(
			sesion.getUsuario().getId(),
			1,
			"SGyL/Pago/" + getSgTipo().getNombre() + "/" + getSgPagoServicio().getId() + "/" + fileName,
			fileName,
			contentType,
			size,
			9,
			"SGyL"
		);
	UtilLog4j.log.info(this, "Aqui después de guardar el archivo");
	if (siAdjunto != null) {
	    v = sgPagoServicioImpl.agregarArchivoPagoServicio(getSgPagoServicio(), sesion.getUsuario(), siAdjunto);
	    UtilLog4j.log.info(this, "Ahora déspues de agreegar a pago servicio");
	    if (v) {
		v = true;
	    } else {
		siAdjuntoImpl.remove(siAdjunto);
	    }
	}
	return v;
    }

    public void eliminarComprobante() {
	//Se eliminan fisicamente los archivos
	String path = this.siParametroImpl.find(1).getUploadDirectory();
	try {
	    File file = new File(path + getSgPagoServicio().getSiAdjunto().getUrl());
	    if (file.delete()) {
		getSgPagoServicio().setSiAdjunto(null);
		sgPagoServicioImpl.modificarPagoServicio(getSgPagoServicio(), sesion.getUsuario(), getSgPagoServicio().getMoneda().getId());
		siAdjuntoImpl.eliminarArchivo(getSgPagoServicio().getSiAdjunto(), sesion.getUsuario().getId(), Constantes.BOOLEAN_TRUE);
	    }
	    //Elimina la carpeta
	    //         for (PcTipoDocumento pcTipoDocumento : this.pcClasificacionServicioRemoto.traerArchivoActivo()) {
	    String dir = "SGyL/Pago/" + getSgTipo().getNombre() + "/" + getSgPagoServicio().getId();
	    UtilLog4j.log.info(this, "Ruta carpeta: " + dir);
	    File sessionfileUploadDirectory = new File(path + dir);
	    if (sessionfileUploadDirectory.isDirectory()) {
		try {
		    sessionfileUploadDirectory.delete();
		} catch (SecurityException e) {
		    UtilLog4j.log.info(this, e.getMessage());
		}
	    }
	    //        }
	} catch (Exception e) {
	    UtilLog4j.log.info(this, e.getMessage());
	}
    }

    public DataModel traerConvenioPorProveedor() {
	setListaContratoProveedor(new ListDataModel(convenioImpl.getConveniosPorProveedor(getPro())));
	return getListaContratoProveedor();
    }

    public List<SgHistorialConvenioOficina> buscarRelacionConvenio() {
	return sgHistorialConvenioOficinaImpl.buscarRelacionConvenio(sesion.getOficinaActual().getId(), getConvenio());
    }

    public void quitarContratoVigente() {
	sgHistorialConvenioOficinaImpl.quitarContratoVigente(getSgHistorialConvenioOficina(), sesion.getUsuario(), sesion.getOficinaActual().getId());
    }

    public SgHistorialConvenioOficina asignarContratoOficina() {
	return sgHistorialConvenioOficinaImpl.asignarContratoOficina(sesion.getOficinaActual().getId(), getConvenio(), sesion.getUsuario());
    }

    public void eliminarRelacionConvenioOficina() {
	sgHistorialConvenioOficinaImpl.eliminarConvenioVigente(getSgHistorialConvenioOficina(), sesion.getUsuario());

    }

    public DataModel traerHistorialConvenio() {
	try {
	    if (getListaRegistro() == null) {
		setListaRegistro(new ListDataModel(sgHistorialConvenioOficinaImpl.traerHistorialConvenio(sesion.getOficinaActual().getId())));
	    }
	} catch (Exception e) {
	    return null;
	}
	return getListaRegistro();
    }

    public void buscarAdjuntoConvenio() {
	List<SiAdjunto> list = siAdjuntoImpl.traerArchivos(6, getSgHistorialConvenioOficina().getConvenio().getId(), "Convenio");
	if (!list.isEmpty()) {
	    SiAdjunto siAdjunto;
	    siAdjunto = list.get(0);
	    setIdAdjunto(siAdjunto.getId());
	    setUuid(siAdjunto.getUuid());
	}
	//Debiera abrir todos archivos que esten ligados
//        getUrl().concat(String.valueOf(getIdAdjunto()));
    }

    public DataModel traerAdjuntoContrato() {
	if (getConvenio() != null) {
	    setListaVistoBueno(new ListDataModel(siAdjuntoImpl.traerArchivos(6, getConvenio().getId(), "Convenio")));
	} else {
	    setListaVistoBueno(null);
	}
	return getListaVistoBueno();
    }

////////    public DataModel traerArchivoConvenioOficia() {
////////        try {
////////            setLista(new ListDataModel(siAdjuntoImpl.traerArchivos(6, getSgHistorialConvenioOficina().getConvenio().getId(), "Convenio")));
////////            return getLista();
////////        } catch (Exception e) {
////////            return null;
////////        }
////////    }
////////
////    public DataModel traerArchivoConvenioOficiaHistorial() {
////        try {
////            setLista(new ListDataModel(siAdjuntoImpl.traerArchivos(6, getConvenio().getId(), "Convenio")));
////            return getLista();
////        } catch (Exception e) {
////            return null;
////        }
////    }
    public List<SiAdjunto> buscarAdjuntoConvenioNuevo() {
	List<SiAdjunto> list = siAdjuntoImpl.traerArchivos(6, getSgHistorialConvenioOficina().getConvenio().getId(), "Convenio");
	return list;
	//Debiera abrir todos archivos que esten ligados
//        getUrl().concat(String.valueOf(getIdAdjunto()));
    }

    //CONTRATO
    public void buscarContratoVigente() {
	setSgHistorialConvenioOficina(sgHistorialConvenioOficinaImpl.traerContratoVigente(sesion.getOficinaActual().getId()));
    }

    public DataModel traerContratoOficina() {
	if (getSgHistorialConvenioOficina() != null) {
	    setListaContrato(new ListDataModel(siAdjuntoImpl.traerArchivos(6, getSgHistorialConvenioOficina().getConvenio().getId(), "Convenio")));
	    setTamanioLista(getListaContrato().getRowCount());
	    return getListaContrato();
	} else {
	    return null;
	}
    }

    public DataModel traerContratoOficinaHistorial() {
	try {
	    return new ListDataModel(siAdjuntoImpl.traerArchivos(6, getConvenio().getId(), "Convenio"));
	} catch (Exception e) {
	    return null;
	}
    }

    public boolean isCrearPopUp() {
	return crearPopUp;
    }

    public void setCrearPopUp(boolean crearPopUp) {
	this.crearPopUp = crearPopUp;
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
     * @return the usuario
     */
    public Usuario getUsuario() {
	return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(Usuario usuario) {
	this.usuario = usuario;
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

    public List<SelectItem> getListaUsuariosAlta() {
	return listaUsuariosAlta;
    }

    public void setListaUsuariosAlta(List<SelectItem> listaUsuariosAlta) {
	this.listaUsuariosAlta = listaUsuariosAlta;
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
     * @return the listaRegistro
     */
    public DataModel getListaRegistro() {
	return listaRegistro;
    }

    /**
     * @param listaRegistro the listaRegistro to set
     */
    public void setListaRegistro(DataModel listaRegistro) {
	this.listaRegistro = listaRegistro;
    }

    /**
     * @return the listaVistoBueno
     */
    public DataModel getListaVistoBueno() {
	return listaVistoBueno;
    }

    /**
     * @param listaVistoBueno the listaVistoBueno to set
     */
    public void setListaVistoBueno(DataModel listaVistoBueno) {
	this.listaVistoBueno = listaVistoBueno;
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
     * @return the sgOficinaAnalista
     */
    public SgOficinaAnalista getSgOficinaAnalista() {
	return sgOficinaAnalista;
    }

    /**
     * @param sgOficinaAnalista the sgOficinaAnalista to set
     */
    public void setSgOficinaAnalista(SgOficinaAnalista sgOficinaAnalista) {
	this.sgOficinaAnalista = sgOficinaAnalista;
    }

    /**
     * @return the opcionSeleccionada
     */
    public int getOpcionSeleccionada() {
	return opcionSeleccionada;
    }

    /**
     * @param opcionSeleccionada the opcionSeleccionada to set
     */
    public void setOpcionSeleccionada(int opcionSeleccionada) {
	this.opcionSeleccionada = opcionSeleccionada;
    }

    /**
     * @return the sgOficinaComedor
     */
    public SgComedor getSgComedor() {
	return sgComedor;
    }

    /**
     * @param SgComedor the SgComedor to set
     */
    public void setSgComedor(SgComedor sgComedor) {
	this.sgComedor = sgComedor;
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
     * @return the SgSalaJunta
     */
    public SgSalaJunta getSgSalaJunta() {
	return sgSalaJunta;
    }

    /**
     * @param SgSalaJunta the SgSalaJunta to set
     */
    public void setSgSalaJunta(SgSalaJunta SgSalaJunta) {
	this.sgSalaJunta = SgSalaJunta;
    }

    /**
     * @return the sgOficinaSanitario
     */
    public SgSanitario getSgSanitario() {
	return sgSanitario;
    }

    /**
     * @param sgOficinaSanitario the sgOficinaSanitario to set
     */
    public void setSgSanitario(SgSanitario sgSanitario) {
	this.sgSanitario = sgSanitario;
    }

    /**
     * @return the listaContrato
     */
    public DataModel getListaContrato() {
	return listaContrato;
    }

    /**
     * @param listaContrato the listaContrato to set
     */
    public void setListaContrato(DataModel listaContrato) {
	this.listaContrato = listaContrato;
    }

    /**
     * @return the sgCaracteristicaOficina
     */
    public SgCaracteristica getSgCaracteristica() {
	return sgCaracteristica;
    }

    /**
     * @param sgCaracteristica
     */
    public void setSgCaracteristica(SgCaracteristica sgCaracteristica) {
	this.sgCaracteristica = sgCaracteristica;
    }

    /**
     * @return the mostrarPanel
     */
    public boolean isMostrarPanel() {
	return mostrarPanel;
    }

    /**
     * @param mostrarPanel the mostrarPanel to set
     */
    public void setMostrarPanel(boolean mostrarPanel) {
	this.mostrarPanel = mostrarPanel;
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
     * @return the sgPagoServicio
     */
    public SgPagoServicio getSgPagoServicio() {
	return sgPagoServicio;
    }

    /**
     * @param sgPagoServicio the sgPagoServicio to set
     */
    public void setSgPagoServicio(SgPagoServicio sgPagoServicio) {
	this.sgPagoServicio = sgPagoServicio;
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
     * @return the listaTipoEspecifico
     */
    public List<SelectItem> getListaTipoEspecifico() {
	return listaTipoEspecifico;
    }

    /**
     * @param listaTipoEspecifico the listaTipoEspecifico to set
     */
    public void setListaTipoEspecifico(List<SelectItem> listaTipoEspecifico) {
	this.listaTipoEspecifico = listaTipoEspecifico;
    }

    /**
     * @return the sgTipo
     */
    public SgTipo getSgTipo() {
	return sgTipo;
    }

    /**
     * @param sgTipo the sgTipo to set
     */
    public void setSgTipo(SgTipo sgTipo) {
	this.sgTipo = sgTipo;
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
     * @return the moneda
     */
    public Moneda getMoneda() {
	return moneda;
    }

    /**
     * @param moneda the moneda to set
     */
    public void setMoneda(Moneda moneda) {
	this.moneda = moneda;
    }

    /**
     * @return the idMoneda
     */
    public int getIdMoneda() {
	return idMoneda;
    }

    /**
     * @param idMoneda the idMoneda to set
     */
    public void setIdMoneda(int idMoneda) {
	this.idMoneda = idMoneda;
    }

    /**
     * @return the opcionPagar
     */
    public String getOpcionPagar() {
	return opcionPagar;
    }

    /**
     * @param opcionPagar the opcionPagar to set
     */
    public void setOpcionPagar(String opcionPagar) {
	this.opcionPagar = opcionPagar;
    }

    /**
     * @return the idStaff
     */
    public int getIdStaff() {
	return idStaff;
    }

    /**
     * @param idStaff the idStaff to set
     */
    public void setIdStaff(int idStaff) {
	this.idStaff = idStaff;
    }

    /**
     * @return the sdVehiculo
     */
    public int getIdVehiculo() {
	return idVehiculo;
    }

    /**
     * @param sdVehiculo the sdVehiculo to set
     */
    public void setIdVehiculo(int idVehiculo) {
	this.idVehiculo = idVehiculo;
    }

    /**
     * @return the listaProveedor
     */
    public List<SelectItem> getListaProveedor() {
	return listaProveedor;
    }

    /**
     * @param listaProveedor the listaProveedor to set
     */
    public void setListaProveedor(List<SelectItem> listaProveedor) {
	this.listaProveedor = listaProveedor;
    }

    /**
     * @return the pro
     */
    public String getPro() {
	return pro;
    }

    /**
     * @param pro the pro to set
     */
    public void setPro(String pro) {
	this.pro = pro;
    }

    /**
     * @return the sgPagoServicioOficina
     */
    public SgPagoServicioOficina getSgPagoServicioOficina() {
	return sgPagoServicioOficina;
    }

    /**
     * @param sgPagoServicioOficina the sgPagoServicioOficina to set
     */
    public void setSgPagoServicioOficina(SgPagoServicioOficina sgPagoServicioOficina) {
	this.sgPagoServicioOficina = sgPagoServicioOficina;
    }

    /**
     * @return the sgCtrlMantenimientoSanitario
     */
    public SgCtrlMantenimientoSanitario getSgCtrlMantenimientoSanitario() {
	return sgCtrlMantenimientoSanitario;
    }

    /**
     * @param sgCtrlMantenimientoSanitario the sgCtrlMantenimientoSanitario to
     * set
     */
    public void setSgCtrlMantenimientoSanitario(SgCtrlMantenimientoSanitario sgCtrlMantenimientoSanitario) {
	this.sgCtrlMantenimientoSanitario = sgCtrlMantenimientoSanitario;
    }

    /**
     * @return the idSanitario
     */
    public int getIdSanitario() {
	return idSanitario;
    }

    /**
     * @param idSanitario the idSanitario to set
     */
    public void setIdSanitario(int idSanitario) {
	this.idSanitario = idSanitario;
    }

    /**
     * @return the principal
     */
    public String getPrincipal() {
	return principal;
    }

    /**
     * @param principal the principal to set
     */
    public void setPrincipal(String principal) {
	this.principal = principal;
    }

    /**
     * @return the agragarCaracteristicaOficia
     */
    public boolean isAgregarCaracteristicaOficia() {
	return agregarCaracteristicaOficia;
    }

    /**
     * @param agragarCaracteristicaOficia the agragarCaracteristicaOficia to set
     */
    public void setAgregarCaracteristicaOficia(boolean agregarCaracteristicaOficia) {
	this.agregarCaracteristicaOficia = agregarCaracteristicaOficia;
    }

    /**
     * @return the subirArchivo
     */
    public boolean isSubirArchivo() {
	return subirArchivo;
    }

    /**
     * @param subirArchivo the subirArchivo to set
     */
    public void setSubirArchivo(boolean subirArchivo) {
	this.subirArchivo = subirArchivo;
    }

    /**
     * @return the sgCaracteristicaOficina
     */
    public CaracteristicaVo getSgCaracteristicaOficina() {
	return sgCaracteristicaOficina;
    }

    /**
     * @param sgCaracteristicaOficina the sgCaracteristicaOficina to set
     */
    public void setSgCaracteristicaOficina(CaracteristicaVo sgCaracteristicaOficina) {
	this.sgCaracteristicaOficina = sgCaracteristicaOficina;
    }

    /**
     * @return the verDetallePop
     */
    public boolean isVerDetallePop() {
	return verDetallePop;
    }

    /**
     * @param verDetallePop the verDetallePop to set
     */
    public void setVerDetallePop(boolean verDetallePop) {
	this.verDetallePop = verDetallePop;
    }

    /**
     * @return the listaPagos
     */
    public List<SelectItem> getListaPagos() {
	return listaPagos;
    }

    /**
     * @param listaPagos the listaPagos to set
     */
    public void setListaPagos(List<SelectItem> listaPagos) {
	this.listaPagos = listaPagos;
    }

    /**
     * @return the eliminarPop
     */
    public boolean isEliminarPop() {
	return eliminarPop;
    }

    /**
     * @param eliminarPop the eliminarPop to set
     */
    public void setEliminarPop(boolean eliminarPop) {
	this.eliminarPop = eliminarPop;
    }

    /**
     * @return the sgHistorialConvenioOficina
     */
    public SgHistorialConvenioOficina getSgHistorialConvenioOficina() {
	return sgHistorialConvenioOficina;
    }

    /**
     * @param sgHistorialConvenioOficina the sgHistorialConvenioOficina to set
     */
    public void setSgHistorialConvenioOficina(SgHistorialConvenioOficina sgHistorialConvenioOficina) {
	this.sgHistorialConvenioOficina = sgHistorialConvenioOficina;
    }

    /**
     * @return the listaContratoProveedor
     */
    public DataModel getListaContratoProveedor() {
	return listaContratoProveedor;
    }

    /**
     * @param listaContratoProveedor the listaContratoProveedor to set
     */
    public void setListaContratoProveedor(DataModel listaContratoProveedor) {
	this.listaContratoProveedor = listaContratoProveedor;
    }

    /**
     * @return the convenio
     */
    public Convenio getConvenio() {
	return convenio;
    }

    /**
     * @param convenio the convenio to set
     */
    public void setConvenio(Convenio convenio) {
	this.convenio = convenio;
    }

    /**
     * @return the idAdjunto
     */
    public int getIdAdjunto() {
	return idAdjunto;
    }

    /**
     * @param idAdjunto the idAdjunto to set
     */
    public void setIdAdjunto(int idAdjunto) {
	this.idAdjunto = idAdjunto;
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
     * @return the tamanioLista
     */
    public int getTamanioLista() {
	return tamanioLista;
    }

    /**
     * @param tamanioLista the tamanioLista to set
     */
    public void setTamanioLista(int tamanioLista) {
	this.tamanioLista = tamanioLista;
    }

    /**
     * @return the abrirArchivHistorial
     */
    public boolean isAbrirArchivHistorial() {
	return abrirArchivHistorial;
    }

    /**
     * @param abrirArchivHistorial the abrirArchivHistorial to set
     */
    public void setAbrirArchivHistorial(boolean abrirArchivHistorial) {
	this.abrirArchivHistorial = abrirArchivHistorial;
    }

    /**
     * @return the sgOficinaPlano
     */
    public SgOficinaPlano getSgOficinaPlano() {
	return sgOficinaPlano;
    }

    /**
     * @param sgOficinaPlano the sgOficinaPlano to set
     */
    public void setSgOficinaPlano(SgOficinaPlano sgOficinaPlano) {
	this.sgOficinaPlano = sgOficinaPlano;
    }

    /**
     * @return the listaProveedorBuscar
     */
    public List<String> getListaProveedorBuscar() {
	return listaProveedorBuscar;
    }

    /**
     * @param listaProveedorBuscar the listaProveedorBuscar to set
     */
    public void setListaProveedorBuscar(List<String> listaProveedorBuscar) {
	this.listaProveedorBuscar = listaProveedorBuscar;
    }

    public List<String> traerProveedor() {
	return proveedorImpl.traerNombreProveedorQueryNativo(sesion.getRfcEmpresa(), ProveedorEnum.ACTIVO.getId());
    }

    /**
     * @return the numeroFactura
     */
    public String getNumeroFactura() {
	return numeroFactura;
    }

    /**
     * @param numeroFactura the numeroFactura to set
     */
    public void setNumeroFactura(String numeroFactura) {
	this.numeroFactura = numeroFactura;
    }

    /**
     * @return the prefijo
     */
    public String getPrefijo() {
	return prefijo;
    }

    /**
     * @param prefijo the prefijo to set
     */
    public void setPrefijo(String prefijo) {
	this.prefijo = prefijo;
    }

    /**
     * @return the cantidadCaracteristica
     */
    public Integer getCantidadCaracteristica() {
	return cantidadCaracteristica;
    }

    /**
     * @param cantidadCaracteristica the cantidadCaracteristica to set
     */
    public void setCantidadCaracteristica(Integer cantidadCaracteristica) {
	this.cantidadCaracteristica = cantidadCaracteristica;
    }

    /**
     * @return the listaPlano
     */
    public DataModel getListaPlano() {
	return listaPlano;
    }

    /**
     * @param listaPlano the listaPlano to set
     */
    public void setListaPlano(DataModel listaPlano) {
	this.listaPlano = listaPlano;
    }

    /**
     * @return the agregarPopContrato
     */
    public boolean isAgregarPopContrato() {
	return agregarPopContrato;
    }

    /**
     * @param agregarPopContrato the agregarPopContrato to set
     */
    public void setAgregarPopContrato(boolean agregarPopContrato) {
	this.agregarPopContrato = agregarPopContrato;
    }

    /**
     * @return the caracteristicas
     */
    public List<SelectItem> getCaracteristicas() {
	return caracteristicas;
    }

    /**
     * @param caracteristicas the caracteristicas to set
     */
    public void setCaracteristicas(List<SelectItem> caracteristicas) {
	this.caracteristicas = caracteristicas;
    }

    /**
     * @return the matchesList
     */
    public List<SelectItem> getMatchesList() {
	return matchesList;
    }

    /**
     * @param matchesList the matchesList to set
     */
    public void setMatchesList(List<SelectItem> matchesList) {
	this.matchesList = matchesList;
    }

    /**
     * @return the object
     */
    public Object getObject() {
	return object;
    }

    /**
     * @param object the object to set
     */
    public void setObject(Object object) {
	this.object = object;
    }

    /**
     * @return the idPais
     */
    public int getIdPais() {
	return idPais;
    }

    /**
     * @param idPais the idPais to set
     */
    public void setIdPais(int idPais) {
	this.idPais = idPais;
    }

    /**
     * @return the idEstado
     */
    public int getIdEstado() {
	return idEstado;
    }

    /**
     * @param idEstado the idEstado to set
     */
    public void setIdEstado(int idEstado) {
	this.idEstado = idEstado;
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
     * @return the telefono
     */
    public String getTelefono() {
	return telefono;
    }

    /**
     * @param telefono the telefono to set
     */
    public void setTelefono(String telefono) {
	this.telefono = telefono;
    }

    /**
     * @return the municipio
     */
    public String getMunicipio() {
	return municipio;
    }

    /**
     * @param municipio the municipio to set
     */
    public void setMunicipio(String municipio) {
	this.municipio = municipio;
    }

    /**
     * @return the colonia
     */
    public String getColonia() {
	return colonia;
    }

    /**
     * @param colonia the colonia to set
     */
    public void setColonia(String colonia) {
	this.colonia = colonia;
    }

    /**
     * @return the calle
     */
    public String getCalle() {
	return calle;
    }

    /**
     * @param calle the calle to set
     */
    public void setCalle(String calle) {
	this.calle = calle;
    }

    /**
     * @return the numExterior
     */
    public String getNumExterior() {
	return numExterior;
    }

    /**
     * @param numExterior the numExterior to set
     */
    public void setNumExterior(String numExterior) {
	this.numExterior = numExterior;
    }

    /**
     * @return the numInterior
     */
    public String getNumInterior() {
	return numInterior;
    }

    /**
     * @param numInterior the numInterior to set
     */
    public void setNumInterior(String numInterior) {
	this.numInterior = numInterior;
    }

    /**
     * @return the numPiso
     */
    public String getNumPiso() {
	return numPiso;
    }

    /**
     * @param numPiso the numPiso to set
     */
    public void setNumPiso(String numPiso) {
	this.numPiso = numPiso;
    }

    /**
     * @return the codigoPostal
     */
    public String getCodigoPostal() {
	return codigoPostal;
    }

    /**
     * @param codigoPostal the codigoPostal to set
     */
    public void setCodigoPostal(String codigoPostal) {
	this.codigoPostal = codigoPostal;
    }

    /**
     * @return the dataModelAux
     */
    public DataModel getDataModelAux() {
	return dataModelAux;
    }

    /**
     * @param dataModelAux the dataModelAux to set
     */
    public void setDataModelAux(DataModel dataModelAux) {
	this.dataModelAux = dataModelAux;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
	this.sesion = sesion;
    }

    /**
     * @param soporteProveedor the soporteProveedor to set
     */
    public void setSoporteProveedor(SoporteProveedor soporteProveedor) {
	this.soporteProveedor = soporteProveedor;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
	return uuid;
    }

    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
	this.uuid = uuid;
    }

    /**
     * @return the latitud
     */
    public String getLatitud() {
	return latitud;
    }

    /**
     * @param latitud the latitud to set
     */
    public void setLatitud(String latitud) {
	this.latitud = latitud;
    }

    /**
     * @return the longitud
     */
    public String getLongitud() {
	return longitud;
    }

    /**
     * @param longitud the longitud to set
     */
    public void setLongitud(String longitud) {
	this.longitud = longitud;
    }

    /**
     * @return the listaPais
     */
    public List<SelectItem> getListaPais() {
	return listaPais;
    }

    /**
     * @param listaPais the listaPais to set
     */
    public void setListaPais(List<SelectItem> listaPais) {
	this.listaPais = listaPais;
    }

    /**
     * @return the listaEstado
     */
    public List<SelectItem> getListaEstado() {
	return listaEstado;
    }

    /**
     * @param listaEstado the listaEstado to set
     */
    public void setListaEstado(List<SelectItem> listaEstado) {
	this.listaEstado = listaEstado;
    }

    /**
     * @return the listaCiudad
     */
    public List<SelectItem> getListaCiudad() {
	return listaCiudad;
    }

    /**
     * @param listaCiudad the listaCiudad to set
     */
    public void setListaCiudad(List<SelectItem> listaCiudad) {
	this.listaCiudad = listaCiudad;
    }

    /**
     * @return the dataModelAnlistas
     */
    public DataModel getDataModelAnlistas() {
	return dataModelAnlistas;
    }

    /**
     * @param dataModelAnlistas the dataModelAnlistas to set
     */
    public void setDataModelAnlistas(DataModel dataModelAnlistas) {
	this.dataModelAnlistas = dataModelAnlistas;
    }

    /**
     * @return the mapaLista
     */
    public Map<String, DataModel> getMapaLista() {
	return mapaLista;
    }

    /**
     * @param mapaLista the mapaLista to set
     */
    public void setMapaLista(Map<String, DataModel> mapaLista) {
	this.mapaLista = mapaLista;
    }

}
