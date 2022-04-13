/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.servicios.orden.impl;


import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jcarranza
 */
@Getter
@Setter
public class OrdenTiemposVO {
    
    private int idOrden;
    private String consecutivo;
    private String consecutivoRequi;
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
    
    private String nombreAutoriza;
    private Date fechaAutoriza;
    private int diasAutoriza;
    
    private String nombreCarta;
    private Date fechaCarta;
    private int diasCarta;
    
    private String nombreJuridico;
    private Date fechaJuridico;
    private int diasJuridico;
    
    private String nombreEnvia;
    private Date fechaEnvia;
    private int diasEnvia;
    
    private boolean sinCarta;
    
    private Date fechaEntrega;
    private Date fechaRecepcion;
    private int estatus;
    private int diasEntregaRecepcion;
    private int diasEnvidoRecepcion;
    
    private String nombreSocio;
    private Date fechaSocio;
    private int diasSocio;
}
