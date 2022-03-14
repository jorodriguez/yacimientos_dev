/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.vo;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import sia.util.UtilSia;

/**
 *
 * @author jrodriguez
 */
@Getter
@Setter
public class EstatusAprobacionVO extends Vo{  

    private String codigo;
    private String nombreTipoSolicitud;
    private String nombreOrigen;
    private String nombreDestino;
    private Date fechaSalida;
    private Date horaSalida;
    private Date fechaRegreso;
    private Date horaRegreso;
    private String nombreMotivo;
    private String nombreUsuarioAprobo;
    private String IdUsuarioAprobo;

    private Integer idEstatus;
    private String nombreEstatus;
    private String nombreGerenciaResponsable;
    private String observacion;
    private int idSolicitud;
    private int idTipoSolicitud;
    private int idTipoEspecifico;
    private boolean automatico;
    private int viajerosCount;

  
    private String justificacion;
    private String justificacionCorta;
    private boolean verJustificacionLarga;
    private boolean redondo;
    private String semaforo;
    private String colorSemaforo;
    private Date horaMinimaSemaforoActual;
    private Date horaMaximaSemaforoActual;
    private int countEscalas;
    private String motivoAutorizarJustificacion;
    private String correoUsuario;

    private boolean visible;

    /* True :  si la solicitud esta autorizada por Direccion general (singnifica que ya tiene un estatus 435)*/
    private boolean solicitudAutorizada;

  
    private boolean selected;

    /**
     * Representa la la descripcion del color del semaforo
     */
    private String mensajePoliticaColorSemaforo;
    private boolean justificado;


  

    
    public String getJson(){        
        return UtilSia.getGson().toJson(this);
    }

}
