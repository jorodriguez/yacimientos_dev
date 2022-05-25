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
import lombok.Getter;
import lombok.Setter;
import sia.constantes.Constantes;
import sia.ihsa.contratos.Sesion;
import sia.modelo.Convenio;
import sia.modelo.Proveedor;
import sia.modelo.contrato.vo.ClasificacionVo;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.proveedor.Vo.ProveedorVo;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.EstatusImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.convenio.impl.ConvenioImpl;
import sia.servicios.convenio.impl.CvClasificacionImpl;
import sia.servicios.convenio.impl.CvTipoImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.proveedor.impl.PvProveedorCompaniaImpl;
import sia.util.ProveedorEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
//@ViewScoped
@Named(value = "agregarContratoBean")
@ViewScoped
public class AgregarContratoModel implements Serializable {

    static final long serialVersionUID = 1;

    /**
     * Creates a new instance of AgregarContratoModel
     */
    public AgregarContratoModel() {
        contratoVO = new ContratoVO();
    }

    @Inject
    private Sesion sesion;
    //
    @Inject
    private CvClasificacionImpl cvClasificacionImpl;
    @Inject
    private EstatusImpl estatusServicioRemoto;
    @Inject
    private CvTipoImpl cvTipoServicioRemoto;
    @Inject
    private ConvenioImpl convenioServicioRemoto;
    @Inject
    private ProveedorServicioImpl proveedorImpl;
    @Inject
    private PvProveedorCompaniaImpl pvProveedorCompaniaImpl;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    private GerenciaImpl gerenciaImpl;

    private ContratoVO contratoVO;
    @Setter
    @Getter
    private ProveedorVo proveedorVo;
    private List<ContratoVO> listaContratos;
    private final Map<String, List> mapaSelectItem = new HashMap<>();
    private final Map<Integer, List> mapaCategorias = new HashMap<>();
    private int id, idS, idT, idC, idCi;
    //
    private int indice;

    @Setter
    @Getter
    private String proveedorSeleccionado;

    //
    //
    @PostConstruct
    public void iniciar() {
        llenarMapa();
        proveedorVo = new ProveedorVo();
    }

    public void traerContratoMaestroPorProveedor() {

        String[] cad = proveedorSeleccionado.split("/");

        setListaContratos(convenioServicioRemoto.traerConvenioMaestroPorRfcProveedor(cad[0], sesion.getUsuarioSesion().getIdCampo()));
    }

    public void llenarMapa() {
        llenarGerencia(sesion.getUsuarioSesion().getIdCampo());
        mapaSelectItem.put("tipos", cvTipoServicioRemoto.traerTodo());
        mapaSelectItem.put("estados", estatusServicioRemoto.traerPorTipo(Constantes.ESTATUS_COMPROBANTE_CONV));
        mapaSelectItem.put("campos", apCampoUsuarioRhPuestoImpl.getAllPorUsurio(sesion.getUsuarioSesion().getId()));
        mapaSelectItem.put("clasificaciones", cvClasificacionImpl.traerClasificacionPrincipal());
    }

    public List<String> completaProveedor(String query) {
        return proveedorImpl.traerRfcNombreLikeProveedorQueryNativo(query, sesion.getUsuarioSesion().getRfcEmpresa(), ProveedorEnum.ACTIVO.getId());

    }

    public void llenarGerencia(int campo) {
        mapaSelectItem.put("gerencias", gerenciaImpl.traerGerenciaActivaPorCampo(campo));
    }

    public Convenio buscarContratoPorNumero() {
        return convenioServicioRemoto.buscarContratoPorNumero(contratoVO.getNumero());
    }

    public void cabiarClasificacion(int id) {
        llenarSub(id);
    }

    public void cabiarClasificacionDos(int idS) {
        llenarSub(idS);
    }

    public void cabiarClasificacionTres(int id3) {
        llenarSub(id3);
    }

    public void cabiarClasificacionCuatro(int id4) {
        llenarSub(id4);
    }

    public List getTraerClasificacion2() {
        if (getId() > 0) {
            return llenarSub(getId());
        }
        return null;
    }

    public List getTraerClasificacion3() {
        if (getIdS() > 0) {
            return llenarSub(getIdS());
        }
        return null;
    }

    public List getTraerClasificacion4() {
        if (getIdT() > 0) {
            return llenarSub(getIdT());
        }
        return null;
    }

    public List getTraerClasificacion5() {
        if (getIdC() > 0) {
            return llenarSub(getIdC());
        }
        return null;
    }

    public boolean buscarProveedorPorId() {
        try {
            return pvProveedorCompaniaImpl.buscarRelacionProveedorCompania(contratoVO.getProveedor(), sesion.getRfcEmpresa());
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            return false;
        }
    }

    public boolean guardarContrato() {
        boolean v = true;
        try {
            List listaEntero = new ArrayList<Integer>();
            listaEntero.add(id);
            if (idS > 0) {
                listaEntero.add(idS);
                if (idT > 0) {
                    listaEntero.add(idT);
                    if (idC > 0) {
                        listaEntero.add(idC);
                        if (idCi > 0) {
                            listaEntero.add(idCi);
                        }
                    }
                }
            }
            for (Object get : mapaSelectItem.get("gerencias")) {
                GerenciaVo gerenciaVo = (GerenciaVo) get;
                if (gerenciaVo.isSelected()) {
                    contratoVO.getListaGerencia().add(gerenciaVo);
                }
            }
            contratoVO.setIdCampo(sesion.getUsuarioSesion().getIdCampo());
            convenioServicioRemoto.guardar(sesion.getUsuarioSesion().getId(), contratoVO, listaEntero);
            setIndice(0);
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            v = false;
        }

        return v;
    }

    public List<SelectItem> llenarSub(int idClas) {
        mapaCategorias.put(idClas, cvClasificacionImpl.traerPorClasificacion(idClas));
        List<SelectItem> listaClasi = new ArrayList<>();
        try {
            for (Object obj : mapaCategorias.get(idClas)) {
                ClasificacionVo est = (ClasificacionVo) obj;
                SelectItem item = new SelectItem(est.getId(), est.getNombre());
                listaClasi.add(item);
            }
            return listaClasi;
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
     * @param listaContratos the listaContratos to set
     */
    public void setListaContratos(List<ContratoVO> listaContratos) {
        this.listaContratos = listaContratos;
    }

    /**
     * @return
     */
    public Map<String, List> getMapaSelectItem() {
        return mapaSelectItem;
    }

    /**
     * @return the indice
     */
    public int getIndice() {
        return indice;
    }

    /**
     * @param indice the indice to set
     */
    public void setIndice(int indice) {
        this.indice = indice;
    }

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
     * @return the mapaCategorias
     */
    public Map<Integer, List> getMapaCategorias() {
        return mapaCategorias;
    }

    /**
     * @return the idS
     */
    public int getIdS() {
        return idS;
    }

    /**
     * @param idS the idS to set
     */
    public void setIdS(int idS) {
        this.idS = idS;
    }

    /**
     * @return the idT
     */
    public int getIdT() {
        return idT;
    }

    /**
     * @param idT the idT to set
     */
    public void setIdT(int idT) {
        this.idT = idT;
    }

    /**
     * @return the idC
     */
    public int getIdC() {
        return idC;
    }

    /**
     * @param idC the idC to set
     */
    public void setIdC(int idC) {
        this.idC = idC;
    }

    /**
     * @return the idCi
     */
    public int getIdCi() {
        return idCi;
    }

    /**
     * @param idCi the idCi to set
     */
    public void setIdCi(int idCi) {
        this.idCi = idCi;
    }
}
