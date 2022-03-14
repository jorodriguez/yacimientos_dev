/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.accesorio;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.oficina.vo.OficinaVO;

/**
 *
 * @author ihsa
 */
@Getter
@Setter
public class AccesorioVo implements Serializable {

    private int id;
    private int idModelo;
    private String modelo;
    private int idMarca;
    private String marca;
    private int idTipoEspecifico;
    private String tipoEspecifico;
    private int idTipo;
    private String tipo;
    private int idCondicion;
    private String condicion;
    private OficinaVO oficinaVO = new OficinaVO();
    private String serie;
    private String descripcion;
    private Date fechaAdquisicion;
    private int idProveedor;
    private String proveedor;
    private boolean disponible;
    private String sistemaOperativo;
    private boolean garantia;
    private Date fechaVencimiento;
    private Date fechaGenero;
    private LineaVo lineaVo = new LineaVo();
    private int idAsiganarAccesorio;
    private String idUsuario;
    private String usuario;
    private Date fechaOperacion;
}
