/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.licencia.vo;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author mluis
 */
@Getter
@Setter
public class LicenciaVo extends Vo {

    private String numero;
    private int idPais;
    private int idTipo;
    private String pais;
    private String idUsuario;
    private String usuario;
    private String tipo;
    private Date expedida;
    private Date vencimiento;
    private String estado;
    private int adjunto;
    private String uuid;
    private boolean vigente;
    private int idOficina;
    private int idApCampo;
    private String nombreOficina;
    private String nombreApCampo;
    private boolean  select;
    private int gerencia;

    
}
