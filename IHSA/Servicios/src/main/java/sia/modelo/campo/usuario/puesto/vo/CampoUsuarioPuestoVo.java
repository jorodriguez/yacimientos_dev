/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.campo.usuario.puesto.vo;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sia.servicios.sistema.vo.SiOpcionVo;

/**
 *
 * @author mluis
 */
@Getter
@Setter
@ToString
public class CampoUsuarioPuestoVo {

    private int idCampoUsuarioPuesto;
    private int idCampo;
    private String idUsuario;
    private String campo;
    private String usuario;
    private int idPuesto;
    private String puesto;
    private boolean selected;
    private String rfcCompania;
    private int idGerencia;
    private String gerencia;    
    private Map<String, List<SiOpcionVo>> listaOpcion;
    private String tipo;
}
