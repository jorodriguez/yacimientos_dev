/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.requisicion.vo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Getter
@Setter
public class RequisicionDetalleVO {

    private int idRequisicionDetalle;
    private int idRequisicion;
    private double cantidadSolicitada;
    private double cantidadAutorizada;
    private String observacion;
    private boolean autorizado;
    private boolean disgregado;
    private boolean selected = false;
    private int idCaracteristicaServicio;
    private Integer ocTarea;
    private int idTarea;
    private String codeTarea;
    private String nombreTarea;

    private String artNumeroParte;
    private String artDescripcion;
    private String artNombre;
    private String artUnidad;
    private int artIdUnidad;
    private int artID;

    private int idUnidadCosto;
    private String unidadCosto;

    private int idMoneda;
    private double precioUnitario;
    private double importe;
    private String moneda;
    private String textNav;
//
    private int idProyectoOt;
    private String proyectoOt;
    private int idTipoTarea;
    private String tipoTarea;
    private int idSubTarea;
    private String subTarea;
    private String proyectoOtCC;
    private int idConvenio;
    private String nombreActPedrolera;
    private String codigoActPedrolera;
    private int idActPedrolera;
    private String codigoSubTarea;
    private String multiProyectos;
    private String convenio;
    private int idAgrupador;
    private int idpresupuesto;
    private int mesPresupuesto;
    private int idOcCodigoSubtarea;
    private int idOcCodigoTarea;
    private String presupuestoNombre;
    private String presupuestoCodigo;
    private int anioPresupuesto;
    private double totalInventario;
    private String usuarioBeneficiado;

    public String getArtDescripcion() {
        String ret = "";
        ret += this.artDescripcion;
        if (this.textNav != null && !this.textNav.isEmpty()) {
            ret += " ";
            ret += this.textNav;
        }
        return ret.toUpperCase();
    }

    public String getSoloArtDescripcion() {
        return this.artDescripcion;
    }

    public String getTextNav() {
        String ret = "";
        if (this.textNav != null) {
            ret += this.textNav;
        }
        return ret.toUpperCase();
    }

    public double getCantidadSolicitadaFormato() {
        BigDecimal canA = BigDecimal.valueOf(this.cantidadSolicitada);
        canA = canA.setScale(3, RoundingMode.DOWN);
        return canA.doubleValue();
    }

    public double getCantidadAutorizadaFormato() {
        BigDecimal canA = BigDecimal.valueOf(this.cantidadAutorizada);
        canA = canA.setScale(3, RoundingMode.DOWN);
        return canA.doubleValue();
    }
}
