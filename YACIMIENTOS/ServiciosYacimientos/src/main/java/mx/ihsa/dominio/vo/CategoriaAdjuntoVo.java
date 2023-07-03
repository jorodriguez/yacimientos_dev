/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.dominio.vo;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jrodriguez
 */
@Getter
@Setter
public class CategoriaAdjuntoVo extends Vo {

    private int idAdjunto;
    private String adjunto;
    private int idCategoria;
    private String nombreCategoria;
    private String categorias;
    private LocalDate fecha;
    private String face;
    private boolean selected;
}
