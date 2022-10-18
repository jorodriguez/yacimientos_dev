/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.cursoManejo.vo;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author jevazquez
 */
@Getter
@Setter
public class CursoManejoVo extends Vo{
    private int idCursoManejo;
    private Date fechaExpedicion;
    private Date fechaVencimiento;
    private boolean Vigente;
    private int idAdjunto;
    private int idSgTipoEspecifico;
    private String genero;
    private Date fechaGenero;
    private Date horaGenero;
    private String modifico;
    private Date fechaModifico;
    private Date horaModifico;
    private boolean eliminado;
    private String idUsuario;
    private String nameUser;
    private int numCurso;
    private int idSgOficina;
    private int idApCampo;
    private String oficina;
    private String campo;
    private boolean select;
    private int gerencia;
    
    
}
