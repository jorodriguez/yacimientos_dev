/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.proveedor.Vo;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.AdjuntoVO;

/**
 *
 * @author ihsa
 */
@Getter
@Setter
public class ProveedorDocumentoVO {

    private int id;
    private int idDocumento;
    private String documento;
    private boolean valido;
    private Date fechaEntrega;
    private Date inicioVigencia;
    private Date finVigencia;
    private boolean multiArchivo;
    private boolean obligatoria;

    private AdjuntoVO adjuntoVO = new AdjuntoVO();
    
    private String ayuda;

}
