/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.ihsa.gr.sistema.soporte;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import sia.constantes.Constantes;
import sia.modelo.gr.vo.GrArchivoVO;
import sia.modelo.gr.vo.GrPuntoVO;
import sia.modelo.gr.vo.GrSitioVO;
import sia.modelo.gr.vo.MapaVO;
import sia.modelo.sgl.oficina.vo.OficinaVO;
import sia.modelo.sgl.semaforo.vo.SgEstadoSemaforoVO;
import sia.modelo.sgl.viaje.vo.RutaTerrestreVo;
import sia.servicios.gr.impl.GrArchivoImpl;
import sia.servicios.gr.impl.GrMapaImpl;
import sia.servicios.gr.impl.GrPuntoImpl;
import sia.servicios.gr.impl.GrSitioImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sgl.semaforo.impl.SgEstadoSemaforoImpl;
import sia.servicios.sgl.viaje.impl.SgRutaTerrestreImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named(value = "configuracionBean")
@ViewScoped
public class ConfiguracionBean implements Serializable {

    //ManagedBeans
    //Servicios
    @Inject
    private GrMapaImpl grMapaImpl;
    @Inject
    private GrArchivoImpl grArchivoImpl;
    @Inject
    private GrSitioImpl grSitioImpl;
    @Inject
    private SgOficinaImpl sgOficinaImpl;
    @Inject
    private SgRutaTerrestreImpl sgRutaTerrestreImpl;
    @Inject
    private GrPuntoImpl grPuntoImpl;
    @Inject
    private SgEstadoSemaforoImpl sgEstadoSemaforoImpl;

    private List<GrArchivoVO> mapas;
    private List<GrSitioVO> sitios;
    private List<GrArchivoVO> recomendaciones;
    private List<GrArchivoVO> situaciones;
    private List<MapaVO> zonas;
    private List<SelectItem> zonasSem;
    private List<GrPuntoVO> puntos;
    private List<OficinaVO> oficinas;
    private List<RutaTerrestreVo> rutas;
    private List<SgEstadoSemaforoVO> semaforos;
    private int oficinaID;
    private int semZonaID = 0;

    /**
     * Creates a new instance of Sesion
     */
    public ConfiguracionBean() {
    }

    /**
     * @return the mapas
     */
    @PostConstruct
    public void iniciar() {
        zonasSem = new ArrayList<>();
        //
        FacesContext ctx = FacesContext.getCurrentInstance();
        HttpServletRequest servletRequest = (HttpServletRequest) ctx.getExternalContext().getRequest(); // returns something like "/myapplication/home.faces" 
        String fullURI = servletRequest.getRequestURI();
        if (fullURI.contains("confMapas.xhtml")) {
            setMapas(grArchivoImpl.getArchivos(Constantes.GR_TIPO_ARCHIVO_Mapas, false));
        } else if (fullURI.contains("confPuntos.xhtml")) {
            setPuntos(grPuntoImpl.getPuntos());
        } else if (fullURI.contains("confRecomendaciones.xhtml")) {
            setRecomendaciones(grArchivoImpl.getArchivos(Constantes.GR_TIPO_ARCHIVO_Recomendaciones, true));
        } else if (fullURI.contains("confSemaforo.xhtml")) {
            setSemaforos(sgEstadoSemaforoImpl.getEstadoSemaforos(this.getSemZonaID()));
            setZonasSem(grMapaImpl.getMapasItems(true, true));
        } else if (fullURI.contains("confSitios.xhtml")) {
            setSitios(grSitioImpl.getSitios(true));
        } else if (fullURI.contains("confSituaciones.xhtml")) {
            setSituaciones(grArchivoImpl.getArchivos(Constantes.GR_TIPO_ARCHIVO_Situacion, true));
        } else if (fullURI.contains("confTrayectos.xhtml")) {
            List<RutaTerrestreVo> lstRutas = sgRutaTerrestreImpl.traerRutaTerrestrePorOficina(getOficinaID(), Constantes.RUTA_TIPO_OFICINA);
            lstRutas.addAll(sgRutaTerrestreImpl.traerRutaTerrestrePorOficina(getOficinaID(), Constantes.RUTA_TIPO_CIUDAD));
            setRutas(lstRutas);
        } else if (fullURI.contains("confZonas.xhtml")) {
            setZonas(grMapaImpl.getMapas(null));
        }
    }

    public List<GrArchivoVO> getMapasDir() {
        return grArchivoImpl.getArchivos(Constantes.GR_TIPO_ARCHIVO_Mapas, false);
    }

    public List<GrPuntoVO> getPuntosDir() {
        return grPuntoImpl.getPuntos();
    }

    public List<GrArchivoVO> getRecomendacionesDir() {
        return grArchivoImpl.getArchivos(Constantes.GR_TIPO_ARCHIVO_Recomendaciones, true);
    }

    public List<SgEstadoSemaforoVO> getSemaforosDir() {
        return sgEstadoSemaforoImpl.getEstadoSemaforos(this.getSemZonaID());
    }

    public List<SelectItem> getZonasSemDir() {
        return grMapaImpl.getMapasItems(true, true);
    }

    public List<GrSitioVO> setSitiosDir() {
        return grSitioImpl.getSitios(true);
    }

    public List<GrArchivoVO> getSituacionesDir() {
        return grArchivoImpl.getArchivos(Constantes.GR_TIPO_ARCHIVO_Situacion, true);
    }

    public List<RutaTerrestreVo> getRutasDir() {
        List<RutaTerrestreVo> lstRutas = sgRutaTerrestreImpl.traerRutaTerrestrePorOficina(getOficinaID(), Constantes.RUTA_TIPO_OFICINA);
        lstRutas.addAll(sgRutaTerrestreImpl.traerRutaTerrestrePorOficina(getOficinaID(), Constantes.RUTA_TIPO_CIUDAD));
        return lstRutas;
    }

    public List<MapaVO> getZonasDir() {
        return grMapaImpl.getMapas(null);
    }

    /**
     * @param mapas the mapas to set
     */
    public void setMapas(List<GrArchivoVO> mapas) {
        this.mapas = mapas;
    }

    /**
     * @return the mapas
     */
    public List<GrArchivoVO> getMapas() {
        return mapas;
    }

    /**
     * @return the sitios
     */
    public List<GrSitioVO> getSitios() {

        return sitios;
    }

    /**
     * @param sitios the sitios to set
     */
    public void setSitios(List<GrSitioVO> sitios) {
        this.sitios = sitios;
    }

    /**
     * @return the recomendaciones
     */
    public List<GrArchivoVO> getRecomendaciones() {
        return recomendaciones;
    }

    /**
     * @param recomendaciones the recomendaciones to set
     */
    public void setRecomendaciones(List<GrArchivoVO> recomendaciones) {
        this.recomendaciones = recomendaciones;
    }

    /**
     * @return the situaciones
     */
    public List<GrArchivoVO> getSituaciones() {
        return situaciones;
    }

    /**
     * @param situaciones the situaciones to set
     */
    public void setSituaciones(List<GrArchivoVO> situaciones) {
        this.situaciones = situaciones;
    }

    /**
     * @return the zonas
     */
    public List<MapaVO> getZonas() {
        return zonas;
    }

    /**
     * @param zonas the zonas to set
     */
    public void setZonas(List<MapaVO> zonas) {
        this.zonas = zonas;
    }

    /**
     * @return the oficinas
     */
    public List<OficinaVO> getOficinas() {
        this.setOficinas(sgOficinaImpl.traerListaOficina());
        return oficinas;
    }

    /**
     * @param oficinas the oficinas to set
     */
    public void setOficinas(List<OficinaVO> oficinas) {
        this.oficinas = oficinas;
    }

    /**
     * @return the rutas
     */
    public List<RutaTerrestreVo> getRutas() {
        return rutas;
    }

    /**
     * @param rutas the rutas to set
     */
    public void setRutas(List<RutaTerrestreVo> rutas) {
        this.rutas = rutas;
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

    public void cambiarValorOficina() {
        try {
            List<RutaTerrestreVo> lstRutas = sgRutaTerrestreImpl.traerRutaTerrestrePorOficina(getOficinaID(), Constantes.RUTA_TIPO_OFICINA);
            lstRutas.addAll(sgRutaTerrestreImpl.traerRutaTerrestrePorOficina(getOficinaID(), Constantes.RUTA_TIPO_CIUDAD));
            this.setRutas(lstRutas);
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void cambiarValorZonaSem() {
        cargarSemaforosDt();
    }

    public String cargarMapas() {
        this.cargarMapasDt();
        return "/vistas/gr/confMapas.xhtml?faces-redirect=true";
    }

    public void cargarMapasDt() {
        try {
            this.setMapas(grArchivoImpl.getArchivos(Constantes.GR_TIPO_ARCHIVO_Mapas, false));
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public String cargarPuntos() {
        this.cargarPuntosDt();
        return "/vistas/gr/confPuntos.xhtml?faces-redirect=true";
    }

    public void cargarPuntosDt() {
        try {
            this.setPuntos(grPuntoImpl.getPuntos());
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public String cargarRutas() {
        this.cargarRutasDt();
        return "/vistas/gr/confTrayectos.xhtml?faces-redirect=true";
    }

    public void cargarRutasDt() {
        try {
            List<RutaTerrestreVo> lstRutas = sgRutaTerrestreImpl.traerRutaTerrestrePorOficina(getOficinaID(), Constantes.RUTA_TIPO_OFICINA);
            lstRutas.addAll(sgRutaTerrestreImpl.traerRutaTerrestrePorOficina(getOficinaID(), Constantes.RUTA_TIPO_CIUDAD));
            this.setRutas(lstRutas);
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public String cargarSemaforos() {
        this.setSemZonaID(0);
        this.cargarSemaforosDt();
        this.setZonasSem(grMapaImpl.getMapasItems(true, true));
        return "/vistas/gr/confSemaforo.xhtml?faces-redirect=true";
    }

    public void cargarSemaforosDt() {
        try {
            this.setSemaforos(sgEstadoSemaforoImpl.getEstadoSemaforos(this.getSemZonaID()));
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public String cargarSitios() {
        this.cargarSitiosDt();
        return "/vistas/gr/confSitios.xhtml?faces-redirect=true";
    }

    public void cargarSitiosDt() {
        try {
            this.setSitios(grSitioImpl.getSitios(true));
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public String cargarRecomendaciones() {
        this.cargarRecomendacionesDt();
        return "/vistas/gr/confRecomendaciones.xhtml?faces-redirect=true";
    }

    public void cargarRecomendacionesDt() {
        try {
            this.setRecomendaciones(grArchivoImpl.getArchivos(Constantes.GR_TIPO_ARCHIVO_Recomendaciones, true));
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public String cargarSituaciones() {
        this.cargarSituacionesDt();
        return "/vistas/gr/confSituaciones.xhtml?faces-redirect=true";
    }

    public void cargarSituacionesDt() {
        try {
            this.setSituaciones(grArchivoImpl.getArchivos(Constantes.GR_TIPO_ARCHIVO_Situacion, true));
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public String cargarZonas() {
        this.cargarZonasDt();
        return "/vistas/gr/confZonas.xhtml?faces-redirect=true";
    }

    public void cargarZonasDt() {
        try {
            this.setZonas(grMapaImpl.getMapas(null));
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    /**
     * @return the puntos
     */
    public List<GrPuntoVO> getPuntos() {
        return puntos;
    }

    /**
     * @param puntos the puntos to set
     */
    public void setPuntos(List<GrPuntoVO> puntos) {
        this.puntos = puntos;
    }

    /**
     * @return the semaforos
     */
    public List<SgEstadoSemaforoVO> getSemaforos() {
        return semaforos;
    }

    /**
     * @param semaforos the semaforos to set
     */
    public void setSemaforos(List<SgEstadoSemaforoVO> semaforos) {
        this.semaforos = semaforos;
    }

    /**
     * @return the zonasSem
     */
    public List<SelectItem> getZonasSem() {
        return zonasSem;
    }

    /**
     * @param zonasSem the zonasSem to set
     */
    public void setZonasSem(List<SelectItem> zonasSem) {
        this.zonasSem = zonasSem;
    }

    /**
     * @return the semZonaID
     */
    public int getSemZonaID() {
        return semZonaID;
    }

    /**
     * @param semZonaID the semZonaID to set
     */
    public void setSemZonaID(int semZonaID) {
        this.semZonaID = semZonaID;
    }
}
