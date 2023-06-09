/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.correo.service;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.newrelic.api.agent.Trace;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import mx.ihsa.constantes.Constantes;
import mx.ihsa.modelo.Usuario;
import mx.ihsa.servicios.catalogos.impl.UsuarioImpl;
import mx.ihsa.util.GenNrStats;
import mx.ihsa.util.NewRelicEvent;
import mx.ihsa.util.UtilLog4j;


/**
 *
 */
@Stateless 
public class EnviarCorreoImpl {

    @Inject
    private UsuarioImpl usuarioServicioImpl;

    @Resource(name = "mail/gmail")
    private Session sesionGmail;    

    private final static Logger LOGGER = Logger.getLogger(EnviarCorreoImpl.class.getName());

    @Trace
    private List<String> getUniqueAddresses(final String addresses) {
        final List<String> uniqueAddresses = new ArrayList<>();

        if (!Strings.isNullOrEmpty(addresses)) {
            uniqueAddresses.addAll(
                    ImmutableSet.copyOf(
                            Splitter.on(',')
                                    .trimResults()
                                    .split(addresses)
                    ).asList()
            );
        }

        return uniqueAddresses;
    }

    @Trace
    private void prepararCorreo(final MailMessage mailMessage) {

        try {

            mailMessage.prepareMessage();

            final StringBuilder ccoSB = new StringBuilder();
            ccoSB.append(mailMessage.getCco());

            if (!Strings.isNullOrEmpty(mailMessage.getPara())
                    || !Strings.isNullOrEmpty(mailMessage.getCc())
                    || !Strings.isNullOrEmpty(mailMessage.getCco())) {

                final Usuario usrSIA = usuarioServicioImpl.find(MailMessage.USUARIO_SISTEMA);

                if (usrSIA != null && !Strings.isNullOrEmpty(usrSIA.getEmail())) {
                    if (Strings.isNullOrEmpty(mailMessage.getCco())) {
                        ccoSB.append(usrSIA.getEmail());
                    } else {
                        ccoSB.append(',').append(usrSIA.getEmail());
                    }
                }
            }

            // Receptor del mensaje
            if (!Strings.isNullOrEmpty(mailMessage.getPara())) {
                //--asunto
                log4j("ASUNTO : " + mailMessage.getAsunto());
                //---
                log4j("---------------------- Correo Para --------------------");
                for (final String correoFrom : getUniqueAddresses(mailMessage.getPara())) {
                    log4j("Para : " + correoFrom);
                    mailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(correoFrom));
                }
            }
            // Con Copia
            if (!Strings.isNullOrEmpty(mailMessage.getCc())) {
                log4j("---------------------- Con Copia --------------------");
                for (final String correoCC : getUniqueAddresses(mailMessage.getCc())) {
                    log4j("CC : " + correoCC);
                    mailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(correoCC));
                }

            }
            // Con Copia oculta
            if (ccoSB.length() > 0) {
                log4j("---------------------- Con Copia Oculta --------------------");
                for (final String correoCCO : getUniqueAddresses(ccoSB.toString())) {
                    log4j("CCO : " + correoCCO);
                    mailMessage.addRecipient(Message.RecipientType.BCC, new InternetAddress(correoCCO));
                }

            }

        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, null, e);
            limpiarElementos(mailMessage);
        } catch (UnsupportedEncodingException e) {
            LOGGER.log(Level.SEVERE, null, e);
            limpiarElementos(mailMessage);
        }
    }

    @Trace
    private boolean enviar(final MailMessage mailMessage) {
        boolean retVal = false;

        try {
            GenNrStats.saveNrData("EMAIL");

            NewRelicEvent event = new NewRelicEvent();
            event.setSystem("SISTEMA");
            event.setClassName(EnviarCorreoImpl.class.getName());
            event.setMethod("enviar");
            event.setEventName("before-Transport-send");
            event.setData(mailMessage.getAsunto());

            GenNrStats.logEvents(event);

            // Enviar el mensaje
            Transport.send(mailMessage.getMensaje());

            event.setEventName("after-Transport-send");

            GenNrStats.logEvents(event);

            retVal = true;

            log4j("Correo Enviado Correctamente...");

        } catch (MessagingException ex) {
            //-- reportarme el error enviando via correo
            LOGGER.log(Level.SEVERE, null, ex);
            log4j("Excepción al enviar Correo...");
        } finally {
            //-- limpiar todo
            limpiarElementos(mailMessage);
        }

        return retVal;
    }

    @Trace
    private void limpiarElementos(MailMessage mailMessage) {
        try {
            //-- limpiar todo
            mailMessage.clean();
        } catch (MessagingException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            mailMessage = null;
        }
    }

    
    public boolean enviarCorreoGmail(final String para, final String cc, final String cco,
            final String asunto, final StringBuilder mensaje) {

        return enviarCorreoGmail(para, cc, cco, asunto, mensaje, null);
    }

    
    public boolean enviarCorreoGmail(final String para, final String cc, final String cco,
            final String asunto, final StringBuilder mensaje, final byte[] logoSia) {

        boolean retVal = false;
        final MailMessage mailMessage = new MailMessage();

        try {
            mailMessage.setSesion(sesionGmail);
            mailMessage.setPara(para);
            mailMessage.setCc(cc);
            mailMessage.setCco(cco);
            mailMessage.setAsunto(asunto);
            mailMessage.setTextoMensaje(mensaje.toString());

            prepararCorreo(mailMessage);
            mailMessage.addLogoSia(logoSia);

            retVal = enviar(mailMessage);
        } catch (MessagingException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            limpiarElementos(mailMessage);
        }

        return retVal;
    }

    
    public boolean enviarCorreoGmail(final String para, final String cc,
            final String cco, final String asunto, final StringBuilder mensaje,
            final byte[] logoCompany, final byte[] logoEsr) {

        boolean retVal = false;
        final MailMessage mailMessage = new MailMessage();

        try {
            mailMessage.setSesion(sesionGmail);
            mailMessage.setPara(para);
            mailMessage.setCc(cc);
            mailMessage.setCco(cco);
            mailMessage.setAsunto(asunto);
            mailMessage.setTextoMensaje(mensaje.toString());

            prepararCorreo(mailMessage);
            mailMessage.addLogoCompany(logoCompany);
            mailMessage.addLogoESR(logoEsr);

            retVal = enviar(mailMessage);

        } catch (MessagingException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            limpiarElementos(mailMessage);
        }

        return retVal;
    }

//-- Implementacion para IHSA
    /**
     * Facade
     *
     * @param para
     * @param cc
     * @param cco
     * @param asunto
     * @param mensaje
     * @return
     */
    
    @Trace(dispatcher = true)
    public boolean enviarCorreo(
            final Set<String> para, final Set<String> cc, final Set<String> cco,
            final String asunto, final String mensaje, final byte[] logoSia) {

        final String strPara = getCadenaCorreos(para);
        final String strCc = getCadenaCorreos(cc);
        final String strCco = getCadenaCorreos(cco);

        return EnviarCorreoImpl.this.enviarCorreo(strPara, strCc, strCco, asunto, new StringBuilder(mensaje), logoSia);

    }

    private String getCadenaCorreos(final Set<String> correos) {
        String retVal = Constantes.VACIO;

        if (correos != null) {
            final Joiner joiner = Joiner.on(',').skipNulls();
            retVal = joiner.join(correos);
        }

        return retVal;
    }

    
    public boolean enviarCorreo(final String para, final String cc, final String cco,
            final String asunto, final StringBuilder mensaje) {

        return EnviarCorreoImpl.this.enviarCorreo(para, cc, cco, asunto, mensaje, null);
    }

    
    @Trace(dispatcher = true)
    public boolean enviarCorreo(final String para, final String cc, final String cco,
            final String asunto, final StringBuilder mensaje, final byte[] logoSia) {

        return EnviarCorreoImpl.this.enviarCorreo(para, cc, cco, asunto, logoSia, null, mensaje);

    }

    
    @Trace(dispatcher = true)
    public boolean enviarCorreo(final String para, final String cc, final String cco,
            final String asunto, final StringBuilder mensaje, final byte[] logoCompany,
            final byte[] logoEsr) {

        return EnviarCorreoImpl.this.enviarCorreo(para, cc, cco, asunto, mensaje, logoCompany, logoEsr, null, null, null);
    }

    
    @Trace(dispatcher = true)
    public boolean enviarCorreo(final String para, final String cc, final String cco,
            final String asunto, final StringBuilder mensaje, final byte[] logoCompany,
            final byte[] logoEsr, final File pdf, final File pdfCG, final String compSiglas) {

        final MailMessage mailMessage = new MailMessage();
        boolean retVal = false;

        try {
            mailMessage.setSesion(sesionGmail);
            mailMessage.setPara(para);
            mailMessage.setCc(cc);
            mailMessage.setCco(cco);
            mailMessage.setAsunto(asunto);
            mailMessage.setTextoMensaje(mensaje.toString());

            prepararCorreo(mailMessage);

            mailMessage.addLogoCompany(logoCompany);
            mailMessage.addLogoESR(logoEsr);
            mailMessage.addPdf(pdf);
            mailMessage.addPdfCG(pdfCG, compSiglas);

            retVal = enviar(mailMessage);

        } catch (MessagingException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            limpiarElementos(mailMessage);
        }

        return retVal;
    }

    
    @Trace(dispatcher = true)
    public boolean enviarCorreo(final String para, final String cc, final String cco,
            final String asunto, final StringBuilder mensaje, final byte[] logoCompany,
            final byte[] logoEsr, final File archivo1, final File archivo2, final File archivo3, String compSiglas) {

        final MailMessage mailMessage = new MailMessage();
        boolean retVal = false;

        try {
            mailMessage.setSesion(sesionGmail);
            mailMessage.setPara(para);
            mailMessage.setCc(cc);
            mailMessage.setCco(cco);
            mailMessage.setAsunto(asunto);
            mailMessage.setTextoMensaje(mensaje.toString());

            prepararCorreo(mailMessage);

            mailMessage.addLogoCompany(logoCompany);
            mailMessage.addLogoESR(logoEsr);
            mailMessage.addArchivoPortal1(archivo1);
            mailMessage.addArchivoPortal2(archivo2);
            mailMessage.addArchivoPortal3(archivo3);

            retVal = enviar(mailMessage);

        } catch (MessagingException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            limpiarElementos(mailMessage);
        }

        return retVal;
    }

    private void log4j(final String mensaje) {
        UtilLog4j.log.info(this, ">>>>>" + mensaje);
    }

    
    public boolean enviarCorreo(final String para, final String cc, final String cco,
            final String asunto, final byte[] logoSia, final byte[] logoWarning, final StringBuilder mensaje) {
        final MailMessage mailMessage = new MailMessage();
        boolean retVal = false;

        try {
            mailMessage.setSesion(sesionGmail);
            mailMessage.setPara(para);
            mailMessage.setCc(cc);
            mailMessage.setCco(cco);
            mailMessage.setAsunto(asunto);
            mailMessage.setTextoMensaje(mensaje.toString());

            prepararCorreo(mailMessage);
            mailMessage.addLogoSia(logoSia);
            mailMessage.addLogoWarning(logoWarning);

            retVal = enviar(mailMessage);

        } catch (MessagingException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            limpiarElementos(mailMessage);
        }

        return retVal;

    }

    
    public boolean enviarCorreo(final String para, final String cc, final String cco,
            final String asunto, final byte[] logoSia, final byte[] logoWarning, final StringBuilder mensaje, final File pdf) {
        final MailMessage mailMessage = new MailMessage();
        boolean retVal = false;

        try {
            mailMessage.setSesion(sesionGmail);
            mailMessage.setPara(para);
            mailMessage.setCc(cc);
            mailMessage.setCco(cco);
            mailMessage.setAsunto(asunto);
            mailMessage.setTextoMensaje(mensaje.toString());

            prepararCorreo(mailMessage);

            mailMessage.addLogoSia(logoSia);
            mailMessage.addLogoWarning(logoWarning);
            mailMessage.addPdf(pdf);

            retVal = enviar(mailMessage);

        } catch (MessagingException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            limpiarElementos(mailMessage);
        }

        return retVal;

    }

}
