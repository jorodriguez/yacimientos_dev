/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.requisicion.vo;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.proyectoOT.vo.ProyectoOtVo;

/**
 *
 * @author jcarranza
 */
@Setter
@Getter
public class OcActividadVO {
    private int id;
    private String codigo;    
    private String nombre;
    
    private int idNuevoProyecto;
    private boolean otsRelacionadas;
    
    private List<ProyectoOtVo> proyectos;
    
    public OcActividadVO(){
    
    }
    
    public OcActividadVO(int id, String codigo, String nombre){
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
    
    }
    
    public String getProyectosTxt(){
        String ret = "";    
        ret = this.getProyectos().stream().map((vo) -> vo.getNombre()+ ", " ).reduce(ret, String::concat);
        return ret;
    }
}
