

package sia.modelo.estatus.vo;

import sia.modelo.sgl.vo.Vo;

/**
 * Contiene la informacion de un estatus. Relacionado con la entidad ESTATUS en
 * la base de datos.
 * 
 * @author esapien
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
