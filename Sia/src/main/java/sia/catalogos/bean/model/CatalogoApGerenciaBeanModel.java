package sia.catalogos.bean.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import sia.catalogos.bean.backing.UsuarioBean;
import sia.constantes.Constantes;
import sia.excepciones.ExistingItemException;
import sia.modelo.ApCampo;
import sia.modelo.ApCampoGerencia;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.usuario.vo.UsuarioResponsableGerenciaVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.vo.ApCampoGerenciaVo;
import sia.servicios.campo.nuevo.impl.ApCampoGerenciaImpl;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.campo.nuevo.impl.ApCompaniaGerenciaImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.sistema.bean.support.SoporteListas;
import sia.util.UtilLog4j;

/**
 *
 * @author rluna MLUIS
 */
@ManagedBean(name = "catalogoApGerenciaBeanModel")
@CustomScoped(value = "#{window}")
public class CatalogoApGerenciaBeanModel implements Serializable {
    //Sistema

    //Sistema
    @ManagedProperty(value = "#{sesion}")
    private Sesion sesion;
    @ManagedProperty(value = "#{usuarioBean}")
    private UsuarioBean usuarioBean;
    /*
     * @Inject private Sesion sesion; @Inject private Conversation conversation;
     * @Inject private ConversationsManager conversationsManager;
     *
     */
    @ManagedProperty(value = "#{soporteListas}")
    private SoporteListas soporteListas;
    //Servicios
    @EJB
    private ApCampoGerenciaImpl apCampoGerenciaImpl;
    @EJB
    private GerenciaImpl gerenciaImpl;
    @EJB
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @EJB
    private ApCompaniaGerenciaImpl apCompaniaGerenciaImpl;
    @EJB
    private ApCampoImpl apCampoImpl;
    @EJB
    private UsuarioImpl usuarioImpl;
    @EJB    
    private SiUsuarioRolImpl siUsuarioRolImpl;
    //Entidad
    private ApCampoGerencia apCAmpoGerencia;
    //Clases
    private ApCampoGerenciaVo apCampoGerenciaVo;
    //Colecciones
    private DataModel dataModel;
    private List<SelectItem> listaUsuario;
    //
    private List<SelectItem> listaUsuariosAlta;
    //Primitivos
    private String nombreGerencia;
    private String responsable;
    private int idCampo;
    private int idGerencia;
    private int idPuesto;
    private boolean visibleGerencia;
    private String rfcEmpresa;
    private boolean verNewGerencia = false;

    public CatalogoApGerenciaBeanModel() {
    }

    @PostConstruct
    public void iniciarCampo() {
	setIdCampo(sesion.getUsuario().getApCampo().getId());
    }

    public void controlaPopUpFalso(String llave) {
	sesion.getControladorPopups().put(llave, Boolean.FALSE);
    }

    public void controlaPopUpTrue(String llave) {
	sesion.getControladorPopups().put(llave, Boolean.TRUE);
    }

    public void idCampoUsuario() {
	UtilLog4j.log.info(this, "IdCampo usuario: " + sesion.getUsuario().getApCampo().getId());
	setIdCampo(usuarioBean.getUsuarioVO().getIdCampo());
    }

    /**
     * Comienza RhPuesto
     */
//    public RhPuesto getRhPuestoById(int idRhPuesto) {
//        return this.rhPuestoImpl.find(idRhPuesto);
//    }
    public ApCampo traerCampoPorId() {
	return apCampoImpl.find(getIdCampo());
    }

    public void limpiarListaUsuario() {
	soporteListas.setListaUsuario(null);
    }

    public void getAllApGerencia() {
	if (this.dataModel == null) {
	    this.dataModel = (new ListDataModel(this.getApCampoGerenciaImpl().findAllCampoGerenciaPorCampo(getIdCampo())));
	}
    }

    public void reloadAllApGerencia() {
	this.dataModel = (new ListDataModel(this.getApCampoGerenciaImpl().findAllCampoGerenciaPorCampo(this.getIdCampo())));
    }

    public List<ApCampoGerenciaVo> getAllApGerenciaList() {
	return this.apCampoGerenciaImpl.findAllCampoGerenciaPorCampo(this.getIdCampo());
    }

    public List<SelectItem> listaGerencia() {
	List<SelectItem> l = new ArrayList<SelectItem>();
	List<GerenciaVo> lc;

	try {
	    UtilLog4j.log.info(this, "Campo sel: " + getIdCampo());
	    lc = gerenciaImpl.traerGerenciaPorCompaniaCampo(getRfcEmpresa(), getIdCampo(), Constantes.NO_ELIMINADO);

	    for (GerenciaVo ca : lc) {
		SelectItem item = new SelectItem(ca.getId(), ca.getNombre());
		l.add(item);
		//        UtilLog4j.log.info(this, ca.getIdGerencia()+" "+ ca.getNombreGerencia());
	    }
	    //UtilLog4j.log.info(this, l.toString(ca.getIdGerencia() ca.getNombreGerencia());
	    return l;
	} catch (Exception e) {
	    return null;
	}
    }

    public List<SelectItem> listaTodasGerencia() {
	return soporteListas.listaGerencias();
    }

    public boolean buscarGerencia() {
	boolean v = true;
	UsuarioResponsableGerenciaVo g = apCampoGerenciaImpl.buscarResponsablePorGerencia(getIdGerencia(), getIdCampo());
	if (g != null) {
	    v = false;
	}
	return v;
    }

    public void agregarGerenciaCompania() {
	apCompaniaGerenciaImpl.guardarRelacionGerencia(usuarioBean.getUsuarioVO().getId(), getRfcEmpresa(), getIdGerencia());
	listaGerencia();
    }

    public List<SelectItem> listaCampo() {
	List<SelectItem> l = new ArrayList<SelectItem>();
	List<CampoUsuarioPuestoVo> lc;
	try {
	    lc = apCampoUsuarioRhPuestoImpl.getAllPorUsurio(usuarioBean.getUsuarioVO().getId());
	    for (CampoUsuarioPuestoVo ca : lc) {
		SelectItem item = new SelectItem(ca.getIdCampo(), ca.getCampo());
		l.add(item);
	    }
	    return l;
	} catch (Exception e) {
	    return null;
	}
    }

    public void saveCampoGerenciaResposable() throws ExistingItemException {
	apCampoGerenciaImpl.guardarCampoGerenciaResponsable(sesion.getUsuario().getId(), getResponsable(), idCampo, idGerencia);
    }

    public Usuario buscarUsuarioPorNombre() {
	return usuarioImpl.buscarPorNombre(getResponsable());
    }

    public Usuario buscarUsuarioPorIdUsuario() {
	return usuarioImpl.find(getResponsable());
    }

    public void completarCambiarResponsable() {
	apCampoGerenciaImpl.cambiarResponsable(usuarioBean.getUsuarioVO().getId(), getApCampoGerenciaVo().getId(), getResponsable());
	dataModel = (new ListDataModel(this.getApCampoGerenciaImpl().findAllCampoGerenciaPorCampo(getIdCampo())));
    }

    public void completarcambiarVisible() {
	apCampoGerenciaImpl.completarcambiarVisible(usuarioBean.getUsuarioVO().getId(), getApCampoGerenciaVo().getId());
	dataModel = (new ListDataModel(this.getApCampoGerenciaImpl().findAllCampoGerenciaPorCampo(getIdCampo())));
    }

    public void saveGerencia() throws ExistingItemException {
	UtilLog4j.log.info(this, "Entrando a guardar nombre de gerencia");
	this.gerenciaImpl.guardarGerencia(this.sesion.getUsuario().getId(), nombreGerencia);
    }

    public void saveCampoUsuarioRhpuesto() {
	UtilLog4j.log.info(this, "Entrando a guardar nombre de gerencia");
	UsuarioListModel ulm = ((UsuarioListModel) FacesUtils.getManagedBean("usuarioListModel"));
	apCampoUsuarioRhPuestoImpl.save(this.sesion.getUsuario().getId(), idCampo, ulm.getUsuarioVOAlta().getId(), idPuesto, idGerencia);
    }

    public String traerUsuarioJson() {
	return apCampoUsuarioRhPuestoImpl.traerUsuarioActivoPorBloque(getIdCampo(), Constantes.CERO);
    }

    public List<SelectItem> traerUsuario(String cadena) {
	return soporteListas.regresaUsuarioActivo(cadena, getIdCampo());
    }

    public DataModel getPuestosDataModel() {
	return this.getDataModel();
    }

    /**
     * @return the sesion
     */
    public Sesion getSesion() {
	return sesion;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
	this.sesion = sesion;
    }

    public DataModel getDataModel() {
	return dataModel;
    }

    /**
     * @param dataModel the dataModel to set
     */
    public void setDataModel(DataModel dataModel) {
	this.dataModel = dataModel;
    }

    /**
     * @return the apCampoGerenciaImpl
     */
    public ApCampoGerenciaImpl getApCampoGerenciaImpl() {
	return apCampoGerenciaImpl;
    }

    /**
     * @param apCampoGerenciaImpl the apCampoGerenciaImpl to set
     */
    public void setApCampoGerenciaImpl(ApCampoGerenciaImpl apCampoGerenciaImpl) {
	this.apCampoGerenciaImpl = apCampoGerenciaImpl;
    }

    /**
     * @return the apCAmpoGerencia
     */
    public ApCampoGerencia getApCAmpoGerencia() {
	return apCAmpoGerencia;
    }

    /**
     * @param apCAmpoGerencia the apCAmpoGerencia to set
     */
    public void setApCAmpoGerencia(ApCampoGerencia apCAmpoGerencia) {
	this.apCAmpoGerencia = apCAmpoGerencia;
    }

    /**
     * @return the apCampoGerenciaVo
     */
    public ApCampoGerenciaVo getApCampoGerenciaVo() {
	return apCampoGerenciaVo;
    }

    /**
     * @param apCampoGerenciaVo the apCampoGerenciaVo to set
     */
    public void setApCampoGerenciaVo(ApCampoGerenciaVo apCampoGerenciaVo) {
	this.apCampoGerenciaVo = apCampoGerenciaVo;
    }

    /**
     * @return the nombreGerencia
     */
    public String getNombreGerencia() {
	return nombreGerencia;
    }

    /**
     * @param nombreGerencia the nombreGerencia to set
     */
    public void setNombreGerencia(String nombreGerencia) {
	this.nombreGerencia = nombreGerencia;
    }

    /**
     * @return the visibleGerencia
     */
    public boolean isVisibleGerencia() {
	return visibleGerencia;
    }

    /**
     * @param visibleGerencia the visibleGerencia to set
     */
    public void setVisibleGerencia(boolean visibleGerencia) {
	this.visibleGerencia = visibleGerencia;
    }

    /**
     * @return the idCampo
     */
    public int getIdCampo() {
	return idCampo;
    }

    /**
     * @param idCampo the idCampo to set
     */
    public void setIdCampo(int idCampo) {
	this.idCampo = idCampo;
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
     * @return the listaUsuario
     */
    public List<SelectItem> getListaUsuario() {
	return listaUsuario;
    }

    /**
     * @param listaUsuario the listaUsuario to set
     */
    public void setListaUsuario(List<SelectItem> listaUsuario) {
	this.listaUsuario = listaUsuario;
    }

    /**
     * @return the idPuesto
     */
    public int getIdPuesto() {
	return idPuesto;
    }

    /**
     * @param idPuesto the idPuesto to set
     */
    public void setIdPuesto(int idPuesto) {
	this.idPuesto = idPuesto;
    }

    /**
     * @return the listaUsuariosAlta
     */
    public List<SelectItem> getListaUsuariosAlta() {
	return listaUsuariosAlta;
    }

    /**
     * @param listaUsuariosAlta the listaUsuariosAlta to set
     */
    public void setListaUsuariosAlta(List<SelectItem> listaUsuariosAlta) {
	this.listaUsuariosAlta = listaUsuariosAlta;
    }

    /**
     * @return the responsable
     */
    public String getResponsable() {
	return responsable;
    }

    /**
     * @param responsable the responsable to set
     */
    public void setResponsable(String responsable) {
	this.responsable = responsable;
    }

    /**
     * @param soporteListas the soporteListas to set
     */
    public void setSoporteListas(SoporteListas soporteListas) {
	this.soporteListas = soporteListas;
    }

    /**
     * @param usuarioBean
     */
    public void setUsuarioBean(UsuarioBean usuarioBean) {
	this.usuarioBean = usuarioBean;
    }

    /**
     * @return the rfcEmpresa
     */
    public String getRfcEmpresa() {
	return rfcEmpresa;
    }

    /**
     * @param rfcEmpresa the rfcEmpresa to set
     */
    public void setRfcEmpresa(String rfcEmpresa) {
	this.rfcEmpresa = rfcEmpresa;
    }
    
    public void deleteGerencia(){
        apCampoGerenciaImpl.deleteApCampoGerencia(getApCampoGerenciaVo().getId(), sesion.getUsuario().getId());
        if (!apCampoGerenciaImpl.findRelacionGerenciaCampo(getApCampoGerenciaVo().getIdGerencia())){
              gerenciaImpl.deleteGerencia(getApCampoGerenciaVo().getIdGerencia(), sesion.getUsuario().getId());
        }
      
        dataModel=null;
        getAllApGerencia();
    }
    
    public boolean rolDesarrollo() {
        boolean siTieneRol=false;
	try{
             
           UsuarioRolVo urvo =  siUsuarioRolImpl.findUsuarioRolVO(47, sesion.getUsuario().getId(), getIdCampo());
            if (urvo != null){
         siTieneRol = Constantes.TRUE;   
        } else{
                siTieneRol = Constantes.FALSE;
            }
        } catch (Exception e ){
            UtilLog4j.log.error(this,e);
            
        }
	 return siTieneRol;
    }

    /**
     * @return the verNewGerencia
     */
    public boolean isVerNewGerencia() {
        return verNewGerencia;
    }

    /**
     * @param verNewGerencia the verNewGerencia to set
     */
    public void setVerNewGerencia(boolean verNewGerencia) {
        this.verNewGerencia = verNewGerencia;
    }
}
