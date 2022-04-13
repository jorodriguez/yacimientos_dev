/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.vo.inventarios;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
 @Getter
 @Setter
public class EstadoAprobacionSolicitudVo {
    private int id;
    private String idUsuario;
    private String usuario;
    private int idEstatus;
    private String status;
    private Date fecaModifico;
    private Date horaModifico;
    private boolean actual;
    private Date fechaGenero;
    private Date horaGenero;
    
}
