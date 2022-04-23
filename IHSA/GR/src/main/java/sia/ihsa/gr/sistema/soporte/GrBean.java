/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.ihsa.gr.sistema.soporte;

import com.google.gson.Gson;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import sia.constantes.Constantes;
import sia.modelo.SgDetalleRutaTerrestre;
import sia.modelo.SgViaje;
import sia.modelo.gr.vo.GrArchivoVO;
import sia.modelo.gr.vo.GrMapaGPSVO;
import sia.modelo.gr.vo.GrSitioVO;
import sia.modelo.sgl.vo.SgDetalleRutaTerrestreVo;
import sia.servicios.gr.impl.GrArchivoImpl;
import sia.servicios.gr.impl.GrSitioImpl;
import sia.servicios.sgl.viaje.impl.SgDetalleRutaCiudadImpl;
import sia.servicios.sgl.viaje.impl.SgDetalleRutaTerrestreImpl;
import sia.servicios.sgl.viaje.impl.SgViajeImpl;
import sia.servicios.sistema.impl.SiLocalizacionImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@ManagedBean(name = "grBean")
@CustomScoped(value = "#{window}")
public class GrBean implements Serializable {

    //ManagedBeans
    //Servicios
    @EJB
    private GrArchivoImpl grArchivoImpl;
    @EJB
    private GrSitioImpl grSitioImpl;
    @EJB
    private SgViajeImpl sgViajeImpl;
    @EJB
    private SgDetalleRutaTerrestreImpl sgDetalleRutaTerrestreImpl;
    @EJB
    private SiLocalizacionImpl siLocalizacionImpl;
    @EJB
    private SgDetalleRutaCiudadImpl sgDetalleRutaCiudadImpl;

    private PopUpGRBean popUpGRBean = (PopUpGRBean) FacesUtilsBean.getManagedBean("popupGrBean");
    private Sesion sesionBean = (Sesion) FacesUtilsBean.getManagedBean("sesion");
    private GrArchivoVO mapa;
    private List<GrSitioVO> sitios;
    private List<GrArchivoVO> recomendaciones;
    private List<GrArchivoVO> situaciones;
    private final Gson gson = new Gson();
    private List<GrMapaGPSVO> mapasGPS;
    private List<GrMapaGPSVO> origenGPS;
    private List<GrMapaGPSVO> destinoGPS;
    private int idNoticia;

    /**
     * Creates a new instance of Sesion
     */
    public GrBean() {
    }

    /**
     * @param tipo
     * @return the mapa
     */
    public GrArchivoVO getMapa() {
	return mapa;
    }

    /**
     * @param mapa the mapas to set
     */
    public void setMapa(GrArchivoVO mapa) {
	this.mapa = mapa;
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

    public String goMapa() {
	int tipo = Integer.parseInt(FacesUtilsBean.getRequestParameter("tipoArchivo"));
	setMapa(grArchivoImpl.getArchivo(tipo));
	return "/vistas/gr/mapa";
    }

    public String goGPS() {
	try {
	    int idViaje = Integer.parseInt(FacesUtilsBean.getRequestParameter("idViaje"));
	    if (idViaje > 0) {
		SgViaje viaje = sgViajeImpl.find(idViaje);
		List<GrMapaGPSVO> puntosOrGPS = new ArrayList<GrMapaGPSVO>();
		List<GrMapaGPSVO> puntosDeGPS = new ArrayList<GrMapaGPSVO>();
		GrMapaGPSVO origen = new GrMapaGPSVO(String.valueOf(idViaje), viaje.getSgRutaTerrestre().getSgOficina().getLongitud(), viaje.getSgRutaTerrestre().getSgOficina().getLatitud());
		//new StringBuilder().append(Constantes.FMT_yyyy_MM_dd.format(new Date())).append("T").append(Constantes.FMT_HHmmss.format(new Date())).append(".511Z").toString());
		puntosOrGPS.add(origen);
                if (Constantes.RUTA_TIPO_OFICINA == viaje.getSgRutaTerrestre().getSgTipoEspecifico().getId()) {
                    SgDetalleRutaTerrestre detRuta = sgDetalleRutaTerrestreImpl.findSgDetalleRutaTerrestreDestinoBySgRutaTerrestre(viaje.getSgRutaTerrestre().getId());
                    GrMapaGPSVO destino = new GrMapaGPSVO(String.valueOf(idViaje), detRuta.getSgOficina().getLongitud(), detRuta.getSgOficina().getLatitud());                    
                    puntosDeGPS.add(destino);
                } else if (Constantes.RUTA_TIPO_CIUDAD == viaje.getSgRutaTerrestre().getSgTipoEspecifico().getId()) {
                    SgDetalleRutaTerrestreVo detRuta = sgDetalleRutaCiudadImpl.buscarDetalleRutaCiudadDestinoPorRuta(viaje.getSgRutaTerrestre().getId());
                    GrMapaGPSVO destino = new GrMapaGPSVO(String.valueOf(idViaje), detRuta.getLongitud(), detRuta.getLatitud());                    
                    puntosDeGPS.add(destino);
                }
		this.setOrigenGPS(puntosOrGPS);
		this.setDestinoGPS(puntosDeGPS);
		this.setMapasGPS(siLocalizacionImpl.obtenerCoordenadas(idViaje));
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.toString());
	    FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
	}
	return "/vistas/gr/mapaGPS";
    }

    public String goRecomendaciones() {
	int tipo = Integer.parseInt(FacesUtilsBean.getRequestParameter("tipoArchivo"));
	setRecomendaciones(grArchivoImpl.getArchivos(tipo, false));
	return "/vistas/gr/recomendaciones";
    }

    public String goSitios() {
	setSitios(grSitioImpl.getSitios(false));
	return "/vistas/gr/sitios";
    }

    public String goSituaciones() {
	int tipo = Integer.parseInt(FacesUtilsBean.getRequestParameter("tipoArchivo"));
	setSituaciones(grArchivoImpl.getArchivos(tipo, false));
	return "/vistas/gr/situaciones";
    }

    public String goMensaje() {
	popUpGRBean.setDirectorioArchivos("GR/Mensajes/");
	return "/vistas/gr/mensaje";
    }

    public String goAutorizar() {
	popUpGRBean.setDirectorioArchivos("GR/Autorizar/");	
        sesionBean.actualizarRutasPausaGerente();
	return "/vistas/gr/autorizarViaje";
    }

    public boolean isResponsable() {
	return this.sesionBean.isResponsable();
    }

    /**
     * @return the admin
     */
    public boolean isAdmin() {
	return this.sesionBean.isAdmin();
    }

    public boolean isGrvia() {
	return this.sesionBean.isGrVia();
    }

    /**
     * @return the mapasGPS
     */
    public List<GrMapaGPSVO> getMapasGPS() {
	return mapasGPS;
    }

    /**
     * @param mapasGPS the mapasGPS to set
     */
    public void setMapasGPS(List<GrMapaGPSVO> mapasGPS) {
	this.mapasGPS = mapasGPS;
    }

    /**
     * @return the origenGPS
     */
    public List<GrMapaGPSVO> getOrigenGPS() {
	return origenGPS;
    }

    /**
     * @param origenGPS the origenGPS to set
     */
    public void setOrigenGPS(List<GrMapaGPSVO> origenGPS) {
	this.origenGPS = origenGPS;
    }

    /**
     * @return the destinoGPS
     */
    public List<GrMapaGPSVO> getDestinoGPS() {
	return destinoGPS;
    }

    /**
     * @param destinoGPS the destinoGPS to set
     */
    public void setDestinoGPS(List<GrMapaGPSVO> destinoGPS) {
	this.destinoGPS = destinoGPS;
    }

    public String loadMarkerData() {
	if (this.getMapasGPS() != null && this.getMapasGPS().size() > 0) {
	    return gson.toJson(this.getMapasGPS());
	} else {
	    return gson.toJson("");
	}

    }

    public String loadMarkerDataOrg() {
	if (this.getOrigenGPS() != null && this.getOrigenGPS().size() > 0) {
	    return gson.toJson(this.getOrigenGPS());
	} else {
	    return gson.toJson("");
	}
    }

    public String loadMarkerDataDes() {
	if (this.getDestinoGPS() != null && this.getDestinoGPS().size() > 0) {
	    return gson.toJson(this.getDestinoGPS());
	} else {
	    return gson.toJson("");
	}
    }

    /**
     * @return the rutasPausa
     */
//    public List<RutaTerrestreVo> getRutasPausaGerente() {
//        return this.sesionBean.getRutasPausaGerente();
//    }
//    public void goAddComentario(ActionEvent actionEvent) {
//        try {
//            int idNoticiaAux = Integer.parseInt(FacesUtilsBean.getRequestParameter("idNoticia"));
//            setIdNoticia(idNoticiaAux);
//            String metodo = ";abrirDialogoAddComentario();";
//            JavascriptContext.addJavascriptCall(FacesContext.getCurrentInstance(), metodo);
//        } catch (Exception e) {
//            UtilLog4j.log.fatal(this, e);
//            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
//        }
//    }
    /**
     * @return the idNoticia
     */
    public int getIdNoticia() {
	return idNoticia;
    }

    /**
     * @param idNoticia the idNoticia to set
     */
    public void setIdNoticia(int idNoticia) {
	this.idNoticia = idNoticia;
    }

}
