/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.viaje.vo;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jevazquez
 */
@Getter
@Setter
public class EstatusAprobacionSolicitudVO {
    private int id;
    private String idUsuario;
    private String usuario;
    private boolean historial;
    private boolean realizado;
    private int idEstatus;
    private String operacion;
    private Date fechaModifico;
    private Date horaModifico;
    private int idSolicitud;
    
}
