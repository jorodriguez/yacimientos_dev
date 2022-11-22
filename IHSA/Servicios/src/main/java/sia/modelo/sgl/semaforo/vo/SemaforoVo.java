/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.semaforo.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Data;
import sia.modelo.sgl.viaje.vo.RutaTerrestreVo;
import sia.util.UtilSia;

/**
 *
 * @author mluis
 */
@Data
public class SemaforoVo {

    private int idEstadoSemaforo;
    private int idSemaforo;
    private int idRuta;
    private String estado;
    private String ruta;
    private int  idOficinaOrigen;
    private String origen;
//    private Date fechaInicio;
//    private Date horaInicio;
//    private Date fechaFin;
//    private Date horaFin;
    private Date horaMinimaRuta;
    private Date horaMaximaRuta;
    private String justificacion;
    private String justificacionCorto;
    private String color;
    private String nombreRuta;
    private String estilo;
    private String estiloSeleccion;
    private String estiloNoSeleccion;
    private String descripcion;
    private int rutaTipoEspecifico;
    private List<SemaforoVo> listaRutaOficina = new ArrayList<>();
    private List<SemaforoVo> listaRutaCiudad = new ArrayList<>();
    private List<SemaforoVo> listaRutaLugar = new ArrayList<>();
    private RutaTerrestreVo rutaVO;

    public SemaforoVo() {
    }

    public SemaforoVo(int idSemaforo, int idRuta, String estado, String ruta, String origen, Date fechaInicio, Date horaInicio, Date fechaFin, Date horaFin, Date horaMinima, Date horaMaxima, String justificacion, String color, String nombreRuta, String estilo, String estiloSeleccion, String estiloNoSeleccion, String descripcion, int rutaTipoEspecifico, List<SemaforoVo> listaRutaOficina, List<SemaforoVo> listaRutaCiudad, List<SemaforoVo> listaRutaLugar, RutaTerrestreVo rutaVO) {

        //this.idEstadoSemaforo = idEstadoSemaforo;
        this.idSemaforo = idSemaforo;
        this.idRuta = idRuta;
        this.estado = estado;
        this.ruta = ruta;
        this.origen = origen;
//        this.fechaInicio = fechaInicio;
//        this.horaInicio = horaInicio;
//        this.fechaFin = fechaFin;
//        this.horaFin = horaFin;
        this.horaMinimaRuta = horaMinima;
        this.horaMaximaRuta = horaMaxima;
        this.justificacion = justificacion;
        this.color = color;
        this.nombreRuta = nombreRuta;
        this.estilo = estilo;
        this.estiloSeleccion = estiloSeleccion;
        this.estiloNoSeleccion = estiloNoSeleccion;
        this.descripcion = descripcion;
        this.rutaTipoEspecifico = rutaTipoEspecifico;
        this.listaRutaOficina = listaRutaOficina;
        this.listaRutaCiudad = listaRutaCiudad;
        this.listaRutaLugar = listaRutaLugar;
        this.rutaVO = rutaVO;
    }

    public String getJson() {
        return UtilSia.getGson().toJson(this);
    }
}
