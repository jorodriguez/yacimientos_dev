/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.contrato.vo;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Getter
@Setter
public class ExhortoVo {

    private int id;
    private String codigo;
    private int numero;
    private Date fechaExhorto;
    private String contrato;
    private String proveedor;
    private String descripcionContrato;
    private String correoPara;
    private String correoCopia;
    private String representanteLegal;
    private String puestoRepresentante;
    private int diasTranscurridos;
    private int totalDiasTranscurridos;
}
