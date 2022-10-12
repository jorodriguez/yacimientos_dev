/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.catalogos.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;




import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import sia.constantes.Constantes;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.contrato.vo.RhConvenioDocumentoVo;
import sia.modelo.proveedor.Vo.ContactoProveedorVO;
import sia.modelo.proveedor.Vo.ProveedorVo;
import sia.servicios.convenio.impl.ConvenioImpl;
import sia.servicios.proveedor.impl.ContactoProveedorImpl;
import sia.servicios.rh.impl.RhConvenioDocumentosImpl;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;

/**
 *
 * @author mluis
 */
@Named(value = "documentosProveedoresRhBean")
@ViewScoped
public class DocumentosProveedoresRhBean implements Serializable{

    /**
     * Creates a new instance of DocumentosProveedoresRhBean
     */
    public DocumentosProveedoresRhBean() {
    }

    @Inject
    private Sesion sesion;

    @Inject
    RhConvenioDocumentosImpl  rhConvenioDocumentosLocal;
    @Inject
    ContactoProveedorImpl contactoProveedorImpl;
    @Inject
    ConvenioImpl convenioImpl;

    @Getter
    @Setter
    private List<RhConvenioDocumentoVo> doctosRhNoPeriodicos;
    @Getter
    @Setter
    private RhConvenioDocumentoVo doctosRhVo;
    @Getter
    @Setter
    private List<RhConvenioDocumentoVo> doctosRhPeriodicos;
    @Getter
    @Setter
    private List<ProveedorVo> proveedores;
    @Getter
    @Setter
    private ProveedorVo proveedoreVo;
    @Getter
    @Setter
    private List<ContratoVO> contratosProveedor;
    @Getter
    @Setter
    private ContratoVO contratoVo;
    @Getter
    @Setter
    private List<ContactoProveedorVO> contactosProveedor;
    @Getter
    @Setter
    private List<ContactoProveedorVO> listaCorreo;

    @PostConstruct
    public void init() {
        doctosRhNoPeriodicos = new ArrayList<RhConvenioDocumentoVo>();
        doctosRhPeriodicos = new ArrayList<RhConvenioDocumentoVo>();
        ///
        proveedores = new ArrayList<ProveedorVo>();
        proveedores = convenioImpl.traerProveedoresConContratoActivo();
        //
        contactosProveedor = new ArrayList<ContactoProveedorVO>();
        proveedoreVo = new ProveedorVo();
        contratoVo = new ContratoVO();
    }

    public void verContratos() {
        int idP = Integer.parseInt(FacesUtils.getRequestParameter("proveedorId"));
        String rfc = (FacesUtils.getRequestParameter("proveedorRfc"));
        String prv = (FacesUtils.getRequestParameter("proveedorNombre"));
        proveedoreVo = new ProveedorVo();
        proveedoreVo.setIdProveedor(idP);
        proveedoreVo.setRfc(rfc);
        proveedoreVo.setNombre(prv);
        //
        contratosProveedor = convenioImpl.traerConvenioMaestroPorProveedorStatus(idP, Constantes.ESTADO_CONVENIO_ACTIVO);
        contratosProveedor.addAll(convenioImpl.traerConvenioMaestroPorProveedorStatus(idP, Constantes.ESTADO_CONVENIO_EXHORTO));
        contratosProveedor.addAll(convenioImpl.traerConvenioMaestroPorProveedorStatus(idP, Constantes.ESTADO_CONVENIO_PROCESO_FINIQUITO));
        //
        PrimeFaces.current().executeScript("$(dialogoContratosProveedor).modal('show');");
    }
    
    public void verDoctosPorContrato(){
        int indice = Integer.parseInt(FacesUtils.getRequestParameter("indice"));
        //
        contratoVo = new ContratoVO();
        contratoVo = contratosProveedor.get(indice);
        doctosRhNoPeriodicos = rhConvenioDocumentosLocal.traerDoctosNoPeriodicosPorConvenio(contratoVo.getId());
        doctosRhPeriodicos = rhConvenioDocumentosLocal.traerDoctosPeriodicosPorConvenio(contratoVo.getId());
        PrimeFaces.current().executeScript("$(dialogoDoctosProveedor).modal('show');");
    }

    public void inicioEnviarObservacion() {
        int idDoctoRh = Integer.parseInt(FacesUtils.getRequestParameter("doctoRhId"));
        doctosRhVo = new RhConvenioDocumentoVo();
        doctosRhVo = rhConvenioDocumentosLocal.buscarPorId(idDoctoRh);
        //        
        contactosProveedor = contactoProveedorImpl.traerTodosContactoPorProveedor(proveedoreVo.getIdProveedor());
        listaCorreo = new ArrayList<ContactoProveedorVO>();
        //
        PrimeFaces.current().executeScript("$(dialogoEnviarNotaDoctosProveedor).modal('show');");
    }

    public void seleccionarContacto(SelectEvent event) {
        ContactoProveedorVO con = (ContactoProveedorVO) event.getObject();
        //
        listaCorreo.add(con);
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

    public void quitarUsuarioCorreo() {
        int ind = Integer.parseInt(FacesUtils.getRequestParameter("indice"));
        listaCorreo.remove(ind);
    }

    public void enviarObservacion() {
        if (!listaCorreo.isEmpty()) {
            rhConvenioDocumentosLocal.enviarObservacion(doctosRhVo, obtenerCorreosPara(listaCorreo));
            //
            FacesUtils.addInfoMessage("Se envío la notificación");
            PrimeFaces.current().executeScript("$(dialogoEnviarNotaDoctosProveedor).modal('hide');");
        } else {
            FacesUtils.addErrorMessage("Es necesario seleccionar contacto(s) a notificar");
        }
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

}
