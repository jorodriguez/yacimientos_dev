/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package sia.contrato.bean.backing;

import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import sia.constantes.Constantes;
import sia.contrato.bean.soporte.FacesUtils;
import sia.ihsa.contratos.Sesion;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.contrato.vo.FiltroVo;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.vo.StatusVO;
import sia.servicios.catalogos.impl.EstatusImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.convenio.impl.ConvenioImpl;
import sia.servicios.sistema.vo.MonedaVO;

/**
 *
 * @author mluis
 */
@Named(value = "exportarContactosBean")
@ViewScoped
public class ExportarContactosBean implements Serializable {

    /**
     * Creates a new instance of ExportarContactosBean
     */
    public ExportarContactosBean() {
    }

    @Inject
    Sesion sesion;
    //  private FiltroVo filtroVo;
    @Getter
    @Setter
    private List<FiltroVo> listaFiltro;
    @Getter
    @Setter
    private FiltroVo filtroVo; //
    //   private int indice;
    @Inject
    private EstatusImpl estatusImpl;
    @Inject
    private ConvenioImpl convenioImpl;
    @Inject
    private GerenciaImpl gerenciaImpl;
    @Inject
    private MonedaImpl monedaImpl;

    @Getter
    @Setter
    private boolean conConts = true;
    @Getter
    @Setter
    private boolean conRLs = false;
    @Getter
    @Setter
    private boolean conRTs = false;
    //
    @Getter
    @Setter
    private List<ContratoVO> listaContactosFormato;

    @PostConstruct
    public void iniciar() {
        listaFiltro = new ArrayList<>();
        listaContactosFormato = new ArrayList<>();
        iniciarLista();
        //
        //llenarContactos();
    }

    private void llenarContactos() {
        listaContactosFormato = convenioImpl.traerContratosBusquedaContactos(listaFiltro,
                sesion.getUsuarioSesion().getId(), sesion.getIdRol(), sesion.getUsuarioSesion().getIdCampo(),
                this.conConts, this.conRLs, this.conRTs);
    }

    private void iniciarLista() {
        filtroVo = new FiltroVo();
        setListaFiltro(new ArrayList<>());
        filtroVo.setId(Constantes.CERO);
        filtroVo.setCampos(new ArrayList<>());
        // filtroVo.getCampos().add("");
        filtroVo.getCampos().add("Proveedor");
        filtroVo.getCampos().add("Estado");
        filtroVo.getCampos().add("Gerencia");
        filtroVo.getCampos().add("Monto");
        filtroVo.getCampos().add("Vencimiento");
        filtroVo.getCampos().add("Número");
        filtroVo.getCampos().add("Nombre");
        //
        filtroVo.setOperadorRelacional(new ArrayList<>());
        filtroVo.getOperadorRelacional().add("Contiene ");
        filtroVo.setCampoSeleccionado("");
        //
        filtroVo.setFiltroCaja(true);
        listaFiltro.add(filtroVo.getId(), filtroVo);
    }

    public void limpiarBusqueda() {
        iniciarLista();
        llenarContactos();
    }

    public void buscarAvanzadoContactos() {
        if (listaFiltro.get(Constantes.CERO).getCampoSeleccionado() != null
                && !listaFiltro.get(Constantes.CERO).getCampoSeleccionado().isEmpty()) {
            llenarContactos();
        }
    }

    public void agregarOperadorLogicoCondiciones(int index, String opLogSel) {
        listaFiltro.get(index).setOperadorLogicoSeleccionado(opLogSel);
    }

    public void mostrarCondiciones(int index, String opLogSel) {
//	JavascriptContext.addJavascriptCall(FacesContext.getCurrentInstance(),
//		";ejecutarFiltros('frmFiltroBuscar','btnPasarParametro" + getIndice() + "');");
        //
        filtroVo = listaFiltro.get(index);
        filtroVo.setCampoSeleccionado(opLogSel);
        if (filtroVo.getCampoSeleccionado().equals("")) {
            filtroVo.setFiltroCaja(true);
            filtroVo.setFiltroCombo(false);
            filtroVo.setFiltroFecha(false);
        } else if (filtroVo.getCampoSeleccionado().equals("Vencimiento")) {
            // mostrar fecha
            filtroVo.setFiltroCombo(false);
            filtroVo.setFiltroFecha(true);
            filtroVo.setFiltroCaja(false);
            filtroVo.setOperadorRelacional(new ArrayList<>());
            //
            filtroVo.getOperadorRelacional().add("Igual a ");
            filtroVo.getOperadorRelacional().add("Menor a ");
            filtroVo.getOperadorRelacional().add("Menor igual a ");
            filtroVo.getOperadorRelacional().add("Mayor  a ");
            filtroVo.getOperadorRelacional().add("Mayor igual a ");
            filtroVo.getOperadorRelacional().add("Entre ");
        } else if (filtroVo.getCampoSeleccionado().equals("Estado")) {
            // mostrar combo
            filtroVo.setListaEstatus(new ArrayList<>());
            filtroVo.setListaEstatus(listaEstatus());
            //
            filtroVo.setFiltroCombo(true);
            filtroVo.setFiltroFechaRango(false);
            filtroVo.setFiltroFecha(false);
            filtroVo.setFiltroCaja(false);
            filtroVo.setOperadorRelacional(new ArrayList<>());
            //
            filtroVo.getOperadorRelacional().add("Igual a ");
            filtroVo.getOperadorRelacional().add("Menor a ");
            filtroVo.getOperadorRelacional().add("Menor igual a ");
            filtroVo.getOperadorRelacional().add("Mayor  a ");
            filtroVo.getOperadorRelacional().add("Mayor igual a ");
            //	    llenar los estados
        } else if (filtroVo.getCampoSeleccionado().equals("Gerencia")) {
            // mostrar combo
            filtroVo.setListaGerencia(new ArrayList<>());
            filtroVo.setListaGerencia(listaGererencia());
            //
            filtroVo.setFiltroCombo(true);
            filtroVo.setFiltroFecha(false);
            filtroVo.setFiltroCaja(false);
            filtroVo.setFiltroFechaRango(false);
            filtroVo.setOperadorRelacional(new ArrayList<>());
            //
            filtroVo.getOperadorRelacional().add("Igual a ");
        } else if (filtroVo.getCampoSeleccionado().equals("Monto")) {
            filtroVo.setListaMoneda(new ArrayList<>());
            filtroVo.setListaMoneda(listaMoneda());
            filtroVo.setFiltroCombo(false);
            filtroVo.setFiltroFecha(false);
            filtroVo.setFiltroCaja(true);
            filtroVo.setFiltroMoneda(true);
            filtroVo.setFiltroFechaRango(false);
            filtroVo.setOperadorRelacional(new ArrayList<>());
            //
            filtroVo.getOperadorRelacional().add("Igual a ");
            filtroVo.getOperadorRelacional().add("Menor a ");
            filtroVo.getOperadorRelacional().add("Menor igual a ");
            filtroVo.getOperadorRelacional().add("Mayor  a ");
            filtroVo.getOperadorRelacional().add("Mayor igual a ");
        } else {
            // mostrar caja y operador cadena
            filtroVo.setOperadorRelacional(new ArrayList<>());
            //
            filtroVo.setFiltroFechaRango(false);
            filtroVo.setFiltroCaja(true);
            filtroVo.setFiltroCombo(false);
            filtroVo.setFiltroFecha(false);
            filtroVo.getOperadorRelacional().add("Contiene ");
        }
        //filtroVo.setOperadorRelacional(new ArrayList<>());
        //
        filtroVo.setOperadorRelacionalCadena(new ArrayList<>());
//	filtroVo.getOperadorRelacionalCadena().add("Contiene ");
        ///
        listaFiltro.set(filtroVo.getId(), filtroVo);
        //setIndice(Constantes.MENOS_UNO);
    }

    public void seleccionarOperadorRelacional(int index, String opRelSel) {
        filtroVo = listaFiltro.get(index);
        filtroVo.setOperadorRelacionalSeleccionado(opRelSel);
        if (filtroVo.getCampoSeleccionado().equals("Vencimiento")) {
            if (filtroVo.getOperadorRelacionalSeleccionado().trim().equals("Entre")) {
                filtroVo.setFiltroFechaRango(true);
                filtroVo.setFiltroFecha(false);
            } else {
                filtroVo.setFiltroFechaRango(false);
                filtroVo.setFiltroFecha(true);
            }

        } else {
            filtroVo.setFiltroFechaRango(false);
        }
        listaFiltro.set(filtroVo.getId(), filtroVo);
    }

    private List<SelectItem> listaEstatus() {
        List<StatusVO> le = estatusImpl.traerPorTipo("CON");
        List<SelectItem> lseItems = new ArrayList<>();
        for (StatusVO le1 : le) {
            lseItems.add(new SelectItem(le1.getIdStatus(), le1.getNombre()));
        }
        return lseItems;
    }

    private List<SelectItem> listaGererencia() {
        List<GerenciaVo> le = gerenciaImpl.traerGerenciaActiva(sesion.getUsuarioSesion().getRfcEmpresa(), sesion.getUsuarioSesion().getIdCampo());
        // siURGerenciaLocal.traerGerenciaPorRolUsuario(sesion.getIdRol(), sesion.getUsuarioSesion().getId());
        List<SelectItem> lseItems = new ArrayList<>();
        for (GerenciaVo le1 : le) {
            lseItems.add(new SelectItem(le1.getId(), le1.getNombre()));
        }
        return lseItems;
    }

    private List<SelectItem> listaMoneda() {
        List<MonedaVO> le = monedaImpl.traerMonedaActiva(sesion.getUsuarioSesion().getIdCampo());
        List<SelectItem> lseItems = new ArrayList<>();
        lseItems.add(new SelectItem(-1, "Todas"));
        for (MonedaVO le1 : le) {
            lseItems.add(new SelectItem(le1.getId(), le1.getSiglas()));
        }
        return lseItems;
    }

    public void agregarNuevoFiltro() {
        filtroVo = new FiltroVo();
        filtroVo.setId(listaFiltro.size());
        filtroVo.setCampos(new ArrayList<>());
        //filtroVo.getCampos().add("");
        filtroVo.getCampos().add("Proveedor");
        filtroVo.getCampos().add("Estado");
        filtroVo.getCampos().add("Gerencia");
        filtroVo.getCampos().add("Monto");
        filtroVo.getCampos().add("Vencimiento");
        filtroVo.getCampos().add("Número");
        filtroVo.getCampos().add("Nombre");
//
        filtroVo.setOperadorLogico(new ArrayList<>());
        filtroVo.getOperadorLogico().add(" Y ");
        filtroVo.getOperadorLogico().add(" O ");
        //
        filtroVo.setOperadorRelacional(new ArrayList<>());
        filtroVo.getOperadorRelacional().add("Contiene ");
        //
        filtroVo.setFiltroCaja(true);
        listaFiltro.add(filtroVo.getId(), filtroVo);
    }

    public void quitarFiltro(int id) {
        //
        reasiganarId(listaFiltro, id);
        // llenar la lista
        if (listaFiltro.size() > 1) {
            llenarContactos();
        }
    }

    public void quitarFiltroContacto(int id) {
        reasiganarId(listaFiltro, id);
    }

    private void reasiganarId(List<FiltroVo> filtros, int idBorrado) {
        listaFiltro.remove(filtros.get(idBorrado));
        for (int i = idBorrado; i < filtros.size(); i++) {
            FiltroVo filtro = filtros.get(i);
            filtro.setId(i);
            listaFiltro.set(i, filtro);
        }
    }
}
