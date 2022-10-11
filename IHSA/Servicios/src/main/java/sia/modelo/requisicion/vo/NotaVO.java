/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.requisicion.vo;

import java.util.Date;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author jorodriguez
 */

/*08/04/2013
 * Esta clase funciona como objeto virtual para Notas de Requisicion y Notas de  OC/S
 * se tomo la decision de hacer una sola clase por que los atributos son similares
 */
public class NotaVO extends Vo {
    
    
    private String autor; //hace referencia a un usuario pero en la clase Vo no existe
    private String titulo;
    private String mensaje;
    private Integer respuestas;
    private Date ultRespuesta;
    private String finalizada;
    private Integer identificador;
    
    private String consecutivo;
    private String idAutor;
    private  String invitado;
    
    private boolean selected = false;
    
    
    /**
     * @return the selected
     */
    public boolean isSelected() { return selected; }
    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) { this.selected = selected; }
    
    /**
     * @return the autor
     */
    public String getAutor() {
        return autor;
    }

    /**
     * @param autor the autor to set
     */
    public void setAutor(String autor) {
        this.autor = autor;
    }

    /**
     * @return the titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * @param titulo the titulo to set
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
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
     * @return the ultRespuesta
     */
    public Date getUltRespuesta() {
        return ultRespuesta;
    }

    /**
     * @param ultRespuesta the ultRespuesta to set
     */
    public void setUltRespuesta(Date ultRespuesta) {
        this.ultRespuesta = ultRespuesta;
    }

    /**
     * @return the finalizada
     */
    public String getFinalizada() {
        return finalizada;
    }

    /**
     * @param finalizada the finalizada to set
     */
    public void setFinalizada(String finalizada) {
        this.finalizada = finalizada;
    }

    /**
     * @return the identificador
     */
    public Integer getIdentificador() {
        return identificador;
    }

    /**
     * @param identificador the identificador to set
     */
    public void setIdentificador(Integer identificador) {
        this.identificador = identificador;
    }

    /**
     * @return the respuestas
     */
    public Integer getRespuestas() {
        return respuestas;
    }

    /**
     * @param respuestas the respuestas to set
     */
    public void setRespuestas(Integer respuestas) {
        this.respuestas = respuestas;
    }

    /**
     * @return the consecutivo
     */
    public String getConsecutivo() {
        return consecutivo;
    }

    /**
     * @param consecutivo the consecutivo to set
     */
    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    /**
     * @return the idAutor
     */
    public String getIdAutor() {
        return idAutor;
    }

    /**
     * @param idAutor the idAutor to set
     */
    public void setIdAutor(String idAutor) {
        this.idAutor = idAutor;
    }

    /**
     * @return the invitado
     */
    public String getInvitado() {
        return invitado;
    }

    /**
     * @param invitado the invitado to set
     */
    public void setInvitado(String invitado) {
        this.invitado = invitado;
    }

 
}
