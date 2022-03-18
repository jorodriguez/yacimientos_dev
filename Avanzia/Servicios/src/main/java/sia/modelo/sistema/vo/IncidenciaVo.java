/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sistema.vo;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.gr.vo.GrMapaGPSVO;

/**
 *
 * @author ihsa
 */
@Setter
@Getter
public class IncidenciaVo {

    private int idIncidencia;
    private String titulo;
    private String descripcion;
    private int idPrioridad;
    private String prioridad;
    private int idEstado;
    private String estado;
    private int idGerencia;
    private String gerencia;
    private Date fechaGenero;
    private Date horaGenero;
    private String codigo;
    private int idCategoriaIncidencia;
    private String categoriaIncidencia;
    private int idCampo;
    private String campo;
    private String solucion;
    private String idAsignadoA;
    private String asignado;
    private String codigoCategoria;
    private String idGenero;
    private String genero;
    private String correoGenero;
    private String correoAsignado;
    private Integer idNivel;
    private String nivel;
    private String codigoNivel;
    private String motivoEscala;
    private boolean escalado;
    //
    private GrMapaGPSVO grMapaGPSVO = new GrMapaGPSVO(gerencia, codigo, titulo);
}
