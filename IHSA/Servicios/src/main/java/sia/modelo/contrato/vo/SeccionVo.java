/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.contrato.vo;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jcarranza
 */
@Getter
@Setter
public class SeccionVo {

    public SeccionVo() {
    }

    public SeccionVo(String seccionNombre) {
        this.seccionNombre = seccionNombre;
    }

   
    private int seccionId;
    private String seccionNombre;
    private int seccionMaximo;
    private double total;
    
    private List<PreguntaVo> preguntas = new ArrayList<>();
    
    
}
