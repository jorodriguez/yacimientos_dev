/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.vo;

import java.util.List;
import java.util.Map;
import sia.modelo.campo.vo.CampoVo;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author b75ckd35th
 */
public class SiModuloVo extends Vo {

    private String ruta;
    private String icono;
    private String rutaServlet;
    private String toolTip;
    private String extraLinkRender;
    private String bgIcono;
    private String bgCuerpo;   
    private long pendiente;
    private List<CampoVo> listaCampo;
    private Map<String, List<SiOpcionVo>> mapOpcion;

    /**
     * @return the ruta
     */
    public String getRuta() {
	return ruta;
    }

    /**
     * @param ruta the ruta to set
     */
    public void setRuta(String ruta) {
	this.ruta = ruta;
    }

    
    public String toString() {
	return "SiModuloVo{" + "ruta=" + ruta + '}';
    }

    /**
     * @return the icono
     */
    public String getIcono() {
	return icono;
    }

    /**
     * @param icono the icono to set
     */
    public void setIcono(String icono) {
	this.icono = icono;
    }

    /**
     * @return the rutaServlet
     */
    public String getRutaServlet() {
	return rutaServlet;
    }

    /**
     * @param rutaServlet the rutaServlet to set
     */
    public void setRutaServlet(String rutaServlet) {
	this.rutaServlet = rutaServlet;
    }

    /**
     * @return the toolTip
     */
    public String getToolTip() {
	return toolTip;
    }

    /**
     * @param toolTip the toolTip to set
     */
    public void setToolTip(String toolTip) {
	this.toolTip = toolTip;
    }

    /**
     * @return the extraLinkRender
     */
    public String getExtraLinkRender() {
	return extraLinkRender;
    }

    /**
     * @param extraLinkRender the extraLinkRender to set
     */
    public void setExtraLinkRender(String extraLinkRender) {
	if (this.rutaServlet != null) {
	    this.extraLinkRender = "#{usuarioBean.agregarUsuario(event)}";
	}
	//this.extraLinkRender = extraLinkRender;
    }

    /**
     * @return the pendiente
     */
    public long getPendiente() {
	return pendiente;
    }

    /**
     * @param pendiente the pendiente to set
     */
    public void setPendiente(long pendiente) {
	this.pendiente = pendiente;
    }

    /**
     * @return the listaCampo
     */
    public List<CampoVo> getListaCampo() {
        return listaCampo;
    }

    /**
     * @param listaCampo the listaCampo to set
     */
    public void setListaCampo(List<CampoVo> listaCampo) {
        this.listaCampo = listaCampo;
    }

    /**
     * @return the mapOpcion
     */
    public Map<String, List<SiOpcionVo>> getMapOpcion() {
        return mapOpcion;
    }

    /**
     * @param mapOpcion the mapOpcion to set
     */
    public void setMapOpcion(Map<String, List<SiOpcionVo>> mapOpcion) {
        this.mapOpcion = mapOpcion;
    }

    public String getBgCuerpo() {
        return bgCuerpo;
    }

    public void setBgCuerpo(String bgCuerpo) {
        this.bgCuerpo = bgCuerpo;
    }

    public String getBgIcono() {
        return bgIcono;
    }

    public void setBgIcono(String bgIcono) {
        this.bgIcono = bgIcono;
    }

}
