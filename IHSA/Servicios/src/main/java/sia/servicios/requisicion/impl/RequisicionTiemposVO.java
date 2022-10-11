/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.servicios.requisicion.impl;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jcarranza
 */
@Getter
@Setter
public class RequisicionTiemposVO {

    private int idRequisicion;
    private String consecutivo;
    private String referencia;
    private String gerencia;
    private int idGerencia;
    private String tipo;
    
    private String nombreSolicita;
    private Date fechaSolicita;
    
    private String nombreRevisa;
    private Date fechaRevisa;
    private int diasRevisa;
    
    private String nombreVistoBueno;
    private Date fechaVistoBueno;
    private int diasVistoBueno;
    
    private String nombreAprueba;
    private Date fechaAprueba;
    private int diasAprueba;
    
    private String nombreAsigna;
    private Date fechaAsigna;
    private int diasAsigna;

}
