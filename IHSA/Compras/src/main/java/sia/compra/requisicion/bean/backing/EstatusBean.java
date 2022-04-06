/*
 * EstatusBean.java
 * Creado el 30/06/2009, 11:05:47 AM
 * Managed Bean desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este Managed Bean, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: hacosta.0505@gmail.com
 */
package sia.compra.requisicion.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.CustomScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import sia.constantes.Constantes;
import sia.modelo.Estatus;
import sia.modelo.vo.StatusVO;
import sia.servicios.catalogos.impl.EstatusImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail hacosta.0505@gmail.com @date 30/06/2009
 */
@Named (value = EstatusBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class EstatusBean implements Serializable{

    //------------------------------------------------------
    public static final String BEAN_NAME = "estatusBean";
    //------------------------------------------------------
    @Inject
    private EstatusImpl estatusServicioRemoto;

    /**
     * Creates a new instance of EstatusBean
     */
    public EstatusBean() {
    }

    public Estatus getPorId(Object id) {
        return estatusServicioRemoto.find(id);
    }

    public List<SelectItem> listaStatus() {
        List<StatusVO> le = estatusServicioRemoto.traerPorTipo("REQ");
        List<SelectItem> li = null;
        try {
            if (le != null) {
                li = new ArrayList<>();
                for (StatusVO estatus : le) {
                    if (estatus.getIdStatus() > Constantes.UNO && estatus.getIdStatus() <= Constantes.REQUISICION_ASIGNADA) {
                        li.add(new SelectItem(estatus.getIdStatus(), estatus.getNombre()));
                    }
                }
            }
            return li;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al recuperar los estatus " + e.getMessage());
            return null;
        }
    }
    public List<SelectItem> getListaStatusOrden() {
        List<StatusVO> le = estatusServicioRemoto.traerPorTipo("ODC");
        List<SelectItem> li = null;
        try {
            if (le != null) {
                li = new ArrayList<>();
                for (StatusVO estatus : le) {
                    if (estatus.getIdStatus() > Constantes.ORDENES_SIN_SOLICITAR && estatus.getIdStatus() <= Constantes.ESTATUS_AUTORIZADA) {
                        li.add(new SelectItem(estatus.getIdStatus(), estatus.getNombre()));
                    }
                }
            }
            return li;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al recuperar los estatus " + e.getMessage());
            return null;
        }
    }
}
