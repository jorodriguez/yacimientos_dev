/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.contrato.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.CvConvenioFormasNotificaciones;

/**
 *
 * @author mluis
 */
@Setter
@Getter
public class ContratoFormasVo {
    private int id;
    private int idCampo;
    private int idConvenio;
    private String codigoConvenio;
    private String convenio;
    private int idForma;
    private String forma;
    private int idGerencia;
    private String gerencia;
    private int idProveedor;
    private String proveedor;
    private String rfcProveedor;
    private int idRol;
    private String rol;
    private int idAdjuntoPlantilla;
    private String adjuntoPlantilla;
    private String uuIdPlantilla;
    private String idUsuarioValido;
    private String usuarioValido;
    private boolean validado;
    private Date fechaEntrega;
    private Date fechaValido;
    private Date horaValido;
    //
    private int idAdjunto;
    private String adjunto;
    private String uuIdAdjunto;
    private long totalNotas;
    private String responsableGerencia;
    private String fechaPrimerNotificacion;
    private String formaCodigo;
    //
    List<ContratoFormasNotificacionesVo> notificaciones = new ArrayList<>();

    //
}
