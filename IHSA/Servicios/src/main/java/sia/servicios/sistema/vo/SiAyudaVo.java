/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.vo;

import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author b75ckd35th
 */
public class SiAyudaVo extends Vo {

    private int idSiModulo;
    private int idSiOpcion;

    /**
     * @return the idSiModulo
     */
    public int getIdSiModulo() {
        return idSiModulo;
    }

    /**
     * @param idSiModulo the idSiModulo to set
     */
    public void setIdSiModulo(int idSiModulo) {
        this.idSiModulo = idSiModulo;
    }

    /**
     * @return the idSiOpcion
     */
    public int getIdSiOpcion() {
        return idSiOpcion;
    }

    /**
     * @param idSiOpcion the idSiOpcion to set
     */
    public void setIdSiOpcion(int idSiOpcion) {
        this.idSiOpcion = idSiOpcion;
    }
}
