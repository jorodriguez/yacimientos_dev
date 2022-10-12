/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.presupuesto.vo;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jcarranza
 */
@Getter
@Setter
public class MontosPresupuestoVO {

    private BigDecimal manoObraCn;
    private BigDecimal manoObraEx;
    private BigDecimal bienasCn;
    private BigDecimal bienesEx;
    private BigDecimal serviciosCn;
    private BigDecimal serviciosEx;
    private BigDecimal capacitacionCn;
    private BigDecimal capacitacionEx;
    private BigDecimal transferenciaTec;
    private BigDecimal infraestructura;

    private int mes;
    private int anio;

    private Date fecha;
    private String fechaTxt;

    private int newDetID;

    public int getMesFromFechaTxt() {
        int mmes = 1;
        if (this.fecha == null) {
            if (this.fechaTxt != null && !this.fechaTxt.isEmpty()) {
                if (this.fechaTxt.toUpperCase().contains("MES")) {
                    try {
                        mmes = Integer.parseInt(this.fechaTxt.toUpperCase().replace("MES", "").trim());
                    } catch (Exception e) {
                        mmes = 1;
                    }
                }
            }
        } else {
            try {
                Calendar cal = Calendar.getInstance();
                cal.setTime(this.fecha);
                mmes = cal.get(Calendar.MONTH);
            } catch (Exception e) {
                mmes = 1;
            }
        }
        return mmes;
    }

}
