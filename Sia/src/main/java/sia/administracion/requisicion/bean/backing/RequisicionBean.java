package sia.administracion.requisicion.bean.backing;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import sia.administracion.requisicion.bean.model.RequisicionModel;

//@author ljmartinez
@ManagedBean(name = "requisicionBean")
@ViewScoped
public class RequisicionBean implements Serializable {

    private int totalRequisicionesPorUsuario;
    //
    @ManagedProperty(value = "#{requisicionModel}")
    private RequisicionModel requisicionModel;

    public RequisicionModel getRequisicionModel() {
        return requisicionModel;
    }

    public void setRequisicionModel(RequisicionModel requisicionModel) {
        this.requisicionModel = requisicionModel;
    }

    public int getTotalRequisicionesPorUsuario() {
        return requisicionModel.obtieneTotalRequisiciones();
    }

    public void setTotalRequisicionesPorUsuario(int totalRequisicionesPorUsuario) {
        this.totalRequisicionesPorUsuario = totalRequisicionesPorUsuario;
    }
}