package sia.administracion.requisicion.bean.model;

import java.io.Serializable;
import javax.inject.Inject;
import javax.faces.view.ViewScoped;
import javax.inject.Named;


import sia.catalogos.bean.backing.UsuarioBean;
import sia.servicios.requisicion.impl.RequisicionImpl;

//@author ljmartinez
@Named(value = "requisicionModel")
@ViewScoped
public class RequisicionModel implements Serializable {

    @Inject
    private RequisicionImpl requisicionServicioRemoto;
    @Inject
    private UsuarioBean usuarioBean;

    public int obtieneTotalRequisiciones() {
        return (int) requisicionServicioRemoto.obtieneTotalRequisiciones(usuarioBean.getUsuarioVO().getId());
    }

    public UsuarioBean getUsuarioBean() {
        return usuarioBean;
    }

    public void setUsuarioBean(UsuarioBean usuarioBean) {
        this.usuarioBean = usuarioBean;
    }
}
