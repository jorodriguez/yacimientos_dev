/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.comunicacion.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author jrodriguez
 */
@Getter
@Setter
public class NoticiaVO extends Vo implements Serializable {

    //el id de la noticia esta en Vo
    private int idRelacionNoticia;
    private String mensaje;
    private String titulo;
    private String mensajeAutomatico;
    private Integer comentarios; //contador
    private Integer meGusta; //contador

    //el id del usuario esta en Vo (Genero)
    private String usuarioFoto;
    private String usuarioNombre;
    private Integer privacidadId;
    private String privacidadNombre;
    private Integer idMegusta;

    //contador de archivos adjunto
    private long adjuntosCount;

    //Usado para controlar el cuadro de texto al dar clic en la opc. comentar
    private boolean comentar = false;
    private List<ComentarioVO> listaComentario = new ArrayList<>();
    List<NoticiaAdjuntoVO> listaAdjunto = new ArrayList<>();
    private boolean mostrarComentarios = false;
    private int idCampo;
    private String respuesta;

}
