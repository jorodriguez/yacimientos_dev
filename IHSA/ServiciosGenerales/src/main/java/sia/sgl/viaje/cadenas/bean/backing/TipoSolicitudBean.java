package sia.sgl.viaje.cadenas.bean.backing;

import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import sia.modelo.SgTipoSolicitudViaje;
import sia.modelo.sgl.viaje.vo.TipoSolicitudViajeVO;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.sgl.viaje.cadenas.bean.model.TipoSolicitudBeanModel;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@Named
@RequestScoped
public class TipoSolicitudBean implements Serializable {

    @Inject
    Sesion sesion;
    @Inject
    private TipoSolicitudBeanModel tipoSolicitudBeanModel;

    public TipoSolicitudBean() {
    }

    public String goToCatalogoTipoSolicitudViaje() {
        if (sesion.getOficinaActual() == null) {
            return "/principal";
        } else {
            
            tipoSolicitudBeanModel.beginConversationTipoSolicitud();
            tipoSolicitudBeanModel.traerTipoSolicitudes();
            tipoSolicitudBeanModel.traerListaTipoEspecificoItems();
            return "/vistas/sgl/cadenas/catalogoTipoSolicitudViaje";
        }
    }

    public void crearTipoSolicitud(ActionEvent event) {
        UtilLog4j.log.info(this, "crearSolicitud en bean");
        if (!validar()) {
            tipoSolicitudBeanModel.crearTipoSolicitudViaje();
            tipoSolicitudBeanModel.traerTipoSolicitudes();
        }
    }

    public void modificarTipoSolicitud(ActionEvent event) {
        if (!validar()) {
            tipoSolicitudBeanModel.modificarTipoSolicitudViaje();
            tipoSolicitudBeanModel.traerTipoSolicitudes();
        }
    }

    public void eliminar(ActionEvent event) {
        //tipoSolicitudBeanModel.setTipoSolicitudViaje((SgTipoSolicitudViaje) tipoSolicitudBeanModel.getTipoSolicitudViajeModel().getRowData());
        TipoSolicitudViajeVO tipoSolicitudVo = (TipoSolicitudViajeVO) tipoSolicitudBeanModel.getTipoSolicitudViajeModel().getRowData();
        tipoSolicitudBeanModel.setTipoSolicitudViaje(tipoSolicitudBeanModel.findSgTipoSolicitudViaje(tipoSolicitudVo.getId())); 
        
        if(!tipoSolicitudBeanModel.validarEliminacion()){
            tipoSolicitudBeanModel.eliminarTipoSolicitudViaje();
            tipoSolicitudBeanModel.traerTipoSolicitudes();
        }else{
            FacesUtils.addInfoMessage("No puede eliminar este tipo de solicitud por que esta siendo utilizado por otro proceso..");
        }
    }

    private boolean validar() {
        if (tipoSolicitudBeanModel.getTipoEspecificoVia() != -1) {
            if (tipoSolicitudBeanModel.getTipoSolicitudViaje().getHorasAnticipacion() != -1 && tipoSolicitudBeanModel.getTipoSolicitudViaje().getHorasAnticipacion() != null) {
                if (!tipoSolicitudBeanModel.getTipoSolicitudViaje().getNombre().equals("")) {
                    if (!tipoSolicitudBeanModel.buscarTipoSolicitudRepetida()) {
                        return false;
                    } else {
                        FacesUtils.addInfoMessage("El nombre del tipo de solicitud ya existe..");
                        return true;
                    }
                } else {
                    FacesUtils.addInfoMessage("Por favor especifique un nombre para la solicitud..");
                    return true;
                }
            } else {
                FacesUtils.addInfoMessage("Por favor especifique un valor para las horas de anticipación..");
                return true;
            }
        } else {
            FacesUtils.addInfoMessage("Por favor especifique un tipo de ruta..");
            return true;
        }
    }

    public List<SelectItem> getTipoEspecificoViaItems() {
        return tipoSolicitudBeanModel.getListaTipoEspecificoViaItems();
    }

    public DataModel getTipoSolicitudViajeModel() {
        return tipoSolicitudBeanModel.getTipoSolicitudViajeModel();
    }

    public SgTipoSolicitudViaje getSgTipoSolicitudViaje() {
        return tipoSolicitudBeanModel.getTipoSolicitudViaje();
    }

    public void setSgTipoSolicitudViaje(SgTipoSolicitudViaje sgTipoSolicitudViaje) {
        tipoSolicitudBeanModel.setTipoSolicitudViaje(sgTipoSolicitudViaje);
    }

    public int getTipoEspecificoVia() {
        return tipoSolicitudBeanModel.getTipoEspecificoVia();
    }

    public void setTipoEspecificoVia(int tipoEspecificoVia) {
        tipoSolicitudBeanModel.setTipoEspecificoVia(tipoEspecificoVia);
    }

    public String getNombre() {
        return tipoSolicitudBeanModel.getNombre();
    }

    public void setNombre(String nombre) {
        tipoSolicitudBeanModel.setNombre(nombre);
    }

    public int getHrsAnticipacion() {
        return tipoSolicitudBeanModel.getHrsAnticipacion();
    }

    public void setHrsAnticipacion(int hrsAnticipacion) {
        tipoSolicitudBeanModel.setHrsAnticipacion(hrsAnticipacion);
    }
    //popup's

    public boolean getPopupCrear() {
        return tipoSolicitudBeanModel.isMrPopupCrear();
    }

    public void mostrarPopupCrear(ActionEvent event) {
        UtilLog4j.log.info(this, "mostrar popup crear");
        tipoSolicitudBeanModel.setOperacion("INSERTAR");
        tipoSolicitudBeanModel.traerListaTipoEspecificoItems();
        tipoSolicitudBeanModel.setTipoSolicitudViaje(new SgTipoSolicitudViaje());
        tipoSolicitudBeanModel.setTipoEspecificoVia(-1);
        tipoSolicitudBeanModel.setMrPopupCrear(true);
    }

    public void ocultarPopupCrear(ActionEvent event) {
        tipoSolicitudBeanModel.setOperacion("INSERTAR");
        tipoSolicitudBeanModel.setTipoSolicitudViaje(null);
        tipoSolicitudBeanModel.setMrPopupCrear(false);
    }

    public void mostrarPopupModificar(ActionEvent event) {
        UtilLog4j.log.info(this, "mostrar popup para modificar");
        tipoSolicitudBeanModel.setOperacion("MODIFICAR");
        //tipoSolicitudBeanModel.setTipoSolicitudViaje((SgTipoSolicitudViaje) tipoSolicitudBeanModel.getTipoSolicitudViajeModel().getRowData());
        TipoSolicitudViajeVO tipoSolicitudVo = (TipoSolicitudViajeVO) tipoSolicitudBeanModel.getTipoSolicitudViajeModel().getRowData();
        tipoSolicitudBeanModel.setTipoSolicitudViaje(tipoSolicitudBeanModel.findSgTipoSolicitudViaje(tipoSolicitudVo.getId())); 
        tipoSolicitudBeanModel.setTipoEspecificoVia(tipoSolicitudBeanModel.getTipoSolicitudViaje().getSgTipoEspecifico().getId());
        tipoSolicitudBeanModel.setMrPopupCrear(true);
    }

    public String getOperacion() {
        return tipoSolicitudBeanModel.getOperacion();
    }
}
