package sia.modelo.contrato.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
public class ClasificacionVo implements Serializable {

    private int id;
    private String nombre;
    private String descripcion;
    private int idClasificacion;
    private boolean modificar;

    private List<ClasificacionVo> listaClasificacion = new ArrayList<ClasificacionVo>();
}
