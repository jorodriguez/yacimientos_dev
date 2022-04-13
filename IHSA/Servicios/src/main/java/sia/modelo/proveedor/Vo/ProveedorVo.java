/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.proveedor.Vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sia.constantes.Constantes;
import sia.modelo.contrato.vo.ContratoVO;

/**
 *
 * @author ihsa
 */
@Getter
@Setter
public class ProveedorVo {

    private int idRelacion;
    private int idProveedor;
    //Datos Generales
    private String nombre;
    private String giro;
    private boolean nacional;
    private int persona;
    private String personaTxt;
    private String nombreCorto;

    //Domicilio Fiscal
    private String calle;
    private String numero;
    private String numeroInt;
    private String colonia;
    private String ciudad;
    private String estado;
    private String codigoPostal;
    private String pais;
    private String direccion;

    //Registros
    private int pvRegistroFiscal;
    private String rfc;
    private String clave;
    private String imssPatronal;
    private String curp;
    private String idCIF;
    private String noNotaria;
    private String noBoleta;
    private String noActa;
    private String sede;
    private String nombreNot;
    private Date emision;
    private Date inscripcion;
    private int status;
    private String NombreStatus;
    private int idPago;
    private int tipoProveedor;
    private boolean carta;

    //Cuentas Bancos
    private List<CuentaBancoVO> cuentas;

    //Representante Legal
    private List<ContactoProveedorVO> lstRL;
    private List<ContactoProveedorVO> lstRT;
    private List<ContactoProveedorVO> contactos;
    private List<ProveedorDocumentoVO> lstDocsProveedor;
    private List<ContactoProveedorVO> todoContactos;
    private List<ContratoVO> contratos;
    //
    private boolean editar;
    private boolean primerSesion;
    
    private ProveedorDocumentoVO portalServEsp;
    private ProveedorDocumentoVO portalActPrep;
    private ProveedorDocumentoVO portalEstSocVig;
    //
    private boolean repse;
    
    
    public ProveedorVo(){
    
    }
    
    public ContactoProveedorVO crearContacto(String nombre, String correo, String cel, String tipo){
        ContactoProveedorVO newCont = new ContactoProveedorVO();
        newCont.setNombre(nombre);
        newCont.setCorreo(correo);
        newCont.setCelular(cel);
        newCont.setTipoTxt(tipo);
        return newCont;
    }
    
    public String getNombreCompleto(){
        String ret = "";
        if(this.nombre != null && !this.nombre.isEmpty()){
            ret += this.nombre;
        }
        if(this.rfc != null && !this.rfc.isEmpty()){
            ret += "/";
            ret += this.rfc;
        }
        return ret;
    }
    
    public List<ProveedorDocumentoVO> getLstDocsPortal(){
        List<ProveedorDocumentoVO> lstDocsPortal = new ArrayList<>();
        
        if(this.getPortalServEsp() != null){
            this.getPortalServEsp().setObligatoria(false);
            this.getPortalServEsp().setAyuda("SE à Servicio Especializado (en caso de registro).- En este apartado, en caso de que el proveedor proporcione servicios especializados a las que se refiere la reforma laboral, deberá subir el documento que lo acredite como proveedor de servicios especializados.");
            lstDocsPortal.add(this.getPortalServEsp());
        } else {
            ProveedorDocumentoVO voSE = new ProveedorDocumentoVO();
            voSE.setDocumento("Servicios Especializados");
            voSE.setIdDocumento(Constantes.DOCUMENTO_TIPO_SERV_ESP);
            voSE.getAdjuntoVO().setTipoArchivo("SE");
            voSE.setObligatoria(false);
            voSE.setAyuda("SE à Servicio Especializado (en caso de registro).- En este apartado, en caso de que el proveedor proporcione servicios especializados a las que se refiere la reforma laboral, deberá subir el documento que lo acredite como proveedor de servicios especializados.");
            lstDocsPortal.add(voSE);
        }
        
        if(this.getPortalActPrep()!= null){
            this.getPortalActPrep().setObligatoria(true);
            this.getPortalActPrep().setAyuda("AP à actividad preponderante (documento libre).- En este apartado, las personas morales y/o físicas, pueden subir su Constancia de Situación Fiscal, (CIF), o documento libre en donde se detalle su actividad preponderante. Esta actividad preponderante debe ser consistente y estar relacionada con los servicios que proporciona, es decir si tienes contrato por servicios de instalaciones eléctricas, y te proporciona un documento donde dice que su actividad preponderante es servicios contables; pues evidentemente no corresponde.");
            lstDocsPortal.add(this.getPortalActPrep());
        } else {
            ProveedorDocumentoVO voAP = new ProveedorDocumentoVO();
            voAP.setDocumento("Actividad Preponderante");
            voAP.setIdDocumento(Constantes.DOCUMENTO_TIPO_ACT_PREP);
            voAP.getAdjuntoVO().setTipoArchivo("AP");
            voAP.setObligatoria(true);
            voAP.setAyuda("AP à actividad preponderante (documento libre).- En este apartado, las personas morales y/o físicas, pueden subir su Constancia de Situación Fiscal, (CIF), o documento libre en donde se detalle su actividad preponderante. Esta actividad preponderante debe ser consistente y estar relacionada con los servicios que proporciona, es decir si tienes contrato por servicios de instalaciones eléctricas, y te proporciona un documento donde dice que su actividad preponderante es servicios contables; pues evidentemente no corresponde.");
            lstDocsPortal.add(voAP);
        }
        
        if(this.getPortalEstSocVig()!= null){            
            this.getPortalEstSocVig().setObligatoria(true);
            this.getPortalEstSocVig().setAyuda("ESV à Los estatutos sociales vigentes.- Cuando nos referimos a estatutos sociales, evidentemente nos referimos al acta constitutiva de una empresa o al última acta donde se encuentren sus estatutos sociales vigentes; sobre todo en lo referente a su objeto social. Las personas físicas no cuentan con este documento, sin embargo debemos solicitarles que suban su curriculum empresarial, en donde se detalle las actividades y/o giros comerciales a los que se dedican.");
            lstDocsPortal.add(this.getPortalEstSocVig());
        } else {
            ProveedorDocumentoVO voESV = new ProveedorDocumentoVO();
            voESV.setDocumento("Estatutos Sociales Vigentes");
            voESV.setIdDocumento(Constantes.DOCUMENTO_TIPO_EST_SOC_VIG);
            voESV.getAdjuntoVO().setTipoArchivo("ESV");
            voESV.setObligatoria(true);
            voESV.setAyuda("ESV à Los estatutos sociales vigentes.- Cuando nos referimos a estatutos sociales, evidentemente nos referimos al acta constitutiva de una empresa o al última acta donde se encuentren sus estatutos sociales vigentes; sobre todo en lo referente a su objeto social. Las personas físicas no cuentan con este documento, sin embargo debemos solicitarles que suban su curriculum empresarial, en donde se detalle las actividades y/o giros comerciales a los que se dedican.");
            lstDocsPortal.add(voESV);
        }
    
        return lstDocsPortal;
    }

    
    public boolean isArchivosCargados(){
        boolean ret = false;
        if(this.getPortalEstSocVig() != null && this.getPortalActPrep()!= null
                && this.getPortalEstSocVig().getId() > 0 && this.getPortalActPrep().getId() > 0){
            ret = true; 
        }
        return ret;
    }
}
