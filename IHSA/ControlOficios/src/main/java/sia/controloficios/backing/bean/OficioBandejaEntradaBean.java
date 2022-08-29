

package sia.controloficios.backing.bean;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.MessagingException; 
import sia.constantes.Constantes;
import sia.excepciones.InsufficientPermissionsException;
import sia.modelo.oficio.vo.OficioConsultaVo;
import sia.servicios.oficio.impl.OfOficioImpl;
import sia.util.ui.AccionUI;

/**
 * Backing bean para la pantalla de Bandeja de Entrada.
 * 
 * @author esapien
 */
//@ManagedBean
@Named(value = "oficioBandejaEntradaBean")
public class OficioBandejaEntradaBean extends OficioBaseBean {
    
    /**
     * 
     */
    @Inject
    private OfOficioImpl oficioServicioRemoto;
    private AccionUI botonNuevo;
    
    
    private int oficioEntradaId;
    
    private int oficioSalidaId;
    
    
    
    /**
     * Tareas PostConstruct.
     * 
     * @throws InsufficientPermissionsException 
     */
    @Override
    protected void postConstruct() throws InsufficientPermissionsException {
        
        getLogger().info(this, "OficioBandejaEntradaBean - iniciar()");
        System.out.println("@@@@OficioBandejaEntradaBean");
        
        // inicializar bean default
        setVo(new OficioConsultaVo());
        
        // configurar boton Nuevo
        // solo roles emisores pueden dar de alta oficios
        botonNuevo = new AccionUI();
        
        botonNuevo.setVisible(getPermisos().isAltaOficio());
        
        // obtener oficios para los resultados según los permisos
        buscarOficiosBandejaEntrada(null);
        
        // solo para roles de edición
        getSesion().setModoEdicion(getPermisos().isRolEdicionOficios());
        
        getLogger().info(this, "modoEdicion = " + getSesion().isModoEdicion());
        
        // valores para popup de simbologia
        
        this.oficioEntradaId = Constantes.OFICIOS_TIPO_OFICIO_ENTRADA_ID;
        this.oficioSalidaId = Constantes.OFICIOS_TIPO_OFICIO_SALIDA_ID;
        System.out.println("@@@@OficioBandejaEntradaBean Termino");
    }
    
    

    /**
     * 
     * @return 
     */
    @Override
    protected boolean permisosRequeridos() {
        return getPermisos().puedeEntrarBandejaEntrada();
    }
   
    /*** BORRAR
     * //jevazquez 18/02/15
     * @param actionEvent
     * @throws MessagingException 
     */
    public void botonTemporalEnvioOficios(ActionEvent actionEvent) throws MessagingException {
      
     oficioServicioRemoto.enviarNotificacionAltaOficios();
     //enviarNotificacionNoPromovidoOficios();
    }
    
    /**
     * Búsqueda de oficios desde la vista de Bandeja de Entrada.
     * 
     * @param actionEvent 
     */
    public void buscarOficiosBandejaEntrada(ActionEvent actionEvent) {
        
  // List<OficioPromovibleVo> oficiosPromoviblesVo=  
     this.setOficios(      this.getOficioConsultaServicioRemoto()
                .buscarOficiosBandejaEntrada(
                getVo(), 
                getPermisos(), 
                getSesion().getBloquesUsuario(),
                getSesion().getBloqueActivo().getBloqueId()));
 //  for( OficioPromovibleVo vo :oficiosPromoviblesVo){
   //    if (getSesion().getUsuario().)
      // if((vo.getEstatusId() == 800 && vo.getTipoOficioId() == 2) ||
        //       ( vo.getEstatusId() == 815 && vo.getTipoOficioId() == 2) || (vo.getEstatusId() == 810 && vo.getTipoOficioId() == 1)){
       
   //} else if((vo.getEstatusId() == 800 && vo.getTipoOficioId() == 1) || (vo.getEstatusId() == 805 && vo.getTipoOficioId() == 2)){
       
   }
  // }
      
  //  }
    

    /**
     * 
     * @return 
     */
    public AccionUI getBotonNuevo() {
        return botonNuevo;
    }
    
    public int getOficioEntradaId() {
        return oficioEntradaId;
    }

    public int getOficioSalidaId() {
        return oficioSalidaId;
    }
    
    public String goDetalleTestIncludeParam(){
        return "detalle_test?faces-redirect=true&includeViewParams=true";
    }
    
    public String goDetalleTest(){
        return "detalle_test.xhtml?faces-redirect=true;";
    }
    
    public String goDetalleTestConParams(int id){
        return "detalle_test.xhtml?oficioId="+id+"&faces-redirect=true;";
    }
    
    
}
