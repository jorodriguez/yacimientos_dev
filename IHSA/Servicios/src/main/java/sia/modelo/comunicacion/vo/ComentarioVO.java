/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.comunicacion.vo;

import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author jrodriguez
 */
public final class ComentarioVO extends Vo{   
        private String mensaje;
        private int meGusta;                
        private String usuarioFoto;
        private String usuarioNombre;
       
        private int idMegusta;
         //prueba rapida
        private String usuariosLike;
       
        
        
        public ComentarioVO(){
        }
    public ComentarioVO(int id,String mensaje,int meGusta,String genero,String usuarioFoto,String usuarioNombre){
        this.setId(id);
        this.setMensaje(mensaje);
        this.setMeGusta(meGusta);
        this.setGenero(genero);
        this.setUsuarioFoto(usuarioFoto);
        this.setUsuarioNombre(usuarioNombre);
    }

    /**
     * @return the mensaje
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * @param mensaje the mensaje to set
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * @return the meGusta
     */
    public int getMeGusta() {
        return meGusta;
    }

    /**
     * @param meGusta the meGusta to set
     */
    public void setMeGusta(int meGusta) {
        this.meGusta = meGusta;
    }

    /**
     * @return the usuarioFoto
     */
    public String getUsuarioFoto() {
        return usuarioFoto;
    }

    /**
     * @param usuarioFoto the usuarioFoto to set
     */
    public void setUsuarioFoto(String usuarioFoto) {
        this.usuarioFoto = usuarioFoto;
    }

    /**
     * @return the usuarioNombre
     */
    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    /**
     * @param usuarioNombre the usuarioNombre to set
     */
    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    /**
     * @return the idMegusta
     */
    public int getIdMegusta() {
        return idMegusta;
    }

    /**
     * @param idMegusta the idMegusta to set
     */
    public void setIdMegusta(int idMegusta) {
        this.idMegusta = idMegusta;
    }

    /**
     * @return the usuariosLike
     */
    public String getUsuariosLike() {
        return usuariosLike;
    }

    /**
     * @param usuariosLike the usuariosLike to set
     */
    public void setUsuariosLike(String usuariosLike) {
        this.usuariosLike = usuariosLike;
    }

    
}
