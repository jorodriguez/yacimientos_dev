/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.viajes.corridas.bean.baking;

import java.io.Serializable;
import java.util.List;
import javax.inject.Named;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import org.jfree.util.Log;
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgRutaTerrestre;
import sia.modelo.SgViaje;
import sia.modelo.SgViajeVehiculo;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.sgl.viaje.bean.model.ViajeBeanModel;

/**
 *
 * @author EVAZQUEZ 
 */
@Named(value = "ViajesBySalirBean")
@RequestScoped
public class ViajesBySalirBean implements Serializable {
    
   
    
    //Sistema
   
   
    //Beans
    @Inject
    ViajeBeanModel viajeBeanModel;
    @Inject
     private Sesion sesion;
        
    public ViajesBySalirBean (){
        
    }
    
    public List getViajesBySalirMty() throws SIAException {

        return viajeBeanModel.traerViajesXSalirMty();
    }

    public List getViajesBySalirRey() throws SIAException {

        return viajeBeanModel.traerViajesXSalirRey();
    }

    public List getViajesBySalirSF() throws SIAException {

        return viajeBeanModel.traerViajesXSalirSF();
    }

    public List getViajesBySalirMon() throws SIAException {

        List e = viajeBeanModel.traerViajesXSalirMon();
       // buscarDetalleRutaEnViaje();
        return e;
        
    }

    public List getViajesBySalirAereos() throws SIAException {

        return viajeBeanModel.traerViajesXSalirAereo();
    }

    public void verDetalleViajeTerrestreBySalir(String codigo) {
        
        viajeBeanModel.setViajeVO(viajeBeanModel.searchViajeVoByCodigo(codigo));
        viajeBeanModel.setSgViaje(viajeBeanModel.buscarViajePorId(viajeBeanModel.getViajeVO().getId()));
        if (viajeBeanModel.getViajeVO().isVehiculoEmpresa()) {
            //busca el vehiculo asignado en viaje_vehiculo
            viajeBeanModel.setSgViajeVehiculo(viajeBeanModel.buscarVehiculoPorViajeVo());
        }
        buscarDetalleRutaEnViaje();
        viajeBeanModel.setIdRuta(viajeBeanModel.getViajeVO().getIdRuta());
        viajeBeanModel.setVerDetallePop(true);
    }
    public void buscarDetalleRutaEnViaje() {
        if (viajeBeanModel.getViajeVO() != null) {
            try {
                viajeBeanModel.getViajeVO().setDestino(viajeBeanModel.traerDestinoRutaEnViaje());
                
            } catch (SIAException ex) {
                FacesUtils.addErrorMessage(ex.getMessage());
            }
        }
    }
    public boolean isVerDetallePop() {
        return viajeBeanModel.isVerDetallePop();
    }
    
    public SgViaje getSgViaje() {
        return viajeBeanModel.getSgViaje();
    }
    public SgRutaTerrestre getSgRutaTerrestre() {
        try{
            return viajeBeanModel.getSgRutaTerrestre();
        }
        catch (Exception e){
            System.out.println("--------------"+e);
            return null;
        }
        
    
    }
    public SgViajeVehiculo getSgViajeVehiculo() {
        return viajeBeanModel.getSgViajeVehiculo();
    }
    
    public void AutorizarViaje (String s){
        try{
           viajeBeanModel.autoriza(s,sesion);
        }
        catch (Exception e){
            System.out.println("--------------"+e);
            
        
    }
}
    public boolean getPopAddViajero (){
        return viajeBeanModel.isPopAddViajero();
    }
    public void mostrarPopAddViajero (ActionEvent e  ){
        viajeBeanModel.setPopAddViajero(true);
    }
    public void cerrarPopAddViajero(ActionEvent a){
        viajeBeanModel.setPopAddViajero(false);
    }
    public String getCadena() {
        return this.viajeBeanModel.getCadena();
    }

    /**
     * @param cadena the cadena to set
     */
    public void setCadena(String cadena) {
        this.viajeBeanModel.setCadena(cadena);
    }
    public void iniciarDatosInvitado(ActionEvent event) {
        viajeBeanModel.setCadena("");
        llenarInvitadoJson();
    }

    public void iniciarDatosEmpleado(ActionEvent event) {
        String usuario = viajeBeanModel.usuariosJson();
        PrimeFaces.current().executeScript(";llenarProveedor('frmViajeAdministrar'," + usuario + ", 'nombreUsuario');");
        viajeBeanModel.setIdInvitado(0);
    }
    public void llenarInvitadoJson() {
        String invitado = viajeBeanModel.llenarInvitadoJson();
        PrimeFaces.current().executeScript(";llenarInvitado('frmViajeAdministrar'," + invitado + ", 'nombreInv');");
    }
    public void agregarEmpleadoAViaje(ActionEvent event) { //Método desde la página de agregar viajero a viaje
        if (viajeBeanModel.getCadena().trim().isEmpty()) {
            FacesUtils.addInfoMessage("Es necesario agregar el empleado para hacer esta operación.");
        } else {
            if (viajeBeanModel.agregarEmpleadoAViaje()) {
                FacesUtils.addInfoMessage("Se agregó el empleado al viaje. ");
                //   viajeBeanModel.setViajeVO(null);
                viajeBeanModel.setViajeVO(viajeBeanModel.searchViajeVoByCodigo(viajeBeanModel.getViajeVO().getCodigo()));
            } else {
                FacesUtils.addErrorMessage("Ocurrió una excepción al  guardar el viajero. ");
            }
            viajeBeanModel.setCadena("");
        }
    }

    public void agregarInvitadoAViaje(ActionEvent event) { //Método desde la página de agregar viajero a viaje
        if (viajeBeanModel.getIdInvitado() == Constantes.CERO) {
            FacesUtils.addInfoMessage("Es necesario agregar un invitado para hacer esta operación.");
        } else {
            if (viajeBeanModel.agregarInvitadoAViaje()) {
                viajeBeanModel.setIdInvitado(0);
                FacesUtils.addInfoMessage("Se agregó el invitado al viaje. ");
                //   viajeBeanModel.setViajeVO(null);
                viajeBeanModel.setViajeVO(viajeBeanModel.searchViajeVoByCodigo(viajeBeanModel.getViajeVO().getCodigo()));
            } else {
                FacesUtils.addErrorMessage("Ocurrió una excepción al  guardar el viajero. ");
            }
            viajeBeanModel.setCadena("");
        }
    }
    
    public boolean  getAddArchivo(){
        return viajeBeanModel.isAddArchivo();
    }
    public void popArchivo(ActionEvent e ){
        try{
        viajeBeanModel.setAddArchivo(true);
        int cod= Integer.valueOf(FacesUtils.getRequestParameter("viaje"));
        viajeBeanModel.setSgViaje(viajeBeanModel.buscarViajePorId(cod));  
            System.out.println("sgviaje "+viajeBeanModel.getSgViaje().getCodigo());
        viajeBeanModel.setCadena(viajeBeanModel.getSgViaje().getCodigo());
        System.out.println("sgviaje "+viajeBeanModel.getCadena());
        viajeBeanModel.setViajeVO(viajeBeanModel.buscarViajePorCodigo());
        System.out.println("sgviaje "+viajeBeanModel.getViajeVO().getCodigo());
        
        }
        catch(Exception ex){
            Log.info("Fallo en el pop de añadir archivo "+ ex );
        }
    }
     public ViajeVO getViajeVO() {
        return viajeBeanModel.getViajeVO();
    }

    /**
     * @param viajeVO the viajeVO to set
     */
    public void setViajeVO(ViajeVO viajeVO) {
        viajeBeanModel.setViajeVO(viajeVO);
    }
     public String getDir() {
        String d = "";
        if (viajeBeanModel.getSgViaje() != null) {
            d = viajeBeanModel.dir();
        }
        return d;
    }
     public void generarPDF(ActionEvent event){
         
        int viaje= Integer.valueOf(FacesUtils.getRequestParameter("viaje"));
        if (viaje > 0 ){
            viajeBeanModel.subirDocumentoAutomatico(viaje);
            FacesUtils.addInfoMessage( FacesUtils.getKeyResourceBundle("sistema.archivo.subirSatisfactorio"));
        }
     }
     
     public void cancelarViajes() throws SIAException{
       //  viajeBeanModel.cancelarViaje();
     }

}
