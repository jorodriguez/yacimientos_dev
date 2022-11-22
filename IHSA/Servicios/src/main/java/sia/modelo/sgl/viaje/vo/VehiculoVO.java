/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.viaje.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author
 */
@Getter
@Setter
public class VehiculoVO extends Vo implements Serializable {

    private String numeroPlaca;
    private String cajonEstacionamiento;
    private String serie;
    private Integer capacidadPasajeros;
    private String tipo;
    private Integer kmActual;
    private Integer periodoMantenimiento;
    private Integer partida;
    private Long kmNotificacion;
    private Long kmProximoMantenimiento;
    private Date fechaProxMantenimiento;
    private String mantenimientoTerminado;
    private String color;
    private String marca;
    private String modelo;
    private int idMarca;
    private int idModelo;
    private int idColor;
    private int idTipo;
    private int idTipoEspecifico;
    private String Observacion;
    private String tipoEspecifico;
    private String asignado;
    private Date fechaAsigno;
    private int periodoKmMantenimiento;
    private int idOficina;
    private String oficina;
    private String numeroActivo;
    private String numeroEconomico;
    private String seguro;
    private boolean gps;
    private boolean cajaHerramienta;
    private int idProveedor;
    private String proveedor;
    private int idEstado;
    private String estado;
    private int idKilometraje;
    private int kilometraje;
    private String usuarioAsignada;
    private String idusuarioAsignada;
    private int idEmpresaEmp;
    private String empresaEmp;

}
