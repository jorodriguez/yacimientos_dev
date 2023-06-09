/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.dominio.vo;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ihsa
 */
@Getter
@Setter
public class DocumentoVO extends Vo {

    private int tipoDoc;
    private String tipoDocTxt;
    private boolean multiArchivo;
    private boolean selected;
    private boolean obligatoria;

}
