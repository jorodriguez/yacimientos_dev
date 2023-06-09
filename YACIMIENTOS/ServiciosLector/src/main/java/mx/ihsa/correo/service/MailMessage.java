/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.correo.service;

import com.google.common.base.Strings;
import java.io.File;
import java.io.UnsupportedEncodingException;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

/**
 *
 */
public class MailMessage {
    private Session sesion;
    private String para;
    private String cc;
    private String cco;
    private String asunto;
    private String textoMensaje;
    
    /*
     * related: se utiliza cuando las partes del cuerpo del Email están
     * relacionadas entre sí, como por ejemplo el texto de una página web y sus
     * imágenes, si éstas están contenidas en el propio mensaje.
     */
    private Multipart mpRelated;
    /*
     * Parte del Email que contiene el cuerpo del mensaje
     */
    private final BodyPart texto;
    /*
     * Parte del Email q contiene el logo de la compañia y el logo ESR
     */
    private final MimeBodyPart partLogoCompany;
    private final MimeBodyPart partLogoEsr;
    private final MimeBodyPart partOrdenPDF;
    private final MimeBodyPart partOrdenPDFCG;
    private final MimeBodyPart partLogoSia;
    private final MimeBodyPart partLogoWarning;
    private final MimeBodyPart partArchivoPortal1;
    private final MimeBodyPart partArchivoPortal2;
    private final MimeBodyPart partArchivoPortal3;
    
    
    /*
     * Este representa el Email completo con todos sus elementos como origen,
     * destino, cuerpo etc
     */
    private MimeMessage mensaje;

    public final static String USUARIO_SISTEMA = "SISTEMA";
    public final static String TAG_LOGO_COMPANY = "<logoCompany>";
    public final static String TAG_LOGO_ESR = "<logoEsr>";
    public final static String TAG_LOGO_SISTEMA = "<logoSistema>";
    public final static String TAG_LOGO_WARNING = "<logoWarning>";
    
    
    public MailMessage() {
        super();
        
        texto = new MimeBodyPart();
        partLogoCompany = new MimeBodyPart();
        partLogoEsr = new MimeBodyPart();
        partOrdenPDF = new MimeBodyPart();
        partOrdenPDFCG = new MimeBodyPart();
        partLogoSia = new MimeBodyPart();
        partLogoWarning = new MimeBodyPart();
        partArchivoPortal1 = new MimeBodyPart();
        partArchivoPortal2 = new MimeBodyPart();
        partArchivoPortal3 = new MimeBodyPart();
        
    }
    

    /**
     * Prepara el mensaje para ser enviado
     * @throws MessagingException
     * @throws UnsupportedEncodingException 
     */
    public void prepareMessage() throws MessagingException, UnsupportedEncodingException {
        // tomo la sesion 
        mensaje = new MimeMessage(sesion);
        mpRelated = new MimeMultipart("related");
        
        // Asunto
        mensaje.setSubject(asunto, "utf-8");
            
        // Emisor del mensaje
        mensaje.setFrom(new InternetAddress(sesion.getProperty("mail.from"), USUARIO_SISTEMA));
        
        // Cuerpo del correo
        texto.setContent(textoMensaje, "text/html; charset=utf-8");
        // meto el texto Multipart related
        mpRelated.addBodyPart(texto);
        // agrego todo al Email
        mensaje.setContent(mpRelated);
    }
    
    /**
     * Agrega un destinatario para el correo
     * @param recipient El destinatario
     * @param internetAddress La dirección del destinatario
     * @throws MessagingException 
     */
    public void addRecipient(final Message.RecipientType recipient, final InternetAddress internetAddress) 
            throws MessagingException {
        mensaje.addRecipient(recipient, internetAddress);
    }
    
    public void clean() throws MessagingException {
        mpRelated.removeBodyPart(texto);
        mpRelated.removeBodyPart(partLogoCompany);
        mpRelated.removeBodyPart(partLogoEsr);
        mpRelated.removeBodyPart(partLogoSia);
        mpRelated.removeBodyPart(partLogoWarning);
        mensaje.removeHeader(TAG_LOGO_COMPANY);
        mensaje.removeHeader(TAG_LOGO_ESR);
        mensaje.removeHeader(TAG_LOGO_SISTEMA);
        mensaje.removeHeader(TAG_LOGO_WARNING);
    }
    
    public void addLogoSia(final byte[] logoSiaBA) throws MessagingException {
        if(logoSiaBA != null) {
            partLogoSia.setDataHandler(buildDataHandler(logoSiaBA));
            partLogoSia.setHeader("Content-ID", TAG_LOGO_SISTEMA);
            mpRelated.addBodyPart(partLogoSia);
        }
    }
    
    public void addLogoWarning(final byte[] logoWarning) throws MessagingException {
        if(logoWarning != null) {
            partLogoWarning.setDataHandler(buildDataHandler(logoWarning));
            partLogoWarning.setHeader("Content-ID", TAG_LOGO_WARNING);
            mpRelated.addBodyPart(partLogoWarning);
        }
    }
    
    public void addLogoCompany(final byte[] logoCompanyBA) throws MessagingException {
        if(logoCompanyBA != null) {
            partLogoCompany.setDataHandler(buildDataHandler(logoCompanyBA));
            partLogoCompany.setHeader("Content-ID", TAG_LOGO_COMPANY);
            mpRelated.addBodyPart(partLogoCompany);
        }
    }
    
    public void addLogoESR(final byte[] logoCompanyEsr) throws MessagingException {
        if(logoCompanyEsr != null) {
            partLogoEsr.setDataHandler(buildDataHandler(logoCompanyEsr));
            partLogoEsr.setHeader("Content-ID", TAG_LOGO_ESR);
            mpRelated.addBodyPart(partLogoEsr);
        }
    }
    
    public void addPdf(final File pdf) throws MessagingException {
        if(pdf != null) {
            partOrdenPDF.setDataHandler(buildDataHandler(pdf));
            partOrdenPDF.setFileName(pdf.getName());
            mpRelated.addBodyPart(partOrdenPDF);
        }
    }
    
    public void addArchivoPortal1(final File archivo) throws MessagingException {
        if(archivo != null) {
            partArchivoPortal1.setDataHandler(buildDataHandler(archivo));
            partArchivoPortal1.setFileName(archivo.getName());
            mpRelated.addBodyPart(partArchivoPortal1);
        }
    }
    
    public void addArchivoPortal2(final File archivo) throws MessagingException {
        if(archivo != null) {
            partArchivoPortal2.setDataHandler(buildDataHandler(archivo));
            partArchivoPortal2.setFileName(archivo.getName());
            mpRelated.addBodyPart(partArchivoPortal2);
        }
    }
    
    public void addArchivoPortal3(final File archivo) throws MessagingException {
        if(archivo != null) {
            partArchivoPortal3.setDataHandler(buildDataHandler(archivo));
            partArchivoPortal3.setFileName(archivo.getName());
            mpRelated.addBodyPart(partArchivoPortal3);
        }
    }
    
    public void addPdfCG(final File pdfCG, final String compSiglas) throws MessagingException {
        if(pdfCG != null) {
            partOrdenPDFCG.setDataHandler(buildDataHandler(pdfCG));
            
            final StringBuilder fName = new StringBuilder();
            fName.append("CondicionesGenerales")
                    .append(Strings.nullToEmpty(compSiglas))
                    .append(".pdf");
                
            partOrdenPDFCG.setFileName(fName.toString());
            
            mpRelated.addBodyPart(partOrdenPDFCG);
        }
    }
    
    private DataHandler buildDataHandler(final byte[] byteArray) {
        final DataSource ds = new ByteArrayDataSource(byteArray, "application/octet-stream");
        return new DataHandler(ds);
    }
    
    private DataHandler buildDataHandler(final File file) {
        final DataSource ds = new FileDataSource(file);
        return new DataHandler(ds);
    }
    
    
    public Session getSesion() {
        return sesion;
    }

    public void setSesion(final Session sesion) {
        this.sesion = sesion;
    }

    public String getPara() {
        return para;
    }

    public void setPara(final String para) {
        this.para = para;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(final String cc) {
        this.cc = cc;
    }

    public String getCco() {
        return cco;
    }

    public void setCco(final String cco) {
        this.cco = cco;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(final String asunto) {
        this.asunto = asunto;
    }

    public String getTextoMensaje() {
        return textoMensaje;
    }

    public void setTextoMensaje(final String textoMensaje) {
        this.textoMensaje = textoMensaje;
    }

    public MimeMessage getMensaje() {
        return mensaje;
    }

}
