/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.reporte.bean.model;

import java.io.Serializable;
import javax.inject.Inject;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.inject.Named;
import sia.constantes.Constantes;
import sia.modelo.SgSolicitudEstancia;
import sia.servicios.sgl.impl.SgDetalleSolicitudEstanciaImpl;
import sia.servicios.sgl.impl.SgHuespedHotelImpl;
import sia.servicios.sgl.impl.SgHuespedStaffImpl;
import sia.servicios.sgl.impl.SgSolicitudEstanciaImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.ConversationsManager;
import sia.util.UtilLog4j;

/**
 *
 * @author marino
 */
@Named
@ConversationScoped
public class ReporteBeanModel implements Serializable {

    @Inject
    Conversation conversation;
    @Inject
    ConversationsManager conversationsManager;
    @Inject
    Sesion sesion;
    
    //Servicios
    @Inject
    private SgSolicitudEstanciaImpl sgSolicitudEstanciaImpl;
    @Inject
    private SgDetalleSolicitudEstanciaImpl sgDetalleSolicitudEstanciaImpl;
    @Inject
    private SgHuespedHotelImpl sgHuespedHotelImpl;
    @Inject
    private SgHuespedStaffImpl sgHuespedStaffImpl;
    //Entidades
    private SgSolicitudEstancia sgSolicitudEstancia;
    //Variables
    private String codigo;
    private DataModel lista;

    /**
     * Creates a new instance of reporteBeanModel
     */    
    public ReporteBeanModel() {
    }

    public void iniciarConvesacionConsulta() {
        conversationsManager.finalizeAllConversations();
        conversationsManager.beginConversation(conversation, ReporteBeanModel.class.getName());
    }

    public SgSolicitudEstancia buscarSolicitudEstancia() {
        try {
            setSgSolicitudEstancia(sgSolicitudEstanciaImpl.buscarEstanciaPorcodigo(getCodigo().toUpperCase()));
            return getSgSolicitudEstancia();

        } catch (Exception e) {
            return null;
        }
    }

    public DataModel traerDetalleSolicitud() {
        setLista(new ListDataModel(sgDetalleSolicitudEstanciaImpl.traerDetallePorSolicitud(getSgSolicitudEstancia().getId(), Constantes.NO_ELIMINADO)));
        return getLista();
    }

    public DataModel traerHospedadosHotel() {
        setLista(new ListDataModel(sgHuespedHotelImpl.traerHospedadosHotel(getSgSolicitudEstancia().getId())));
        return getLista();
    }

    public DataModel traerCanceladosHotel() {
        setLista(new ListDataModel(sgHuespedHotelImpl.traerCanceladosHotel(getSgSolicitudEstancia())));
        return getLista();
    }

    public DataModel traerNoHospedadosNoCanceladosHotel() {
        setLista(new ListDataModel(sgHuespedHotelImpl.traerNoHospedadosNoCanceladosHotel(getSgSolicitudEstancia())));
        return getLista();
    }

    public DataModel traerHospedadosStaff() {
        UtilLog4j.log.info(this, "hospedados staff");
        setLista(new ListDataModel(sgHuespedStaffImpl.getAllHuespedesBySolicitudHospedado(getSgSolicitudEstancia())));
        return getLista();
    }

    public DataModel traerCanceldosHuespedStaff() {
        UtilLog4j.log.info(this, "cancelados staff");
        setLista(new ListDataModel(sgHuespedStaffImpl.getAllHuespedesBySolicitudCancelado(getSgSolicitudEstancia())));
        return getLista();
    }

    public DataModel traerEstanciaTerminadaStaff() {
        UtilLog4j.log.info(this, "terminados staff");
        setLista(new ListDataModel(sgHuespedStaffImpl.getAllHuespedesBySolicitudEstanciaTerminada(getSgSolicitudEstancia())));
        return getLista();
    }

    /**
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * @return the sgSolicitudEstancia
     */
    public SgSolicitudEstancia getSgSolicitudEstancia() {
        return sgSolicitudEstancia;
    }

    /**
     * @param sgSolicitudEstancia the sgSolicitudEstancia to set
     */
    public void setSgSolicitudEstancia(SgSolicitudEstancia sgSolicitudEstancia) {
        this.sgSolicitudEstancia = sgSolicitudEstancia;
    }

    /**
     * @return the lista
     */
    public DataModel getLista() {
        return lista;
    }

    /**
     * @param lista the lista to set
     */
    public void setLista(DataModel lista) {
        this.lista = lista;
    }
}
