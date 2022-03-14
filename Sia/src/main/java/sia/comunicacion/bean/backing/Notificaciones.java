/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.comunicacion.bean.backing;

import sia.sistema.bean.support.FacesUtils;
import javax.ejb.EJB;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import sia.servicios.orden.impl.NotaOrdenImpl;
import sia.sistema.bean.backing.Sesion;

/**
 *
 * @author hacosta
 */
public class Notificaciones {

    @EJB
    private NotaOrdenImpl notaOrdenServicioImpl;
    private Sesion sesion = (Sesion) FacesUtils.getManagedBean("sesion");
    // - - - - - - - - - -
    private DataModel listaNotas = null;
    private HtmlOutputText idNota;
    private DataModel listaRespuestas = null;

    /** Creates a new instance of Notificaciones */
    public Notificaciones() {
    }

    /**
     * @return Lista de Notas por usuario
     */
    public DataModel getNotasPorInvitado() {
        try {
            this.listaNotas = new ListDataModel(this.notaOrdenServicioImpl.getNotasPorInvitado(this.sesion.getUsuario().getId(),sesion.getUsuario().getApCampo().getId()));
        } catch (RuntimeException ex) {
            this.listaNotas = null;
            FacesUtils.addInfoMessage(ex.getMessage());
        }
        return this.listaNotas;
    }

    /**
     * @return Lista de respuestas  por  Nota
     */
    public DataModel getRespuestas() {
        try {
            this.listaRespuestas = new ListDataModel(this.notaOrdenServicioImpl.getRespuestas(getIdNota().getValue()));
        } catch (RuntimeException ex) {
            this.listaRespuestas = null;
            FacesUtils.addInfoMessage(ex.getMessage());
        }
        return this.listaRespuestas;
    }
    /**
     * @return respuestas
     */
//    public NotaOrden[] getRespuestas() {
//        try {
//            List<NotaOrden> tempList = this.notaOrdenServicioImpl.getRespuestas(getIdNota().getValue());
//            return tempList.toArray(new NotaOrden[tempList.size()]);
//        } catch (RuntimeException ex) {
//            FacesUtils.addInfoMessage(ex.getMessage());
//        }
//        return new NotaOrden[0];
//    }

    public int getTotalNotasPorInvitado() {
        try {
            return this.notaOrdenServicioImpl.getTotalNotasPorInvitado(this.sesion.getUsuario().getId(),sesion.getUsuario().getApCampo().getId());
        } catch (RuntimeException ex) {
            FacesUtils.addInfoMessage(ex.getMessage());
        }
        return 0;


    }

    /**
     * @return the idNota
     */
    public HtmlOutputText getIdNota() {
        return idNota;
    }

    /**
     * @param idNota the idNota to set
     */
    public void setIdNota(HtmlOutputText idNota) {
        this.idNota = idNota;
    }
}
