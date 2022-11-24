/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.viaje.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sia.constantes.Constantes;
import sia.modelo.comunicacion.vo.NoticiaVO;
import sia.modelo.gr.vo.GrPuntoVO;
import sia.modelo.sgl.vo.NodoTime;
import sia.modelo.sgl.vo.Vo;
import sia.util.UtilSia;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Getter
@Setter
public class ViajeVO extends Vo {

    private String codigo;
    private Date fechaSalida;
    private Date horaSalida;
    private Date fechaRegreso;
    private Date horaRegreso;
    private Date fechaProgramada;
    private Date horaProgramada;
    private String responsable;
    private int siAdjunto;
    private String status;
    private String terrestre;
    private boolean autobus;
    private boolean vehiculoPropio;
    private boolean vehiculoEmpresa;
    private String oficina;
    private String origen;
    private String adjunto;
    private String ruta;
    private int idRuta;
    private int idAdjunto;
    private String uuid;
    private int idItinerario;
    private String idUsuario;
    private String tipo;
    private int viajeIda;
    private String destinoRuta;
    private int sgViaje;
    private Integer idNoticia;
    private boolean cancelado;
    private int idEstatus;
    private String estatus;
    private String isTerrestreCiudad;
    private int idSgViajeCiudad;
    private int idOficinaDestino;
    private int idOficinaOrigen;
    private boolean redondo;
    private String destinoCiudad;
    private int estatusAnterior;
    private int idTipoEspecifico;
    private List<ViajeroVO> listaViajeros;
    private int numViajeros;
    private String gerencia;
    private int total;
    private String viajero;
    private String destino;
    private String vehiculo;
    private String solicitud;
    private double gasto;
    private int idCampo;
    private int tipoRuta;
    private String vehiculoPlaca;
    private String responsableTel;
    private NoticiaVO noticia;
    private int idOpercion;
    private int idViajeMovimiento;
    private int idOpercionIntercambio;
    private int idOpercionRetomarViaje;
    private int idViajeMovimientoIntercambio;
    private VehiculoVO vehiculoVO = new VehiculoVO();
    private ItinerarioCompletoVo itinerarioCompletoVo = new ItinerarioCompletoVo();
    private int idViajeVehiculo;
    private boolean tieneRegreso;
    private String idResponsable;
    private String idResponsableCombo;
    private List<GrPuntoVO> lstRutaDet;
    private int indicePS;
    private String tiempoViaje;
    private double tiempoViajeValor;
    private int tiempoViajeReal;
    private boolean conChofer;
    private List<NodoTime> lstTiempoNodos;
    private boolean  conInter; 
    private int idIntercambio;

    public String getJson() {
	return UtilSia.getGson().toJson(this);
    }

    public String getViajerosEmails() {
	StringBuilder emails = new StringBuilder();
	for (ViajeroVO viajero : listaViajeros) {
	    if (viajero.getCorreo() != null && !viajero.getCorreo().isEmpty()) {
		if (emails.toString().isEmpty()) {
		    emails.append(viajero.getCorreo());
		} else {
		    emails.append(", ").append(viajero.getCorreo());
		}
	    }
	}
	return emails.toString();
    }

    public String getHoraSalidaTxt() {
	String ret = "";
	if (this.horaSalida != null) {
	    ret = Constantes.FMT_hmm_a.format(this.horaSalida);
	}
	return ret;
    }

    public String getFechaSalidaTxt() {
	String ret = "";
	if (this.fechaSalida != null) {
	    ret = Constantes.FMT_ddMMyyy.format(this.fechaSalida);
	}
	return ret;
    }

    public String getHoraProgramadaTxt() {
	String ret = "";
	if (this.horaProgramada != null) {
	    ret = Constantes.FMT_hmm_a.format(this.horaProgramada);
	}
	return ret;
    }

    public String getFechaProgramadaTxt() {
	String ret = "";
	if (this.fechaProgramada != null) {
	    ret = Constantes.FMT_ddMMyyy.format(this.fechaProgramada);
	}
	return ret;
    }

    public void setTieneRegresoValor(boolean valor) {
	this.setTieneRegreso(valor);
    }

    public int getLugaresLibres() {
	int ret = 0;
	if (this.listaViajeros != null && this.listaViajeros.size() > 0
		&& this.vehiculoVO != null && this.vehiculoVO.getCapacidadPasajeros() > 0) {
	    ret = this.vehiculoVO.getCapacidadPasajeros() - this.listaViajeros.size();
	}
	return ret;
    }
    
    public void setIndicePSvalor(int idPS) {
	if (this.lstRutaDet != null && this.lstRutaDet.size() > 0
		&& idPS > 0) {
            for(int i = 0; i < this.lstRutaDet.size(); i++){
                if(this.lstRutaDet.get(i).getId() == idPS){
                    this.setIndicePS(i);
                }
            }	    
	}	
    }
    
    public void setIndicePSvalor(int idPS, List<GrPuntoVO> puntos) {
	if (puntos != null && puntos.size() > 0
		&& idPS > 0) {
            for(int i = 0; i < puntos.size(); i++){
                if(puntos.get(i).getId() == idPS){
                    this.setIndicePS(i);
                }
            }	    
	}	
    }
    
    public String getResponsableTelTxt(){
        String ret = "Sin captura";
        if(this.getResponsableTel() != null && !this.getResponsableTel().isEmpty()){
            ret = this.getResponsableTel();
        }
        return ret;
    }
    
    public void setTiempoViajeRealVal() {
        try {
            if (this.getTiempoViaje() != null && !this.getTiempoViaje().isEmpty()) {
                
                tiempoViajeValor = Double.valueOf(this.getTiempoViaje());
                
                Calendar hoy = Calendar.getInstance();
                Calendar calHoraSalida = Calendar.getInstance();
                calHoraSalida.setTime(this.getHoraSalida());
                Calendar fechaCompletaSalida = Calendar.getInstance();
                fechaCompletaSalida.setTime(this.getFechaSalida());
                fechaCompletaSalida.set(Calendar.HOUR, calHoraSalida.get(Calendar.HOUR_OF_DAY));
                fechaCompletaSalida.set(Calendar.MINUTE, calHoraSalida.get(Calendar.MINUTE));
                fechaCompletaSalida.set(Calendar.SECOND, 0);
                fechaCompletaSalida.set(Calendar.MILLISECOND, 0);
                long totalMinutosRecorridos = ((hoy.getTimeInMillis() - fechaCompletaSalida.getTimeInMillis()) / 1000 / 60);
                BigDecimal tiempoRutaMinutos = (BigDecimal.valueOf(tiempoViajeValor).multiply(BigDecimal.valueOf(60)));

                if (totalMinutosRecorridos > 0 && tiempoRutaMinutos.longValue() > 0
                        && totalMinutosRecorridos > tiempoRutaMinutos.longValue()) {
                    this.setTiempoViajeReal((int) ((totalMinutosRecorridos * 100) / tiempoRutaMinutos.longValue()));
                } else if (totalMinutosRecorridos > 0 && tiempoRutaMinutos.longValue() > 0) {
                    this.setTiempoViajeReal((int) ((totalMinutosRecorridos * 100) / tiempoRutaMinutos.longValue()));
                } else {
                    this.setTiempoViajeReal(0);
                }
                calculaLineaTempo();
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
    }
    
    private void calculaLineaTempo() {
        lstTiempoNodos = new ArrayList<NodoTime>();
        NodoTime primerNodo = new NodoTime();
        primerNodo.setId(0);
        primerNodo.setEtiqueta(String.valueOf(0));
        primerNodo.setPorcentaje(0);
        
        lstTiempoNodos.add(primerNodo);
        
        NodoTime ultimoNodo = new NodoTime();
        ultimoNodo.setId(0);
        ultimoNodo.setPorcentaje(92);
        ultimoNodo.setEtiqueta(this.getTiempoViaje());
        
        NodoTime auxNodo;
        for (double i = 1.0; i < tiempoViajeValor; i++) {
            auxNodo = new NodoTime();
            auxNodo.setId(BigDecimal.valueOf(i).intValue());
            auxNodo.setEtiqueta(String.valueOf(BigDecimal.valueOf(i).intValue()));
            auxNodo.setPorcentaje((i * ultimoNodo.getPorcentaje()) / tiempoViajeValor);
            lstTiempoNodos.add(auxNodo);
        }
        
        lstTiempoNodos.add(ultimoNodo);
    }
    
    public String getHoraRegresoTxt() {
	String ret = "";
	if (this.horaRegreso != null) {
	    ret = Constantes.FMT_hmm_a.format(this.horaRegreso);
	} else {
            ret = Constantes.FMT_hmm_a.format(new Date());
        }
	return ret;
    }

    public String getFechaRegresoTxt() {
	String ret = "";
	if (this.fechaRegreso != null) {
	    ret = Constantes.FMT_ddMMyyy.format(this.fechaRegreso);
	} else {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, 12);
            c.set(Calendar.MINUTE, 00);
            ret = Constantes.FMT_ddMMyyy.format(c.getTime());
        }
	return ret;
    }

    
    public String toString() {
        return "ViajeVO{" + "codigo=" + codigo + ", fechaSalida=" + fechaSalida + ", horaSalida=" + horaSalida + ", fechaRegreso=" + fechaRegreso + ", horaRegreso=" + horaRegreso + ", fechaProgramada=" + fechaProgramada + ", horaProgramada=" + horaProgramada + ", responsable=" + responsable + ", siAdjunto=" + siAdjunto + ", status=" + status + ", terrestre=" + terrestre + ", autobus=" + autobus + ", vehiculoPropio=" + vehiculoPropio + ", vehiculoEmpresa=" + vehiculoEmpresa + ", oficina=" + oficina + ", origen=" + origen + ", adjunto=" + adjunto + ", ruta=" + ruta + ", idRuta=" + idRuta + ", idAdjunto=" + idAdjunto + ", uuid=" + uuid + ", idItinerario=" + idItinerario + ", idUsuario=" + idUsuario + ", tipo=" + tipo + ", viajeIda=" + viajeIda + ", destinoRuta=" + destinoRuta + ", sgViaje=" + sgViaje + ", idNoticia=" + idNoticia + ", cancelado=" + cancelado + ", idEstatus=" + idEstatus + ", estatus=" + estatus + ", isTerrestreCiudad=" + isTerrestreCiudad + ", idSgViajeCiudad=" + idSgViajeCiudad + ", idOficinaDestino=" + idOficinaDestino + ", idOficinaOrigen=" + idOficinaOrigen + ", redondo=" + redondo + ", destinoCiudad=" + destinoCiudad + ", estatusAnterior=" + estatusAnterior + ", idTipoEspecifico=" + idTipoEspecifico + ", listaViajeros=" + listaViajeros + ", gerencia=" + gerencia + ", total=" + total + ", viajero=" + viajero + ", destino=" + destino + ", vehiculo=" + vehiculo + ", solicitud=" + solicitud + ", gasto=" + gasto + ", idCampo=" + idCampo + ", tipoRuta=" + tipoRuta + ", vehiculoPlaca=" + vehiculoPlaca + ", responsableTel=" + responsableTel + ", noticia=" + noticia + ", idOpercion=" + idOpercion + ", idViajeMovimiento=" + idViajeMovimiento + ", idOpercionIntercambio=" + idOpercionIntercambio + ", idOpercionRetomarViaje=" + idOpercionRetomarViaje + ", idViajeMovimientoIntercambio=" + idViajeMovimientoIntercambio + ", vehiculoVO=" + vehiculoVO + ", itinerarioCompletoVo=" + itinerarioCompletoVo + ", idViajeVehiculo=" + idViajeVehiculo + ", tieneRegreso=" + tieneRegreso + ", idResponsable=" + getIdResponsable() + ", lstRutaDet=" + lstRutaDet + ", indicePS=" + indicePS + ", tiempoViaje=" + tiempoViaje + ", tiempoViajeValor=" + tiempoViajeValor + ", tiempoViajeReal=" + tiempoViajeReal + ", conChofer=" + conChofer + ", lstTiempoNodos=" + lstTiempoNodos + ", conInter=" + conInter + '}';
    }

    /**
     * @return the idResponsable
     */
    public String getIdResponsable() {
        return idResponsable;
    }

    /**
     * @param idResponsable the idResponsable to set
     */
    public void setIdResponsable(String idResponsable) {
        this.idResponsable = idResponsable;
    }

    /**
     * @return the idResponsableCombo
     */
    public String getIdResponsableCombo() {
        return idResponsableCombo;
    }

    /**
     * @param idResponsableCombo the idResponsableCombo to set
     */
    public void setIdResponsableCombo(String idResponsableCombo) {
        this.idResponsableCombo = idResponsableCombo;
    }
    
}
