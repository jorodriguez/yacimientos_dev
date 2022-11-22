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
import sia.contrato.bean.soporte.FacesUtils;
import sia.ihsa.contratos.Sesion;
import sia.modelo.Convenio;
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
@Named(value = "agregarContratoBean")
@ViewScoped
public class AgregarContratoBean implements Serializable {

    static final long serialVersionUID = 1;

    public AgregarContratoBean() {
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
    private List<SelectItem> listaContratos;
    @Setter
    @Getter
    private List<GerenciaVo> gerencias;
    @Setter
    @Getter
    private List<GerenciaVo> gerenciasSeleccionadas;
    @Setter
    @Getter
    private Map<String, List<SelectItem>> mapaSelectItem = new HashMap<>();
    @Setter
    @Getter
    private List<SelectItem> categoriasPrin;
    @Setter
    @Getter
    private List<SelectItem> categoriasSec;
    @Setter
    @Getter
    private List<SelectItem> categoriasTer;
    @Setter
    @Getter
    private List<SelectItem> categoriasCuart;
    @Setter
    @Getter
    private List<SelectItem> categoriasQuin;
    @Setter
    @Getter
    private int id;
    @Setter
    @Getter
    private int idS;
    @Setter
    @Getter
    private int idT;
    @Setter
    @Getter
    private int idC;
    @Setter
    @Getter
    private int idCi;
    //

    @Setter
    @Getter
    private String proveedorSeleccionado;

    //
    //
    @PostConstruct
    public void iniciar() {
        categoriasPrin = new ArrayList<>();
        categoriasSec = new ArrayList<>();
        categoriasTer = new ArrayList<>();
        categoriasCuart = new ArrayList<>();
        categoriasQuin = new ArrayList<>();
        gerencias = new ArrayList<>();
        gerenciasSeleccionadas = new ArrayList<>();
        llenarMapa();
        listaContratos = new ArrayList<>();
        proveedorVo = new ProveedorVo();
        contratoVO = new ContratoVO();
        id = 0;
    }

    public void traerContratoMaestroPorProveedor() {
        String[] cad = proveedorSeleccionado.split("/");
        proveedorVo = proveedorImpl.traerProveedorPorRFC(cad[0].trim());
        List<ContratoVO> cts = convenioServicioRemoto.traerConvenioMaestroPorRfcProveedor(cad[0].trim(), sesion.getUsuarioSesion().getIdCampo());
        listaContratos.clear();
        cts.stream().forEach(ct -> {
            listaContratos.add(new SelectItem(ct.getId(), ct.getNombre()));
        });
    }

    private void llenarMapa() {
        llenarGerencia(sesion.getUsuarioSesion().getIdCampo());
        //
        List<SelectItem> tcits = new ArrayList<>();
        cvTipoServicioRemoto.traerTodo().stream().forEach(tc -> {
            tcits.add(new SelectItem(tc.getId(), tc.getNombre()));
        });
        List<SelectItem> tcsts = new ArrayList<>();
        estatusServicioRemoto.traerPorTipo(Constantes.ESTATUS_COMPROBANTE_CONV).stream().forEach(tc -> {
            tcsts.add(new SelectItem(tc.getIdStatus(), tc.getNombre()));
        });
        List<SelectItem> tccmps = new ArrayList<>();
        apCampoUsuarioRhPuestoImpl.getAllPorUsurio(sesion.getUsuarioSesion().getId()).stream().forEach(tc -> {
            tccmps.add(new SelectItem(tc.getIdCampo(), tc.getCampo()));
        });
        cvClasificacionImpl.traerClasificacionPrincipal().stream().forEach(tc -> {
            categoriasPrin.add(new SelectItem(tc.getId(), tc.getNombre()));
        });

        mapaSelectItem.put("tipos", tcits);
        mapaSelectItem.put("estados", tcsts);
        mapaSelectItem.put("campos", tccmps);
        //
    }

    public List<String> completaProveedor(String query) {
        return proveedorImpl.traerRfcNombreLikeProveedorQueryNativo(query, sesion.getUsuarioSesion().getRfcEmpresa(), ProveedorEnum.ACTIVO.getId());

    }

    public void addGerencia(GerenciaVo ger) {
        gerenciasSeleccionadas.add(ger);
        gerencias.remove(ger);

    }

    public void quitarGerencia(GerenciaVo gerenciaVo) {
        gerenciaVo.setSelected(Boolean.FALSE);
        gerencias.add(gerenciaVo);
        gerenciasSeleccionadas.remove(gerenciaVo);
    }

    private void llenarGerencia(int campo) {
        gerencias = gerenciaImpl.traerGerenciaActivaPorCampo(campo);
    }

    public Convenio buscarContratoPorNumero() {
        return convenioServicioRemoto.buscarContratoPorNumero(contratoVO.getNumero());
    }

    public void cabiarClasificacion(int idCp) {
        List<ClasificacionVo> sub = llenarSub(idCp);
        categoriasSec.clear();
        categoriasTer.clear();
        categoriasCuart.clear();
        categoriasQuin.clear();
        idS = 0;
        idT = 0;
        idC = 0;
        idCi = 0;
        sub.stream().forEach(c -> {
            try {
                categoriasSec.add(new SelectItem(c.getId(), c.getNombre()));
            } catch (Exception e) {
                e.getMessage();
            }
        });
    }

    public void cabiarClasificacionDos(int idCs) {
        List<ClasificacionVo> sub = llenarSub(idCs);
        categoriasTer.clear();
        categoriasCuart.clear();
        categoriasQuin.clear();
        idT = 0;
        idC = 0;
        idCi = 0;
        sub.stream().forEach(c -> {
            try {
                categoriasTer.add(new SelectItem(c.getId(), c.getNombre()));
            } catch (Exception e) {
                e.getMessage();
            }
        });
    }

    public void cabiarClasificacionTres(int idTer) {
        List<ClasificacionVo> sub = llenarSub(idTer);
        categoriasCuart.clear();
        categoriasQuin.clear();
        idC = 0;
        idCi = 0;
        sub.stream().forEach(c -> {
            try {
                categoriasCuart.add(new SelectItem(c.getId(), c.getNombre()));
            } catch (Exception e) {
                e.getMessage();
            }
        });
    }

    public void cabiarClasificacionCuatro(int idCuar) {
        List<ClasificacionVo> sub = llenarSub(idCuar);
        categoriasQuin.clear();
        idCi = 0;
        sub.stream().forEach(c -> {
            try {
                categoriasQuin.add(new SelectItem(c.getId(), c.getNombre()));
            } catch (Exception e) {
                e.getMessage();
            }
        });
    }

    public boolean buscarProveedorPorId() {
        try {
            return pvProveedorCompaniaImpl.buscarRelacionProveedorCompania(contratoVO.getProveedor(), sesion.getRfcEmpresa());
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            return false;
        }
    }

    public String guardarContrato() {
        String pagina = "";
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

            if (proveedorVo.getIdProveedor() > 0) {
                if (!listaEntero.isEmpty()) {
                    if (!contratoVO.getNombre().trim().isEmpty()) {
                        if (!gerenciasSeleccionadas.isEmpty()) {
                            gerenciasSeleccionadas.stream().forEach(gs -> {
                                contratoVO.getListaGerencia().add(gs);
                            });
                            contratoVO.setProveedorVo(proveedorVo);
                            contratoVO.setProveedor(proveedorVo.getIdProveedor());
                            contratoVO.setIdCampo(sesion.getUsuarioSesion().getIdCampo());
                            //
                            convenioServicioRemoto.guardar(sesion.getUsuarioSesion().getId(), contratoVO, listaEntero);
                            //
                            proveedorVo = new ProveedorVo();
                            contratoVO = new ContratoVO();
                            gerenciasSeleccionadas.clear();
                            FacesUtils.addErrorMessage("Se registró el contrato.");
                            //
                            pagina = "/vistas/contrato/admin/inicio.xhtml?faces-redirect=true";
                        } else {
                            FacesUtils.addErrorMessage("Selecciones al menos una gerencia.");
                        }
                    } else {
                        FacesUtils.addErrorMessage("Agregue el nombre u objetivo del contrato.");
                    }
                } else {
                    FacesUtils.addErrorMessage("Seleccione la clasificación.");
                }
            } else {
                FacesUtils.addErrorMessage("Seleccione el proveedor.");
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }

        return pagina;
    }

    private List<ClasificacionVo> llenarSub(int idClas) {
        return cvClasificacionImpl.traerPorClasificacion(idClas);
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
    public List<SelectItem> getListaContratos() {
        return listaContratos;
    }

    /**
     * @param listaContratos the listaContratos to set
     */
    public void setListaContratos(List<SelectItem> listaContratos) {
        this.listaContratos = listaContratos;
    }

}
