/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.bean.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;

import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import sia.catalogos.bean.backing.UsuarioBean;
import sia.constantes.Constantes;
import sia.modelo.AutorizacionesOrden;
import sia.modelo.Orden;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.campoVO.CampoOrden;
import sia.modelo.sgl.vo.RequisicionVO;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.orden.impl.AutorizacionesOrdenImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.requisicion.impl.RequisicionImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.sistema.bean.backing.Sesion;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Named
@ViewScoped
public class ModelDevolucionRequisicion implements Serializable {

    @Inject
    private Sesion sesion;
    @Inject
    private RequisicionImpl requisicionServicioRemoto;
    @Inject
    private OrdenImpl ordenServicioRemoto;
    @Inject
    private AutorizacionesOrdenImpl autorizacionesOrdenServicioRemoto;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private SiParametroImpl siParametroImpl;
    @Inject
    private SiAdjuntoImpl siAdjuntoImpl;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    //
    private RequisicionVO requisicionVO;
    //
    private String motivo;
    private boolean devPop = false;
    private String idAnalista;
    private int idBloque;
    private int idGerencia;

    /**
     * Creates a new instance of ModelDevolucionRequisicion
     */
    public ModelDevolucionRequisicion() {
    }

    @PostConstruct
    public void iniciar() {
        setIdBloque(sesion.getUsuarioVo().getIdCampo());
    }

    public List<SelectItem> listaCampo() {
        List<SelectItem> l = new ArrayList<SelectItem>();
        List<CampoUsuarioPuestoVo> lc;
        try {
            lc = apCampoUsuarioRhPuestoImpl.getAllPorUsurio(sesion.getUsuarioVo().getId());
            for (CampoUsuarioPuestoVo ca : lc) {
                SelectItem item = new SelectItem(ca.getIdCampo(), ca.getCampo());
                l.add(item);
            }
            return l;
        } catch (Exception e) {
            return null;
        }
    }

    //
    public int regresaBloqueSesion() {
        return sesion.getUsuarioVo().getIdCampo();
    }

    public RequisicionVO buscarRequisicion(String req) {
        return this.requisicionServicioRemoto.buscarPorConsecutivoBloque(req, getIdBloque(), true, false);
    }

    public boolean devolverSIARequisicion() throws Exception {
        return requisicionServicioRemoto.devolverSIARequisicion(sesion.getUsuarioVo().getId(), getRequisicionVO().getId(), getMotivo(), getIdAnalista());
    }

    public List<SelectItem> listaCampoPorUsuario() {
        List<CampoUsuarioPuestoVo> campos = sesion.getCamposPorUsuario();
        List<SelectItem> lista = new ArrayList<>();
        campos.stream().forEach(cu -> {
            lista.add(new SelectItem(cu.getIdCampo(), cu.getCampo()));
        });
        return lista;
    }

    /**
     * @return Lista de usuarios Que Colocan orden de compra y o servicio
     */
    public List<SelectItem> listaAnalista() {
        List<SelectItem> resultList = new ArrayList<SelectItem>();
        try {
            List<UsuarioVO> tempList = usuarioImpl.traerListaRolPrincipalUsuarioRolModulo(Constantes.ROL_COMPRADOR, Constantes.MODULO_COMPRA, getIdBloque());
            for (UsuarioVO lista : tempList) {
                SelectItem item = new SelectItem(lista.getId(), lista.getNombre());// esta linea es por si quiero agregar mas de un valoritem.setValue(Lista.getId());
                resultList.add(item);
                //}
                //}
            }
            return resultList;
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
        return resultList;
    }

    public List<Orden> buscarOrdenConReqJPA(Integer id) {
        return this.ordenServicioRemoto.getOrdenesPorRequisicionJPA(id);
    }

    public AutorizacionesOrden buscarAutoOrden(Integer id) {
        return this.autorizacionesOrdenServicioRemoto.buscarPorOrden(id);
    }

    public List<AutorizacionesOrden> buscarAutoOrden(List listOrden) {
        List<AutorizacionesOrden> lau = new ArrayList<>();
        for (int i = 0; i < listOrden.size(); i++) {
            lau.add(this.autorizacionesOrdenServicioRemoto.buscarPorOrden((int) listOrden.get(i)));
        }
        return lau;
    }

    /**
     * @return the motivo
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * @param motivo the motivo to set
     */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    /**
     * @return the devPop
     */
    public boolean isDevPop() {
        return devPop;
    }

    /**
     * @param devPop the devPop to set
     */
    public void setDevPop(boolean devPop) {
        this.devPop = devPop;
    }

    /**
     * @return the idAnalista
     */
    public String getIdAnalista() {
        return idAnalista;
    }

    /**
     * @param idAnalista the idAnalista to set
     */
    public void setIdAnalista(String idAnalista) {
        this.idAnalista = idAnalista;
    }

    /**
     * @return the idBloque
     */
    public int getIdBloque() {
        return idBloque;
    }

    /**
     * @param idBloque the idBloque to set
     */
    public void setIdBloque(int idBloque) {
        this.idBloque = idBloque;
    }

    /**
     * @return the requisicionVO
     */
    public RequisicionVO getRequisicionVO() {
        return requisicionVO;
    }

    /**
     * @param requisicionVO the requisicionVO to set
     */
    public void setRequisicionVO(RequisicionVO requisicionVO) {
        this.requisicionVO = requisicionVO;
    }

    /**
     * @return the idGerencia
     */
    public int getIdGerencia() {
        return idGerencia;
    }

    /**
     * @param idGerencia the idGerencia to set
     */
    public void setIdGerencia(int idGerencia) {
        this.idGerencia = idGerencia;
    }

}
