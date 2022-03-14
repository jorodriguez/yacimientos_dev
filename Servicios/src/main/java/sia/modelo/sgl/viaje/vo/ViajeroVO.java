/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.viaje.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Getter
@Setter
public class ViajeroVO implements Serializable {

    private Integer id;
    private Integer idInvitado;
    private int idViaje;
    private String invitado;
    private String idUsuario;
    private String usuario;
    private String codigoViaje; //codigo de viaje
    private String codigoEstancia; //codigo de solicitud de estancia
    private Date fechaSalida;
    private Date horaSalida;
    private Date fechaRegreso;
    private Date horaRegreso;
    private Date fechaRegresoViaje;
    private Date horaRegresoViaje;
    private String destino;
    private boolean estancia;
    private Integer sgSolicitudEstancia;
    private String observacion;
    private String correo;
    private String gerenciaResponsable;
    private String responsableDeGerencia;
    private String empresa;
    private Integer sgViaje;
    private Date fechaSalidaViaje;
    private Date horaSalidaViaje;
    private String telefono;
    private boolean agregado;
    private boolean estanciaB;
    private boolean sinEstancia;
    private boolean selected;
    private boolean empleado;
    private boolean savedDB;
    private boolean filtered;
    private int idSolicitudViaje;
    private boolean esEmpleado;
    private boolean redondo;
    private String codigoSolicitudViaje;
    private int idEstatus;
    private int tipoViajero; //Este campo es para saber si el viajero 1. va por primera vez en un viaje, 2. lo bajaron en la oficina destino, 3. l bajaron del viaje en la oficina origen,
    private ViajeVO viajeVO;
    private SolicitudViajeVO solicitudViajeVO;
    private String rutaViaje;
    private String conductor;
    private String tipoViaje;
    private String origen;
    private int idDestino;
    private int idOrigen;
    private int viajeroQuedado;
    private boolean viajo;
    private String generoSolicitudViaje;
    private int idOperacion;
    private String operacion;
    private int total;
    private String fechaGenero;
    private String gerencia;
    private int idGerencia;
    private boolean intercambiarEnViaje = true;
    private int idRutaViaje;
    private int idViajeroEscala;
    private boolean grAutorizo;
    private String grAutorizoMotivo;
    private boolean confirTel = false;
    private boolean editarTel = false;
    

    public ViajeroVO() {
    }

    public ViajeroVO(int id, String invitado, String usuario, String codigoViaje, Date fechaSalida, Date horaSalida, Date fechaRegreso, Date horaRegreso, String destino) {
	this.id = id;
	this.invitado = invitado;
	this.usuario = usuario;
	this.codigoViaje = codigoViaje;
	this.fechaSalida = fechaSalida;
	this.horaSalida = horaSalida;
	this.fechaRegreso = fechaRegreso;
	this.horaRegreso = horaRegreso;
	this.destino = destino;
    }

    
    public boolean equals(Object obj){
        return this.idUsuario.equals(((ViajeroVO)obj).getIdUsuario());
    }
    
    public String getUsuarioTxt(){
        String usuarioTxt;
        if(this.idUsuario == null || this.idUsuario.isEmpty() || this.idUsuario.equals("null")){
            usuarioTxt = this.invitado;
        } else {
            usuarioTxt = this.usuario;
        }
        return usuarioTxt;
    }
}
