/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.vehiculo.vo;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ihsa
 */
@Getter
@Setter
public class AsignarVehiculoVo {

    private int id;
    private Date fechaOperacion;
    private int idVehiculo;
    private String idUsuario;
    private String usuario;
    private int idCheck;
    private String checklist;
    private Date inicioSemana;
    private Date finSemana;
    private int idAdjunto;
    private String uuId;

}
