/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.presupuesto.vo;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jcarranza
 */
@Getter
@Setter
public class PresupuestoVO {
    
    private int id;
    private String nombre;
    private String codigo;
    private int apCampoId;
    private int idMoneda;
    private double tipoCambio;
    private String compania;
    
    public PresupuestoVO(){
    
    }
    
    public PresupuestoVO(int id, String nombre, String codigo){
        this.id =  id;
        this.nombre = nombre;
        this.codigo = codigo;
    }
    
    
}
