package sia.administracion.requisicion.bean.model;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import sia.catalogos.bean.backing.UsuarioBean;
import sia.servicios.requisicion.impl.RequisicionImpl;

//@author ljmartinez
@ManagedBean(name = "requisicionModel")
@CustomScoped(value = "#{window}")
public class RequisicionModel implements Serializable {

    @EJB
    private RequisicionImpl requisicionServicioRemoto;
    @ManagedProperty(value = "#{usuarioBean}")
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
