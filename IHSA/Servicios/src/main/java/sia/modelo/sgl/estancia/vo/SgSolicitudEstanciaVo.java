/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.estancia.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author b75ckd35th
 */
@Getter
@Setter
public class SgSolicitudEstanciaVo implements Serializable {

    private int id;
    private Date fechaGenero;
    private String genero;
    private String nombre;
    private Date horaGenero;
    private int idSgOfina;
    private String nombreSgOficina;
    private int diasEstancia;
    private Date inicioEstancia;
    private Date finEstancia;
    private int idEstatus;
    private String nombreEstatus;
    private String codigo;
    private boolean cancelado;
    private int idSgMotivo;
    private String nombreSgMotivo;
    private int idGerencia;
    private String nombreGerencia;
    private int contIntegrantes;
    private boolean porViaje; //'true' si esta Solicitud de Estancia fue generada por una Solicitud de Viaje
    private String idUsuario;
    private String correoGenero;
    private String nombreGenero;
    private String correoUsuarioHospeda;
    private String idUsuarioHospeda;
    private List<DetalleEstanciaVO> detalle = new ArrayList<DetalleEstanciaVO>();
    private String observacion;
}
