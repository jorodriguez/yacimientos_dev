package sia.modelo.contrato.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.faces.model.SelectItem;
import lombok.Getter;
import lombok.Setter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ihsa
 */
@Getter
@Setter
public class FiltroVo implements Serializable {

    private int id;
    private int idOperador;
    private String operador;
    private int idMoneda;
    private String moneda;
    private double importe;
    private String alcance;
    private Date fecha;
    private Date fechaInicio = new Date();
    private Date fechaFin;
    private boolean filtroImporte = false;
    private boolean filtroAlcance = false;
    private boolean filtroBuscarAlcance = false;
    private boolean filtroFechaRango = false;
    private int idProveedor;
    private String proveedor;
    private int idEstado;
    private int idGerencia;
    private String fechaRango;
    //
    //
    private List<String> campos;
    private List<String> operadorRelacional;
    private List<String> operadorRelacionalCadena;
    private List<String> operadorLogico;
    private String valor;
    //
    private String campoSeleccionado;
    private String operadorRelacionalSeleccionado;
    private String operadorLogicoSeleccionado;
    private boolean filtroCombo = false;
    private boolean filtroFecha = false;
    private boolean filtroCaja = false;
    private boolean filtroMoneda = false;
    private List<SelectItem> listaEstatus;
    private List<SelectItem> listaGerencia;
    private List<SelectItem> listaMoneda;
    private List<SelectItem> listaProveedores;
    //  private boolean aplicar = true;
    //

}
