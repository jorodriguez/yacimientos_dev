/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.notificaciones.convenio.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import sia.constantes.Constantes;
import sia.correo.impl.CodigoHtml;
import sia.modelo.Compania;
import sia.modelo.SiPlantillaHtml;
import sia.modelo.contrato.vo.ContratoFormasNotasVo;
import sia.modelo.contrato.vo.ContratoFormasVo;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.contrato.vo.ExhortoVo;
import sia.modelo.contrato.vo.RhConvenioDocumentoVo;
import sia.modelo.usuario.vo.UsuarioResponsableGerenciaVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.campo.nuevo.impl.ApCampoGerenciaImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.convenio.impl.ConvenioImpl;
import sia.servicios.sistema.impl.SiPlantillaHtmlImpl;

/**
 *
 * @author ihsa
 */
@LocalBean 
public class HtmlNotificacionConvenioImpl extends CodigoHtml  {

    @Inject
    private SiPlantillaHtmlImpl plantillaHtml;
    @Inject
    private ApCampoGerenciaImpl apCampoGerenciaRemote;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoRemote;
    @Inject
    private ConvenioImpl convenioRemote;

    
    public StringBuilder conveniosPorVencer(String asunto, List<ContratoVO> convenios) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

        limpiarCuerpoCorreo();
        cuerpoCorreo.append(plantilla.getInicio());
        cuerpoCorreo.append(getTitulo(asunto));
        cuerpoCorreo.append("<br/>");
        //Aquí va todo el contenido del cuerpo,
        cuerpoCorreo.append("<p>A continuación se envian los convenios próximos a vencer.").append("</p>");
        //Lista de convenios
        listaConvenios(convenios);
        // Fin
        cuerpoCorreo.append(plantilla.getFin());
        return cuerpoCorreo;
    }

    private void listaConvenios(List<ContratoVO> contratos) {
        this.cuerpoCorreo.append("<br/><table width=\"100%\" align=\"center\" cellspacing=\"0\">");
        this.cuerpoCorreo.append("<tr><th colspan=\"5\"").append(getEstiloTituloTabla()).append("> Convenios(s)</th></tr>");
        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<th style=\"").append(getEstiloTitulo()).append("\">Código</th>");
        this.cuerpoCorreo.append("<th style=\"").append(getEstiloTitulo()).append("\">Alcance</th>");
        this.cuerpoCorreo.append("<th style=\"").append(getEstiloTitulo()).append("\">Proveedor</th>");
        //this.cuerpoCorreo.append("<th style=\"").append(getEstiloTitulo()).append("\">Gerencia</th>");
        this.cuerpoCorreo.append("<th style=\"").append(getEstiloTitulo()).append("\">Vencimiento</th>");
        this.cuerpoCorreo.append("</tr>");
        for (ContratoVO contrato : contratos) {
            this.cuerpoCorreo.append("<tr>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\">").append(validarNullHtml(contrato.getNumero())).append("</td>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\">").append(validarNullHtml(contrato.getNombre())).append("</td>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\">").append(validarNullHtml(contrato.getNombreProveedor())).append("</td>");
            // this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\">").append(validarNullHtml(contrato.getGerencia())).append("</td>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\">").append(validarNullFechaHtml(contrato.getFechaVencimiento())).append("</td>");
            this.cuerpoCorreo.append("</tr>");
        }
        cuerpoCorreo.append("</table><br/>");
    }

    
    public StringBuilder conveniosPorVencidos(String asunto, List<ContratoVO> convenios) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

        limpiarCuerpoCorreo();
        cuerpoCorreo.append(plantilla.getInicio());
        cuerpoCorreo.append(getTitulo(asunto));
        cuerpoCorreo.append("<br/>");
        //Aquí va todo el contenido del cuerpo,
        cuerpoCorreo.append("<p>A continuación se envian los convenios vencidos el dia de hoy <b>").append(Constantes.FMT_TextDateLarge.format(new Date())).append("</b></p>");
        //Lista de convenios
        listaConvenios(convenios);

        cuerpoCorreo.append("<p>Un saludo.").append("</p>");
        // Fin
        cuerpoCorreo.append(plantilla.getFin());
        return cuerpoCorreo;
    }

    
    public StringBuilder convenioPorRevisar(String asunto, ContratoVO convenio) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

        limpiarCuerpoCorreo();
        cuerpoCorreo.append(plantilla.getInicio());
        cuerpoCorreo.append(getTitulo(asunto));
        cuerpoCorreo.append("<br/>");
        //Aquí va todo el contenido del cuerpo,
        cuerpoCorreo.append("<p>    Se va a generar un contrato con la empresa  <b>").append(convenio.getNombreProveedor()).append("</b>");
        cuerpoCorreo.append(", favor de revisar y especificar la documentación requerida para este convenio. </p>");
        //Lista de convenios
        convenio(convenio);

        cuerpoCorreo.append("<p>Un saludo.").append("</p>");
        // Fin
        cuerpoCorreo.append(plantilla.getFin());
        return cuerpoCorreo;
    }

    
    public StringBuilder convenioFormalizado(String asunto, ContratoVO convenio, String empresaInterna, String representanteProveedor, String puesto) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(Constantes.UNO);

        limpiarCuerpoCorreo();
        cuerpoCorreo.append(plantilla.getInicio())
                .append(getTitulo(asunto))
                .append("<br/>")
                .append(convenio.getNombreProveedor())
                .append("<br/>Aviso de notificación: ").append(convenio.getNumero())
                .append("<br/>")
                //Aquí va todo el contenido del cuerpo,
                .append("<p> Presente<br/>")
                .append("Atención: <b>").append(representanteProveedor).append(" ").append(validarNullHtml(puesto)).append("</b></p>")
                .append("<br/>")
                .append("<p> Para su conocimiento y aplicación, se les informa atentamente, que el siguiente documento contractual,")
                .append(" después de ser formalizado  ha sido dado de alta en el Sistema Integral de Administración (SIA) de <b>")
                .append(empresaInterna).append("</b>").append(":</p><br/>")
                .append("Contrato:  <b>      ").append(convenio.getNumero()).append("</b>")
                .append("<br/>Alcance:  <b>  ").append(convenio.getNombre()).append("</b>")
                .append("<br/>");
        if (convenio.getIdContratoRelacionado() > 0) {
            //Datos del convenio maestro
            ContratoVO contratoVO = convenioRemote.buscarPorId(convenio.getIdContratoRelacionado(), 0, "", false);
            cuerpoCorreo.append("<p>mismo que forma parte de:</p>")
                    .append("<br/>Contrato:  <b>").append(contratoVO.getNumero()).append("</b>")
                    .append("<br/>Alcance:  <b>").append(contratoVO.getNombre()).append("</b>");
        }
        cuerpoCorreo.append("<br/>");
        UsuarioResponsableGerenciaVo cgVo = apCampoGerenciaRemote.buscarResponsablePorGerencia(Constantes.GERENCIA_JURIDICO, convenio.getIdCampo());
        cuerpoCorreo.append("<p> Conforme las obligaciones contractuales contraídas por su representada con ")
                .append("<b>").append(empresaInterna).append("</b>, deberá tramitar de forma inmediata las fianzas y seguros que el Contrato y/o Convenio ")
                .append("establece, presentándolas a nuestra área Jurídica");
        if (cgVo != null) {
            cuerpoCorreo.append(", cuya titular es la Lic. ")
                    .append(cgVo.getNombreUsuario()).append(" (")
                    .append(cgVo.getEmailUsuario())
                    .append(")");

            List<UsuarioVO> lu = apCampoUsuarioRhPuestoRemote.traerUsurioGerenciaCampoMenosGerente(Constantes.GERENCIA_JURIDICO, convenio.getIdCampo());
            if (lu != null && !lu.isEmpty()) {
                cuerpoCorreo.append(", con copia a la Lic. ");
                for (UsuarioVO lu1 : lu) {
                    cuerpoCorreo.append(lu1.getNombre()).append(" (").append(lu1.getMail()).append(") ");
                }
                cuerpoCorreo.append(", quienes le orientaran, revisaran y autorizaran en el proceso de gestión.</p>");
            } else {
                cuerpoCorreo.append(".");
            }
        } else {
            cuerpoCorreo.append(".</p>");
        }
        //
        cuerpoCorreo.append("<br/><p>A partir de la presente notificación de formalización")
                .append(", la Gerencia usuaria es la responsable de administrar, supervisar y hacer cumplir la ejecución satisfactoria del Contrato y/o Convenio.</p>")
                .append("<br/><br/> Con motivo de  la aceptación de las pólizas de fianza y/o seguro que está obligado a entregar con base en el")
                .append(" citado Contrato y/o Convenio de referencia, se hace de su conocimiento el siguiente procedimiento. <br/>")
                .append("<br/><p><font style=\"color: blue;\"> 1.</font> Para la validación por parte de LA COMPAÑÍA de las pólizas de fianza y/o seguro, deberá enviar por este medio, previamente revisadas y aceptadas")
                .append(" por usted, las pólizas de fianza electrónicas (no así los comprobantes de pago), y las carátulas de las pólizas de seguro, incluyendo la totalidad de las")
                .append(" condiciones de aseguramiento solicitadas en el Contrato y/o Convenio, por lo tanto debe cerciorarse que las pólizas de seguro no contengan exclusiones ")
                .append(" contrarias a la cobertura solicitada. Es requisito indispensable, envíe la totalidad de las pólizas de fianza y/o seguro en una sola exhibición.</p>")
                .append("<br/><p><font style=\"color: blue;\"> 2.</font> En caso que, alguna póliza de seguro este a nombre de persona física o moral distinta al Contratista y/o Proveedor, deberá enviar el documento que vincule a su representada")
                .append(" con el tercero. Como ejemplo, contrato de servicio, acuerdo, y/o documentos corporativos que demuestren la relación entre filiales o sucursales.</p>")
                .append("<br/><p><font style=\"color: blue;\"> 3.</font> Para cualquier duda o aclaración en relación a la emisión, corrección y/o expedición de las pólizas de fianza, deberá dirigirse con")
                .append(" Patricia Vazquez pvazquez@senties-chauvet.com.mx y/o Raúl Cortes rcortes@senties-chauvet.com.mx de Senties & Chauvet.</p>")
                .append("<br/><p>    Por último se hace de su conocimiento que en caso de no dar trámite a las pólizas de fianza y/o seguro dentro de los 30 días naturales posteriores")
                .append(" a este aviso, deberá enviar en una sola exhibición, la totalidad de los siguiente requisitos, siendo no aceptable entregas parciales. </p> ")
                .append("<br/><br/><p><font style=\"color: blue;\"> a)</font> Aviso escrito o correo electrónico del Representante Técnico de La Compañía, mediante el que certifique la NO EXISTENCIA DE INCUMPLIMIENTO DE OBLIGACIONES.")
                .append(" El texto debe hacer referencia exacta a cada una de las fianzas y seguros que el Contrato y/o Convenio en cuestión establece. </p> ")
                .append("<br/><p><font style=\"color: blue;\"> b)</font> Sólo en caso de la necesidad de tramitar una Fianza Contra Contingencias Laborales, El Contratista debe entregar un escrito firmado por su apoderado")
                .append(" legal que contenga lo siguiente: </p> ")
                .append(" &nbsp;&nbsp;&nbsp;      - Que a la fecha NO SE HA PRESENTADO NINGUN TIPO DE CONTINGENCIA DE CARÁCTER LABORAL.<br/>")
                .append(" &nbsp;&nbsp;&nbsp;      - Que EL CONTRATISTA/PROVEEDOR se encuentra EN CUMPLIMIENTO TOTAL POR CONCEPTO DE SUS OBLIGACIONES OBRERO – PATRONALES ANTE EL IMSS.<br/>")
                .append(" &nbsp;&nbsp;&nbsp;      - Que EL CONTRATISTA/PROVEEDOR se hace RESPONSABLE Y DEFENDERÁ E INDEMNIZARÁ A LA COMPAÑÍA CON MOTIVO DE CUALQUIER DEMANDA LABORAL.")
                .append("<br/><br/><center>Cordialmente<br/>")
                .append("Grupo Técnico de Subcontratación<br/></center>")
                .append(plantilla.getFin());
        return cuerpoCorreo;
    }

    private void convenio(ContratoVO contrato) {
        this.cuerpoCorreo.append("<br/><table width=\"100%\" align=\"center\" cellspacing=\"0\">");
        this.cuerpoCorreo.append("<tr><th colspan=\"5\"").append(getEstiloTituloTabla()).append("> Convenios(s)</th></tr>");
        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<th style=\"").append(getEstiloTitulo()).append("\">Código</th>");
        this.cuerpoCorreo.append("<th style=\"").append(getEstiloTitulo()).append("\">Alcance</th>");
        this.cuerpoCorreo.append("<th style=\"").append(getEstiloTitulo()).append("\">Proveedor</th>");
        this.cuerpoCorreo.append("<th style=\"").append(getEstiloTitulo()).append("\">Importe</th>");
        this.cuerpoCorreo.append("<th style=\"").append(getEstiloTitulo()).append("\">Inicio</th>");
        this.cuerpoCorreo.append("<th style=\"").append(getEstiloTitulo()).append("\">Vencimiento</th>");
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\">").append(validarNullHtml(contrato.getNumero())).append("</td>");
        this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\">").append(validarNullHtml(contrato.getNombre())).append("</td>");
        this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\">").append(validarNullHtml(contrato.getNombreProveedor())).append("</td>");
        this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\">").append(validarNullMontoHtml(contrato.getMonto())).append(" ").append(validarNullHtml(contrato.getMoneda())).append("</td>");
        this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\">").append(validarNullFechaHtml(contrato.getFechaInicio())).append("</td>");
        this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\">").append(validarNullFechaHtml(contrato.getFechaVencimiento())).append("</td>");
        this.cuerpoCorreo.append("</tr>");
        cuerpoCorreo.append("</table><br/>");
    }

    
    public StringBuilder convenioFiniquitado(String asunto, ContratoVO convenio, String empresa, String represetnante, String puesto) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(Constantes.UNO);

        limpiarCuerpoCorreo();
        cuerpoCorreo.append(plantilla.getInicio());
        cuerpoCorreo.append(getTitulo(asunto));
        cuerpoCorreo.append("<br/>");
        cuerpoCorreo.append("Aviso de notificación:").append(convenio.getNombre()).append(", ").append(Constantes.FMT_ddMMyyy.format(new Date()));
        cuerpoCorreo.append("<br/><br/>");
        cuerpoCorreo.append(convenio.getNombreProveedor());
        cuerpoCorreo.append("<br/>");
        cuerpoCorreo.append("Presente");
        cuerpoCorreo.append("<br/>");
        cuerpoCorreo.append("Atención: ").append(represetnante).append(" ").append(validarNullHtml(puesto));

        //Aquí va todo el contenido del cuerpo,
        cuerpoCorreo.append("<p> Para su conocimiento y aplicación, se les informa atentamente, que el siguiente documento contractual, después de ser formalizado  ha sido dado de alta en el Sistema Integral de Administración (SIA): </p>");
        cuerpoCorreo.append("Convenio Modificatorio:  <b>      ").append(convenio.getNumero()).append("</b>");
        cuerpoCorreo.append("<br/>Objeto del Convenio:  <b>  ").append(convenio.getNombre()).append("</b>");
        if (convenio.getIdContratoRelacionado() > 0) {
            //Datos del convenio maestro
            ContratoVO contratoVO = convenioRemote.buscarPorId(convenio.getIdContratoRelacionado(), 0, "", false);
            cuerpoCorreo.append("<p>Mismo que forma parte de:</p>");
            cuerpoCorreo.append("<br/>Contrato:  <b>").append(contratoVO.getNumero()).append("</b>");
            cuerpoCorreo.append("<br/>Alcance:  <b>").append(contratoVO.getNombre()).append("</b>");
        }
        cuerpoCorreo.append("<br/>");
        cuerpoCorreo.append("<br/>");
        cuerpoCorreo.append("<br/>");
        cuerpoCorreo.append("Cordialmente <br/><i>Grupo Técnico de Subcontratación</i><br/>");
        cuerpoCorreo.append("Dirección de Procura y Contratos");
        // Fin
        cuerpoCorreo.append(plantilla.getFin());
        return cuerpoCorreo;
    }

    /**
     *
     * @param asunto
     * @param convenio
     * @param exhortoVo
     * @param empresa
     * @return
     */
    
    public StringBuilder exhortoFiniquito(String asunto, ContratoVO convenio, ExhortoVo exhortoVo,
            Compania empresa) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(Constantes.UNO);

        limpiarCuerpoCorreo();
        cuerpoCorreo.append(plantilla.getInicio());
        cuerpoCorreo.append(getTitulo("Exhorto - " + Constantes.FMT_TextDate.format(new Date())));
        cuerpoCorreo.append("<br/><br/>");
        cuerpoCorreo.append(empresa.getCiudad()).append(", a ").append(Constantes.FMT_TextDate.format(new Date()))
                .append("<br/>");
        cuerpoCorreo.append("<b>").append(convenio.getNombreProveedor()).append("</b>");
        cuerpoCorreo.append("<br/>");
        cuerpoCorreo.append("Domicilio: ").append(convenio.getProveedorVo().getCalle()).append(" ")
                .append(convenio.getProveedorVo().getNumero()).append(" ")
                .append(convenio.getProveedorVo().getColonia()).append(" ")
                .append(validarNullFechaHtml(convenio.getProveedorVo().getEstado())).append(" ")
                .append(convenio.getProveedorVo().getCodigoPostal());
        cuerpoCorreo.append("<br/>")
                .append("Presente");
        cuerpoCorreo.append("<br/><br/>");
        cuerpoCorreo.append("Número de exhorto: ").append(exhortoVo.getCodigo());
        cuerpoCorreo.append("<br/>");
        cuerpoCorreo.append("Número de contrato: ").append(convenio.getNumero());
        cuerpoCorreo.append("<br/>Atención: ")
                .append(exhortoVo.getRepresentanteLegal()).append(" ")
                .append("<br/>")
                .append(validarNullHtml(exhortoVo.getPuestoRepresentante()));
        cuerpoCorreo.append("<br/><br/>");
        cuerpoCorreo.append("<p style=\"text-align: right;\">Asunto: <b> ").append(empresa.getSiglas()).append(" - ")
                .append(convenio.getCampo()).append(" Exhorto para finiqito.</b></p>");
        //Aquí va todo el contenido del cuerpo,
        cuerpoCorreo.append("<p> Con la presente, <b>").append(empresa.getNombre()).append("</b> exhorta atentamente")
                .append(" a su representada <b>").append(convenio.getNombreProveedor()).append("</b>, ")
                .append(" con RFC <b>").append(convenio.getProveedorVo().getRfc()).append("</b>")
                .append(" para que en el marco del contrato en vigor número ").append(convenio.getNumero())
                .append(".-").append(convenio.getNombre()).append(" con periodo contractual vencido el ")
                .append(convenio.getFechaVencimiento())
                .append(" inicien a la brevedad posible su trámite de finiquito, de conformidad al \"Procedimiento Finiquito de Subcontratos\"")
                .append(" y formatos relacionados (Descargables desde el portal de proveedores). ");
        cuerpoCorreo.append("<br/>");
        cuerpoCorreo.append("<br/>");
        cuerpoCorreo.append("Atentamente <br/>");
        cuerpoCorreo.append("Gerencia de Procura y Contratos");
        // Fin
        cuerpoCorreo.append(plantilla.getFin());
        return cuerpoCorreo;
    }

    
    public StringBuilder solicitudFiniquito(String asunto, ContratoVO convenio, ExhortoVo exhortoVo) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(Constantes.UNO);

        limpiarCuerpoCorreo();
        cuerpoCorreo.append(plantilla.getInicio());
        cuerpoCorreo.append(getTitulo(asunto));
        cuerpoCorreo.append("<br/><br/>");
        cuerpoCorreo.append("<b>").append(convenio.getCompania()).append(".</b><br/> Presente.").append("<br/> A quién corresponda.");

        cuerpoCorreo.append("<b>").append(convenio.getNombreProveedor()).append("</b>");
        cuerpoCorreo.append("<br/><br/>");
        cuerpoCorreo.append("Número de contrato: ").append(convenio.getNumero());
        cuerpoCorreo.append("<br/>");
        //Aquí va todo el contenido del cuerpo,
        cuerpoCorreo.append("<p> Con la presente mi representada <b>").append(convenio.getNombreProveedor())
                .append("</b> con RFC <b>").append(convenio.getProveedorVo().getRfc()).append("</b> ")
                .append(" \"Confirma\" a partir de esta fecha el inicio del proceso de finiquito del Contrato número ")
                .append(convenio.getNumero()).append(" - ").append(convenio.getNombre()).append(" con periodo contractual vencido el ")
                .append(convenio.getFechaVencimiento())
                .append(".  ");
        cuerpoCorreo.append("<br/><br/>");
        cuerpoCorreo.append("Lo anterior en el marco del contrato referido y del Procedimiento de Finiquito de Subcontratos de IHSA.")
                .append("<br/><br/>")
                .append("Derivado de la presente confirmación procederemos a la formulación, gestión y formalización y subir al portal ")
                .append(" de Proveedores del SIA los siguientes documentos requeridos e indispensables para el finiquito contractual:");
        cuerpoCorreo.append("<br/>");
        cuerpoCorreo.append("<ul>")
                .append("<li>").append("Solicitud inicio Proceso de Finiquito").append("</li>")
                .append("<li>").append("Acta de Entrega Recepción del Objeto Contractual").append("</li>")
                .append("<li>").append("Emisión de Estado de Cuenta Contractual").append("</li>")
                .append("<li>").append("Conciliación y Validación Contable").append("</li>")
                .append("<li>").append("Revisión y Validación de Obligaciones Obrero- Patronal, conforme Contrato").append("</li>")
                .append("</ul>")
                .append("<br/>");

        cuerpoCorreo.append("De conformidad<br/>");
        cuerpoCorreo.append(exhortoVo.getRepresentanteLegal()).append("<br/>")
                .append(exhortoVo.getPuestoRepresentante()).append("<br/>")
                .append(convenio.getNombreProveedor());
        // Fin
        cuerpoCorreo.append(plantilla.getFin());
        return cuerpoCorreo;
    }

    
    public StringBuilder validacionFormaFiniquito(ContratoVO coVo, ContratoFormasVo conFormaVo) {

        SiPlantillaHtml plantilla = this.plantillaHtml.find(Constantes.UNO);

        limpiarCuerpoCorreo();
        cuerpoCorreo.append(plantilla.getInicio());
        cuerpoCorreo.append(getTitulo("Validación de forma en el proceso de finiquito"));
        cuerpoCorreo.append("<br/><br/>");
        cuerpoCorreo.append("<br/><br/>");
        cuerpoCorreo.append("Contrato: ").append(coVo.getNumero());
        cuerpoCorreo.append("<br/>");
        cuerpoCorreo.append("<b>").append(coVo.getNombreProveedor()).append("</b>");
        //Aquí va todo el contenido del cuerpo,
        cuerpoCorreo.append("<p> Con la presente hago de su conocimiento que se ha revisado  y validado el documento ")
                .append(conFormaVo.getForma()).append("</b>.");
        cuerpoCorreo.append("<br/>");
        cuerpoCorreo.append("<br/>");
        cuerpoCorreo.append("Un cordial saludo. <br/>");
        // Fin
        cuerpoCorreo.append(plantilla.getFin());
        return cuerpoCorreo;
    }

    
    public StringBuilder mensajeValidarFormaFiniquito(ContratoFormasVo conFormaVo) {

        SiPlantillaHtml plantilla = this.plantillaHtml.find(Constantes.UNO);

        limpiarCuerpoCorreo();
        cuerpoCorreo.append(plantilla.getInicio());
        cuerpoCorreo.append(getTitulo("Validar forma en el Proceso de Finiquito de Contratos"));
        cuerpoCorreo.append("<br/><br/>");
        cuerpoCorreo.append("<br/><br/>");
        cuerpoCorreo.append("Contrato: ").append(conFormaVo.getCodigoConvenio());
        cuerpoCorreo.append("<br/>");
        cuerpoCorreo.append("<b>").append(conFormaVo.getProveedor()).append("</b>");
        //Aquí va todo el contenido del cuerpo,
        cuerpoCorreo.append("<p> Con la presente hago de su conocimiento que se ha cargado en el sistema ")
                .append(" la forma para el Proceso de Finiquito de Contratos <b>")
                .append(conFormaVo.getForma()).append("</b>.")
                .append(", por lo que solicito realice la revisión correspondiente. ");
        cuerpoCorreo.append("<br/>");
        cuerpoCorreo.append("<br/>");
        cuerpoCorreo.append("Un cordial saludo. <br/>");
        // Fin
        cuerpoCorreo.append(plantilla.getFin());
        return cuerpoCorreo;
    }

    
    public StringBuilder mensajeObservacionFormaFiniquito(ContratoFormasVo conFormaVo, ContratoFormasNotasVo contratoFormasNotasVo) {

        SiPlantillaHtml plantilla = this.plantillaHtml.find(Constantes.UNO);

        limpiarCuerpoCorreo();
        cuerpoCorreo.append(plantilla.getInicio());
        cuerpoCorreo.append(getTitulo("Proceso de Finiquito de Contratos"));
        cuerpoCorreo.append("<br/><br/>");
        cuerpoCorreo.append("<br/><br/>");
        cuerpoCorreo.append("Contrato: ").append(conFormaVo.getCodigoConvenio());
        cuerpoCorreo.append("<br/>");
        cuerpoCorreo.append("<b>").append(conFormaVo.getProveedor()).append("</b>");
        //Aquí va todo el contenido del cuerpo,
        cuerpoCorreo.append("<p> En el proceso de revisión de la forma ").append(conFormaVo.getForma())
                .append(" se realiza la siguiente observación: <br/>")
                .append(contratoFormasNotasVo.getObservacion())
                .append("</b>");
        cuerpoCorreo.append("<br/>");
        cuerpoCorreo.append("<br/>");
        cuerpoCorreo.append("Un cordial saludo. <br/>");
        // Fin
        cuerpoCorreo.append(plantilla.getFin());
        return cuerpoCorreo;
    }
    
    
    public StringBuilder mensajeDocuementoRh(RhConvenioDocumentoVo documentoVo) {

        SiPlantillaHtml plantilla = this.plantillaHtml.find(Constantes.UNO);

        limpiarCuerpoCorreo();
        cuerpoCorreo.append(plantilla.getInicio());
        cuerpoCorreo.append(getTitulo("Obsevación de documento Recursos Humanos"));
        cuerpoCorreo.append("<br/><br/>");
        cuerpoCorreo.append("<br/><br/>");
        cuerpoCorreo.append("<br/>");
        //Aquí va todo el contenido del cuerpo,        
        cuerpoCorreo.append("<p> En el proceso de revisión del documento <b>").append(documentoVo.getDocumento()).append("</b>")
                .append(" se realiza la siguiente observación: <br/>")
                .append(documentoVo.getObservacion())
                .append("</b>");
        cuerpoCorreo.append("<br/>");
        cuerpoCorreo.append("<br/>");
        cuerpoCorreo.append("Un cordial saludo. <br/>");
        // Fin
        cuerpoCorreo.append(plantilla.getFin());
        return cuerpoCorreo;
    }

}
