/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.model.SelectItem;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ihsa
 */
@Getter
@Setter
public class ReporteVo implements Serializable {

    private int idOficina;
    private String oficina;
    private int idStaffHouse;
    private String staffHouse;
    private int idServicio;
    private String servicio;
    private int idVehiculo;
    private String vehiculo;
    private Date fechaInicio;
    private Date fechaFin;
    private double total;
    private Date fecha;
    private int mes;
    private int anio;
    private String nombre;
    private String gerencia;
    private String fechaCompuesta;
    List<SelectItem> listaAnio = new ArrayList<SelectItem>();
    List<SelectItem> listaMes = new ArrayList<SelectItem>();

}
