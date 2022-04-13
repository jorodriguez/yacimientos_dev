/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.gr.vo;

import java.util.Date;
import sia.modelo.sgl.semaforo.vo.SemaforoVo;
import sia.modelo.sgl.vo.AdjuntoVO;

/**
 *
 * @author ihsa
 */
public class GrArchivoVO {

    private int id;
    private int siAdjunto;
    private AdjuntoVO siAdjuntoVO;
    private int sgSemaforo;
    private SemaforoVo sgSemaforoVO;
    private int grTipoArchivo;
    private GrTipoArchivoVO grTipoArchivoVO;
    private int grMapa;
    private MapaVO grMapaVO;
    private Date fechaGenero;
    private Date horaGenero;
    private String titulo;
    private boolean activo;
    private boolean visible;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the siAdjunto
     */
    public int getSiAdjunto() {
        return siAdjunto;
    }

    /**
     * @param siAdjunto the siAdjunto to set
     */
    public void setSiAdjunto(int siAdjunto) {
        this.siAdjunto = siAdjunto;
    }

    /**
     * @return the siAdjuntoVO
     */
    public AdjuntoVO getSiAdjuntoVO() {
        return siAdjuntoVO;
    }

    /**
     * @param siAdjuntoVO the siAdjuntoVO to set
     */
    public void setSiAdjuntoVO(AdjuntoVO siAdjuntoVO) {
        this.siAdjuntoVO = siAdjuntoVO;
    }

    /**
     * @return the sgSemaforo
     */
    public int getSgSemaforo() {
        return sgSemaforo;
    }

    /**
     * @param sgSemaforo the sgSemaforo to set
     */
    public void setSgSemaforo(int sgSemaforo) {
        this.sgSemaforo = sgSemaforo;
    }

    /**
     * @return the sgSemaforoVO
     */
    public SemaforoVo getSgSemaforoVO() {
        return sgSemaforoVO;
    }

    /**
     * @param sgSemaforoVO the sgSemaforoVO to set
     */
    public void setSgSemaforoVO(SemaforoVo sgSemaforoVO) {
        this.sgSemaforoVO = sgSemaforoVO;
    }

    /**
     * @return the grTipoArchivo
     */
    public int getGrTipoArchivo() {
        return grTipoArchivo;
    }

    /**
     * @param grTipoArchivo the grTipoArchivo to set
     */
    public void setGrTipoArchivo(int grTipoArchivo) {
        this.grTipoArchivo = grTipoArchivo;
    }

    /**
     * @return the grTipoArchivoVO
     */
    public GrTipoArchivoVO getGrTipoArchivoVO() {
        return grTipoArchivoVO;
    }

    /**
     * @param grTipoArchivoVO the grTipoArchivoVO to set
     */
    public void setGrTipoArchivoVO(GrTipoArchivoVO grTipoArchivoVO) {
        this.grTipoArchivoVO = grTipoArchivoVO;
    }

    /**
     * @return the grMapa
     */
    public int getGrMapa() {
        return grMapa;
    }

    /**
     * @param grMapa the grMapa to set
     */
    public void setGrMapa(int grMapa) {
        this.grMapa = grMapa;
    }

    /**
     * @return the grMapaVO
     */
    public MapaVO getGrMapaVO() {
        return grMapaVO;
    }

    /**
     * @param grMapaVO the grMapaVO to set
     */
    public void setGrMapaVO(MapaVO grMapaVO) {
        this.grMapaVO = grMapaVO;
    }

    /**
     * @return the fechaGenero
     */
    public Date getFechaGenero() {
        return fechaGenero;
    }

    /**
     * @param fechaGenero the fechaGenero to set
     */
    public void setFechaGenero(Date fechaGenero) {
        this.fechaGenero = fechaGenero;
    }

    /**
     * @return the horaGenero
     */
    public Date getHoraGenero() {
        return horaGenero;
    }

    /**
     * @param horaGenero the horaGenero to set
     */
    public void setHoraGenero(Date horaGenero) {
        this.horaGenero = horaGenero;
    }

    /**
     * @return the titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * @param titulo the titulo to set
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * @return the activo
     */
    public boolean isActivo() {
        return activo;
    }

    /**
     * @param activo the activo to set
     */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /**
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param visible the visible to set
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
