/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.vo;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author b75ckd35th
 */
@Getter
@Setter
public class SiOpcionVo extends Vo {

    private String pagina;
    private String paginaListener;
    private int idSiModulo;
    private Boolean check;
    private Integer estatusContar;
    private Integer idPadre;
    private String padre;
    private Integer posicion;
    private boolean selected;
    private int idUsuarioOpcion;
    private String modulo;
    private long pendiente;
    private List<SiOpcionVo> listaOpciones = new ArrayList<>();
    private String icono;
    private SiOpcionVo opcionVo;
    private long total;
    private int idCampo;
    private String campo;

}
