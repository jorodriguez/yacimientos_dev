/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.vo;

import sia.modelo.SgAvisoPago;

/**
 *
 * @author jrodriguez
 */
public class AvisoPagoVO implements Comparable {

    public AvisoPagoVO(SgAvisoPago pago){
        this.pago = pago;
    }
    
    private SgAvisoPago pago;
    
    

    /**
     * @return the pago
     */
    public SgAvisoPago getPago() {
        return pago;
    }

    /**
     * @param pago the pago to set
     */
    public void setPago(SgAvisoPago pago) {
        this.pago = pago;
    }

    
    public boolean equals(Object objeto) {
// Indica en base a que atributos se iguala el objeto 
        if (objeto == null) {
            return false;
        }
        AvisoPagoVO pago = (AvisoPagoVO) objeto;
        if (this.getPago().getId() == pago.getPago().getId()) {
            return true;
        }

        return false;
    }

    
    public int hashCode() {
// retorna un identificador unico del objeto. 
        return this.getPago().hashCode();
    }

    
    public int compareTo(Object objeto) {
// Indica en base a que atributos se compara el objeto 
// Devuelve +1 si this es > que objeto 
// Devuelve -1 si this es < que objeto 
// Devuelve 0 si son iguales 

        AvisoPagoVO obj = (AvisoPagoVO) objeto;
        int ret = obj.getPago().getId();
        int idObjeto = obj.getPago().getId();
        int idThis = this.getPago().getId();
        if (idThis > idObjeto) {
            ret += 1;
        }
        if (idThis < idObjeto) {
            ret -= 1;

        }
        if (idThis == idObjeto) {
            ret = 0;
        }
         return ret ;
    }
   
}

