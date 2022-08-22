/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.vo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ihsa
 */
public class ParidadAnual {
    private List<ParidadMensual> meses;

    public ParidadAnual() {        
        this.meses = new ArrayList<ParidadMensual>(12);
    }
    
    /**
     * @return the meses
     */
    public List<ParidadMensual> getMeses() {
        return meses;
    }

    /**
     * @param meses the meses to set
     */
    public void setMeses(List<ParidadMensual> meses) {
        this.meses = meses;
    }
   
}
