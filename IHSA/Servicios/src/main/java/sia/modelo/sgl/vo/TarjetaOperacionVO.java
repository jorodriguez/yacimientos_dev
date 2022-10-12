/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.vo;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jevazquez
 */
@Getter
@Setter
public class TarjetaOperacionVO {
    private int id;
    private int tarjetaBancaria;
    private String usuario;
    private int usSinRegistro;
    private int estacion;
    private int vehiculo;
    private String descripcion;
    private String placa;
    private double cargo;
    private String concepto;
    private Date fechaOperacion;
    private Date horaOperacion;
    private String tipo;
    private String tipoCombustible;
    private String producto;
    private double precioUnitario;
    private int kilometrajeInicial;
    private int kilometrajeFinal;
    private double cantidad;
    private double rendimiento;
    private double iva;
    private String genero;
    private Date fechaGenero;
    private Date horaGenero;
    private String modifico;
    private Date fechaModifico;
    private Date horaModifico;
    private boolean eliminado;
    private int idOperacion;
    private long kmMensual;
    private String gerencia;
    private boolean esUsuario;
    private int idOficina;
    private String nameOficina;
    
    
    
}
