package sia.administracion.requisicion.bean.backing;

import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;



import sia.administracion.requisicion.bean.model.RequisicionModel;

//@author ljmartinez
@Named(value = "requisicionBean")
@ViewScoped
public class RequisicionBean implements Serializable {

    private int totalRequisicionesPorUsuario;
    //
    @Inject
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