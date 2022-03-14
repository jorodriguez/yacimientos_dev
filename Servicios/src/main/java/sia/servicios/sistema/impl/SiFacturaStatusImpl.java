/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.impl;

import java.util.Date;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.Estatus;
import sia.modelo.SiFactura;
import sia.modelo.SiFacturaStatus;
import sia.modelo.Usuario;
import sia.modelo.proveedor.Vo.ProveedorVo;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.sistema.vo.FacturaVo;
import sia.util.FacturaEstadoEnum;
import sia.util.OrdenEstadoEnum;
import sia.util.UtilLog4j;
import sia.constantes.Configurador;
import sia.modelo.AutorizacionesOrden;
import sia.notificaciones.proveedor.impl.NotificacionProveedorImpl;
import sia.servicios.orden.impl.AutorizacionesOrdenImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SiFacturaStatusImpl extends AbstractFacade<SiFacturaStatus>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiFacturaStatusImpl() {
        super(SiFacturaStatus.class);
    }
    @Inject
    NotificacionProveedorImpl notificacionServicioRemoto;
    @Inject
    SiUsuarioRolImpl siUsuarioRolRemote;
    @Inject
    OrdenImpl ordenRemote;
    @Inject
    AutorizacionesOrdenImpl autorizacionesOrdenRemote;
    @Inject
    ProveedorServicioImpl proveedorRemote;
    @Inject
    SiFacturaMovimientoImpl facturaMovimientoLocal;
    @Inject
    SiFacturaImpl siFacturaRemote;
    @Inject
    SiFacturaAdjuntoImpl siFacturaAdjuntoLocal;

    
    public void guardar(int idFactura, int status, String sesion) {
        SiFacturaStatus siFacturaStatus = new SiFacturaStatus();
        siFacturaStatus.setSiFactura(new SiFactura(idFactura));
        siFacturaStatus.setEstatus(new Estatus(status));
        siFacturaStatus.setActual(Boolean.TRUE);
        siFacturaStatus.setGenero(new Usuario(sesion));
        siFacturaStatus.setFechaGenero(new Date());
        siFacturaStatus.setHoraGenero(new Date());
        siFacturaStatus.setEliminado(Constantes.NO_ELIMINADO);
        //
        create(siFacturaStatus);
    }

    
    public void cambiarEstatus(int idFactura, int estatusOld, int estatusNew, String sesion) {
        SiFacturaStatus facturaStatus = traerFacturaStatus(idFactura, estatusOld);
        facturaStatus.setActual(Constantes.BOOLEAN_FALSE);
        facturaStatus.setModifico(new Usuario(sesion));
        facturaStatus.setFechaModifico(new Date());
        facturaStatus.setHoraModifico(new Date());
        edit(facturaStatus);

        guardar(idFactura, estatusNew, sesion);

    }

    
    public void procesarFactura(ProveedorVo proveedorVo, FacturaVo facturaVo, OrdenVO compraVo) {
        try {
            boolean continuar;
            // enviar notificacion
            continuar = notificacionServicioRemoto.notificacionEnvioFacturaCliente(correoPara(compraVo.getIdBloque(),Constantes.COD_ROL_CON_NAC), facturaVo, compraVo, proveedorVo);
            //
            if (continuar) {
                SiFacturaStatus facturaStatus = traerFacturaStatus(facturaVo.getId(), FacturaEstadoEnum.CREADA.getId());
                facturaStatus.setActual(Constantes.BOOLEAN_FALSE);
                facturaStatus.setModifico(new Usuario(proveedorVo.getRfc()));
                facturaStatus.setFechaModifico(new Date());
                facturaStatus.setHoraModifico(new Date());
                edit(facturaStatus);
                //
                guardar(facturaVo.getId(), FacturaEstadoEnum.ENVIADA_CLIENTE.getId(), proveedorVo.getRfc());
            }
            //
            double totalFacturado = siFacturaRemote.totalPorOrden(compraVo.getId());
            // actualizar la orden de compra            
            if (((Double) totalFacturado).intValue() >= compraVo.getSubTotal().intValue()) {
                autorizacionesOrdenRemote.cambiarStatusOrden(compraVo.getId(), proveedorVo.getRfc(), OrdenEstadoEnum.OCS_RECEPCION_FACTURA.getId());
            }

        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

    private String correoPara(int idCampo, String codigoRol) {
        return siUsuarioRolRemote.traerCorreosPorCodigoRolList(codigoRol, idCampo);
    }

    
    public SiFacturaStatus traerFacturaStatus(int facturaVo, int status) {
        try {
            return (SiFacturaStatus) em.createNamedQuery("SiFacturaStatus.findFacturaStatus").setParameter(1, facturaVo).setParameter(2, status).getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return null;
    }

    
    public void eliminarPorFactura(int facturaVo, int status, String sesion) {
        try {
            SiFacturaStatus sfs = traerFacturaStatus(facturaVo, status);
            sfs.setEliminado(Constantes.ELIMINADO);
            sfs.setActual(Constantes.BOOLEAN_FALSE);
            sfs.setModifico(new Usuario(sesion));
            sfs.setFechaModifico(new Date());
            sfs.setHoraModifico(new Date());
            //
            edit(sfs);
        } catch (Exception e) {
            UtilLog4j.log.info("No se encontró la factura . . . ");
            UtilLog4j.log.error(e);
        }
    }

    
    public void aceptarFactura(String sesion, FacturaVo facturaVo, String correoSesion, int estatusOrg, int estatusFinal) {
        try {
//            modificarEstatusActual(sesion, facturaVo, FacturaEstadoEnum.PROCESO_INTERNO_CLIENTE.getId(), Constantes.BOOLEAN_FALSE);
            modificarEstatusActual(sesion, facturaVo, estatusOrg, Constantes.BOOLEAN_FALSE);

            //
            guardar(facturaVo.getId(), estatusFinal, sesion);

            String emails = "";
            emails += correoSesion;
            if (emails != null && !emails.isEmpty()) {
                emails += ", ";
                emails += Configurador.notificacionRecepcionFacturas();
            } else {
                emails += Configurador.notificacionRecepcionFacturas();
            }
            // notificar aceptacion factura
            notificacionServicioRemoto.notificaAceptarFactrura(facturaVo, proveedorRemote.traerProveedor(facturaVo.getIdProveedor(), facturaVo.getRfcCompania()), emails);
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

    private void modificarEstatusActual(String sesion, FacturaVo facturaVo, int estatusOrg, boolean nuewActual) {
        try {
            SiFacturaStatus sfs = traerFacturaStatus(facturaVo.getId(), estatusOrg);
            sfs.setActual(nuewActual);
            sfs.setModifico(new Usuario(sesion));
            sfs.setFechaModifico(new Date());
            sfs.setHoraModifico(new Date());
            edit(sfs);
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

    
    public void aceptarFacturaAvanzia(String sesion, FacturaVo facturaVo, String correoSesion, int estatusOrg, int estatusFinal) {
        try {
            String tiposArchivos = "";
            if (facturaVo != null && facturaVo.getIdStatus() != estatusFinal && !facturaVo.isNotaCredito()) {
                modificarEstatusActual(sesion, facturaVo, estatusOrg, Constantes.BOOLEAN_FALSE);
                guardar(facturaVo.getId(), estatusFinal, sesion);                
            }
            
            if (facturaVo != null && !facturaVo.isNotaCredito()) {
                tiposArchivos = "'XML (Factura)', 'PDF (Factura)'";
            } else {
                tiposArchivos = "'XML (Nota Credito)', 'PDF (Nota Credito)'";
            }
            
            siFacturaAdjuntoLocal.notificacionArchivosFactura(facturaVo.getId(), "", "", tiposArchivos);
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

    
    public void aceptarCCN(String sesion, FacturaVo facturaVo, String correoSesion, int estatusOrg, int estatusFinal) {
        try {
            modificarEstatusActual(sesion, facturaVo, estatusOrg, Constantes.BOOLEAN_FALSE);
            //
            guardar(facturaVo.getId(), estatusFinal, sesion);
            // notificar aceptacion factura
            notificacionServicioRemoto.notificaAceptarCCN(facturaVo, proveedorRemote.traerProveedor(facturaVo.getIdProveedor(), facturaVo.getRfcCompania()), correoSesion);
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

    
    public void pagarFactura(String sesion, FacturaVo facturaVo, String correoSesion, int estatusOrg, int estatusFinal) {
        try {
            modificarEstatusActual(sesion, facturaVo, estatusOrg, Constantes.BOOLEAN_FALSE);
            //
            guardar(facturaVo.getId(), estatusFinal, sesion);
            // notificar aceptacion factura
            notificacionServicioRemoto.notificaFactruraPagada(facturaVo, correoSesion);
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

    
    public void rechazarFactura(String sesion, FacturaVo facturaVo, String motivo, String correoSesion, int status) {
        modificarEstatusActual(sesion, facturaVo, status, Constantes.BOOLEAN_FALSE);
        //
        guardar(facturaVo.getId(), FacturaEstadoEnum.CREADA.getId(), sesion);
        // guarda el movimiento de la factura
        facturaMovimientoLocal.guardar(facturaVo.getId(), motivo, sesion);
        // cambiar el status a la orden compra
        //
        String emails = correoPara(facturaVo.getIdCampo(),Constantes.COD_ROL_CON_NAC);
        if (emails != null && !emails.isEmpty()) {
            emails += ", ";
            emails += correoSesion;
            emails += ", ";
            emails += Configurador.notificacionRecepcionFacturas();
        }
        //Notificar la devolucion
        notificacionServicioRemoto.notificaDevolverFactrura(facturaVo, proveedorRemote.traerProveedor(facturaVo.getIdProveedor(), facturaVo.getCompania()), motivo,
                emails
        );

        AutorizacionesOrden ao = autorizacionesOrdenRemote.buscarPorOrden(facturaVo.getIdRelacion());
        if (ao != null && ao.getEstatus() != null && ao.getEstatus().getId() == Constantes.ESTATUS_ORDEN_RECEPCION_FACTURA) {
            autorizacionesOrdenRemote.cambiarStatusOrden(facturaVo.getIdRelacion(), sesion, Constantes.ESTATUS_ORDEN_RECIBIDA);

        }
    }

    
    public void rechazarCCN(String sesion, FacturaVo facturaVo, String motivo, String correoSesion, int status) {
        modificarEstatusActual(sesion, facturaVo, status, Constantes.BOOLEAN_FALSE);
        //
        guardar(facturaVo.getId(), FacturaEstadoEnum.CREADA.getId(), sesion);
        // guarda el movimiento de la factura
        facturaMovimientoLocal.guardar(facturaVo.getId(), motivo, sesion);
        // cambiar el status a la orden compra
        //
        //Notificar la devolucion
        notificacionServicioRemoto.notificaDevolverCCN(facturaVo, proveedorRemote.traerProveedor(facturaVo.getIdProveedor(), facturaVo.getCompania()), motivo, correoSesion);
    }
    
    
    public void procesarFacturaExtranjera(ProveedorVo proveedorVo, FacturaVo facturaVo, OrdenVO compraVo) {
        try {
            boolean continuar;
            // enviar notificacion
            continuar = notificacionServicioRemoto.notificacionEnvioFacturaExtranjeraCliente(correoPara(compraVo.getIdBloque(),Constantes.COD_ROL_REV_FACT), facturaVo, compraVo, proveedorVo);
            //
            if (continuar) {
                SiFacturaStatus facturaStatus = traerFacturaStatus(facturaVo.getId(), FacturaEstadoEnum.CREADA.getId());
                facturaStatus.setActual(Constantes.BOOLEAN_FALSE);
                facturaStatus.setModifico(new Usuario(proveedorVo.getRfc()));
                facturaStatus.setFechaModifico(new Date());
                facturaStatus.setHoraModifico(new Date());
                edit(facturaStatus);
                //
                guardar(facturaVo.getId(), FacturaEstadoEnum.PROCESO_INTERNO_CLIENTE.getId(), proveedorVo.getRfc());
            }
            //
            double totalFacturado = siFacturaRemote.totalPorOrden(compraVo.getId());
            // actualizar la orden de compra            
            if (((Double) totalFacturado).intValue() >= compraVo.getSubTotal().intValue()) {
                autorizacionesOrdenRemote.cambiarStatusOrden(compraVo.getId(), proveedorVo.getRfc(), OrdenEstadoEnum.OCS_RECEPCION_FACTURA.getId());
            }

        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }
}
