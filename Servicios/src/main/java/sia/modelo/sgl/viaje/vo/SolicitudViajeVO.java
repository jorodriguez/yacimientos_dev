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
import sia.constantes.Constantes;
import sia.modelo.sgl.semaforo.vo.SemaforoVo;
import sia.util.UtilSia;

/**
 *
 * @author mluis
 */
@Getter
@Setter
public class SolicitudViajeVO implements Serializable, Cloneable {

    private int idSolicitud;
    private int idSgTipoSolicitudViaje;
    private int idSgTipoEspecifico;
    private int idEstatusAprobacion;
    private int idOficinaOrigen;
    private int idOficinaDestino;
    private int idEstatus;
    private int idGerencia;
    private int idSgMotivo;
    private int countSgViajero;
//    private int idEstadoSemaforo;
    private String estatus;
    private String codigo;
    private String tipoSolicitud;
    private String tipoEspecifico;
    private String origen;
    private String gerencia;
    private String destino;
    private String motivo;
    private Date fechaSalida;
    private Date horaSalida;
    private Date fechaRegreso;
    private Date horaRegreso;
    private String observacion;
    private String genero;
    private String justificacionRetraso;
    private Date horaReunion;
    private String lugarReunion;
    private int idSiCiudadOrigen;
    private int idSiCiudadDestino;
    private String nombreSiCiudadOrigen;
    private boolean sencillo;
    private String colorSgSemaforo;
    private boolean redondo;
    private int idMotivoRetraso;
    private int idNoticia;
    //--Se muestran en el hostorial de aprobaciones
    private String nombreGenero;
    private Date fechaGenero;
    private Date horaGenero;
    private MotivoRetrasoVO motivoRetrasoVo;
    private ItinerarioCompletoVo itinerarioCompletoVoIda;
    private ItinerarioCompletoVo itinerarioCompletoVoVuelta;
    private int idSemaforo;
    private int idSolicitudEstancia;
    private int idMotivo;
    private Date horaMaximaSolicitar;
    private String correoGenero;
    private SemaforoVo semaforoVo;
    private SolicitudViajeMovimientoVo solicitudViajeMovimientoVo;
    private int total;
    private String viajero;
    private int idRutaTerrestre;
    private RutaTerrestreVo ruta;
    private List<ViajeroVO> viajeros;
    private int solicitudViajeDeRetorno;
    private boolean conChofer;
    private boolean select;
    private int idItinerarioIda;
    private int idItinerarioVuelta;
    private VehiculoVO vehiculoVO;
    private String nombreModifico;
    private Date fechaModifico;
    private Date horaModifico;
    private int idSolImcumplimiento = 0;
    private String nombreRuta = "";
    private String codEstancia = "";
    private Integer idEstancia = 0;
    private String listViajes;

    /**
     * Representa la la descripcion la justificacion de gerente
     */
    private JustIncumSolVo justIncumSol;

    public String getJson() {
        return UtilSia.getGson().toJson(this);
    }

    public String getFechaHoraSalidaTxt() {
        StringBuilder txt = new StringBuilder();
        if (Constantes.QUEDADO_ORIGEN == this.solicitudViajeDeRetorno || Constantes.PRIMERA_VEZ_VIAJE == this.solicitudViajeDeRetorno) {
            if (this.fechaSalida != null) {
                txt.append("Salida: ").append(Constantes.FMT_ddMMyyy.format(this.fechaSalida));
            }
            if (this.horaSalida != null) {
                txt.append(" ").append(Constantes.FMT_hmm_a.format(this.horaSalida));
            }
        } else if (Constantes.QUEDADO_OFICINA_DESTINO == this.solicitudViajeDeRetorno) {
            if (this.fechaRegreso != null) {
                txt.append("Regreso: ").append(Constantes.FMT_ddMMyyy.format(this.fechaRegreso));
            }
            if (this.horaRegreso != null) {
                txt.append(" ").append(Constantes.FMT_hmm_a.format(this.horaRegreso));
            }
        }
        return txt.toString();
    }
    
    
    public SolicitudViajeVO clone() throws CloneNotSupportedException{
        SolicitudViajeVO svCopia = (SolicitudViajeVO)super.clone();
        return svCopia;
    }
}
