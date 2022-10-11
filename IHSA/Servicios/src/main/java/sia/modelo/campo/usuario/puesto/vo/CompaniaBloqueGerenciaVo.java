

package sia.modelo.campo.usuario.puesto.vo;

import sia.modelo.sgl.vo.Vo;

/**
 * Clase contenedora para las opciones de bloques de un usuario con 
 * compañía y gerencias correspondientes.
 * 
 *
 * @author esapien
 */
public class CompaniaBloqueGerenciaVo extends Vo {
    
    // llave compuesta para su identificacion en la vista
    private String registroId;
    
    private String companiaRfc;
    private String companiaSiglas;
    private String companiaNombre;
    
    private int bloqueId;
    private String bloqueNombre;
    
    private int gerenciaId;
    private String gerenciaNombre;
    
    
    // <editor-fold defaultstate="collapsed" desc="Getters / Setters">

    public String getRegistroId() {
        return registroId;
    }

    public void setRegistroId(String registroId) {
        this.registroId = registroId;
    }
    
    public int getBloqueId() {
        return bloqueId;
    }

    public void setBloqueId(int bloqueId) {
        this.bloqueId = bloqueId;
    }

    public String getBloqueNombre() {
        return bloqueNombre;
    }

    public void setBloqueNombre(String bloqueNombre) {
        this.bloqueNombre = bloqueNombre;
    }

    public String getCompaniaNombre() {
        return companiaNombre;
    }

    public void setCompaniaNombre(String companiaNombre) {
        this.companiaNombre = companiaNombre;
    }

    public String getCompaniaRfc() {
        return companiaRfc;
    }

    public void setCompaniaRfc(String companiaRfc) {
        this.companiaRfc = companiaRfc;
    }

    public String getCompaniaSiglas() {
        return companiaSiglas;
    }

    public void setCompaniaSiglas(String companiaSiglas) {
        this.companiaSiglas = companiaSiglas;
    }

    public int getGerenciaId() {
        return gerenciaId;
    }

    public void setGerenciaId(int gerenciaId) {
        this.gerenciaId = gerenciaId;
    }

    public String getGerenciaNombre() {
        return gerenciaNombre;
    }

    public void setGerenciaNombre(String gerenciaNombre) {
        this.gerenciaNombre = gerenciaNombre;
    }
    
    
    // </editor-fold>

    /**
     * 
     * @return 
     */
    
    public String toString() {
        
        return "CompaniaBloqueGerenciaVo{" 
                + "registroId=" + getRegistroId()
                + ", companiaRfc=" + companiaRfc 
                + ", companiaSiglas=" + companiaSiglas 
                + ", companiaNombre=" + companiaNombre 
                + ", bloqueId=" + bloqueId 
                + ", bloqueNombre=" + bloqueNombre 
                + ", gerenciaId=" + gerenciaId 
                + ", gerenciaNombre=" + gerenciaNombre + '}';
    }

    
    
    
    
    
}
