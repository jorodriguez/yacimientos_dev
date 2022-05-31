/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.contrato.bean.backing;

import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedProperty;


import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import sia.constantes.Constantes;
import sia.contrato.bean.soporte.FacesUtils;
import sia.ihsa.contratos.Sesion;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.contrato.vo.ExhortoVo;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.proveedor.Vo.ContactoProveedorVO;
import sia.modelo.usuario.vo.UsuarioResponsableGerenciaVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.campo.nuevo.impl.ApCampoGerenciaImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.convenio.impl.ConvenioImpl;
import sia.servicios.convenio.impl.CvConvenioExhortoImpl;
import sia.servicios.convenio.impl.CvConvenioGerenciaImpl;
import sia.servicios.proveedor.impl.ContactoProveedorImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;

/**
 *
 * @author mluis
 */
@Named(value  = "contratoFiniquitoBean")
@ViewScoped
public class ContratoFiniquitoBean implements Serializable {

    @Inject
    private Sesion sesion;
    //
    @Inject
    private ConvenioImpl convenioImpl;
    @Inject
    CvConvenioExhortoImpl convenioExhortoImpl;
    @Inject
    SiUsuarioRolImpl usuarioRolImpl;
    @Inject
    ContactoProveedorImpl contactoProveedorImpl;
    @Inject
    CvConvenioGerenciaImpl convenioGerenciaImpl;
    @Inject
    ApCampoGerenciaImpl campoGerenciaImpl;
    @Inject
    ApCampoUsuarioRhPuestoImpl campoUsuarioRhPuestoImpl;
    @Inject
    GerenciaImpl gerenciaImpl;
    //
    @Getter
    @Setter
    private List<ContratoVO> contratos;
    @Getter
    @Setter
    private List<ContactoProveedorVO> representanteLegalProveedor;
    @Getter
    @Setter
    private List<ContactoProveedorVO> contactosProveedor;
    @Getter
    @Setter
    private List<ContactoProveedorVO> listaCorreoPara;
    @Getter
    @Setter
    private List<ContactoProveedorVO> listaCorreoCopia;
    @Getter
    @Setter
    private ContactoProveedorVO contactoProveedorVo;
    @Getter
    @Setter
    private ExhortoVo exhortoVo;
    @Getter
    @Setter
    private ContratoVO contratoVo;
    @Getter
    @Setter
    private String motivo;
    @Getter
    @Setter
    private int idGerencia;
    @Getter
    @Setter
    private int idFiltro;
    @Getter
    @Setter
    private List<SelectItem> gerencias;
    @Getter
    @Setter
    private List<UsuarioVO> listaUsuarioGerencia;

    @PostConstruct
    public void iniciar() {
        idFiltro = 0;
        contactosProveedor = new ArrayList<ContactoProveedorVO>();
        listaCorreoPara = new ArrayList<ContactoProveedorVO>();
        representanteLegalProveedor = new ArrayList<ContactoProveedorVO>();
        contratos = new ArrayList<ContratoVO>();
        exhortoVo = new ExhortoVo();
        contratoVo = new ContratoVO();
        gerencias = new ArrayList<SelectItem>();
        listaUsuarioGerencia = new ArrayList<UsuarioVO>();
        llenarContratos();
        for (GerenciaVo ger : gerenciaImpl.traerGerenciaActivaPorCampo(sesion.getUsuarioSesion().getIdCampo())) {
            gerencias.add(new SelectItem(ger.getId(), ger.getNombre()));
        }
    }

    public void cambiarFiltro(AjaxBehaviorEvent event) {        
        switch (idFiltro) {
            case 1:
                contratos = convenioExhortoImpl.traerContratosPorVencer(new Date(), 30, sesion.getUsuarioSesion().getIdCampo());
                break;
            case 2:
                contratos = convenioExhortoImpl.traerContratosConExhortos(sesion.getUsuarioSesion().getIdCampo());
                break;
            default:
                llenarContratos();
                break;

        }
    }

    private void llenarContratos() {
        contratos = convenioExhortoImpl.traerContratosVencidos(sesion.getUsuarioSesion().getIdCampo());
    }

    public void crearCalificacion() {
        String cod = FacesUtils.getRequestParam("convnum");
        contratoVo = convenioImpl.traerConveniosPorCodigo(cod);
        motivo = "";
        PrimeFaces.current().executeScript("$(dialogoCalificar).modal('show');");
    }

    public void guardarCalificacion() {
        convenioImpl.finalizarConvenio(sesion.getUsuarioSesion().getId(), contratoVo, motivo);
        llenarContratos();
        //
        PrimeFaces.current().executeScript( "$(dialogoCalificar).modal('hide');");
    }

    public void traerEmpleadoPorGerencia(AjaxBehaviorEvent event) {
        listaUsuarioGerencia = campoUsuarioRhPuestoImpl.traerUsurioGerenciaCampo(idGerencia, contratoVo.getIdCampo());
        //
        UsuarioResponsableGerenciaVo urgv = campoGerenciaImpl.buscarResponsablePorGerencia(getIdGerencia(), contratoVo.getIdCampo());
        ContactoProveedorVO cpvo = new ContactoProveedorVO();
        cpvo.setNombre(urgv.getNombreUsuario());
        cpvo.setCorreo(urgv.getEmailUsuario());
        listaCorreoCopia.add(cpvo);
    }

    public void crearExhorto() {
        exhortoVo = new ExhortoVo();
        contactosProveedor = new ArrayList<ContactoProveedorVO>();
        listaCorreoPara = new ArrayList<ContactoProveedorVO>();
        listaCorreoCopia = new ArrayList<ContactoProveedorVO>();
        representanteLegalProveedor.clear();
        String cod = FacesUtils.getRequestParam("convnum");
        contratoVo = convenioImpl.traerConveniosPorCodigo(cod);
        representanteLegalProveedor = contactoProveedorImpl.traerContactoPorProveedor(contratoVo.getProveedor(), Constantes.CONTACTO_REP_LEGAL);
        //
        contactosProveedor = contactoProveedorImpl.traerContactoPorProveedor(contratoVo.getProveedor(), Constantes.CONTACTO_REP_TECNICO);
        contactosProveedor.addAll(contactoProveedorImpl.traerContactoPorProveedor(contratoVo.getProveedor(), Constantes.CONTACTO_REP_COMPRAS));
        //
        llenarCorreoCopia(contratoVo.getId(), contratoVo.getIdCampo());
        PrimeFaces.current().executeScript( "$(dialogoEnviarExhorto).modal('show');");
    }

    public void seleccionarContactoPara(SelectEvent event) {
        ContactoProveedorVO con = (ContactoProveedorVO) event.getObject();
        //
        if (listaCorreoPara.isEmpty()) {
            listaCorreoPara.add(con);
        } else {
            listaCorreoCopia.add(con);
        }
    }

    public void quitarUsuarioPara() {
        listaCorreoPara.remove(contactoProveedorVo);
    }

    public void seleccionarContactoCopia(SelectEvent event) {
        ContactoProveedorVO con = (ContactoProveedorVO) event.getObject();
        //
        listaCorreoCopia.add(con);
    }

    public void seleccionarContactoGerenciaCopia(SelectEvent event) {
        UsuarioVO us = (UsuarioVO) event.getObject();
        ContactoProveedorVO con = new ContactoProveedorVO();
        con.setNombre(us.getNombre());
        con.setCorreo(us.getMail());
        //
        listaCorreoCopia.add(con);
    }

    public void quitarUsuarioCopia() {
        listaCorreoCopia.remove(contactoProveedorVo);
    }

    public void enviarExhorto() {
        try {
            exhortoVo.setCorreoPara(obtenerCorreosPara(listaCorreoPara));
            exhortoVo.setCorreoCopia(obetenerCorreoCopia());
            exhortoVo.setRepresentanteLegal(listaCorreoPara.get(0).getNombre());
            exhortoVo.setPuestoRepresentante(listaCorreoPara.get(0).getPuesto());
            contratoVo.setCampo(sesion.getUsuarioSesion().getCampo());
            Preconditions.checkArgument(listaCorreoPara != null && !listaCorreoPara.isEmpty(), "Es necesario a quien se enviará el correo");
            convenioExhortoImpl.guardar(sesion.getUsuarioSesion(), contratoVo, exhortoVo);
            //
            llenarContratos();
            //
            PrimeFaces.current().executeScript( "$(dialogoEnviarExhorto).modal('hide');");
        } catch (IllegalArgumentException e) {
            FacesUtils.addErrorMessage(e.getMessage());
        }

    }

    private String obtenerCorreosPara(List<ContactoProveedorVO> listaCorreo) {
        String correo = "";
        for (ContactoProveedorVO cpVo : listaCorreo) {
            if (correo.isEmpty()) {
                correo = cpVo.getCorreo();
            } else {
                correo += "," + cpVo.getCorreo();
            }
        }
        return correo;
    }

    private String obetenerCorreoCopia() {
        String cc = obtenerCorreosPara(listaCorreoCopia);
        //
        return cc;
    }

    private void llenarCorreoCopia(int contratoId, int campoId) {
        ContactoProveedorVO cpVo = new ContactoProveedorVO();
        List<GerenciaVo> lg = convenioGerenciaImpl.convenioPorGerenica(contratoId);
        listaCorreoCopia = new ArrayList<ContactoProveedorVO>();
        // gerencia compras
        UsuarioResponsableGerenciaVo usuarioResponsableGerenciaVo = campoGerenciaImpl.buscarResponsablePorGerencia(Constantes.GERENCIA_ID_COMPRAS, campoId);
        if (usuarioResponsableGerenciaVo != null) {
            cpVo.setNombre(usuarioResponsableGerenciaVo.getNombreUsuario());
            cpVo.setCorreo(usuarioResponsableGerenciaVo.getEmailUsuario());
            listaCorreoCopia.add(cpVo);
        }

        //Gerencia tesoreria
        usuarioResponsableGerenciaVo = campoGerenciaImpl.buscarResponsablePorGerencia(Constantes.GERENCIA_ID_DIRECCION_FINANZAS, campoId);
        if (usuarioResponsableGerenciaVo != null) {
            cpVo = new ContactoProveedorVO();
            cpVo.setNombre(usuarioResponsableGerenciaVo.getNombreUsuario());
            cpVo.setCorreo(usuarioResponsableGerenciaVo.getEmailUsuario());
            listaCorreoCopia.add(cpVo);
        }
        //Gerencia de juridico
        usuarioResponsableGerenciaVo = campoGerenciaImpl.buscarResponsablePorGerencia(Constantes.GERENCIA_JURIDICO, campoId);
        if (usuarioResponsableGerenciaVo != null) {
            cpVo = new ContactoProveedorVO();
            cpVo.setNombre(usuarioResponsableGerenciaVo.getNombreUsuario());
            cpVo.setCorreo(usuarioResponsableGerenciaVo.getEmailUsuario());
            listaCorreoCopia.add(cpVo);
        }
        //Gerencia de juridico
        usuarioResponsableGerenciaVo = campoGerenciaImpl.buscarResponsablePorGerencia(Constantes.GERENCIA_ID_RR_HH, campoId);
        if (usuarioResponsableGerenciaVo != null) {
            cpVo = new ContactoProveedorVO();
            cpVo.setNombre(usuarioResponsableGerenciaVo.getNombreUsuario());
            cpVo.setCorreo(usuarioResponsableGerenciaVo.getEmailUsuario());
            listaCorreoCopia.add(cpVo);
        }
        // Rol contrato
        for (UsuarioRolVo urVo : usuarioRolImpl.traerUsuarioPorRolModulo(Constantes.ROL_ADMINISTRA_CONTRATO, Constantes.MODULO_CONTRATO, campoId)) {
            cpVo = new ContactoProveedorVO();
            cpVo.setNombre(urVo.getUsuario());
            cpVo.setCorreo(urVo.getCorreo());
            listaCorreoCopia.add(cpVo);
        }

        for (GerenciaVo lg1 : lg) {
            UsuarioResponsableGerenciaVo urgv = campoGerenciaImpl.buscarResponsablePorGerencia(lg1.getId(), campoId);
            cpVo = new ContactoProveedorVO();
            cpVo.setNombre(urgv.getNombreUsuario());
            cpVo.setCorreo(urgv.getEmailUsuario());
            listaCorreoCopia.add(cpVo);
        }
    }

    /**
     *
     * @param sesion
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }
}
