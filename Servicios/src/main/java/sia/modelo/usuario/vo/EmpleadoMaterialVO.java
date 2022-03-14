/*
 *  Document   : EmpleadoMaterialVO.java 
 *  Create on  : May 28, 2013, 12:23:59 PM
 *  Author     : Héctor Acosta
 *  Information: Para información sobre el uso de esta clase, asi como bugs, actualizaciones o mejoras
 *               enviar un correo a: hacost@hotmail.com
 *  Description: 
 *  Purpose of the class follows.
 */

package sia.modelo.usuario.vo;

import sia.modelo.sgl.vo.Vo;



/**
 * @empresa IHSA
 * @author Héctor Acosta
 * @correo hacost@hotmail.com
 */
public class EmpleadoMaterialVO extends  Vo{
    private int idGerencia;
    private String gerencia;
    private String descripcion;
    private boolean agregar;
    private boolean flag;
    

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the agregar
     */
    public boolean isAgregar() {
        return agregar;
    }

    /**
     * @param agregar the agregar to set
     */
    public void setAgregar(boolean agregar) {
        this.agregar = agregar;
    }

    /**
     * @return the idGerencia
     */
    public int getIdGerencia() {
        return idGerencia;
    }

    /**
     * @param idGerencia the idGerencia to set
     */
    public void setIdGerencia(int idGerencia) {
        this.idGerencia = idGerencia;
    }

    /**
     * @return the gerencia
     */
    public String getGerencia() {
        return gerencia;
    }

    /**
     * @param gerencia the gerencia to set
     */
    public void setGerencia(String gerencia) {
        this.gerencia = gerencia;
    }

    /**
     * @return the flag
     */
    public boolean isFlag() {
        return flag;
    }

    /**
     * @param flag the flag to set
     */
    public void setFlag(boolean flag) {
        this.flag = flag;
    }

}
