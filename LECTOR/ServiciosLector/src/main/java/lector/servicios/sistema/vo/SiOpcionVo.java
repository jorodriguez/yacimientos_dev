/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.servicios.sistema.vo;

import java.util.ArrayList;
import java.util.List;
import lector.dominio.vo.Vo;
import lombok.Getter;
import lombok.Setter;


/**
 *
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
