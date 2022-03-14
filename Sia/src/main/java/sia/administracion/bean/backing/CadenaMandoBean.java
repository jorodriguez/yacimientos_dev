/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.bean.backing;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import org.primefaces.PrimeFaces;
import sia.administracion.bean.model.CadenaMandoModel;
import sia.excepciones.SIAException;
import sia.modelo.Usuario;
import sia.modelo.cadena.aprobacion.vo.CadenaAprobacionVo;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@ManagedBean(name = "cadenaMandoBean")
@RequestScoped
public class CadenaMandoBean implements Serializable {

    @ManagedProperty(value = "#{cadenaMandoModel}")
    private CadenaMandoModel cadenaMandoModel;
///Lsita de campos

    /**
     * Creates a new instance of CadenaMando
     */
    public CadenaMandoBean() {
    }

    @PostConstruct
    public void iniciar() {
	cadenaMandoModel.iniciar();

	traerUsuarioJson();
    }

    public List<SelectItem> getListaCampo() {
	return cadenaMandoModel.listaCampo();
    }

    public void traerUsuarioJson() {
	String datos = cadenaMandoModel.traerUsuarioJson();
	PrimeFaces.current().executeScript(";llenarJsonUsuario(" + datos + ");");
    }

    public void irCadenaMando() {
	cadenaMandoModel.setIdCampo(1);
	cadenaMandoModel.setIdOrdena(1);
	//    getTraerCadena();
    }

    public void cambiarValorCampo(ValueChangeEvent valueChangeEvent) {
	cadenaMandoModel.setIdCampo((Integer) valueChangeEvent.getNewValue());
	UtilLog4j.log.info(this, "campo: " + cadenaMandoModel.getIdCampo());
	cadenaMandoModel.setUsuario(null);
	cadenaMandoModel.setIdOrdena(1);
	traerUsuarioJson();
	cadenaMandoModel.llenarListaCadena();
	//    getTraerCadena();
//        if (cadenaMandoModel.getListaCadena().getRowCount() < 0) {
//            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("sia.cadena.aprobacion.no.existe"));
//        }
    }

    public void cambiarValorOrdena(ValueChangeEvent valueChangeEvent) {
	cadenaMandoModel.setIdOrdena((Integer) valueChangeEvent.getNewValue());
	UtilLog4j.log.info(this, "Operacion: " + cadenaMandoModel.getIdOrdena());
	if (cadenaMandoModel.getListaCadena().getRowCount() == 0) {
	    FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("sia.cadena.aprobacion.no.existe"));
	}
    }

    public void buscarCadena() {
	//    getTraerCadena();
	if (cadenaMandoModel.getListaCadena().getRowCount() < 1) {
	    FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("sia.cadena.aprobacion.no.existe"));
	}
//        this.agregar = "False";
	cadenaMandoModel.setCadenaAprobacionVo(null);
    }

    public void agregarCadenaMando() {
	//cadenaMandoModel.setSolicita(cadenaMandoModel.getU().getNombre());
	cadenaMandoModel.setUsuario(null);
	cadenaMandoModel.setRevisa(null);
	cadenaMandoModel.setAprueba(null);
	cadenaMandoModel.setCadenaAprobacionVo(null);
	traerUsuarioJson();
	cadenaMandoModel.setModalPop(true);
    }

    public void registroCadenaMando() {
	boolean v;
	try {
	    v = this.cadenaMandoModel.registroCadenaMando();
	    if (v) {
		cadenaMandoModel.setUsuario(null);
		cadenaMandoModel.setRevisa(null);
		cadenaMandoModel.setAprueba(null);
//                        cadenaMandoModel.setModalPop(false);
		cadenaMandoModel.llenarListaCadena();
		PrimeFaces.current().executeScript(";dialogoOK('dialogoAgregarCad');");
		//                    getTraerCadena();
	    } else {
		FacesUtils.addInfoMessage(new SIAException().getMessage());
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion: " + e.getMessage());
	}
    }

    public void cancelarRegistroCadenaMando() {
	cadenaMandoModel.setRevisa(null);
	cadenaMandoModel.setAprueba(null);
	cadenaMandoModel.setModalPop(false);
    }

    public void cerrarPop() {
	if (cadenaMandoModel.isModalPop()) {
	    cadenaMandoModel.setModalPop(false);
	}
	if (cadenaMandoModel.isModalModificarPop()) {
	    cadenaMandoModel.setCadenaAprobacionVo(null);
	    cadenaMandoModel.setModalModificarPop(false);
	}
    }

    public void modificarCadena() {
	int idCad = Integer.parseInt(FacesUtils.getRequestParameter("idCad"));
	//cadenaMandoModel.setCadenaAprobacionVo((CadenaAprobacionVo) cadenaMandoModel.getListaCadena().getRowData());
	cadenaMandoModel.setCadenaAprobacionVo(cadenaMandoModel.traerCadenaAprobacion(idCad));
	UtilLog4j.log.info(this, "Ca: " + cadenaMandoModel.getCadenaAprobacionVo().getSolicita() + cadenaMandoModel.getRevisa());
	traerUsuarioJson();
	cadenaMandoModel.setRevisa("");
	cadenaMandoModel.setAprueba("");
	//setModalModificarPop(true);
    }

    public void cancelarModificacion() {
	cadenaMandoModel.setCadenaAprobacionVo(null);
	cadenaMandoModel.setRevisa(null);
	cadenaMandoModel.setAprueba(null);
	PrimeFaces.current().executeScript(";dialogoOK('dialogoModificarCad');");
	//      cadenaMandoModel.setModalModificarPop(false);
    }

    public void completarModificacion() {
	if (cadenaMandoModel.getRevisa().equals("")
		&& cadenaMandoModel.getAprueba().equals("")) {
	    FacesUtils.addInfoMessage("Es necesario agregar al menos un campo para poder modificar la cadena de mando");
	}  else {
	    boolean v;
	    v = this.cadenaMandoModel.completarModificacion();
	    if (v) {
		cadenaMandoModel.setCadenaAprobacionVo(null);
		cadenaMandoModel.setRevisa(null);
		cadenaMandoModel.setAprueba(null);
		cadenaMandoModel.llenarListaCadena();
		PrimeFaces.current().executeScript(";dialogoOK('dialogoModificarCad');");
	    } else {
		FacesUtils.addInfoMessage("Ocurrio un error : : : : ");
	    }
	}
    }

    public void eliminarCadena() {
	int idCad = Integer.parseInt(FacesUtils.getRequestParameter("idCad"));
	cadenaMandoModel.setCadenaAprobacionVo(new CadenaAprobacionVo());
	cadenaMandoModel.getCadenaAprobacionVo().setId(idCad);
//        cadenaMandoModel.setCadenaAprobacionVo((CadenaAprobacionVo) cadenaMandoModel.getListaCadena().getRowData());
	this.cadenaMandoModel.eliminarCadena();
	cadenaMandoModel.llenarListaCadena();
	if (cadenaMandoModel.getListaCadena().getRowCount() < 1) {
	    FacesUtils.addInfoMessage("No hay cadenas de aprobación para el usuario " + getUsuario());
	}
	cadenaMandoModel.setCadenaAprobacionVo(null);
//        this.agregar = "False";
    }
    
    public void eliminaVariasCadenas(){
        cadenaMandoModel.eliminarVariasCadenas();
    }

    public void usuarioListenerBusca(ValueChangeEvent textChangeEvent) {
	if (textChangeEvent.getComponent() instanceof SelectInputText) {
	    SelectInputText autoComplete = (SelectInputText) textChangeEvent.getComponent();
	    String cadenaDigitada = (String) textChangeEvent.getNewValue();
	    //
	    if (!cadenaDigitada.trim().isEmpty()) {
		cadenaMandoModel.setListaUsuarios(cadenaMandoModel.regresaUsuarioCampo(cadenaDigitada));//,"nombre", true, true, false));
		//
		if (autoComplete.getSelectedItem() != null) {
		    Usuario usuaroiSel = (Usuario) autoComplete.getSelectedItem().getValue();
		    cadenaMandoModel.setUsuario(usuaroiSel.getId());
		} else {
		    cadenaMandoModel.setUsuario(null);
		}
	    } else {
		cadenaMandoModel.setUsuario(null);
	    }
	}
    }

//    public List<SelectItem> regresaUsuarioActivo(String cadenaDigitada) {
//        List<SelectItem> list = new ArrayList<SelectItem>();
//        for (Usuario p : this.soporteProveedor.getUsuario()) {
//            if (p.getNombre() != null) {
//                String cadenaPersona = p.getNombre().toLowerCase();
//                cadenaDigitada = cadenaDigitada.toLowerCase();
//                if (cadenaPersona.indexOf(cadenaDigitada) >= 0) {
//                    SelectItem item = new SelectItem(p, p.getNombre());
//                    list.add(item);
//                }
//            }
//        }
//        return list;
//    }
    public void usuarioListenerAgregarSolicita(ValueChangeEvent textChangeEvent) {
	if (textChangeEvent.getComponent() instanceof SelectInputText) {
	    SelectInputText autoComplete = (SelectInputText) textChangeEvent.getComponent();
	    String cadenaDigitada = (String) textChangeEvent.getNewValue();
	    //

	    cadenaMandoModel.setListaUsuarios(cadenaMandoModel.regresaUsuarioActivo(cadenaDigitada));//,"nombre", true, true, false));
	    //
	    if (autoComplete.getSelectedItem() != null) {
		Usuario usuaroiSel = (Usuario) autoComplete.getSelectedItem().getValue();
		cadenaMandoModel.setSolicita(usuaroiSel.getNombre());
		UtilLog4j.log.info(this, "Solicita:" + cadenaMandoModel.getSolicita());
//                cadenaMandoModel.setListaCadena(null);
	    }
	}
    }

    public void usuarioListenerAgregarRevisa(ValueChangeEvent textChangeEvent) {
	if (textChangeEvent.getComponent() instanceof SelectInputText) {
	    SelectInputText autoComplete = (SelectInputText) textChangeEvent.getComponent();
	    String cadenaDigitada = (String) textChangeEvent.getNewValue();
	    //
	    if (!cadenaDigitada.trim().isEmpty()) {
		cadenaMandoModel.setListaUsuarios(cadenaMandoModel.regresaUsuarioCampo(cadenaDigitada));//,"nombre", true, true, false));
		//
		if (autoComplete.getSelectedItem() != null) {
		    Usuario usuaroiSel = (Usuario) autoComplete.getSelectedItem().getValue();
		    cadenaMandoModel.setRevisa(usuaroiSel.getNombre());
		    UtilLog4j.log.info(this, "Revisa:" + cadenaMandoModel.getRevisa());
//                    cadenaMandoModel.setListaCadena(null);
		} else {
		    cadenaMandoModel.setRevisa("");
//                    cadenaMandoModel.setU(null);
		}
	    }
	}
    }

    public void usuarioListenerAgregarAprueba(ValueChangeEvent textChangeEvent) {
	if (textChangeEvent.getComponent() instanceof SelectInputText) {
	    SelectInputText autoComplete = (SelectInputText) textChangeEvent.getComponent();
	    String cadenaDigitada = (String) textChangeEvent.getNewValue();
	    //
	    if (!cadenaDigitada.trim().isEmpty()) {
		cadenaMandoModel.setListaUsuarios(cadenaMandoModel.regresaUsuarioCampo(cadenaDigitada));//,"nombre", true, true, false));
		//
		if (autoComplete.getSelectedItem() != null) {
		    Usuario usuaroiSel = (Usuario) autoComplete.getSelectedItem().getValue();
		    cadenaMandoModel.setAprueba(usuaroiSel.getNombre());
		    UtilLog4j.log.info(this, "Aprueba:" + cadenaMandoModel.getAprueba());
//                    cadenaMandoModel.setListaCadena(null);
		} else {
		    cadenaMandoModel.setAprueba("");
//                    cadenaMandoModel.setU(null);
		}
	    }
	}
    }
    //Modificaciones

    public void usuarioListenerRevisaMod(ValueChangeEvent textChangeEvent) {
	if (textChangeEvent.getComponent() instanceof SelectInputText) {
	    SelectInputText autoComplete = (SelectInputText) textChangeEvent.getComponent();
	    String cadenaDigitada = (String) textChangeEvent.getNewValue();
	    //
	    if (!cadenaDigitada.trim().isEmpty()) {
		cadenaMandoModel.setListaUsuarios(cadenaMandoModel.regresaUsuarioCampo(cadenaDigitada));//,"nombre", true, true, false));
		//
		if (autoComplete.getSelectedItem() != null) {
		    Usuario usuaroiSel = (Usuario) autoComplete.getSelectedItem().getValue();
		    cadenaMandoModel.setRevisa(usuaroiSel.getNombre());
		    UtilLog4j.log.info(this, "Revisa Mod:" + cadenaMandoModel.getRevisa());
//                    cadenaMandoModel.setListaCadena(null);
		} else {
//                cadenaMandoModel.setUsuario(null);
//                    cadenaMandoModel.setU(null);
		}
	    }
	}
    }

    public void usuarioListenerApruebaMod(ValueChangeEvent textChangeEvent) {
	if (textChangeEvent.getComponent() instanceof SelectInputText) {
	    SelectInputText autoComplete = (SelectInputText) textChangeEvent.getComponent();
	    String cadenaDigitada = (String) textChangeEvent.getNewValue();
	    //
	    if (!cadenaDigitada.trim().isEmpty()) {
		cadenaMandoModel.setListaUsuarios(cadenaMandoModel.regresaUsuarioCampo(cadenaDigitada));//,"nombre", true, true, false));
		//
		//
		if (autoComplete.getSelectedItem() != null) {
		    Usuario usuaroiSel = (Usuario) autoComplete.getSelectedItem().getValue();
		    cadenaMandoModel.setAprueba(usuaroiSel.getNombre());
		    UtilLog4j.log.info(this, "Aprueba Mod:" + cadenaMandoModel.getAprueba());
//                    cadenaMandoModel.setListaCadena(null);
		} else {
//                cadenaMandoModel.setUsuario(null);
//                    cadenaMandoModel.setU(null);
		}
	    }
	}
    }
    //Fin

    /*
     *
     */
    public DataModel getListaCadena() {
	return cadenaMandoModel.getListaCadena();
    }

    public void setListaCadena(DataModel listaCadena) {
	cadenaMandoModel.setListaCadena(listaCadena);
    }

    public List<SelectItem> getListaUsuarios() {
	return cadenaMandoModel.getListaUsuarios();
    }

    public void setListaUsuarios(List<SelectItem> listaUsuarios) {
	cadenaMandoModel.setListaUsuarios(listaUsuarios);
    }

    public void setCadenaMandoModel(CadenaMandoModel cadenaMandoModel) {
	this.cadenaMandoModel = cadenaMandoModel;
    }

    public String getAprueba() {
	return cadenaMandoModel.getAprueba();
    }

    public void setAprueba(String aprueba) {
	cadenaMandoModel.setAprueba(aprueba);
    }

    public String getRevisa() {
	return cadenaMandoModel.getRevisa();
    }

    public void setRevisa(String revisa) {
	cadenaMandoModel.setRevisa(revisa);
    }

    public String getSolicita() {
	return cadenaMandoModel.getSolicita();
    }

    public void setSolicita(String solicita) {
	cadenaMandoModel.setSolicita(solicita);
    }

    public String getUsuario() {
	return cadenaMandoModel.getUsuario();
    }

    public void setUsuario(String usuario) {
	cadenaMandoModel.setUsuario(usuario);
    }

    /**
     * @return the modalPop
     */
    public boolean isModalPop() {
	return cadenaMandoModel.isModalPop();
    }

    /**
     * @param modalPop the modalPop to set
     */
    public void setModalPop(boolean modalPop) {
	cadenaMandoModel.setModalPop(modalPop);
    }

    /**
     * @return the modalModificarPop
     */
    public boolean isModalModificarPop() {
	return cadenaMandoModel.isModalModificarPop();
    }

    /**
     * @param modalModificarPop the modalModificarPop to set
     */
    public void setModalModificarPop(boolean modalModificarPop) {
	cadenaMandoModel.setModalModificarPop(modalModificarPop);
    }

    /**
     * @return the idOperacion
     */
    public int getIdOrdena() {
	return cadenaMandoModel.getIdOrdena();
    }

    /**
     * @param idOperacion the idOperacion to set
     */
    public void setIdOrdena(int idOrdena) {
	cadenaMandoModel.setIdOrdena(idOrdena);
    }

    /**
     * @return the cadenaAprobacionVo
     */
    public CadenaAprobacionVo getCadenaAprobacionVo() {
	return cadenaMandoModel.getCadenaAprobacionVo();
    }

    /**
     * @param cadenaAprobacionVo the cadenaAprobacionVo to set
     */
    public void setCadenaAprobacionVo(CadenaAprobacionVo cadenaAprobacionVo) {
	cadenaMandoModel.setCadenaAprobacionVo(cadenaAprobacionVo);
    }

    /**
     * @return the idCampo
     */
    public int getIdCampo() {
	return cadenaMandoModel.getIdCampo();
    }

    /**
     * @param idCampo the idCampo to set
     */
    public void setIdCampo(int idCampo) {
	cadenaMandoModel.setIdCampo(idCampo);
    }

    /**
     * @return the campo
     */
    public String getCampo() {
	return cadenaMandoModel.getCampo();
    }

    /**
     * @param campo the campo to set
     */
    public void setCampo(String campo) {
	cadenaMandoModel.setCampo(campo);
    }
    
    public void traerCadenasUsuario(){
        System.out.println("prueba " +isRevisaReq()+ " "+ isApruebaReq()+ " "+getUsuario()); 
        cadenaMandoModel.cadenaByApruebaOrAndRevisa(isRevisaReq(), isApruebaReq(), getUsuario());
        
    }
    /**
     * @return the revisaReq
     */
    public boolean isRevisaReq() {
        return cadenaMandoModel.isRevisaReq();
    }

    /**
     * @param revisaReq the revisaReq to set
     */
    public void setRevisaReq(boolean revisaReq) {
        cadenaMandoModel.setRevisaReq(revisaReq);
    }

    /**
     * @return the apruebaReq
     */
    public boolean isApruebaReq() {
        return cadenaMandoModel.isApruebaReq();
    }

    /**
     * @param apruebaReq the apruebaReq to set
     */
    public void setApruebaReq(boolean apruebaReq) {
        cadenaMandoModel.setApruebaReq(apruebaReq);
    }
}
