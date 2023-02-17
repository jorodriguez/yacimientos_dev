/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.correo.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.Resource;
import javax.ejb.AsyncResult;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import lector.modelo.Usuario;
import lector.servicios.catalogos.impl.UsuarioImpl;
import lector.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Stateless 
public class EnviarAsincronoImpl {

    @Inject
    private UsuarioImpl usuarioServicioImpl;
//
    //@Resource(name = "mail/sending_resource")
    private Session sesionEmail;
    //
    private Multipart mpRelated = new MimeMultipart("related");
    private final BodyPart texto = new MimeBodyPart();
    private final MimeBodyPart logoCompany = new MimeBodyPart();
    private final MimeBodyPart logoSecundario = new MimeBodyPart();       
    private final MimeBodyPart logo = new MimeBodyPart();
    TreeSet noDuplicados = new TreeSet();
    private MimeMessage mensaje; // new MimeMessage(sesion);

    private TreeSet obtenerListaCorreoNoDuplicados(String direcciones) {
        String[] listaDirecciones = direcciones.split(",");
        log("Lista original de direcciones : " + direcciones);
        noDuplicados.clear();
        log("Comenzando a omitir duplicados..");
        for (String lista : listaDirecciones) {
            noDuplicados.add(lista.trim());
        }
        return noDuplicados;
    }

    
    public Future<Boolean> enviarCorreoIhsa(String para, String cc, String cco, String asunto, StringBuilder mensaje, byte[] logoCompany, byte[] logoEsr) throws Exception {
        prepararCorreo(sesionEmail, para, cc, cco, asunto, mensaje);
        procesarLogos(logoCompany, logoEsr);
        Future<Boolean> v = enviar();
        return new AsyncResult<Boolean>(v.get());
    }

    
    public Future<Boolean> enviarCorreoIhsa(String para, String cc, String cco, String asunto, StringBuilder mensaje, byte[] logoSistema) {
        prepararCorreo(sesionEmail, para, cc, cco, asunto, mensaje);
        procesarLogos(logoSistema);
        enviar();
        return new AsyncResult<Boolean>(true);
    }

    private void prepararCorreo(Session sesion, String para, String cc, String cco, String asunto, StringBuilder mensaje) {
        // tomo la sesion 
        this.mensaje = new MimeMessage(sesion);
        this.mpRelated = new MimeMultipart("related");
        try {
            // Asunto
            this.mensaje.setSubject(asunto, "utf-8");
            try {
                // Emisor del mensaje
                this.mensaje.setFrom(new InternetAddress(sesion.getProperty("mail.from"), "SISTEMA"));

            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(EnviarCorreoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }

            StringBuilder ccoSB = new StringBuilder();
            ccoSB.append(cco);
            if ((para != null && para.isEmpty()) || (cc != null && cc.isEmpty()) || (cco != null && cco.isEmpty())) {
                Usuario usuarioSistema = this.usuarioServicioImpl.find(1);
                if (usuarioSistema != null && usuarioSistema.getEmail() != null && !usuarioSistema.getEmail().isEmpty()) {
                    if (cco == null || cco.isEmpty()) {
                        ccoSB.append(usuarioSistema.getEmail());
                    } else {
                        ccoSB.append(",").append(usuarioSistema.getEmail());
                    }
                }
            }
            cco = ccoSB.toString();

            // Receptor del mensaje
            if (!"".equals(para.trim())) {
                //--asunto
                log("ASUNTO : " + asunto);
                //---
                log("----------------------Correo Para--------------------");
                for (Iterator it = obtenerListaCorreoNoDuplicados(para).iterator(); it.hasNext();) {
                    String correoFrom = (String) it.next();
                    log("Para : " + correoFrom.trim());
                    this.mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress(correoFrom.trim()));
                }
            }
            // Con Copia
            if (!"".equals(cc.trim())) {
                log("----------------------Con Copia--------------------");
                for (Iterator it = obtenerListaCorreoNoDuplicados(cc).iterator(); it.hasNext();) {
                    String correoCC = (String) it.next();
                    log("CC : " + correoCC.trim());
                    this.mensaje.addRecipient(Message.RecipientType.CC, new InternetAddress(correoCC.trim()));
                }

            }
            // Con Copia oculta
            if (!"".equals(cco.trim())) {
                log("----------------------Con Copia Oculta--------------------");
                for (Iterator it = obtenerListaCorreoNoDuplicados(cco).iterator(); it.hasNext();) {
                    String correoCCO = (String) it.next();
                    log("CCO : " + correoCCO.trim());
                    this.mensaje.addRecipient(Message.RecipientType.BCC, new InternetAddress(correoCCO.trim()));
                }

            }
            // Cuerpo del correo
            this.texto.setContent(mensaje.toString(), "text/html; charset= utf-8");
            // meto el texto Multipart related
            this.mpRelated.addBodyPart(this.texto);
            // agrego todo al Email
            this.mensaje.setContent(this.mpRelated);
        } catch (MessagingException ex) {
            this.limpiarElementos();
            UtilLog4j.log.error(ex);
            
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

    private Future<Boolean> enviar() {
        try {
            // Enviar el mensaje
            Transport.send(this.mensaje);
            //-- limpiar todo
            this.limpiarElementos();
            log("Correo Enviado Correctamente...");
            return new AsyncResult<Boolean>(true);
        } catch (MessagingException ex) {
            //-- reportarme el error enviando via correo
            this.limpiarElementos();
            Logger.getLogger(EnviarCorreoImpl.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(ex.getMessage());
            log("Excepción al enviar Correo...");
            return new AsyncResult<Boolean>(false);
        }
    }

    private void limpiarElementos() {
        try {
            //-- limpiar todo
            this.mpRelated.removeBodyPart(this.texto);
            this.mpRelated.removeBodyPart(this.logoCompany);
            this.mpRelated.removeBodyPart(this.logoSecundario);
            this.mpRelated.removeBodyPart(this.logo);
            this.mensaje.removeHeader("<logoCompany>");
            this.mensaje.removeHeader("<logoEsr>");
            this.mensaje.removeHeader("<logoSistema>");
        } catch (MessagingException ex) {
            Logger.getLogger(EnviarCorreoImpl.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(ex.getMessage());
        }
    }

    private void procesarLogos(byte[] logoCompany, byte[] logoEsr) {
        try {
            // Procesar logo company
            if (logoCompany != null) {
                DataSource dslogoCompany = new ByteArrayDataSource(logoCompany, "application/octet-stream");
                this.logoCompany.setDataHandler(new DataHandler(dslogoCompany));
                this.logoCompany.setHeader("Content-ID", "<logoCompany>");
                this.mpRelated.addBodyPart(this.logoCompany);
            }
            if (logoEsr != null) {
                // Procesar logo esr
                DataSource dslogoEsr = new ByteArrayDataSource(logoEsr, "application/octet-stream");
                this.logoSecundario.setDataHandler(new DataHandler(dslogoEsr));
                this.logoSecundario.setHeader("Content-ID", "<logoEsr>");
                this.mpRelated.addBodyPart(this.logoSecundario);
            }

        } catch (MessagingException ex) {
            this.limpiarElementos();
            Logger.getLogger(EnviarCorreoImpl.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(ex.getMessage());
        }
    }

    private void procesarLogos(byte[] logoSistema) {
        try {
            // Procesar logo 
            if (logoSistema != null) {
                DataSource dslogoSistema = new ByteArrayDataSource(logoSistema, "application/octet-stream");
                this.logo.setDataHandler(new DataHandler(dslogoSistema));
                this.logo.setHeader("Content-ID", "<logoSia>");
                this.mpRelated.addBodyPart(this.logo);
            }
        } catch (MessagingException ex) {
            this.limpiarElementos();
            Logger.getLogger(EnviarCorreoImpl.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(ex.getMessage());
        }
    }

    private void log(String mensaje) {
        UtilLog4j.log.info(this, ">>>>>" + mensaje);
        //System.out.println( ">>"+mensaje);
    }
}
