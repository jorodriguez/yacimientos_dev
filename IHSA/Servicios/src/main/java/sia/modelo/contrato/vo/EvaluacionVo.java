/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.contrato.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jcarranza
 */
@Getter
@Setter
public class EvaluacionVo {
    
    private int id;
    
    private int templateId;
    private String templateNombre;
    private String templateDescripcion;
    private String templateTitulo;
    private String templateInterpretacion;
    private String templateNotas;
    
    private int proveedorId;
    private String nombreProveedor;
    private String correoProveedor;
    
    private int gerenciaId;
    private String nombreGerencia;
    
    private int convenioId;
    private String convenioNombre;
    private String convenioCodigo;
    
    private List<SeccionVo> secciones = new ArrayList<>();
    
    private String responsable;
    private String nombreResponsable;
    private String nombreClasificacion;
    private Date fechaSolicitada;
    
    private String observaciones;
    
    private int sumatoriaMax;
    private double sumatoriaResp;
    
    private Date fechaEvaluacion = new Date();
    
    
}
