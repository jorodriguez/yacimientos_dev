/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.vo.inventarios;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Getter
@Setter
public class SolicitudMaterialAlmacenVo implements Serializable {

    private int id;
    private int idAlmacen;
    private String almacen;
    private int idGerencia;
    private String gerencia;
    private String folio;
    private int idCampo;
    private String campo;
    private String observacion;
    private int idStatus;
    private String status;
    private double cantidadSolicitada;
    private double cantidadRecibida;
    private String telefono;
    private Date fechaSolicita;
    private Date fechaRequiere;
    private Date fechaEntrega;
    private Date horaEntrega;
    private Long totalDetalle;
    private String usuarioRecoge;
    private String solicita;
    private String correoSolicita;
    private String idAutoriza;
    private String autoriza;
    private String idSolicita;
    private String usuarioRecibeMaterial;
    private boolean devuelta;
    private boolean selected;
    private int idEstadoAprobacion;
    //
    private List<DetalleSolicitudMaterialAlmacenVo> materiales;
}
