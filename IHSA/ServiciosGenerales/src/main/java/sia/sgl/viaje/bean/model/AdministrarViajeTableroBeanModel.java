/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.viaje.bean.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.model.SelectItem;
import sia.constantes.Constantes;
import sia.modelo.gr.vo.GrIntercepcionVO;
import sia.modelo.gr.vo.GrPuntoVO;
import sia.modelo.sgl.oficina.vo.OficinaVO;
import sia.modelo.sgl.viaje.vo.ItinerarioTerrestreVO;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.servicios.gr.impl.GrInterseccionImpl;
import sia.servicios.gr.impl.GrPuntoImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sgl.viaje.impl.SgViajeImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named(value = "administrarViajeTableroBeanModel")

public class AdministrarViajeTableroBeanModel implements Serializable {

    @Inject
    private Sesion sesion;

    @Inject
    private SgViajeImpl sgViajeImpl;
    @Inject
    private GrInterseccionImpl grInterseccionImpl;
    @Inject
    private SiManejoFechaImpl siManejoFechaLocal;
    @Inject
    private GrPuntoImpl grPuntoImpl;
    @Inject
    private SgOficinaImpl sgOficinaImpl;

    private List<ItinerarioTerrestreVO> viajesProgramadosMTY;
    private List<ItinerarioTerrestreVO> viajesProgramadosREY;
    private List<ItinerarioTerrestreVO> viajesProgramadosSF;
    private List<ItinerarioTerrestreVO> viajesEnCursoMTY;
    private List<ItinerarioTerrestreVO> viajesEnCursoREY;
    private List<ItinerarioTerrestreVO> viajesEnCursoSF;
    private List<ItinerarioTerrestreVO> viajesEnCursoMTYCd;
    private List<ItinerarioTerrestreVO> viajesEnCursoREYCd;
    private List<ItinerarioTerrestreVO> viajesEnCursoSFCd;
    private List<ItinerarioTerrestreVO> viajesProgramadosMTYCd;
    private List<ItinerarioTerrestreVO> viajesProgramadosREYCd;
    private List<ItinerarioTerrestreVO> viajesProgramadosSFCd;
    private List<ItinerarioTerrestreVO> viajesProgramadosInt;
    private ItinerarioTerrestreVO itinerarioReferencia;

    private Date fechaInt1 = null;
    private Date fechaInt2 = null;

    private GrIntercepcionVO intercepcion;
    private int idViajeIntA;
    private int idViajeIntB;
    private List<ViajeVO> lstViajesInt;
    private List<SelectItem> lstPuntos;
    private int idPSLlegada;
    private ViajeVO infoViaje;
    private int oficinaID;
    private List<SelectItem> lstOficinasOrigen = new ArrayList<SelectItem>();
    private boolean conChofer = true;
    private int indexTab = 1;
    private String activeTab1 = "active";
    private String activeTab2 = "";

    public AdministrarViajeTableroBeanModel() {
    }

    @PostConstruct
    public void iniciarConversasionCrearViaje() {
        ItinerarioTerrestreVO itiVO = new ItinerarioTerrestreVO();

        ViajeVO viajeMTYREY = new ViajeVO();
        viajeMTYREY.setIdRuta(Constantes.RUTA_MTY_REY);
        viajeMTYREY.setLstRutaDet(sgViajeImpl.getRutaSectores(viajeMTYREY.getIdRuta()));

        ViajeVO viajeREYSF = new ViajeVO();
        viajeREYSF.setIdRuta(Constantes.RUTA_REY_SF);
        viajeREYSF.setLstRutaDet(sgViajeImpl.getRutaSectores(viajeREYSF.getIdRuta()));

        itiVO.setViajeMTY(viajeMTYREY);
        itiVO.setViajeREY(viajeREYSF);
        itiVO.setLstRutaDetPredefinida(viajeMTYREY.getLstRutaDet());
        itiVO.getLstRutaDetPredefinida().addAll(viajeREYSF.getLstRutaDet().subList(1, viajeREYSF.getLstRutaDet().size()));
        this.setItinerarioReferencia(itiVO);
        this.setLstOficinasOrigen(this.mtlistaOficina());
        cargarViajes();
    }
    
    public void changeTab() {
        activeTab1 = "active";
        activeTab2 = "";
        if(indexTab == 1){
            activeTab1 = "active";
            activeTab2 = "";
        } else if (indexTab == 2) {
            activeTab1 = "";
            activeTab2 = "active";
        }
    }

    public void cargarViajes() {
        if (this.getOficinaID() == 0 || this.getOficinaID() == Constantes.ID_OFICINA_TORRE_MARTEL) {
            this.setViajesProgramadosMTY(this.traerLstViajesProgramados(Constantes.ID_OFICINA_TORRE_MARTEL, Constantes.SOLICITUDES_TERRESTRE_OFICINA, false, isConChofer()));
        } else {
            this.setViajesProgramadosMTY(new ArrayList<ItinerarioTerrestreVO>());
        }
        if (this.getOficinaID() == 0 || this.getOficinaID() == Constantes.ID_OFICINA_REY_PRINCIPAL) {
            this.setViajesProgramadosREY(this.traerLstViajesProgramados(Constantes.ID_OFICINA_REY_PRINCIPAL, Constantes.SOLICITUDES_TERRESTRE_OFICINA, false, isConChofer()));
        } else {
            this.setViajesProgramadosREY(new ArrayList<ItinerarioTerrestreVO>());
        }
        if (this.getOficinaID() == 0 || this.getOficinaID() == Constantes.ID_OFICINA_SAN_FERNANDO) {
            this.setViajesProgramadosSF(this.traerLstViajesProgramados(Constantes.ID_OFICINA_SAN_FERNANDO, Constantes.SOLICITUDES_TERRESTRE_OFICINA, false, isConChofer()));
        } else {
            this.setViajesProgramadosSF(new ArrayList<ItinerarioTerrestreVO>());
        }
        if (this.getOficinaID() == 0 || this.getOficinaID() == Constantes.ID_OFICINA_TORRE_MARTEL) {
            this.setViajesEnCursoMTY(this.traerLstViajesEnCurso(Constantes.ID_OFICINA_TORRE_MARTEL, Constantes.SOLICITUDES_TERRESTRE_OFICINA, false, isConChofer()));
        } else {
            this.setViajesEnCursoMTY(new ArrayList<ItinerarioTerrestreVO>());
        }
        if (this.getOficinaID() == 0 || this.getOficinaID() == Constantes.ID_OFICINA_REY_PRINCIPAL) {
            this.setViajesEnCursoREY(this.traerLstViajesEnCurso(Constantes.ID_OFICINA_REY_PRINCIPAL, Constantes.SOLICITUDES_TERRESTRE_OFICINA, false, isConChofer()));
        } else {
            this.setViajesEnCursoREY(new ArrayList<ItinerarioTerrestreVO>());
        }
        if (this.getOficinaID() == 0 || this.getOficinaID() == Constantes.ID_OFICINA_SAN_FERNANDO) {
            this.setViajesEnCursoSF(this.traerLstViajesEnCurso(Constantes.ID_OFICINA_SAN_FERNANDO, Constantes.SOLICITUDES_TERRESTRE_OFICINA, false, isConChofer()));
        } else {
            this.setViajesEnCursoSF(new ArrayList<ItinerarioTerrestreVO>());
        }
        if (this.getOficinaID() == 0 || this.getOficinaID() == Constantes.ID_OFICINA_TORRE_MARTEL) {
            this.setViajesProgramadosMTYCd(this.traerLstViajesProgramados(Constantes.ID_OFICINA_TORRE_MARTEL, Constantes.SOLICITUDES_TERRESTRE_CIUDAD, false, isConChofer()));
        } else {
            this.setViajesProgramadosMTYCd(new ArrayList<ItinerarioTerrestreVO>());
        }
        if (this.getOficinaID() == 0 || this.getOficinaID() == Constantes.ID_OFICINA_REY_PRINCIPAL) {
            this.setViajesProgramadosREYCd(this.traerLstViajesProgramados(Constantes.ID_OFICINA_REY_PRINCIPAL, Constantes.SOLICITUDES_TERRESTRE_CIUDAD, false, isConChofer()));
        } else {
            this.setViajesProgramadosREYCd(new ArrayList<ItinerarioTerrestreVO>());
        }
        if (this.getOficinaID() == 0 || this.getOficinaID() == Constantes.ID_OFICINA_SAN_FERNANDO) {
            this.setViajesProgramadosSFCd(this.traerLstViajesProgramados(Constantes.ID_OFICINA_SAN_FERNANDO, Constantes.SOLICITUDES_TERRESTRE_CIUDAD, false, isConChofer()));
        } else {
            this.setViajesProgramadosSFCd(new ArrayList<ItinerarioTerrestreVO>());
        }
         if (this.getOficinaID() == 0 || this.getOficinaID() == Constantes.ID_OFICINA_TORRE_MARTEL) {
            this.setViajesEnCursoMTYCd(this.traerLstViajesEnCurso(Constantes.ID_OFICINA_TORRE_MARTEL, Constantes.SOLICITUDES_TERRESTRE_CIUDAD, false, isConChofer()));
        } else {
            this.setViajesEnCursoMTYCd(new ArrayList<ItinerarioTerrestreVO>());
        }
        if (this.getOficinaID() == 0 || this.getOficinaID() == Constantes.ID_OFICINA_REY_PRINCIPAL) {
            this.setViajesEnCursoREYCd(this.traerLstViajesEnCurso(Constantes.ID_OFICINA_REY_PRINCIPAL, Constantes.SOLICITUDES_TERRESTRE_CIUDAD, false, isConChofer()));
        } else {
            this.setViajesEnCursoREYCd(new ArrayList<ItinerarioTerrestreVO>());
        }
        if (this.getOficinaID() == 0 || this.getOficinaID() == Constantes.ID_OFICINA_SAN_FERNANDO) {
            this.setViajesEnCursoSFCd(this.traerLstViajesEnCurso(Constantes.ID_OFICINA_SAN_FERNANDO, Constantes.SOLICITUDES_TERRESTRE_CIUDAD, false, isConChofer()));
        } else {
            this.setViajesEnCursoSFCd(new ArrayList<ItinerarioTerrestreVO>());
        }
        if (this.getItinerarioReferencia() != null && this.getItinerarioReferencia().getLstRutaDetPredefinida() != null && !this.getItinerarioReferencia().getLstRutaDetPredefinida().isEmpty()) {
            this.setViajesProgramadosInt(this.traerLstItinerioInserseccion(0, this.getItinerarioReferencia().getLstRutaDetPredefinida()));
        }
    }

    public void goInterceptarViaje(int idViaje) {
        try {
            setIntercepcion(null);
            setIdViajeIntA(idViaje);
            setLstViajesInt(new ArrayList<ViajeVO>());
            getLstViajesInt().addAll(sgViajeImpl.traerViajesInterceptantes(Constantes.ESTATUS_VIAJE_POR_SALIR, getIdViajeIntA(), this.getFechaInt1(), this.getFechaInt2()));
            setLstPuntos(grPuntoImpl.getPuntosItems(idViaje));
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void goInfoViaje(int idViaje) {
        try {
            setInfoViaje(sgViajeImpl.buscarPorId(idViaje, true));
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void interceptarViaje() {
        try {
            if (this.getIntercepcion() == null) {
                if (getIdViajeIntA() > 0 && getIdViajeIntB() > 0) {
                    grInterseccionImpl.crearIntercepcionViajes(getIdViajeIntA(), getIdViajeIntB(), getIdPSLlegada(), getUsrID());
                    cargarViajes();
                }
            } else if (this.getIntercepcion() != null && this.getIdViajeIntA() > 0 && this.getIdViajeIntB() > 0) {
                grInterseccionImpl.crearIntercepcionViajes(getIdViajeIntA(), getIdViajeIntB(), getUsrID(), this.getIntercepcion());
                cargarViajes();
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public List<ItinerarioTerrestreVO> traerLstViajesProgramados(int oficinaID, int tipoSolicitud, boolean enInterseccion, boolean conChofer) {
        List<ItinerarioTerrestreVO> l = new ArrayList<ItinerarioTerrestreVO>();
        try {
            for (ViajeVO vo : sgViajeImpl.getRoadTripByExit(oficinaID,
                    Constantes.ESTATUS_VIAJE_POR_SALIR,
                    Constantes.CERO,
                    true,
                    this.getFechaInt1(), this.getFechaInt2(), true, tipoSolicitud, enInterseccion, conChofer)) {
                ItinerarioTerrestreVO itiVO = new ItinerarioTerrestreVO();
                if (Constantes.ID_OFICINA_TORRE_MARTEL == oficinaID) {
                    itiVO.setViajeMTY(vo);
                } else if (Constantes.ID_OFICINA_REY_PRINCIPAL == oficinaID) {
                    itiVO.setViajeREY(vo);
                } else if (Constantes.ID_OFICINA_SAN_FERNANDO == oficinaID) {
                    itiVO.setViajeSF(vo);
                }
                l.add(itiVO);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
        return l;
    }

    public List<ItinerarioTerrestreVO> traerLstViajesEnCurso(int oficinaID, int tipoSolicitud, boolean enInterseccion, boolean conChofer) {
        List<ItinerarioTerrestreVO> l = new ArrayList<ItinerarioTerrestreVO>();
        try {
            for (ViajeVO vo : sgViajeImpl.getRoadTripByExit(oficinaID,
                    Constantes.ESTATUS_VIAJE_PROCESO,
                    Constantes.CERO,
                    true,
                    this.getFechaInt1(), this.getFechaInt2(), true, tipoSolicitud, enInterseccion, conChofer)) {
                ItinerarioTerrestreVO itiVO = new ItinerarioTerrestreVO();
                if (Constantes.ID_OFICINA_TORRE_MARTEL == oficinaID) {
                    itiVO.setViajeMTY(vo);
                } else if (Constantes.ID_OFICINA_REY_PRINCIPAL == oficinaID) {
                    itiVO.setViajeREY(vo);
                } else if (Constantes.ID_OFICINA_SAN_FERNANDO == oficinaID) {
                    itiVO.setViajeSF(vo);
                }
                l.add(itiVO);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
        return l;
    }

    public List<ItinerarioTerrestreVO> traerLstItinerioInserseccion(int IDInterseccion, List<GrPuntoVO> puntos) {
        List<ItinerarioTerrestreVO> l = new ArrayList<ItinerarioTerrestreVO>();
        try {
            l.addAll(grInterseccionImpl.traerItinerarioIntercecciones(IDInterseccion, puntos));
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
        return l;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    public String getUsrID() {
        String usr = "";
        if (sesion != null && sesion.getUsuario() != null
                && sesion.getUsuario().getId() != null
                && !sesion.getUsuario().getId().isEmpty()) {
            usr = sesion.getUsuario().getId();
        }
        return usr;
    }

    /**
     * @return the viajeReferencia
     */
    public ItinerarioTerrestreVO getItinerarioReferencia() {
        return itinerarioReferencia;
    }

    /**
     * @param itinerarioReferencia the itinerarioReferencia to set
     */
    public void setItinerarioReferencia(ItinerarioTerrestreVO itinerarioReferencia) {
        this.itinerarioReferencia = itinerarioReferencia;
    }

    /**
     * @return the viajesProgramadosMTY
     */
    public List<ItinerarioTerrestreVO> getViajesProgramadosMTY() {
        return viajesProgramadosMTY;
    }

    /**
     * @param viajesProgramadosMTY the viajesProgramadosMTY to set
     */
    public void setViajesProgramadosMTY(List<ItinerarioTerrestreVO> viajesProgramadosMTY) {
        this.viajesProgramadosMTY = viajesProgramadosMTY;
    }

    /**
     * @return the viajesProgramadosREY
     */
    public List<ItinerarioTerrestreVO> getViajesProgramadosREY() {
        return viajesProgramadosREY;
    }

    /**
     * @param viajesProgramadosREY the viajesProgramadosREY to set
     */
    public void setViajesProgramadosREY(List<ItinerarioTerrestreVO> viajesProgramadosREY) {
        this.viajesProgramadosREY = viajesProgramadosREY;
    }

    /**
     * @return the viajesProgramadosSF
     */
    public List<ItinerarioTerrestreVO> getViajesProgramadosSF() {
        return viajesProgramadosSF;
    }

    /**
     * @param viajesProgramadosSF the viajesProgramadosSF to set
     */
    public void setViajesProgramadosSF(List<ItinerarioTerrestreVO> viajesProgramadosSF) {
        this.viajesProgramadosSF = viajesProgramadosSF;
    }

    /**
     * @return the viajesProgramadosInt
     */
    public List<ItinerarioTerrestreVO> getViajesProgramadosInt() {
        return viajesProgramadosInt;
    }

    /**
     * @param viajesProgramadosInt the viajesProgramadosInt to set
     */
    public void setViajesProgramadosInt(List<ItinerarioTerrestreVO> viajesProgramadosInt) {
        this.viajesProgramadosInt = viajesProgramadosInt;
    }

    /**
     * @return the viajesProgramadosMTYCd
     */
    public List<ItinerarioTerrestreVO> getViajesProgramadosMTYCd() {
        return viajesProgramadosMTYCd;
    }

    /**
     * @param viajesProgramadosMTYCd the viajesProgramadosMTYCd to set
     */
    public void setViajesProgramadosMTYCd(List<ItinerarioTerrestreVO> viajesProgramadosMTYCd) {
        this.viajesProgramadosMTYCd = viajesProgramadosMTYCd;
    }

    /**
     * @return the viajesProgramadosREYCd
     */
    public List<ItinerarioTerrestreVO> getViajesProgramadosREYCd() {
        return viajesProgramadosREYCd;
    }

    /**
     * @param viajesProgramadosREYCd the viajesProgramadosREYCd to set
     */
    public void setViajesProgramadosREYCd(List<ItinerarioTerrestreVO> viajesProgramadosREYCd) {
        this.viajesProgramadosREYCd = viajesProgramadosREYCd;
    }

    /**
     * @return the viajesProgramadosSFCd
     */
    public List<ItinerarioTerrestreVO> getViajesProgramadosSFCd() {
        return viajesProgramadosSFCd;
    }

    /**
     * @param viajesProgramadosSFCd the viajesProgramadosSFCd to set
     */
    public void setViajesProgramadosSFCd(List<ItinerarioTerrestreVO> viajesProgramadosSFCd) {
        this.viajesProgramadosSFCd = viajesProgramadosSFCd;
    }

    private int diferenciaDias(Date d1, Date d2) {
        int dias = siManejoFechaLocal.dias(d2, d1);
        return dias < 1 ? 1 : dias;
    }

    /**
     * @return the fechaInt1
     */
    public Date getFechaInt1() {
        if (this.fechaInt1 == null) {
            this.fechaInt1 = new Date();
        }
        return fechaInt1;
    }

    /**
     * @param fechaInt1 the fechaInt1 to set
     */
    public void setFechaInt1(Date fechaInt1) {
        this.fechaInt1 = fechaInt1;
    }

    /**
     * @return the fechaInt2
     */
    public Date getFechaInt2() {
        if (fechaInt2 == null) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, 1);
            fechaInt2 = c.getTime();
        }
        return fechaInt2;
    }

    /**
     * @param fechaInt2 the fechaInt2 to set
     */
    public void setFechaInt2(Date fechaInt2) {
        this.fechaInt2 = fechaInt2;
    }

    /**
     * @return the intercepcion
     */
    public GrIntercepcionVO getIntercepcion() {
        return intercepcion;
    }

    /**
     * @param intercepcion the intercepcion to set
     */
    public void setIntercepcion(GrIntercepcionVO intercepcion) {
        this.intercepcion = intercepcion;
    }

    /**
     * @return the idViajeIntA
     */
    public int getIdViajeIntA() {
        return idViajeIntA;
    }

    /**
     * @param idViajeIntA the idViajeIntA to set
     */
    public void setIdViajeIntA(int idViajeIntA) {
        this.idViajeIntA = idViajeIntA;
    }

    /**
     * @return the idViajeIntB
     */
    public int getIdViajeIntB() {
        return idViajeIntB;
    }

    /**
     * @param idViajeIntB the idViajeIntB to set
     */
    public void setIdViajeIntB(int idViajeIntB) {
        this.idViajeIntB = idViajeIntB;
    }

    /**
     * @return the lstViajesInt
     */
    public List<ViajeVO> getLstViajesInt() {
        return lstViajesInt;
    }

    /**
     * @param lstViajesInt the lstViajesInt to set
     */
    public void setLstViajesInt(List<ViajeVO> lstViajesInt) {
        this.lstViajesInt = lstViajesInt;
    }

    /**
     * @return the lstPuntos
     */
    public List<SelectItem> getLstPuntos() {
        return lstPuntos;
    }

    /**
     * @param lstPuntos the lstPuntos to set
     */
    public void setLstPuntos(List<SelectItem> lstPuntos) {
        this.lstPuntos = lstPuntos;
    }

    /**
     * @return the idPSLlegada
     */
    public int getIdPSLlegada() {
        return idPSLlegada;
    }

    /**
     * @param idPSLlegada the idPSLlegada to set
     */
    public void setIdPSLlegada(int idPSLlegada) {
        this.idPSLlegada = idPSLlegada;
    }

    /**
     * @return the infoViaje
     */
    public ViajeVO getInfoViaje() {
        return infoViaje;
    }

    /**
     * @param infoViaje the infoViaje to set
     */
    public void setInfoViaje(ViajeVO infoViaje) {
        this.infoViaje = infoViaje;
    }

    /**
     * @return the oficinaID
     */
    public int getOficinaID() {
        return oficinaID;
    }

    /**
     * @param oficinaID the oficinaID to set
     */
    public void setOficinaID(int oficinaID) {
        this.oficinaID = oficinaID;
    }

    /**
     * @return the lstOficinasOrigen
     */
    public List<SelectItem> getLstOficinasOrigen() {
        return lstOficinasOrigen;
    }

    /**
     * @param lstOficinasOrigen the lstOficinasOrigen to set
     */
    public void setLstOficinasOrigen(List<SelectItem> lstOficinasOrigen) {
        this.lstOficinasOrigen = lstOficinasOrigen;
    }

    public List<SelectItem> mtlistaOficina() {
        List<SelectItem> l = new ArrayList<SelectItem>();
        try {
            List<OficinaVO> lv = sgOficinaImpl.traerListaOficina();
            for (OficinaVO sgO : lv) {
                l.add(new SelectItem(sgO.getId(), sgO.getNombre()));
            }
        } catch (Exception ex) {
            Logger.getLogger(AdministrarViajeTableroBeanModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return l;
    }

    /**
     * @return the conChofer
     */
    public boolean isConChofer() {
        return conChofer;
    }

    /**
     * @param conChofer the conChofer to set
     */
    public void setConChofer(boolean conChofer) {
        this.conChofer = conChofer;
    }

    /**
     * @return the viajesEnCursoMTY
     */
    public List<ItinerarioTerrestreVO> getViajesEnCursoMTY() {
        return viajesEnCursoMTY;
    }

    /**
     * @param viajesEnCursoMTY the viajesEnCursoMTY to set
     */
    public void setViajesEnCursoMTY(List<ItinerarioTerrestreVO> viajesEnCursoMTY) {
        this.viajesEnCursoMTY = viajesEnCursoMTY;
    }

    /**
     * @return the viajesEnCursoREY
     */
    public List<ItinerarioTerrestreVO> getViajesEnCursoREY() {
        return viajesEnCursoREY;
    }

    /**
     * @param viajesEnCursoREY the viajesEnCursoREY to set
     */
    public void setViajesEnCursoREY(List<ItinerarioTerrestreVO> viajesEnCursoREY) {
        this.viajesEnCursoREY = viajesEnCursoREY;
    }

    /**
     * @return the viajesEnCursoSF
     */
    public List<ItinerarioTerrestreVO> getViajesEnCursoSF() {
        return viajesEnCursoSF;
    }

    /**
     * @param viajesEnCursoSF the viajesEnCursoSF to set
     */
    public void setViajesEnCursoSF(List<ItinerarioTerrestreVO> viajesEnCursoSF) {
        this.viajesEnCursoSF = viajesEnCursoSF;
    }

    /**
     * @return the indexTab
     */
    public int getIndexTab() {
        return indexTab;
    }

    /**
     * @param indexTab the indexTab to set
     */
    public void setIndexTab(int indexTab) {
        this.indexTab = indexTab;
    }

    /**
     * @return the activeTab1
     */
    public String getActiveTab1() {
        return activeTab1;
    }

    /**
     * @param activeTab1 the activeTab1 to set
     */
    public void setActiveTab1(String activeTab1) {
        this.activeTab1 = activeTab1;
    }

    /**
     * @return the activeTab2
     */
    public String getActiveTab2() {
        return activeTab2;
    }

    /**
     * @param activeTab2 the activeTab2 to set
     */
    public void setActiveTab2(String activeTab2) {
        this.activeTab2 = activeTab2;
    }

    /**
     * @return the viajesEnCursoMTYCd
     */
    public List<ItinerarioTerrestreVO> getViajesEnCursoMTYCd() {
        return viajesEnCursoMTYCd;
    }

    /**
     * @param viajesEnCursoMTYCd the viajesEnCursoMTYCd to set
     */
    public void setViajesEnCursoMTYCd(List<ItinerarioTerrestreVO> viajesEnCursoMTYCd) {
        this.viajesEnCursoMTYCd = viajesEnCursoMTYCd;
    }

    /**
     * @return the viajesEnCursoREYCd
     */
    public List<ItinerarioTerrestreVO> getViajesEnCursoREYCd() {
        return viajesEnCursoREYCd;
    }

    /**
     * @param viajesEnCursoREYCd the viajesEnCursoREYCd to set
     */
    public void setViajesEnCursoREYCd(List<ItinerarioTerrestreVO> viajesEnCursoREYCd) {
        this.viajesEnCursoREYCd = viajesEnCursoREYCd;
    }

    /**
     * @return the viajesEnCursoSFCd
     */
    public List<ItinerarioTerrestreVO> getViajesEnCursoSFCd() {
        return viajesEnCursoSFCd;
    }

    /**
     * @param viajesEnCursoSFCd the viajesEnCursoSFCd to set
     */
    public void setViajesEnCursoSFCd(List<ItinerarioTerrestreVO> viajesEnCursoSFCd) {
        this.viajesEnCursoSFCd = viajesEnCursoSFCd;
    }

}
