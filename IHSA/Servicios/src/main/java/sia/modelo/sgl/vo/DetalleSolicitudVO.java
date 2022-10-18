/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.vo;

/**
 *
 * @author mluis
 */
public class DetalleSolicitudVO {

    private String nombre;
    private String descripcion;
    private int tipoEspecifico;
    private int idInvitado;
    private String empresa;

    public DetalleSolicitudVO() {
    }

    public DetalleSolicitudVO(String nombre, String descripcion, int tipoEspecifico, int idInvitado, String empresa) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipoEspecifico = tipoEspecifico;
        this.idInvitado = idInvitado;
        this.empresa = empresa;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getTipoEspecifico() {
        return tipoEspecifico;
    }

    public void setTipoEspecifico(int tipoEspecifico) {
        this.tipoEspecifico = tipoEspecifico;
    }

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
     * @return the idInvitado
     */
    public int getIdInvitado() {
        return idInvitado;
    }

    /**
     * @param idInvitado the idInvitado to set
     */
    public void setIdInvitado(int idInvitado) {
        this.idInvitado = idInvitado;
    }

    /**
     * @return the empresa
     */
    public String getEmpresa() {
        return empresa;
    }

    /**
     * @param empresa the empresa to set
     */
    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }
}
