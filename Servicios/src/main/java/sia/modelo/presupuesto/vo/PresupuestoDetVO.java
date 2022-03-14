/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.presupuesto.vo;

import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.OcActividadPetrolera;
import sia.modelo.OcCodigoSubtarea;
import sia.modelo.OcCodigoTarea;
import sia.modelo.OcNombreTarea;
import sia.modelo.OcUnidadCosto;
import sia.modelo.ProyectoOt;
import sia.modelo.requisicion.vo.OcActividadVO;
import sia.modelo.requisicion.vo.OcTareaVo;

/**
 *
 * @author jcarranza
 */
@Getter
@Setter
public class PresupuestoDetVO {

    private int id;
    private int idPresupuesto;
    private String idPresupuestoTxt;
    private boolean existePresupuesto;
    private boolean existePresDet;
    private int mes;
    private int anio;

    private int actPetroleraId;
    private String actPetroleraCodigo;
    private String actPetroleraNombre;
    private boolean existeActP;

    private int unidadCostoId;
    private String unidadCostoCodigo;
    private String unidadCostoNombre;
    private boolean existeUnidadC;

    private int tareaId;
    private int tareaCodigoId;
    private int tareaNombreId;
    private String tareaCodigo;
    private String tareaNombre;
    private boolean existeTarea;
    private boolean existeTareaCod;
    private boolean existeTareaNom;

    private int subTareaId;
    private int subTareaCodigoId;
    private String subTareaCodigo;
    private String subTareaNombre;
    private boolean existeSubTarea;

    private int proyectoOtId;
    private String proyectoOtNombre;
    private String proyectoOtCodigo;
    private boolean existeOT;

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

    private BigDecimal montoNuevo;
    private BigDecimal montoActual;

    private List<MontosPresupuestoVO> montos;
    private OcActividadVO newProys;

    private String otsTexto;
    private String otsCCTexto;

    private boolean sinOT;
    private boolean aplicaTodos;

    public String getSubTareaCode() {
        String ret = "";
        if (this.idPresupuesto > 0) {
            ret += this.idPresupuesto;
        }
        if (this.subTareaCodigo != null && !this.subTareaCodigo.isEmpty()) {
            ret += this.subTareaCodigo;
        }
        return ret;
    }

    public OcTareaVo getTareaVO() {
        OcTareaVo vo = new OcTareaVo();
        vo.setIdProyectoOt(this.getProyectoOtId());
        vo.setIdNombreTarea(this.getTareaNombreId());
        vo.setIdcodigoTarea(this.getTareaCodigoId());
        vo.setIdUnidadCosto(this.getUnidadCostoId());
        vo.setIdActPetrolera(this.getActPetroleraId());
        return vo;
    }

    public OcCodigoTarea getNuevoCodigosTarea() {
        OcCodigoTarea aux = new OcCodigoTarea();
        aux.setNombre(this.getTareaCodigo());
        return aux;
    }

    public OcNombreTarea getNuevosNombresTarea() {
        OcNombreTarea aux = new OcNombreTarea();
        aux.setNombre(this.getTareaNombre());
        return aux;
    }

    public ProyectoOt getNuevosProyectosOts() {
        ProyectoOt aux = new ProyectoOt();
        aux.setNombre(this.getProyectoOtNombre());
        aux.setCuentaContable(this.getProyectoOtCodigo());
        return aux;
    }

    public OcCodigoSubtarea getNuevosSubTareas() {
        OcCodigoSubtarea aux = new OcCodigoSubtarea();
        aux.setNombre(this.getSubTareaNombre());
        aux.setCodigo(this.getSubTareaCodigo());
        return aux;
    }

    public OcUnidadCosto getNuevosSubActividadP() {
        OcUnidadCosto aux = new OcUnidadCosto();
        aux.setNombre(this.getUnidadCostoNombre());
        aux.setCodigo(this.getUnidadCostoCodigo());
        return aux;
    }
    
    public OcActividadPetrolera getNuevosActividadP() {
        OcActividadPetrolera aux = new OcActividadPetrolera();
        aux.setNombre(this.getActPetroleraNombre());
        aux.setCodigo(this.getActPetroleraCodigo());
        return aux;
    }

}
