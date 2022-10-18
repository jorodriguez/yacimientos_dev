/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.viaje.cadenas.bean.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.inject.Inject;
import sia.constantes.Constantes;
import sia.modelo.SgTipoSolicitudViaje;
import sia.modelo.SgTipoTipoEspecifico;
import sia.servicios.sgl.impl.SgCadenaAprobacionImpl;
import sia.servicios.sgl.impl.SgTipoImpl;
import sia.servicios.sgl.impl.SgTipoSolicitudViajeImpl;
import sia.servicios.sgl.impl.SgTipoTipoEspecificoImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.ConversationsManager;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@Named(value = "tipoSolicitudBeanModel")
@ConversationScoped
public class TipoSolicitudBeanModel implements Serializable{
    
    @Inject
    private Sesion sesion;
    @Inject
    private Conversation conversationTipoSolicitud;
    @Inject
    private ConversationsManager conversationsManager;
    @Inject
    private SgTipoSolicitudViajeImpl tipoSolicitudViajeService; 
    @Inject
    private SgTipoTipoEspecificoImpl tipoTipoEspecificoService;
    @Inject
    private SgCadenaAprobacionImpl cadenaAprobacionService;
    @Inject
    private SgTipoImpl sgTipoImpl;
    
    private SgTipoSolicitudViaje tipoSolicitudViaje;
    private int tipoEspecificoVia;
    private String nombre;
    private int hrsAnticipacion;
    private DataModel tipoSolicitudViajeModel;
    private List<SelectItem> listaTipoEspecificoViaItems;
    
    //pop
    private boolean mrPopupCrear=false;
    private String operacion="";
   
   
    public TipoSolicitudBeanModel() {
    }
    
     public void beginConversationTipoSolicitud() {
        UtilLog4j.log.info(this, "tipoSolicitud.beginConversationTipoSolicitud");
        this.conversationsManager.beginConversation(conversationTipoSolicitud, "sgTipoSolicitud");     
        
    }
    
    public void traerTipoSolicitudes(){
        UtilLog4j.log.info(this, "traerTipoSolicitudes");
         try{
            ListDataModel<SgTipoSolicitudViaje> tipoSolicitudesModel = new ListDataModel(this.tipoSolicitudViajeService.findAllTipoSolicitud());
            setTipoSolicitudViajeModel(tipoSolicitudesModel);
        }catch(Exception e){
            UtilLog4j.log.info(this, "Excepcion en crearTipoSolicitud "+e.getMessage());
        }
     }     
    
    public SgTipoSolicitudViaje findSgTipoSolicitudViaje(Integer idTipoSolicitudViaje){
        return tipoSolicitudViajeService.find(idTipoSolicitudViaje);        
    }
    public List<SelectItem> traerListaTipoEspecificoItems() {
        List<SelectItem> l = new ArrayList<SelectItem>();
        List<SgTipoTipoEspecifico> lt = null;
        try {
            //lt = tipoTipoEspecificoService.traerPorIdTipo(5,Constantes.BOOLEAN_FALSE);
            
            lt = tipoTipoEspecificoService.traerPorTipoPago(sgTipoImpl.find(5),Constantes.BOOLEAN_FALSE,Constantes.BOOLEAN_FALSE);
            
            for (SgTipoTipoEspecifico via : lt) {
                SelectItem item = new SelectItem(via.getSgTipoEspecifico().getId(),via.getSgTipoEspecifico().getNombre());
                l.add(item);
            }
            this.setListaTipoEspecificoViaItems(l);
            return getListaTipoEspecificoViaItems();
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio un error en la consulta de items " + e.getMessage());
            return null;
        }
    }
    
    public boolean buscarTipoSolicitudRepetida(){
        UtilLog4j.log.info(this, "buscarTipoSolicitudRepetida");
         try{
            return tipoSolicitudViajeService.buscarTipoSolicitudRepetida(tipoSolicitudViaje.getNombre(),tipoSolicitudViaje.getHorasAnticipacion(),getTipoEspecificoVia());            
        }catch(Exception e){
            UtilLog4j.log.info(this, "Excepcion al buscar el tipo de solicitud repetida "+e.getMessage());
            return false;
        }
     }
    
    public boolean validarEliminacion(){
         try{
            return cadenaAprobacionService.buscarTipoSolicitudViajeOcupado(getTipoSolicitudViaje().getId());
        }catch(Exception e){
            UtilLog4j.log.info(this, "Excepcion al validar la eliminacion "+e.getMessage());
            return false;
        }
    }
    
    public void crearTipoSolicitudViaje(){
        UtilLog4j.log.info(this, "tiposolicitudBeanModel.creattipoSolicitud");
        UtilLog4j.log.info(this, "NOmbre "+tipoSolicitudViaje.getNombre());
        UtilLog4j.log.info(this, "tipo espe "+getTipoEspecificoVia());
        UtilLog4j.log.info(this, "hrs ant "+tipoSolicitudViaje.getHorasAnticipacion());
        try{
//            tipoSolicitudViaje = new SgTipoSolicitudViaje();
//            tipoSolicitudViaje.setNombre(this.getNombre());
//            tipoSolicitudViaje.setHorasAnticipacion(this.getHrsAnticipacion());            
            tipoSolicitudViajeService.crearTipoSolicitud(this.tipoSolicitudViaje, this.getTipoEspecificoVia(), sesion.getUsuario());
            setMrPopupCrear(false);
        }catch(Exception e){
            UtilLog4j.log.info(this, "Excepcion en crearTipoSolicitud en beanModel"+e.getMessage());
        }
    }
    
    public void modificarTipoSolicitudViaje(){
        try{
            tipoSolicitudViajeService.modificarTipoSolicitud(this.tipoSolicitudViaje, this.getTipoEspecificoVia(), sesion.getUsuario());
            setMrPopupCrear(false);
        }catch(Exception e){
            UtilLog4j.log.info(this, "Excepcion en modificarTipoSolicitud"+e.getMessage());
        }
    }
    
    public void eliminarTipoSolicitudViaje(){
        try{
            tipoSolicitudViajeService.eliminarTipoSolicitud(tipoSolicitudViaje, sesion.getUsuario());
        }catch(Exception e){
            UtilLog4j.log.info(this, "excepcion en eliminar tipoSolicitud"+e.getMessage());
        }
    }


    /**
     * @return the tipoSolicitudViaje
     */
    public SgTipoSolicitudViaje getTipoSolicitudViaje() {
        return tipoSolicitudViaje;
    }

    /**
     * @param tipoSolicitudViaje the tipoSolicitudViaje to set
     */
    public void setTipoSolicitudViaje(SgTipoSolicitudViaje tipoSolicitudViaje) {
        this.tipoSolicitudViaje = tipoSolicitudViaje;
    }

    /**
     * @return the tipoSolicitudViajeModel
     */
    public DataModel getTipoSolicitudViajeModel() {
        return tipoSolicitudViajeModel;
    }

    /**
     * @param tipoSolicitudViajeModel the tipoSolicitudViajeModel to set
     */
    public void setTipoSolicitudViajeModel(DataModel tipoSolicitudViajeModel) {
        this.tipoSolicitudViajeModel = tipoSolicitudViajeModel;
    }

    /**
     * @return the tipoEspecificoVia
     */
    public int getTipoEspecificoVia() {
        return tipoEspecificoVia;
    }

    /**
     * @param tipoEspecificoVia the tipoEspecificoVia to set
     */
    public void setTipoEspecificoVia(int tipoEspecificoVia) {
        this.tipoEspecificoVia = tipoEspecificoVia;
    }

    /**
     * @return the mrPopupCrear
     */
    public boolean isMrPopupCrear() {
        return mrPopupCrear;
    }

    /**
     * @param mrPopupCrear the mrPopupCrear to set
     */
    public void setMrPopupCrear(boolean mrPopupCrear) {
        this.mrPopupCrear = mrPopupCrear;
    }

    /**
     * @return the operacion
     */
    public String getOperacion() {
        return operacion;
    }

    /**
     * @param operacion the operacion to set
     */
    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    /**
     * @return the listaTipoEspecificoViaItems
     */
    public List<SelectItem> getListaTipoEspecificoViaItems() {
        return listaTipoEspecificoViaItems;
    }

    /**
     * @param listaTipoEspecificoViaItems the listaTipoEspecificoViaItems to set
     */
    public void setListaTipoEspecificoViaItems(List<SelectItem> listaTipoEspecificoViaItems) {
        this.listaTipoEspecificoViaItems = listaTipoEspecificoViaItems;
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
     * @return the hrsAnticipacion
     */
    public int getHrsAnticipacion() {
        return hrsAnticipacion;
    }

    /**
     * @param hrsAnticipacion the hrsAnticipacion to set
     */
    public void setHrsAnticipacion(int hrsAnticipacion) {
        this.hrsAnticipacion = hrsAnticipacion;
    }
    
}
