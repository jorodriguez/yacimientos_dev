

package lector.dominio.vo;



/**
 * 
 */
public class EstatusVo extends Vo {
    
    private final String textoInformativo;

    /**
     * 
     * @param id
     * @param nombre 
     */
    public EstatusVo(final int id, final String nombre, final String textoInformativo) {
        
        this.setId(id);
        this.setNombre(nombre);
        this.textoInformativo = textoInformativo;
        
    }

    public String getTextoInformativo() {
        return textoInformativo;
    }
       
    
}
