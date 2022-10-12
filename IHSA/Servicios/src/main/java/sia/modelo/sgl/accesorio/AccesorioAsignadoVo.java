/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.accesorio;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.modelo.usuario.vo.UsuarioVO;

/**
 *
 * @author ihsa
 */
@Setter
@Getter
public class AccesorioAsignadoVo implements Serializable {

    private int id;
    private AccesorioVo accesorioVo = new AccesorioVo();
    private UsuarioVO usuarioVO = new UsuarioVO();
    private AdjuntoVO adjuntoVO = new AdjuntoVO();
    private Date fechaAsignacion;
    private Date horaAsignacion;
    private int pertenece;
    private String terminada;
}
