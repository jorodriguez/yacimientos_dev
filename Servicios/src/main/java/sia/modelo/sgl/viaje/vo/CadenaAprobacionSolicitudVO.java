/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.viaje.vo;
import java.io.Serializable;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import sia.constantes.Constantes;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author jrodriguez
 */

public class CadenaAprobacionSolicitudVO extends Vo implements Serializable{
    private Integer idEstatus;
    private String nombreEstatus;
    private Integer idGerencia;
    private String nombreGerencia;
    private int idSiRol;    
    private boolean apruebaGerenteArea;
    private boolean apruebaRol;
    private boolean verificarSemaforoAlterno;

    private String nombreResponsableGerencia;
    private String nombrePuestoResponsableGerencia;
    
    private String mensajeAsuntoCorreo;
             
    private Integer idTipoSolicitudViaje;
    
    private boolean autoApruebaFlujo=false;
    
    /* Este atributo es true cuando es la ultima cadena de aprobacion, es utilizado para saber cuando debe de pasar al analista
     de Sgl para realizar el viaje. y para envio de correos a oficinas destinos-*/
    private boolean ultimaCadena;
    
    /**
     * @return the idEstatus
     */
    public Integer getIdEstatus() {
        return idEstatus;
    }

    /**
     * @param idEstatus the idEstatus to set
     */
    public void setIdEstatus(Integer idEstatus) {
        this.idEstatus = idEstatus;
    }

    /**
     * @return the nombreEstatus
     */
    public String getNombreEstatus() {
        return nombreEstatus;
    }

    /**
     * @param nombreEstatus the nombreEstatus to set
     */
    public void setNombreEstatus(String nombreEstatus) {
        this.nombreEstatus = nombreEstatus;
    }

    /**
     * @return the idGerencia
     */
    public Integer getIdGerencia() {
        return idGerencia;
    }

    /**
     * @param idGerencia the idGerencia to set
     */
    public void setIdGerencia(Integer idGerencia) {
        this.idGerencia = idGerencia;
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
     * @return the nombreResponsableGerencia
     */
    public String getNombreResponsableGerencia() {
        return nombreResponsableGerencia;
    }

    /**
     * @param nombreResponsableGerencia the nombreResponsableGerencia to set
     */
    public void setNombreResponsableGerencia(String nombreResponsableGerencia) {
        this.nombreResponsableGerencia = nombreResponsableGerencia;
    }

    /**
     * @return the nombrePuestoResponsableGerencia
     */
    public String getNombrePuestoResponsableGerencia() {
        return nombrePuestoResponsableGerencia;
    }

    /**
     * @param nombrePuestoResponsableGerencia the nombrePuestoResponsableGerencia to set
     */
    public void setNombrePuestoResponsableGerencia(String nombrePuestoResponsableGerencia) {
        this.nombrePuestoResponsableGerencia = nombrePuestoResponsableGerencia;
    }

    /**
     * @return the idTipoSolicitudViaje
     */
    public Integer getIdTipoSolicitudViaje() {
        return idTipoSolicitudViaje;
    }

    /**
     * @param idTipoSolicitudViaje the idTipoSolicitudViaje to set
     */
    public void setIdTipoSolicitudViaje(Integer idTipoSolicitudViaje) {
        this.idTipoSolicitudViaje = idTipoSolicitudViaje;
    }

    /**
     * @return the idSiRol
     */
    public int getIdSiRol() {
        return idSiRol;
    }

    /**
     * @param idSiRol the idSiRol to set
     */
    public void setIdSiRol(int idSiRol) {
        this.idSiRol = idSiRol;
    }

    /**
     * @return the apruebaGerenteArea
     */
    public boolean isApruebaGerenteArea() {
        return apruebaGerenteArea;
    }

    /**
     * @param apruebaGerenteArea the apruebaGerenteArea to set
     */
    public void setApruebaGerenteArea(boolean apruebaGerenteArea) {
        this.apruebaGerenteArea = apruebaGerenteArea;
    }

    /**
     * @return the verificarSemaforoAlterno
     */
    public boolean isVerificarSemaforoAlterno() {
        return verificarSemaforoAlterno;
    }

    /**
     * @param verificarSemaforoAlterno the verificarSemaforoAlterno to set
     */
    public void setVerificarSemaforoAlterno(boolean verificarSemaforoAlterno) {
        this.verificarSemaforoAlterno = verificarSemaforoAlterno;
    }

    /**
     * @return the apruebaRol
     */
    public boolean isApruebaRol() {
        return apruebaRol;
    }

    /**
     * @param apruebaRol the apruebaRol to set
     */
    public void setApruebaRol(boolean apruebaRol) {
        this.apruebaRol = apruebaRol;
    }
    
   public String getMensajeAsuntoCorreo(String codigo, boolean tipoAoT){
       switch(this.idEstatus){
           case Constantes.ESTATUS_PENDIENTE :
               this.mensajeAsuntoCorreo = Constantes.MENSAJE_ASUNTO_CORREO_SOLICITAR_VIAJE;
               break;
           case Constantes.ESTATUS_VISTO_BUENO:
               this.mensajeAsuntoCorreo = Constantes.MENSAJE_ESTATUS_SOLICITUD_VIAJE_VISTO_BUENO;
               break;
           case Constantes.ESTATUS_APROBAR : //AGREGAR OTRO BOLEANO PARA VALIDAR EL TIPO
               if(tipoAoT){
                   this.mensajeAsuntoCorreo = Constantes.MENSAJE_ESTATUS_SOLICITUD_AUTORIZADA;//aereo
               } else {
                   this.mensajeAsuntoCorreo = Constantes.MENSAJE_ESTATUS_SOLICITUD_VIAJE_APROBAR;//terrestre
               }
               break;           
           case Constantes.ESTATUS_JUSTIFICAR:
               this.mensajeAsuntoCorreo = Constantes.MENSAJE_ESTATUS_SOLICITUD_VIAJE_JUSTIFICAR;
               break;
           case Constantes.ESTATUS_AUTORIZAR:
               this.mensajeAsuntoCorreo = Constantes.MENSAJE_ESTATUS_SOLICITUD_VIAJE_AUTORIZAR;
               break;
           case Constantes.ESTATUS_PARA_HACER_VIAJE:
               this.mensajeAsuntoCorreo = Constantes.MENSAJE_ESTATUS_SOLICITUD_VIAJE_PARA_CREAR_VIAJE_POR_ANALISTAS;
               break;
            case Constantes.ESTATUS_CON_CENTOPS:
               this.mensajeAsuntoCorreo = Constantes.MENSAJE_ESTATUS_SOLICITUD_VIAJE_AUTORIZAR;
               break;
           
       }       
       //
       return this.mensajeAsuntoCorreo + codigo;
   }
      
    /**
     * @return the autoApruebaFlujo
     */
    public boolean isAutoApruebaFlujo() {
        return autoApruebaFlujo;
    }

    /**
     * @param autoApruebaFlujo the autoApruebaFlujo to set
     */
    public void setAutoApruebaFlujo(boolean autoApruebaFlujo) {
        this.autoApruebaFlujo = autoApruebaFlujo;
    }

    /**
     * @return the ultimaCadena
     */
    public boolean isUltimaCadena() {
        return ultimaCadena;
    }

    /**
     * @param ultimaCadena the ultimaCadena to set
     */
    public void setUltimaCadena(boolean ultimaCadena) {
        this.ultimaCadena = ultimaCadena;
    }
   
    
}
