/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.contrato.bean.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import sia.ihsa.contratos.Sesion;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.servicios.convenio.impl.ConvenioImpl;

/**
 *
 * @author ihsa
 */
@Named(value = "reporteBean")
@ViewScoped
public class ReporteModel implements Serializable {

    static final long serialVersionUID = 1;

    /**
     * Creates a new instance of ReporteModel
     */
    public ReporteModel() {
    }

    @Inject
    Sesion sesion;
    @Inject
    private ConvenioImpl convenioImpl;

    private ContratoVO contratoVO;
    private List<ContratoVO> listaContratos;
    private List<ContratoVO> listaContactos;

    private final Map<String, List> lista = new HashMap<>();

//Reporte
    public void traerReporteFechas() {
        setListaContratos(convenioImpl.getContratoPorFecha(contratoVO.getFechaInicio(), contratoVO.getFechaVencimiento(), sesion.getUsuarioSesion().getIdCampo()));
    }

    @PostConstruct
    public void iniciar() {

        contratoVO = new ContratoVO();
    }

    public List getTraerGerencia() {
        List<SelectItem> listaGerencia = new ArrayList<>();
        try {
            for (Object obj : getLista().get("gerencias")) {
                GerenciaVo cg = (GerenciaVo) obj;
                SelectItem item = new SelectItem(cg.getId(), cg.getNombre());
                listaGerencia.add(item);
            }
            return listaGerencia;
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    /**
     * @return the contratoVO
     */
    public ContratoVO getContratoVO() {
        return contratoVO;
    }

    /**
     * @param contratoVO the contratoVO to set
     */
    public void setContratoVO(ContratoVO contratoVO) {
        this.contratoVO = contratoVO;
    }

    /**
     * @return the listaContratos
     */
    public List<ContratoVO> getListaContratos() {
        return listaContratos;
    }

    /**
     * @return the lista
     */
    public Map<String, List> getLista() {
        return lista;
    }

    /**
     * @param listaContratos the listaContratos to set
     */
    public void setListaContratos(List<ContratoVO> listaContratos) {
        this.listaContratos = listaContratos;
    }

    /**
     * @return the listaContactos
     */
    public List<ContratoVO> getListaContactos() {
        return listaContactos;
    }

    /**
     * @param listaContactos the listaContactos to set
     */
    public void setListaContactos(List<ContratoVO> listaContactos) {
        this.listaContactos = listaContactos;
    }

}
