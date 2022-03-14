/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.contrato.vo;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.AdjuntoVO;

/**
 *
 * @author mluis
 */
@Setter
@Getter
public class ContratoDocumentoVo {

    private int id;
    private int idConvenio;
    private int idDocumento;
    private String documento;
    private boolean valido;
    private Date fechaEntrega;
    private Date inicioVigencia;
    private Date finVigencia;

    private AdjuntoVO adjuntoVO = new AdjuntoVO();
    //

}
