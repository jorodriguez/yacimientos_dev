/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.checklist.bean.model;

import java.io.Serializable;
import java.util.Iterator;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgChecklist;
import sia.modelo.SgChecklistDetalle;
import sia.modelo.SgChecklistExtVehiculo;
import sia.modelo.SgChecklistLlantas;
import sia.modelo.SgKilometraje;
import sia.modelo.SgTipoEspecifico;
import sia.modelo.SgVehiculoChecklist;
import sia.modelo.SgVehiculoMantenimiento;
import sia.modelo.SiAdjunto;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.modelo.sgl.vo.CheckListDetalleVo;
import sia.servicios.sgl.impl.SgChecklistDetalleImpl;
import sia.servicios.sgl.impl.SgChecklistImpl;
import sia.servicios.sgl.impl.SgKilometrajeImpl;
import sia.servicios.sgl.impl.SgTipoEspecificoImpl;
import sia.servicios.sgl.impl.SgVehiculoChecklistImpl;
import sia.servicios.sgl.impl.SgVehiculoMantenimientoImpl;
import sia.servicios.sgl.vehiculo.impl.SgChecklistExtVehiculoImpl;
import sia.servicios.sgl.vehiculo.impl.SgChecklistLlantasImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named(value = "checkListVehiculoModel")

public class CheckListVehiculoModel implements Serializable {

    private final static UtilLog4j LOGGER = UtilLog4j.log;

    /**
     * Creates a new instance of CheckListVehiculoModel
     */
    public CheckListVehiculoModel() {
    }

    //EJBs
    @Inject
    private SgChecklistImpl checklistService;
    @Inject
    private SgChecklistExtVehiculoImpl checklistExteriorVehiculoService;
    @Inject
    private SgChecklistLlantasImpl checklistLlantasVehiculoService;
    @Inject
    private SgVehiculoChecklistImpl vehiculoChecklistService;
    @Inject
    private SgChecklistDetalleImpl checklistDetalleService;
    @Inject
    private SgVehiculoMantenimientoImpl vehiculoMantenimientoService;
    @Inject
    private SgKilometrajeImpl kilometrajeService;
    @Inject
    private SiAdjuntoImpl adjuntoService;
    @Inject
    private SiManejoFechaImpl manejoFechaService;
    @Inject
    private SgTipoEspecificoImpl tipoEspecificoService;
    @Inject
    private SiParametroImpl siParametroService;
    //
    @Inject
    private Sesion sesion;

    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;

    //
    private SgChecklist checklist;
    private SgVehiculoChecklist vehiculoChecklist;
    private SgChecklistExtVehiculo checklistExtVehiculo;
    private SgChecklistLlantas checklistLlantas;
    private SgKilometraje kilometraje;
    private SgKilometraje kilometrajeActual;
    private DataModel checklistVODataModel;
    private boolean disabledChecklistInterior = true;
    private boolean disabledChecklistExterior = true;
    private boolean disabledChecklistLlantas = true;
    private String selectedIndex = "0";
    private String cadena;
    private VehiculoVO vehiculoVO;
    private boolean flag = false;

    @PostConstruct
    public void beginConversationChecklistOficina() {
	//Reiniciando variables necesarias
	this.vehiculoChecklist = null;
	this.checklistExtVehiculo = null;
	this.checklistLlantas = null;
	this.kilometrajeActual = null;
	controlarPopFalse("popupObservacionToAdjunto");
	controlarPopFalse("popupUpdateObservacionToAdjunto");
	controlarPopFalse("popupUploadChecklistExterior");
	setDisabledChecklistInterior(false);
	setDisabledChecklistExterior(true);
	setDisabledChecklistLlantas(true);
    }

    public void beginConversationChecklistVehiculo() {
	try {
	    //Reiniciando variables necesarias
	    this.kilometraje = new SgKilometraje();
	    kilometrajeActual = kilometrajeService.findKilometrajeActualVehiculo(vehiculoVO.getId());
	    this.kilometraje.setKilometraje(kilometrajeActual.getKilometraje() != null ? kilometrajeActual.getKilometraje() : 0);
	    this.vehiculoChecklist = null;
	    this.checklistExtVehiculo = null;
	    this.checklistLlantas = null;

	    checklistVODataModel = new ListDataModel(checklistService.getAllItemsChecklistVO(vehiculoVO, Constantes.NO_ELIMINADO));

	} catch (Exception ex) {
	    LOGGER.error(ex);
	}
    }

    public void createChecklistInteriorVehiculo() throws SIAException, Exception {
	if (getChecklistVODataModel() == null || this.getChecklistVODataModel().getRowCount() <= 0) { //No se deben crear Checklist vacíos
	    throw new SIAException("No es posible crear un Checklist sin elementos");
	} else {
	    boolean itemsSavedSucessfull = true;
//            //Guardar el Checklist
	    this.setChecklist(checklistService.createChecklist(manejoFechaService.getInicioSemana(), manejoFechaService.getFinSemana(), sesion.getUsuario().getId()));
//            //Guardar el Vehículo-Checklist
	    //Darle su tipo(14) y tipo especifico (checklist)
	    System.out.println("antes de crear");
	    SgTipoEspecifico tipoEspecifico = new SgTipoEspecifico();
	    tipoEspecifico.setNombre("Checklist");
	    this.vehiculoChecklist = vehiculoChecklistService.create(vehiculoVO.getId(), this.getChecklist(), this.kilometraje, (tipoEspecificoService.buscarPorNombre(tipoEspecifico.getNombre(), Constantes.NO_ELIMINADO)).getId(), sesion.getUsuario().getId());
//            //Guardar los Detalles
	    System.out.println("after de crear");
	    if (this.vehiculoChecklist != null) {
		//Guardar ítems
		SgChecklistDetalle item = null;
		for (Iterator iter = this.getChecklistVODataModel().iterator(); iter.hasNext();) {
		    CheckListDetalleVo vo = (CheckListDetalleVo) iter.next();

		    System.out.println("antes de crear item " + vo.getCaracteristicaVo().getId());

		    item = checklistDetalleService.createChecklistDetalle(getChecklist(),
			    vo.getCaracteristicaVo().getId(),
			    (vo.isEstado() ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE),
			    vo.getObservacion(),
			    sesion.getUsuario().getId());
		    System.out.println("after de crear item");
		    if (item == null) {
			itemsSavedSucessfull = false;
		    }
		}
		if (!itemsSavedSucessfull) {
		    throw new SIAException("Algunos ítems no se crearon correctamente");
		}
	    }
	}
    }

    public void traerKilometrajeActualOld() {
    }

    public void createChecklistLlantasVehiculo() throws SIAException, Exception {
	if (this.checklistLlantas.getRefaccion() == null || this.checklistLlantas.getRefaccion().isEmpty()) {
	    this.checklistLlantas.setRefaccion("0");
	}
	this.checklistLlantas.setSgChecklist(this.getChecklist());
	this.checklistLlantas.setBuenEstado(this.isFlag());
	this.checklistLlantas.setObservacion(this.getCadena());
	checklistLlantasVehiculoService.create(this.checklistLlantas, sesion.getUsuario().getId());
    }

    public boolean kilometrajeGreat() {
	UtilLog4j.log.info(this, "Kilometraje: " + this.kilometraje.getKilometraje());
	UtilLog4j.log.info(this, "Kilometraje último: " + this.kilometrajeActual.getKilometraje());
	return this.kilometraje.getKilometraje() >= kilometrajeActual.getKilometraje();
    }

    /**
     * Si el retorno es Verdadero se puede crear un checklist.
     *
     * @return
     */
    public boolean getMantenimientoNoTerminado() {

	SgVehiculoMantenimiento vm = vehiculoMantenimientoService.findRegistroEntradaNOTerminado(vehiculoVO.getId());
	if (vm == null) {
	    return true;
	} else {
	    return vm.getImporte().intValue() != 0;
	}
    }

    public void deleteAdjuntoChecklistExterior() throws SIAException, Exception {
	if (eliminarArchivoFisicamente(this.checklistExtVehiculo.getSiAdjunto().getUrl())) {
	    SiAdjunto siAdjunto = this.checklistExtVehiculo.getSiAdjunto();

	    //Eliminar registro Checklist Exterior
	    checklistExteriorVehiculoService.delete(this.checklistExtVehiculo, sesion.getUsuario().getId());

	    //Eliminar SiAdjunto
	    adjuntoService.delete(siAdjunto, sesion.getUsuario().getId());

	    this.checklistExtVehiculo = null;
	}
    }

    public void updateAdjunto() throws Exception {
	adjuntoService.update(this.checklistExtVehiculo.getSiAdjunto(), sesion.getUsuario().getId());
    }

    /**
     * Elimina físicamente un archivo
     *
     * @param url
     * @return
     */
    public boolean eliminarArchivoFisicamente(String url) throws SIAException, Exception {
	boolean retVal = false;

	try {
	    LOGGER.info(this, "Url a eliminar: " + url);
	    //Files.delete(Paths.get(siParametroService.find(1).getUploadDirectory() + url));
	    proveedorAlmacenDocumentos.getAlmacenDocumentos().borrarDocumento(url);

	    retVal = true;
	} catch (Exception e) {
	    LOGGER.fatal(this, "Excepcion en eliminar adjunto" + url, e);
	}

	return retVal;
    }

    /**
     * Guarda el adjunto del Checklist exterior de un Vehículo
     *
     * @param nombreArchivo
     * @param ruta
     * @param contentType
     * @param tamanioArchivo
     * @return
     * @throws SIAException
     * @throws Exception
     */
    public boolean guardarArchivoChecklistExteriorVehiculo(String nombreArchivo, String ruta, String contentType, long tamanioArchivo) throws SIAException, Exception {

	SiAdjunto adjunto
		= adjuntoService.save(
			nombreArchivo,
			ruta + nombreArchivo,
			contentType,
			tamanioArchivo,
			sesion.getUsuario().getId()
		);

	if (adjunto != null) {
	    this.checklistExtVehiculo = new SgChecklistExtVehiculo();
	    checklistExtVehiculo.setSiAdjunto(adjunto);

	    checklistExtVehiculo.setSgChecklist(this.getChecklist());
	    this.checklistExtVehiculo = checklistExteriorVehiculoService.create(checklistExtVehiculo, sesion.getUsuario().getId());
	}

	return adjunto != null;
    }

    public String getDirectoryChecklistExteriorVehiculo() {
	String retVal = Constantes.VACIO;

	if (this.checklist != null) {
	    retVal
		    = Constantes.NOMBRE_MODULO_SERVICIOS_GENERALES
		    + "/" + "Checklist/"
		    + this.checklist.getId() + "/";
	}

	return retVal;
    }

    public void controlarPopFalse(String popup) {
	sesion.getControladorPopups().put(popup, Boolean.FALSE);
    }

    public void controlarPopTrue(String popup) {
	sesion.getControladorPopups().put(popup, Boolean.TRUE);
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
	this.sesion = sesion;
    }

    public SgChecklistExtVehiculo getChecklistExtVehiculo() {
	return checklistExtVehiculo;
    }

    /**
     * @param checklistExtVehiculo the checklistExtVehiculo to set
     */
    public void setChecklistExtVehiculo(SgChecklistExtVehiculo checklistExtVehiculo) {
	this.checklistExtVehiculo = checklistExtVehiculo;
    }

    /**
     * @return the checklistLlantas
     */
    public SgChecklistLlantas getChecklistLlantas() {
	return checklistLlantas;
    }

    /**
     * @param checklistLlantas the checklistLlantas to set
     */
    public void setChecklistLlantas(SgChecklistLlantas checklistLlantas) {
	this.checklistLlantas = checklistLlantas;
    }

    /**
     * @return the disabledChecklistExterior
     */
    public boolean isDisabledChecklistExterior() {
	return disabledChecklistExterior;
    }

    /**
     * @param disabledChecklistExterior the disabledChecklistExterior to set
     */
    public void setDisabledChecklistExterior(boolean disabledChecklistExterior) {
	this.disabledChecklistExterior = disabledChecklistExterior;
    }

    /**
     * @return the vehiculoChecklist
     */
    public SgVehiculoChecklist getVehiculoChecklist() {
	return vehiculoChecklist;
    }

    /**
     * @param vehiculoChecklist the vehiculoChecklist to set
     */
    public void setVehiculoChecklist(SgVehiculoChecklist vehiculoChecklist) {
	this.vehiculoChecklist = vehiculoChecklist;
    }

    /**
     * @return the disabledChecklistInterior
     */
    public boolean isDisabledChecklistInterior() {
	return disabledChecklistInterior;
    }

    /**
     * @param disabledChecklistInterior the disabledChecklistInterior to set
     */
    public void setDisabledChecklistInterior(boolean disabledChecklistInterior) {
	this.disabledChecklistInterior = disabledChecklistInterior;
    }

    /**
     * @return the kilometraje
     */
    public SgKilometraje getKilometraje() {
	return kilometraje;
    }

    /**
     * @param kilometraje the kilometraje to set
     */
    public void setKilometraje(SgKilometraje kilometraje) {
	this.kilometraje = kilometraje;
    }

    /**
     * @return the disabledChecklistLlantas
     */
    public boolean isDisabledChecklistLlantas() {
	return disabledChecklistLlantas;
    }

    /**
     * @param disabledChecklistLlantas the disabledChecklistLlantas to set
     */
    public void setDisabledChecklistLlantas(boolean disabledChecklistLlantas) {
	this.disabledChecklistLlantas = disabledChecklistLlantas;
    }

    /**
     * @return the kilometrajeActual
     */
    public SgKilometraje getKilometrajeActual() {
	return kilometrajeActual;
    }

    /**
     * @param kilometrajeActual the kilometrajeActual to set
     */
    public void setKilometrajeActual(SgKilometraje kilometrajeActual) {
	this.kilometrajeActual = kilometrajeActual;
    }

    /**
     * @return the checklist
     */
    public SgChecklist getChecklist() {
	return checklist;
    }

    /**
     * @param checklist the checklist to set
     */
    public void setChecklist(SgChecklist checklist) {
	this.checklist = checklist;
    }

    /**
     * @return the selectedIndex
     */
    public String getSelectedIndex() {
	return selectedIndex;
    }

    /**
     * @param selectedIndex the selectedIndex to set
     */
    public void setSelectedIndex(String selectedIndex) {
	this.selectedIndex = selectedIndex;
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
     * @return the flag
     */
    public boolean isFlag() {
	return flag;
    }

    /**
     * @param flag the flag to set
     */
    public void setFlag(boolean flag) {
	this.flag = flag;
    }

    /**
     * @return the checklistVODataModel
     */
    public DataModel getChecklistVODataModel() {
	return checklistVODataModel;
    }

    /**
     * @param checklistVODataModel the checklistVODataModel to set
     */
    public void setChecklistVODataModel(DataModel checklistVODataModel) {
	this.checklistVODataModel = checklistVODataModel;
    }

    /**
     * @return the vehiculoVO
     */
    public VehiculoVO getVehiculoVO() {
	return vehiculoVO;
    }

    /**
     * @param vehiculoVO the vehiculoVO to set
     */
    public void setVehiculoVO(VehiculoVO vehiculoVO) {
	this.vehiculoVO = vehiculoVO;
    }

}
