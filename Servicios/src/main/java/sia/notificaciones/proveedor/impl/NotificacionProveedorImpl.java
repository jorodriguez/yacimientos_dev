/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.notificaciones.proveedor.impl;

import java.io.File;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import sia.constantes.Constantes;
import sia.modelo.proveedor.Vo.ProveedorVo;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.sistema.vo.FacturaVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.util.UtilLog4j;
import sia.constantes.Configurador;
import sia.correo.impl.EnviarCorreoImpl;
import sia.servicios.orden.impl.ContactosOrdenImpl;
import sia.servicios.proveedor.impl.ContactoProveedorImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;

/**
 *
 * @author mluis
 */
@LocalBean 
public class NotificacionProveedorImpl {

    @Inject
    private EnviarCorreoImpl enviarCorreoRemote;
    @Inject
    HtmlNotificacionProveedorImpl htmlNotificaiconProveedorRemote;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolRemote;
    @Inject
    private SiParametroImpl siParametroRemote;
    @Inject
    ContactosOrdenImpl contactosOrdenRemote;
    @Inject
    ContactoProveedorImpl contactoProveedorRemote;
    @Inject
    ProveedorServicioImpl proveedorRemote;

    private final static UtilLog4j LOGGER = UtilLog4j.log;

    
    public boolean notificacionAltaProveedor(ProveedorVo proveedorVo, String mail, int campo) {
        return this.enviarCorreoRemote.enviarCorreoIhsa(mail, correoPorRol(Constantes.CODIGO_ROL_VALPRO, campo), "", "Activación Proveedor " + proveedorVo.getRfc(), htmlNotificaiconProveedorRemote.correoF(proveedorVo));
    }

    
    public boolean notificacionActivarProveedor(String rfc, String correo, String pass) {
        return this.enviarCorreoRemote.enviarCorreoIhsa(correo, "", "", "Editar datos", htmlNotificaiconProveedorRemote.mensageCorreo(rfc, pass));
    }

    
    public boolean notificarTodosProveedores(String correo, String rfc, String clave, int consecutivo) {
        return this.enviarCorreoRemote.enviarCorreoIhsa(correo, "", "", "Notificación proveedor ", htmlNotificaiconProveedorRemote.mensageCorreoNotificacion(consecutivo, rfc, clave));
    }

    
    public boolean notificacionProveedorProceso(ProveedorVo proveedorVo, UsuarioVO usuarioVO) {
        return this.enviarCorreoRemote.enviarCorreoIhsa(correoPorRol(Constantes.CODIGO_ROL_VALPRO, usuarioVO.getIdCampo()), usuarioVO.getMail(), "", "Registro del proveedor " + proveedorVo.getRfc(),
                htmlNotificaiconProveedorRemote.mensajeProveedorProceso(proveedorVo, usuarioVO), siParametroRemote.find(Constantes.UNO).getLogo());
    }

    private String correoPorRol(String rol, int campo) {
        String correo = "";
        for (UsuarioRolVo urvo : siUsuarioRolRemote.traerRolPorCodigo(rol, campo, Constantes.MODULO_ADMIN_SIA)) {
            if (correo.isEmpty()) {
                correo = urvo.getCorreo();
            } else {
                correo += "," + urvo.getCorreo();
            }
        }
        return correo;
    }

    private String correoPorOrden(int idOrden) {
        return contactosOrdenRemote.correoContactoOrden(idOrden);
    }

    
    public boolean notificacionDevolucionProveedor(ProveedorVo proveedorVo, String motivo, String mailSesion, String emailProveedor) {
        return this.enviarCorreoRemote.enviarCorreoIhsa(emailProveedor, mailSesion, "", "Devolución del proveedor " + proveedorVo.getRfc(),
                htmlNotificaiconProveedorRemote.mensajeProveedorDevuelto(proveedorVo, motivo), siParametroRemote.find(Constantes.UNO).getLogo());
    }

    
    public boolean notificacionEnvioFacturaCliente(String correoPara, FacturaVo facturaVo, OrdenVO compraVo, ProveedorVo proveedorVo) {
        //System.out.println("para: " + correoPara);
        return enviarCorreoRemote.enviarCorreoIhsa(correoPara, "", "", "Factura / Contenido Nacional ",
                htmlNotificaiconProveedorRemote.mensajeEnvioFactura(facturaVo, proveedorVo, compraVo));
    }

    
    public boolean notificaDevolverFactrura(FacturaVo facturaVo, ProveedorVo proveedorVo, String motivo, String correoSesion) {
        //
        String correoNotifica = correoNotificaProveedor(proveedorVo.getIdProveedor());
        return enviarCorreoRemote.enviarCorreoIhsa(correoPorOrden(facturaVo.getIdRelacion()), correoCopia(correoSesion, correoNotifica), "", "Factura (Rechazada)",
                htmlNotificaiconProveedorRemote.mensajeDevolicionFactura(facturaVo, proveedorVo, motivo));
    }

    
    public boolean notificaDevolverCCN(FacturaVo facturaVo, ProveedorVo proveedorVo, String motivo, String correoSesion) {
        //
        String correoNotifica = correoNotificaProveedor(proveedorVo.getIdProveedor());
        return enviarCorreoRemote.enviarCorreoIhsa(correoPorOrden(facturaVo.getIdRelacion()), correoCopia(correoSesion, correoNotifica), "", "Factura / Contenido Nacional (Rechazada)",
                htmlNotificaiconProveedorRemote.mensajeDevolicionCCN(facturaVo, proveedorVo, motivo));
    }

    
    public boolean notificaAceptarFactrura(FacturaVo facturaVo, ProveedorVo proveedorVo, String correoSesion) {
        String correoNotifica = correoNotificaProveedor(proveedorVo.getIdProveedor());
        return enviarCorreoRemote.enviarCorreoIhsa(correoPorOrden(facturaVo.getIdRelacion()), correoCopia(correoSesion, correoNotifica), "", "Factura (Aceptada) ",
                htmlNotificaiconProveedorRemote.mensajeAceptarFactura(facturaVo, proveedorVo));
    }

    
    public boolean notificaAceptarCCN(FacturaVo facturaVo, ProveedorVo proveedorVo, String correoSesion) {
        String correoNotifica = correoNotificaProveedor(proveedorVo.getIdProveedor());
        return enviarCorreoRemote.enviarCorreoIhsa(correoPorOrden(facturaVo.getIdRelacion()), correoCopia(correoSesion, correoNotifica), "", "Factura / Contenido Nacional (Aceptada) ",
                htmlNotificaiconProveedorRemote.mensajeAceptarCCN(facturaVo, proveedorVo));
    }

    private String correoCopia(String correoSesion, String correoProveedor) {
        if (!correoProveedor.isEmpty()) {
            return correoSesion += ", " + correoProveedor;
        }
        return correoSesion;
    }

    private String correoNotificaProveedor(int idProveedor) {
        return contactoProveedorRemote.correoNotifica(idProveedor);
    }

    
    public boolean notificaCambioContraseña(String para, String clave, ProveedorVo proveedorVo) {
        return this.enviarCorreoRemote.enviarCorreoIhsa(para, "", "", "Reinicio de contraseña ",
                htmlNotificaiconProveedorRemote.mensajeCambioClave(clave, proveedorVo));
    }

    
    public boolean notificaFactruraPagada(FacturaVo facturaVo, String correoSesion) {
        return enviarCorreoRemote.enviarCorreoIhsa(correoPorOrden(facturaVo.getIdRelacion()), correoSesion, "", "Factura Pagada ",
                htmlNotificaiconProveedorRemote.mensajeFacturaPagada(facturaVo));
    }

    
    public boolean notificacionFacturasZip(String para, String copia, String copiaOculta, String asunto, File zipFile) {
        return enviarCorreoRemote.enviarCorreoIhsa(para, copia, copiaOculta, asunto,
                siParametroRemote.find(1).getLogo(),
                siParametroRemote.find(2).getLogo(),
                htmlNotificaiconProveedorRemote.mensajeFacturasFile(),
                zipFile);
    }

    
    public boolean notificacionArchivosPortal(ProveedorVo proveedorVo, String cc, String cco) {
//        File fileZip = null;
        File file1 = null;
        File file2 = null;
        File file3 = null;
        boolean ret = false;
        String para = "";
       
        try {
//            fileZip = proveedorRemote.crearZipFile(proveedorVo.getRfc(), proveedorVo.getLstDocsPortal());

            file1 = proveedorRemote.crearFile(proveedorVo.getRfc(), proveedorVo.getPortalActPrep());
            file2 = proveedorRemote.crearFile(proveedorVo.getRfc(), proveedorVo.getPortalEstSocVig());
            file3 = proveedorRemote.crearFile(proveedorVo.getRfc(), proveedorVo.getPortalServEsp());

            para = Configurador.emailArchivosPortal();
            ret = enviarCorreoRemote.enviarCorreoIhsa(para, cc, cco,
                    "Archivos del portal de proveedores IHSA. RFC: " + proveedorVo.getRfc(),
                    htmlNotificaiconProveedorRemote.mensajeArchivosPortal(proveedorVo),
                    siParametroRemote.find(1).getLogo(),
                    siParametroRemote.find(2).getLogo(),
                    file1 != null ? file1 : null,
                    file2 != null ? file2 : null,
                    file3 != null ? file3 : null,
                    null);

        } catch (Exception e) {
            LOGGER.error("Error al enviar archivos del portal de proveedores : ", e);
        } finally {
//            if (fileZip != null && fileZip.exists()) {
//                fileZip.delete();
//            }
            if (file1 != null && file1.exists()) {
                file1.delete();
            }
            if (file2 != null && file2.exists()) {
                file2.delete();
            }
            if (file3 != null && file3.exists()) {
                file3.delete();
            }
        }
        return ret;
    }
    
    
    public boolean notificaElinadoArchivosPortal(int idProveedor) {
        String correoNotifica = correoNotificaProveedor(idProveedor);
        return enviarCorreoRemote.enviarCorreoIhsa(correoNotifica, "", "", "Archivos requeridos del portal de proveedores",
                htmlNotificaiconProveedorRemote.mensajeEliminarArchivosPortal());
    }
    
    
    public boolean notificacionEnvioFacturaExtranjeraCliente(String correoPara, FacturaVo facturaVo, OrdenVO compraVo, ProveedorVo proveedorVo) {
        System.out.println("para: " + correoPara);
        return enviarCorreoRemote.enviarCorreoIhsa(correoPara, "", "", "Revisión de factura (No Nacional)",
                htmlNotificaiconProveedorRemote.mensajeEnvioFacturaExtranjera(facturaVo, proveedorVo, compraVo));
    }

}
