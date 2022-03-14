/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.requisicion.vo;

import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.gerencia.vo.GerenciaVo;

/**
 *
 * @author ihsa
 */
@Setter
@Getter
public class OcTareaVo implements Serializable{
    private int idTarea;   
    private int idcodigoTarea;
    private String codigoTarea;
    private int idGerencia;
    private String gerencia;    
    private int idProyectoOt;
    private String proyectoOt;
    private int idNombreTarea;
    private String nombreTarea;
    private int idUnidadCosto;
    private String unidadCosto;
    private boolean selected;
    private String cuentaContable;
    private String campo;
    private int idActPetrolera;
    private String actPetrolera;
    //
    private List<GerenciaVo> listaGerencia;
    
    private boolean existeTarea;
}
