/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.contrato.vo;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Setter
@Getter
public class RhConvenioDocumentoVo {

    private int id;
    private int idConvenio;
    private String codigoConvenio;
    private String convenio;
    private int idPeriodicidad;
    private String periodicidad;
    private int mes;
    private int idDocumento;
    private String documento;
    private String observacion;
    private boolean mandatorio;
    private boolean valido;
    private int idAdjunto;
    private String adjunto;
    private String uuId;
    private Date fechaGenero;
    //

}
