/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.contrato.vo;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jcarranza
 */
@Setter
@Getter
public class EvaluacionRespuestaVo {

    private int idEvaluacion;
    private String codigoConvenio;
    private String nombreGerencia;
    private String nombreProveedor;
    private Date fecha;

    private SeccionVo calidadDelServicio = new SeccionVo("Calidad del Servicio");
    private SeccionVo cumplimientoDePlazo = new SeccionVo("Cumplimiento de plazo");
    private SeccionVo cumplimientoHSE = new SeccionVo("Cumplimiento HSE");
    private SeccionVo servicioDiario = new SeccionVo("Servicio diario");

    private SeccionVo CumplimientoDeCantidad = new SeccionVo("Cumplimiento de cantidad");
    private SeccionVo ServicioDurantePostventa = new SeccionVo("Servicio durante y postventa");

    private SeccionVo SeguridadMedioAmbiente = new SeccionVo("Seguridad y medio ambiente");
    private SeccionVo DocumentacionFinal = new SeccionVo("Documentacion final");
    private SeccionVo ServicioDuranteObra = new SeccionVo("Servicio durante la obra");

    private double puntosTotal;
    private String observaciones;
    private String nombreGerenciaRE;
    private String correo;
    
}
