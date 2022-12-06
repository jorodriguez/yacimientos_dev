/*
 * AutorizacionesOrdenFacade.java
 * Creada el 13/10/2009, 06:06:26 PM
 * Clase Java desarrollada por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de esta clase, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: new_nick_name@hotmail.com
 */
package sia.servicios.orden.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import sia.constantes.Constantes;
import sia.modelo.AutorizacionesOrden;
import sia.modelo.Estatus;
import sia.modelo.Orden;
import sia.modelo.OrdenDetalle;
import sia.modelo.Usuario;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.sgl.vo.OrdenDetalleVO;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.sgl.vo.RequisicionVO;
import sia.modelo.sistema.AbstractFacade;
import sia.notificaciones.orden.impl.NotificacionOrdenImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.servicios.sistema.vo.SiOpcionVo;
import sia.util.FacturaEstadoEnum;
import sia.util.OrdenEstadoEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 */
@Stateless 
public class AutorizacionesOrdenImpl extends AbstractFacade<AutorizacionesOrden>  {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Inject
    DSLContext dslCtx;

    public AutorizacionesOrdenImpl() {
        super(AutorizacionesOrden.class);
    }
//
    @Inject
    private SiManejoFechaImpl siManejoFechaLocal;
    @Inject
    private OrdenDetalleImpl ordenDetalleRemote;
    @Inject
    private OrdenImpl ordenRemote;
    @Inject
    private NotificacionOrdenImpl notificacionesOrdenRemote;
    @Inject
    private ProveedorServicioImpl proveedorRemote;

    
    public void crear(AutorizacionesOrden autorizacionesOrden) {
        create(autorizacionesOrden);
    }

    
    public void editar(AutorizacionesOrden autorizacionesOrden) {
        edit(autorizacionesOrden);
    }
    
    
    public AutorizacionesOrden buscarPorOrden(int idOrden) {
        return (AutorizacionesOrden) em.createQuery("SELECT a FROM AutorizacionesOrden a WHERE a.orden.id = :idOrden and a.eliminado = false ").setParameter("idOrden", idOrden).getSingleResult();
    }

    
    public List<AutorizacionesOrden> getOrdenesCompraPorProveedor(Integer idProveedor) {
        return em.createQuery("SELECT a FROM AutorizacionesOrden a WHERE a.orden.proveedor.id = :idP AND a.estatus.id BETWEEN :est AND :estatus "
                + " ORDER BY a.id ASC").setParameter("idP", idProveedor).setParameter("est", 101).setParameter("estatus", 149).getResultList();
    }

    
    public List<OrdenVO> traerOrdenComprador(String inicio, String fin, boolean rechazada, int campo) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("select u.id, u.nombre, count(o.ID) as Ordenes, sum(o.TOTAL_USD) as Dolar,sum(o.TOTAL) as Pesos  from orden o ");
            sb.append(" inner join autorizaciones_orden ao on ao.ORDEN = o.ID");
            sb.append(" inner join usuario u on ao.SOLICITO = u.id");
            sb.append(" where ao.FECHA_SOLICITO between to_date('").append(inicio).append("', 'DD/MM/YYYY') and to_date('").append(fin).append("', 'DD/MM/YYYY') ");
            sb.append(" and ao.ESTATUS > ").append(Constantes.ORDENES_SIN_SOLICITAR);
            sb.append(" and o.AP_CAMPO = ").append(campo);
            sb.append(" and ao.RECHAZADA  = ").append(rechazada);
            sb.append(" group by u.id, u.nombre");
            sb.append(" order by u.nombre asc");
            UtilLog4j.log.info(this, "Script compradores : : : : " + sb.toString());
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            List<OrdenVO> lord = null;
            if (lo != null) {
                lord = new ArrayList<>();
                for (Object[] o : lo) {
                    lord.add(castOrdenVO(o));
                }
            }//
            return lord;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al traer los copradores :  :: : : : " + e.getMessage());
            return null;
        }
    }

    
    public List<OrdenVO> traerOrdenGerencia(String inicio, String fin, boolean rechazada, int campo, int status) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("select g.id, g.nombre, count(o.ID) as Ordenes, sum(o.TOTAL_USD) as Dolar,sum(o.TOTAL) as Pesos  from AUTORIZACIONES_ORDEN ao ");
            sb.append(" inner join orden o on ao.ORDEN = o.ID");
            sb.append(" inner join gerencia g on o.gerencia = g.id");
            sb.append(" where ao.FECHA_SOLICITO between cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(inicio)).append("' as date) and cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(fin)).append("' as date)");
            sb.append(" and ao.ESTATUS >= ").append(status);
            sb.append(" and ao.ESTATUS <> ").append(Constantes.ORDENES_CANCELADAS);
            sb.append(" and ao.solicito <> '").append(Constantes.USUARIO_PRUEBA).append("'");
            sb.append(" and o.AP_CAMPO = ").append(campo);
            sb.append(" and ao.RECHAZADA  = ").append(rechazada);
            sb.append(" group by g.id, g.nombre");
            sb.append(" order by g.nombre asc");
            UtilLog4j.log.info(this, "Script gerencia : : : : " + sb.toString());
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            List<OrdenVO> lord = null;
            if (lo != null) {
                lord = new ArrayList<>();
                for (Object[] o : lo) {
                    lord.add(castOrdenVOGerencia(o));
                }
            }//
            return lord;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al traer los gerencia :  :: : : : " + e.getMessage());
            return null;
        }
    }

    
    public List<OrdenVO> traerOrdenPorGerencia(String inicio, String fin, boolean rechazada, int campo, int status, int idGerencia) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(consultaOrden());
            sb.append(" where ao.FECHA_SOLICITO between cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(inicio)).append("' as date) and cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(fin)).append("' as date)");
            sb.append(" and ao.ESTATUS >= ").append(status);
            sb.append(" and ao.solicito <> '").append(Constantes.USUARIO_PRUEBA).append("'");
            sb.append(" and o.AP_CAMPO = ").append(campo);
            if (idGerencia < 999) {
                sb.append(" and o.gerencia = ").append(idGerencia);
            }
            sb.append(" and ao.RECHAZADA  = ").append(rechazada);
            sb.append(" order by g.nombre asc");
            UtilLog4j.log.info(this, "Script por  gerencia : : : : " + sb.toString());
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            List<OrdenVO> lord = null;
            if (lo != null) {
                lord = new ArrayList<>();
                for (Object[] o : lo) {
                    lord.add(castOrdenVOPorGerencia(o));
                }
            }//
            return lord;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al traer los gerencia :  :: : : : " + e.getMessage());
            return null;
        }
    }

    private OrdenVO castOrdenVOPorGerencia(Object[] objects) {
        OrdenVO o = new OrdenVO();
        o.setIdGerencia((Integer) objects[0]);
        o.setGerencia((String) objects[1]);
        o.setIdProveedor((Integer) objects[2]);
        o.setProveedor((String) objects[3]);
        o.setConsecutivo((String) objects[4]);
        o.setReferencia((String) objects[5]);
        o.setFechaSolicita((Date) objects[6]);
        o.setSubTotal((Double) objects[7]);
        o.setIva((Double) objects[8]);
        o.setTotal((Double) objects[9]);
        o.setTotalUsd((Double) objects[10]);
        o.setEstatus((String) objects[11]);
        o.setAnalista((String) objects[12]);
        o.setFecha((Date) objects[13]);
        o.setNombreProyectoOT((String) objects[14]);
        o.setId((Integer) objects[15]);
        o.setMonedaSiglas((String) objects[16]);
        o.setRequisicion((String) objects[17]);
        o.setIdStatus((Integer) objects[18]);
        o.setIdAutorizaOrden((Integer) objects[19]);
        o.setFechaEntrega((Date) objects[20]);
        o.setFechaEnvioProveedor((Date) objects[21]);
        o.setTotalFactura((Long) objects[22]);
        o.setIdBloque((Integer) objects[23]);
        o.setRfcCompania((String) objects[24]);
        o.setCompania((String) objects[25]);
        o.setBloque((String) objects[26]);
        o.setContratoVO(new ContratoVO());
        o.getContratoVO().setCodigo((String) objects[27]);
        o.setNavCode((String) objects[28]);
        o.setUrl((String) objects[29]);
        o.setUrlRequisicion((String) objects[30]);
        o.setUsuarioReq((String) objects[31]);
        o.setDestino((String) objects[32]);
        o.setTerminoPago((String) objects[33]);
        o.setNumeroContrato((String) objects[34]);
        o.setProveedorRepse((boolean) objects[35]);
        o.setTotalCartaIntencionRechazadas((long) objects[36]);
        o.setProveedorRfc((String) objects[37]);
        o.setRepse((boolean) objects[38]);
        o.setFcreada((boolean) objects[39]);
        return o;
    }

    private OrdenVO castOrdenVOGerencia(Object[] objects) {
        OrdenVO o = new OrdenVO();
        o.setIdGerencia((Integer) objects[0]);
        o.setGerencia((String) objects[1]);
        o.setTotalOrdenes((Long) objects[2]);
        o.setTotalUsd((Double) objects[3]);
        o.setTotal((Double) objects[4]);
        return o;
    }

    private OrdenVO castOrdenVO(Object[] objects) {
        OrdenVO o = new OrdenVO();
        o.setIdAnalista((String) objects[0]);
        o.setAnalista((String) objects[1]);
        o.setTotalOrdenes((Long) objects[2]);
        o.setTotalUsd((Double) objects[3]);
        o.setTotal((Double) objects[4]);
        return o;
    }

    
    public List<OrdenVO> traerOrdenPorProveedor(String inicio, String fin, int campo, int status, int idProveedor, String estadoOrden) {
        try {
            StringBuilder s = new StringBuilder();
            if (estadoOrden.equals(Constantes.ENVIADA_PROVEEDOR)) {
                s.append(" and ao.ESTATUS > ").append(status);
            } else {
                s.append(" and ao.ESTATUS between ").append(status).append(" and ").append(Constantes.ESTATUS_AUTORIZADA);
            }
            StringBuilder sb = new StringBuilder();
            sb.append(consultaOrden());
            sb.append(" where ao.FECHA_SOLICITO between cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(inicio)).append("' as date) and cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(fin)).append("' as date)");
            sb.append(s);
            sb.append(" and ao.solicito <> '").append(Constantes.USUARIO_PRUEBA).append("'");
            sb.append(" and o.AP_CAMPO = ").append(campo);
            sb.append(" and o.proveedor = ").append(idProveedor);
            sb.append(" order by p.nombre asc");
            UtilLog4j.log.info(this, "Script por  proveedor : : : : " + sb.toString());
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            List<OrdenVO> lord = null;
            if (lo != null) {
                lord = new ArrayList<>();
                for (Object[] o : lo) {
                    lord.add(castOrdenVOPorGerencia(o));
                }
            }//
            return lord;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al traer los gerencia :  :: : : : " + e.getMessage());
            return null;
        }
    }
    @Inject
    private SiUsuarioRolImpl siUsuarioRolRemote;

    
    public List<OrdenVO> traerOrdenPorStatusUsuario(int campo, int status, String idUsuario, String sesion) {
        List<OrdenVO> listaOr = null;
        try {
            StringBuilder sb = new StringBuilder();
            //modificacion 1 de septiembre 2021
//            if (status != Constantes.ESTATUS_POR_APROBAR_SOCIO) {
            StringBuilder resultado = estatusCampoCondicion(status, idUsuario, campo);

            //
            listaOr = procesarOCS(resultado.toString(), campo);
            /*} else if (siUsuarioRolRemote.buscarRolPorUsuarioModulo(sesion, Constantes.MODULO_COMPRA, Constantes.CODIGO_ROL_SOCIO, campo)) {
                //
                listaOr = procesarOCS(" and ao.estatus = " + status, campo);
            } else if (siUsuarioRolRemote.buscarRolPorUsuarioModulo(sesion, Constantes.MODULO_COMPRA, Constantes.CODIGO_ROL_CONS_REPORTE_COMPRA, campo)) {
                //
                listaOr = procesarOCS(" and ao.estatus = " + status, campo);
            }*/

            UtilLog4j.log.info(this, "Script por  status : : : : " + sb.toString());
            //
            return listaOr;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al traer los estatus:  :: : : : " + e.getMessage());
            return null;
        }
    }

    private List<OrdenVO> procesarOCS(String consulta, int campo) {

        StringBuilder sb = new StringBuilder();
        sb.append(consultaOrden());
        sb.append(" where ao.solicito <> '").append(Constantes.USUARIO_PRUEBA).append("'");
        sb.append(consulta);
        sb.append(" and o.AP_CAMPO = ").append(campo);
        sb.append(" order by p.nombre asc");

        System.out.println("QUERY ORDEN = " + sb.toString());

        List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
        List<OrdenVO> lord = null;
        if (lo != null) {
            lord = new ArrayList<>();
            for (Object[] o : lo) {
                lord.add(castOrdenVOPorGerencia(o));
            }
        }
        return lord;
    }

    
    public List<OrdenVO> traerOrdenSolicitadaPorUsuario(int campo, int status, String idUsuario, String inicio, String fin) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(consultaOrden());
            sb.append(" where ao.solicito = '").append(idUsuario).append("'");
            sb.append(" and ao.FECHA_SOLICITO between cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(inicio)).append("' as date) and cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(fin)).append("' as date)");
            sb.append(" and ao.estatus > ").append(status);
            sb.append(" and o.AP_CAMPO = ").append(campo);
            sb.append(" order by p.nombre asc");
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            List<OrdenVO> lord = null;
            if (lo != null) {
                lord = new ArrayList<>();
                for (Object[] o : lo) {
                    lord.add(castOrdenVOPorGerencia(o));
                }
            }//
            return lord;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al traer los estatus:  :: : : : " + e.getMessage());
            return null;
        }
    }

    private StringBuilder estatusCampoCondicion(int idStatus, String sesion, int campo) {
        StringBuilder comodinWhere = new StringBuilder();
        comodinWhere.append("   AND ao.estatus = ").append(idStatus);
        switch (idStatus) {
            case Constantes.ESTATUS_PENDIENTE_R: //101
                //comodinWhere.append("   AND ord.analista = '").append(sesion).append("'");
                comodinWhere.append("   AND o.analista = '").append(sesion).append("'");
                break;
            case Constantes.ESTATUS_SOLICITADA_R: //110
                comodinWhere.append("   AND ao.autoriza_gerencia = '").append(sesion).append("'");
                break;
            case Constantes.ESTATUS_VISTO_BUENO_R: //120
                comodinWhere.append("   AND ao.autoriza_mpg = '").append(sesion).append("'");
                break;
            case Constantes.ESTATUS_POR_APROBAR_SOCIO: //135
                /*comodinWhere.append("   ")
                        .append(" AND (ao.AUTORIZA_FINANZAS = '").append(sesion).append("'")
                        .append(" or ao.AUTORIZA_FINANZAS is null )");*/

                comodinWhere.append("   AND '")
                        .append(sesion).append("' in (SELECT usuario FROM si_usuario_rol WHERE si_rol =")
                        .append(Constantes.ROL_SOCIO)
                        .append(" AND eliminado = 'False' AND ap_campo =")
                        .append(campo).append(")")
                        .append(" AND (ao.AUTORIZA_FINANZAS IS NULL OR ao.AUTORIZA_FINANZAS = '").append(sesion).append("')");

                /*comodinWhere.append("   AND '")
                        .append(sesion).append("' = (SELECT usuario FROM si_usuario_rol WHERE si_rol =")
                        .append(Constantes.ROL_SOCIO)
                        .append(" AND eliminado = 'False' AND ap_campo =")
                        .append(campo).append(")");*/
                break;
            case Constantes.ESTATUS_REVISADA://130
                comodinWhere.append("   AND ao.autoriza_ihsa = '").append(sesion).append("'");
                break;
            case Constantes.ESTATUS_APROBADA://140
                comodinWhere.append("   AND ao.autoriza_compras = '").append(sesion).append("'");
                break;
            case Constantes.ESTATUS_AUTORIZADA://150
                //comodinWhere.append("   AND ord.analista = '").append(sesion).append("'");
                comodinWhere.append("   AND o.analista = '").append(sesion).append("'");
                break;
            case Constantes.ORDENES_SIN_AUTORIZAR_LICITACION://151
                comodinWhere.append("   AND ao.autoriza_licitacion = '").append(sesion).append("'");
                break;
            case Constantes.ESTATUS_ENVIADA_PROVEEDOR://160
                //comodinWhere.append("   AND ord.analista = '").append(sesion).append("'");
                comodinWhere.append("   AND o.analista = '").append(sesion).append("'");
                break;
            default:
                // comodinWhere.append(" and ao.SOLICITO = '").append(sesion).append("'");
                break;
        }
        return comodinWhere;
    }

    
    public List<OrdenVO> traerOrdenPorProveedor(String inicio, String fin, int campo, int status) {
        List<OrdenVO> lord = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(consultaOrden());
            sb.append(" where ao.solicito <> '").append(Constantes.USUARIO_PRUEBA).append("'");
            sb.append(" and ao.FECHA_SOLICITO between cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(inicio)).append("' as date) and cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(fin)).append("' as date)");
            sb.append(" and ao.estatus > ").append(status);
            sb.append(" and o.AP_CAMPO = ").append(campo);
            sb.append(" order by p.nombre asc");
            UtilLog4j.log.info(this, "Script por  total OC/S enviadas : : : : " + sb.toString());
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();

            if (lo != null) {
                lord = new ArrayList<>();
                for (Object[] o : lo) {
                    lord.add(castOrdenVOPorGerencia(o));
                }
            }//
            return lord;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al traer los estatus:  :: : : : " + e.getMessage());
        }
        return lord;
    }

    
    public List<OrdenVO> traerOrdenPorProveedorContrato(String inicio, String fin, int idCampo, String panelSeleccion, int status, String estado, int moneda) {
        try {
            StringBuilder panel = new StringBuilder();
            if (panelSeleccion.equals("Si")) {
                panel.append(" in ");
            } else {
                panel.append(" not in ");
            }
            StringBuilder sb = new StringBuilder();
            sb.append("select p.id, p.nombre, count(o.id) as Ordenes, sum(o.SUBTOTAL) as Subtotal, sum(o.IVA) as IVA, sum(o.TOTAL) as total, m.siglas ");
            sb.append(" from AUTORIZACIONES_ORDEN ao  ");
            sb.append(" inner join orden o on ao.ORDEN = o.ID ");
            sb.append(" inner join proveedor p on o.proveedor = p.id ");
            sb.append(" inner join moneda m on o.moneda = m.id ");
            sb.append(" where ao.solicito <> '").append(Constantes.USUARIO_PRUEBA).append("'");
            sb.append(" and ao.FECHA_SOLICITO between cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(inicio)).append("' as date) and cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(fin)).append("' as date)");
            sb.append(" and ao.estatus > ").append(status);
            sb.append(" and o.AP_CAMPO = ").append(idCampo);
            sb.append(" and o.moneda = ").append(moneda);
            sb.append(" and p.id ").append(panel.toString()).append(" (select c.PROVEEDOR from CONVENIO c where c.FECHA_VENCIMIENTO > cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(fin)).append("' as date))");
            sb.append(" group by p.ID, p.NOMBRE, m.SIGLAS ");
            sb.append(" order by p.nombre asc");
            UtilLog4j.log.info(this, "Script por  total proveedor: : : : " + sb.toString());
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            List<OrdenVO> lord = null;
            if (lo != null) {
                lord = new ArrayList<>();
                for (Object[] o : lo) {
                    lord.add(castOrdenVOPorProveedorContrato(o, inicio, fin, idCampo, status, moneda));
                }
            }//
            return lord;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al traer los estatus:  :: : : : " + e.getMessage());
            return null;
        }
    }

    private OrdenVO castOrdenVOPorProveedorContrato(Object[] objects, String inicio, String fin, int idCampo, int status, int moneda) {
        OrdenVO o = new OrdenVO();
        o.setIdProveedor((Integer) objects[0]);
        o.setProveedor((String) objects[1]);
        o.setTotalOrdenes((Long) objects[2]);
        o.setSubTotal((Double) objects[3]);
        o.setIva((Double) objects[4]);
        o.setTotal((Double) objects[5]);
        o.setMoneda((String) objects[6]);
        //Llena las OCS por proveedor
        o.setListaOrden(traerOrdenPorProveedor(inicio, fin, idCampo, status, o.getIdProveedor(), moneda));
        return o;
    }

    private List<OrdenVO> traerOrdenPorProveedor(String inicio, String fin, int campo, int status, int idProveedor, int idMoneda) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(consultaOrden());
            sb.append(" where ao.FECHA_SOLICITO between cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(inicio)).append("' as date) and cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(fin)).append("' as date)");
            sb.append(" and ao.ESTATUS > ").append(status);
            sb.append(" and ao.solicito <> '").append(Constantes.USUARIO_PRUEBA).append("'");
            sb.append(" and o.AP_CAMPO = ").append(campo);
            sb.append(" and o.proveedor = ").append(idProveedor);
            sb.append(" and o.moneda = ").append(idMoneda);
            sb.append(" order by p.nombre asc");
            UtilLog4j.log.info(this, "Script por  proveedor : : : : " + sb.toString());
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            List<OrdenVO> lord = null;
            if (lo != null) {
                lord = new ArrayList<>();
                for (Object[] o : lo) {
                    lord.add(castOrdenVOPorGerencia(o));
                }
            }//
            return lord;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al traer los gerencia :  :: : : : " + e.getMessage());
            return null;
        }
    }

    
    public List<OrdenVO> traerSolDevCan(int dev, int canc, int campo, String inicio, String fin) {
        /**
         * *
         */
        StringBuilder sb = new StringBuilder();
        sb.append(" select u.NOMBRE, (count(ao.ID) - (count(om.ID) + count(oom.ID))), count(om.ID) as Devueltas,count(oom.ID) as Canceladas from AUTORIZACIONES_ORDEN ao");
        sb.append(" inner join ORDEN o on ao.ORDEN = o.ID");
        sb.append(" inner join USUARIO u on ao.SOLICITO = u.ID");
        sb.append(" left join ORDEN_SI_MOVIMIENTO om on om.ORDEN = o.ID ");
        sb.append(" and om.SI_MOVIMIENTO in (select m.ID from SI_MOVIMIENTO m where m.SI_OPERACION = ").append(dev).append(")");
        sb.append(" and ao.ESTATUS > ").append(Constantes.ORDENES_CANCELADAS);
        //
        sb.append(" left join ORDEN_SI_MOVIMIENTO oom on oom.ORDEN = o.ID");
        sb.append(" and oom.SI_MOVIMIENTO in (select mm.ID from SI_MOVIMIENTO mm where mm.SI_OPERACION = ").append(canc).append(")");
        sb.append(" and ao.ESTATUS = ").append(Constantes.ORDENES_CANCELADAS);
        //
        sb.append(" where ao.FECHA_SOLICITO between cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(inicio)).append("' as date) and cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(fin)).append("' as date)");
        sb.append(" and o.AP_CAMPO = ").append(campo);
        sb.append(" and u.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        sb.append(" group by u.NOMBRE");
        //
        //System.out.println("Script por  proveedor : : : : " + sb.toString());
        List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
        List<OrdenVO> lord = null;
        if (lo != null) {
            lord = new ArrayList<>();
            for (Object[] o : lo) {
                lord.add(castOrdenVOSolDevCan(o));
            }
        }//
        return lord;
    }

    private OrdenVO castOrdenVOSolDevCan(Object[] o) {
        OrdenVO ord = new OrdenVO();
        ord.setAnalista((String) o[0]);
        ord.setTotalOrdenes(((Long) o[1]).intValue());
        ord.setTotalDevueltas((Long) o[2]);
        ord.setTotalCanceladas((Long) o[3]);
        return ord;
    }

    
    public void crearAutorizaOrden(Orden orden, int status) {
        AutorizacionesOrden autorizacionesOrden = new AutorizacionesOrden();
        autorizacionesOrden.setOrden(orden);
        autorizacionesOrden.setEstatus(new Estatus(status));
        autorizacionesOrden.setErrorEnvio(Constantes.BOOLEAN_FALSE);
        autorizacionesOrden.setRechazada(Constantes.BOOLEAN_FALSE);
        autorizacionesOrden.setGenero(orden.getAnalista());
        autorizacionesOrden.setFechaGenero(new Date());
        autorizacionesOrden.setHoraGenero(new Date());
        autorizacionesOrden.setEliminado(Constantes.NO_ELIMINADO);
        create(autorizacionesOrden);

    }

    
    public void cambiarAnalistaOCS(String sesion, int idOrden, String analista) {
        AutorizacionesOrden ao = buscarPorOrden(idOrden);
        ao.setSolicito(new Usuario(analista));
        ao.setModifico(new Usuario(sesion));
        ao.setFechaModifico(new Date());
        ao.setHoraModifico(new Date());
        edit(ao);
    }

    
    public List<OrdenVO> traerOrdenSinAutorizar(int status, int dias, int campo) {
        try {
            String cadenaCampo = "";
            if (campo > 0) {
                cadenaCampo = " and o.ap_campo = ".concat("" + campo);
            }
            StringBuilder sb = new StringBuilder();
            sb.append(consultaOrden());
            sb.append(" where ao.solicito <> '").append(Constantes.USUARIO_PRUEBA).append("'");
            sb.append(" and ao.FECHA_AUTORIZO_IHSA <= (SELECT CURRENT_DATE - ").append(dias).append(")");
            sb.append(" and ao.estatus = ").append(status);
            sb.append(cadenaCampo);
            sb.append(" order by p.nombre asc");
            UtilLog4j.log.info(this, "Script por  total ordenes sin autorizar : : : : " + sb.toString());
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            List<OrdenVO> lord = null;
            if (lo != null) {
                lord = new ArrayList<>();
                for (Object[] o : lo) {
                    lord.add(castOrdenVOPorGerencia(o));
                }
            }//
            return lord;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al traer los estatus:  :: : : : " + e.getMessage());
            return null;
        }
    }

    private String consultaOrden() {
        StringBuilder sb = new StringBuilder();
        sb.append("select g.id, g.nombre, p.id, p.nombre, o.CONSECUTIVO, o.REFERENCIA, ao.FECHA_SOLICITO, ")
                .append(" o.SUBTOTAL, o.IVA, o.TOTAL,  o.TOTAL_USD, e.NOMBRE, u.nombre, o.fecha, pot.nombre, ")
                .append(" o.id , m.siglas, r.consecutivo, e.id, ao.id, o.fecha_entrega, ao.fecha_envio_proveedor")
                .append(" ,  (SELECT count(*) from si_factura f "
                        + "  	inner join oc_factura_status fe on fe.si_factura = f.id and fe.estatus > " + FacturaEstadoEnum.CREADA.getId()
                        + " where f.orden = o.id and f.eliminado = false), ")
                .append(" o.ap_campo, o.compania, com.nombre, cam.nombre , o.contrato, o.navcode, o.url, r.url,ur.nombre  ")
                .append(" , o.destino, ter.nombre, o.contrato, p.repse ")
                .append(" , (select count(osm.id) from orden_si_movimiento osm \n")
                .append(" 		inner join si_movimiento sm on osm.si_movimiento  = sm.id \n")
                .append(" 	where  osm.orden = o.id\n")
                .append(" 	and sm.si_operacion = 98\n")
                .append(" 	and osm.eliminado = false)")
                .append(" , p.rfc, o.repse, ")
                .append(" (select count(a.id) > 0 from si_factura a inner join oc_factura_status ofe on ofe.si_factura = a.id and ofe.actual = true and ofe.estatus = 710 where a.eliminado = false and a.orden = o.id ) as fpend ")
                .append(" from orden o  ")
                .append("     inner join ap_campo cam on o.ap_campo = cam.id ")
                .append("     inner join compania com on o.compania = com.rfc ")
                .append("     inner join AUTORIZACIONES_ORDEN ao on ao.ORDEN = o.ID")
                .append("     inner join ESTATUS e on ao.ESTATUS = e.ID ")
                .append("     inner join proveedor p on o.proveedor = p.id")
                .append("     inner join gerencia g on o.gerencia = g.id")
                .append("     inner join usuario u on o.analista = u.id")
                .append("     inner join proyecto_ot pot on o.proyecto_ot = pot.id")
                .append("     inner join moneda m on o.moneda = m.id")
                .append("     inner join requisicion r on o.requisicion = r.id ")
                .append("     inner join usuario ur on ur.id = r.solicita ")
                .append("     inner join oc_termino_pago ter on o.oc_termino_pago = ter.id");
        return sb.toString();
    }

    
    public List<OrdenVO> traerOrdenSinTerminar(int status, int campo, String sesion, boolean cantidadCero) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(consultaOrden().replace(" from orden o  ", ", (SELECT COALESCE(sum(od.cantidad), 0) from ORDEN_DETALLE od where od.orden = o.id and od.ELIMINADO = 'False'), "
                    + "(SELECT COALESCE(sum(od.CANTIDAD_RECIBIDA), 0) from ORDEN_DETALLE od where od.orden = o.id  and od.ELIMINADO = 'False'), o.leida, o.fecha_entrega  "
                    + "  \n from orden o "));
            sb.append(" where ao.estatus = ").append(status);
            if (!sesion.isEmpty()) {
                sb.append(" and ao.solicito = '").append(sesion).append("'");
            }
            if (cantidadCero) {
                sb.append(" and (SELECT count(od.id) from ORDEN_DETALLE od where od.orden = o.id and od.ELIMINADO = 'False' and (od.cantidad_recibida is null or od.cantidad_recibida = 0 )) > 0 ");
            }

            sb.append(" and o.ap_campo = ").append(campo);
            sb.append(" and o.eliminado  =  '").append(Constantes.NO_ELIMINADO).append("'");
            sb.append(" order by o.fecha desc ");
            //
            // 
            //System.out.println("cons: " + sb.toString());
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            List<OrdenVO> lord = null;
            if (lo != null) {
                lord = new ArrayList<>();
                for (Object[] objects : lo) {
                    OrdenVO o = new OrdenVO();
                    o.setIdGerencia((Integer) objects[0]);
                    o.setGerencia((String) objects[1]);
                    o.setIdProveedor((Integer) objects[2]);
                    o.setProveedor((String) objects[3]);
                    o.setConsecutivo((String) objects[4]);
                    o.setReferencia((String) objects[5]);
                    o.setFechaSolicita((Date) objects[6]);
                    o.setSubTotal((Double) objects[7]);
                    o.setIva((Double) objects[8]);
                    o.setTotal((Double) objects[9]);
                    o.setTotalUsd((Double) objects[10]);
                    o.setEstatus((String) objects[11]);
                    o.setAnalista((String) objects[12]);
                    o.setFecha((Date) objects[13]);
                    o.setNombreProyectoOT((String) objects[14]);
                    o.setId((Integer) objects[15]);
                    o.setMonedaSiglas((String) objects[16]);
                    o.setRequisicion((String) objects[17]);
                    o.setIdStatus((Integer) objects[18]);
                    o.setIdAutorizaOrden((Integer) objects[19]);
                    o.setFechaEntrega((Date) objects[20]);
                    o.setFechaEnvioProveedor((Date) objects[21]);
                    o.setTotalFactura((Long) objects[22]);
                    o.setIdBloque((Integer) objects[23]);
                    o.setRfcCompania((String) objects[24]);
                    o.setCompania((String) objects[25]);
                    o.setBloque((String) objects[26]);
                    o.setContratoVO(new ContratoVO());
                    o.getContratoVO().setNombre((String) objects[27]);
                    o.setNavCode((String) objects[28]);
                    o.setUrl((String) objects[29]);
                    o.setUrlRequisicion((String) objects[30]);
                    o.setUsuarioReq((String) objects[31]);
                    o.setDestino((String) objects[32]);
                    o.setTerminoPago((String) objects[33]);
                    o.setNumeroContrato((String) objects[34]);
                    o.setProveedorRepse((boolean) objects[35]);
                    o.setTotalCartaIntencionRechazadas((long) objects[36]);
                    o.setProveedorRfc((String) objects[37]);
                    o.setRepse((boolean) objects[38]);
                    //
                    o.setFcreada((Boolean) objects[39]);
                    o.setTotalItems(objects[40] != null ? (Double) objects[40] : 0);
                    o.setTotalRecibidos(objects[41] != null ? (Double) objects[41] : 0);
                    o.setLeida((Boolean) objects[42]);
                    o.setFechaEntrega((Date) objects[43]);
                    o.setInicioEjecucion(o.getFechaEntrega());
                    lord.add(o);
                }
            }//
            return lord;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            UtilLog4j.log.fatal(this, "Ocurrio un error las ocs por terminar:  :: : : : " + e);
            return null;
        }
    }

    
    public List<OrdenVO> traerOrdenSolicidasPorMesAnio(int mes, int anio, int status, int campo, String contrato) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(consultaOrden());
            sb.append(" where ao.estatus > ").append(status);
            sb.append(" and extract(month from ao.fecha_solicito) = ").append(mes);
            sb.append(" and extract(year from ao.fecha_solicito) = ").append(anio);
            sb.append(" and o.contrato = '").append(contrato).append("'");
            sb.append(" and o.ap_campo = ").append(campo);
            sb.append(" and o.eliminado  =  '").append(Constantes.NO_ELIMINADO).append("'");
            sb.append(" order by o.fecha desc ");

            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            List<OrdenVO> lord = null;
            if (lo != null) {
                lord = new ArrayList<>();
                for (Object[] o : lo) {
                    lord.add(castOrdenVOPorGerencia(o));
                }
            }//
            return lord;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error las ocs por terminar:  :: : : : " + e.getMessage());
            return null;
        }
    }

    
    public List<RequisicionVO> traerOrdenPorEstatus(int status, int campo, String analista) {
        List<RequisicionVO> lr = null;
        String sb = "SELECT r.id, r.consecutivo, r.referencia, r.proveedor, r.FECHA_SOLICITO, r.FECHA_ASIGNO, r.fecha_requerida, r.MONTOTOTAL_USD, o.fecha_genero  FROM orden o"
                + "	inner join requisicion r on o.requisicion = r.id"
                + "	inner join autorizaciones_orden ao on ao.orden = o.id"
                + "  WHERE ao.ESTATUS = " + status + " and o.ap_campo = " + campo + ""
                + "	 and o.analista = '" + analista + "'"
                + "  and o.eliminado = 'False' "
                + " order by o.fecha_genero asc";
        List<Object[]> l = em.createNativeQuery(sb).getResultList();
        if (l != null) {
            lr = new ArrayList<>();
            for (Object[] objects : l) {
                RequisicionVO o = new RequisicionVO();
                o.setId((Integer) objects[0]);
                o.setConsecutivo(String.valueOf(objects[1]));
                o.setReferencia(String.valueOf(objects[2]));
                o.setProveedor((String) objects[3]);
                o.setFechaSolicitada((Date) objects[4]);
                o.setFechaAsignada((Date) objects[5]);
                o.setFechaRequerida((Date) objects[6]);
                o.setMontoDolares((Double) objects[7]);
                o.setFechaGenero((Date) objects[8]);
                o.setSelected(false);

                lr.add(o);
            }
        }
        return lr;
    }

    
    public Integer totalOrdenPorEstatusUsuraio(int status, int campo, String nombreUsuario) {
        try {
            String sb = "SELECT count(o.ID) FROM autorizaciones_orden ao"
                    + "	inner join orden o on o.id = ao.orden"
                    + "	inner join usuario u on o.analista = u.id"
                    + "  WHERE ao.ESTATUS = " + status + " and o.ap_campo = " + campo + ""
                    + "	 and u.nombre = '" + nombreUsuario + "'"
                    + "  and u.eliminado = 'False' "
                    + "  and o.eliminado = 'False' ";

            return (Integer) em.createNativeQuery(sb).getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return 0;
        }
    }

    
    public List<OrdenVO> ordenesPorFechaEntrega(int idCampo, String fecha, int status) {

        String sb = "select g.id, g.nombre, p.id, p.nombre, o.CONSECUTIVO, o.REFERENCIA, ao.FECHA_SOLICITO, \n"
                + "         o.SUBTOTAL, o.IVA, o.TOTAL,  o.TOTAL_USD, u.nombre, o.fecha,\n"
                + "         o.id , m.siglas, r.consecutivo, ao.id, o.fecha_entrega,\n"
                + "         (SELECT sum(od.cantidad) from ORDEN_DETALLE od where od.orden = o.id and od.ELIMINADO = 'False'), \n"
                + "         (SELECT sum(od.CANTIDAD_RECIBIDA) from ORDEN_DETALLE od where od.orden = o.id  and od.ELIMINADO = 'False'),\n"
                + "         (CURRENT_date - FECHA_ENTREGA), ao.FECHA_AUTORIZO_COMPRAS \n"
                + " from AUTORIZACIONES_ORDEN ao  \n"
                + "        inner join orden o on ao.ORDEN = o.ID\n"
                + "        inner join proveedor p on o.proveedor = p.id\n"
                + "        inner join gerencia g on o.gerencia = g.id\n"
                + "        inner join usuario u on o.analista = u.id\n"
                + "        inner join moneda m on o.moneda = m.id\n"
                + "        inner join requisicion r on o.requisicion = r.id"
                + "   where o.ap_campo = ?1"
                + "     and o.fecha_entrega <= ?2";
        if (status == 1) {
            sb += " and ao.estatus between " + OrdenEstadoEnum.POR_VOBO.getId() + " and " + OrdenEstadoEnum.POR_RECIBIR.getId();
        } else {
            sb += " and ao.estatus = " + OrdenEstadoEnum.POR_RECIBIR.getId();
        }

        sb += "     and o.eliminado = 'False'"
                + "     and ao.estatus <> " + Constantes.ORDENES_CANCELADAS + ""
                + " order by r.consecutivo desc";
        UtilLog4j.log.info(this, "Script por ordenes entregadas : : : : " + sb);
        Query q = em.createNativeQuery(sb);
        q.setParameter(1, idCampo);
        q.setParameter(2, siManejoFechaLocal.convertirStringFechaddMMyyyy(fecha));

        List<Object[]> lo = q.getResultList();
        List<OrdenVO> lord = null;
        if (lo != null) {
            lord = new ArrayList<>();
            for (Object[] objects : lo) {
                OrdenVO o = new OrdenVO();
                o.setIdGerencia((Integer) objects[0]);
                o.setGerencia((String) objects[1]);
                o.setIdProveedor((Integer) objects[2]);
                o.setProveedor((String) objects[3]);
                o.setConsecutivo((String) objects[4]);
                o.setReferencia((String) objects[5]);
                o.setFechaSolicita((Date) objects[6]);
                o.setSubTotal((Double) objects[7]);
                o.setIva((Double) objects[8]);
                o.setTotal((Double) objects[9]);
                o.setTotalUsd((Double) objects[10]);
                o.setAnalista((String) objects[11]);
                o.setFecha((Date) objects[12]);
                o.setId((Integer) objects[13]);
                o.setMonedaSiglas((String) objects[14]);
                o.setRequisicion((String) objects[15]);
                o.setIdAutorizaOrden((Integer) objects[16]);
                o.setFechaEntrega((Date) objects[17]);
                o.setTotalItems((Double) objects[18]);
                o.setTotalRecibidos(objects[19] != null ? (Double) objects[19] : Constantes.CERO);
                o.setTotalPendiente(o.getTotalItems() - o.getTotalRecibidos());
                o.setDiasEntrega(((Integer) objects[20]));
                o.setFechaAutoriza(objects[21] != null ? (Date) objects[21] : null);
                lord.add(o);
            }//
        }
        return lord;
    }

    
    public void marcarOrdenRecibida(String sesion, OrdenVO ordenVO) {
        AutorizacionesOrden autorizacionesOrden = find(ordenVO.getIdAutorizaOrden());
        if (autorizacionesOrden != null) {
            //
            List<OrdenDetalleVO> od = ordenVO.getDetalleOrden();
            boolean completa = true;
            double total = 0;
            for (OrdenDetalleVO odVo : od) {
                OrdenDetalle ordenDetalle = ordenDetalleRemote.find(odVo.getId());
                if (ordenDetalle != null) {
                    if (!ordenDetalle.isRecibido()) {
                        if (ordenDetalle.getUnidadesRecibidas() != null) {
                            ordenDetalle.setUnidadesRecibidas(ordenDetalle.getUnidadesRecibidas() + odVo.getTotalPendiente());
                        } else {
                            ordenDetalle.setUnidadesRecibidas(odVo.getTotalPendiente());
                        }
                        ordenDetalle.setFechaRecepcion(odVo.getFechaRecibido());
                        ordenDetalle.setModifico(new Usuario(sesion));
                        ordenDetalle.setFechaModifico(new Date());
                        ordenDetalle.setHoraModifico(new Date());

                        BigDecimal solicitada = new BigDecimal(ordenDetalle.getCantidad());
                        BigDecimal recibida = new BigDecimal(ordenDetalle.getUnidadesRecibidas());

                        if (solicitada.setScale(2, RoundingMode.HALF_EVEN).compareTo(recibida.setScale(2, RoundingMode.HALF_EVEN)) == 0) {
                            ordenDetalle.setRecibido(Constantes.BOOLEAN_TRUE);
                            ordenDetalle.setUnidadesRecibidas(ordenDetalle.getCantidad());
                        } else {
                            ordenDetalle.setRecibido(Constantes.BOOLEAN_FALSE);
                        }
                        ordenDetalleRemote.editar(ordenDetalle);
                    }
                    //
                    total += ordenDetalle.getUnidadesRecibidas();
                }
                //
            }
            if (BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP).compareTo(BigDecimal.valueOf(ordenVO.getTotalItems()).setScale(2, RoundingMode.HALF_UP)) >= 0) {
                autorizacionesOrden.setEstatus(new Estatus(Constantes.ESTATUS_ORDEN_RECIBIDA));
            } else { // incompleta      
                completa = false;
                autorizacionesOrden.setEstatus(new Estatus(Constantes.ESTATUS_ORDEN_RECIBIDA_PARCIAL));
            }
            //
            autorizacionesOrden.setModifico(new Usuario(sesion));
            autorizacionesOrden.setFechaModifico(new Date());
            autorizacionesOrden.setHoraModifico(new Date());
            edit(autorizacionesOrden);
            //
            Orden o = autorizacionesOrden.getOrden();
            o.setInicioEjecucion(ordenVO.getInicioEjecucion());
            o.setFinEjecucion(ordenVO.getFinEjecucion());
            o.setModifico(new Usuario(sesion));
            o.setFechaModifico(new Date());
            o.setHoraModifico(new Date());
            //
            ordenRemote.editarOrden(o);
            //Notificar la recepcion de la compra.
            //out.println("Comprador: " +  o.getAnalista().getNombre());
            notificacionesOrdenRemote.enviarNotificacionRecepcionOrden(o.getRequisicion().getGenero().getEmail(), autorizacionesOrden.getAutorizaMpg().getEmail(), o.getAnalista().getEmail() + "," + proveedorRemote.correosProveedorOrden(o.getId()), o, "Recepción de Orden de compra", od, completa);
        }
    }

    /*
     07/jul/2021
    Author: Joel Rodriguez
    Se cambia el alias de la tabla ord por o debido a que lanza un error al usar el retorno del metodo estatusCampoCondicion, en este se usa el alias ord y debe ser o
     */
    
    public SiOpcionVo totalRevPagina(String usuario, int campo, int status) {
        SiOpcionVo retVal = null;

        String sql
                = "SELECT opc.nombre, opc.pagina, count(ao.id) AS total \n"
                + "FROM si_opcion opc\n"
                + "	INNER JOIN autorizaciones_orden ao ON ao.estatus = opc.estatus_contar AND opc.estatus_contar IS NOT NULL\n"
                + "	INNER JOIN orden o ON ao.orden = o.id\n"
                + " WHERE opc.ELIMINADO = 'False' \n"
                + estatusCampoCondicion(status, usuario, campo)
                + "\n   AND opc.si_modulo = " + Constantes.MODULO_COMPRA
                + "\n	AND o.ap_campo = " + campo
                + "\n	AND o.eliminado = 'False' \n"
                + "GROUP by opc.nombre, opc.pagina \n"
                + "HAVING count(ao.id) > 0";
        try {

            Record r = dslCtx.fetchOne(sql);

            if (r != null) {
                retVal = r.into(SiOpcionVo.class);
            }

        } catch (DataAccessException e) {
            UtilLog4j.log.error(this, "", e);
        }

        return retVal;
    }

    /*
    public SiOpcionVo totalRevPagina(String usuario, int campo, int status) {
        SiOpcionVo retVal = null;

        String sql
                = "SELECT o.nombre, o.pagina, count(ao.id) AS total \n"
                + "FROM si_opcion o\n"
                + "	INNER JOIN autorizaciones_orden ao ON ao.estatus = o.estatus_contar AND o.estatus_contar IS NOT NULL\n"
                + "	INNER JOIN orden ord ON ao.orden = ord.id\n"
                + " WHERE o.ELIMINADO = 'False' \n"
                + estatusCampoCondicion(status, usuario, campo)
                + "\n   AND o.si_modulo = " + Constantes.MODULO_COMPRA
                + "\n	AND ord.ap_campo = " + campo
                + "\n	AND ord.eliminado = 'False' \n"
                + "GROUP by o.nombre, o.pagina \n"
                + "HAVING count(ao.id) > 0";
        try {

            Record r = dslCtx.fetchOne(sql);

            if (r != null) {
                retVal = r.into(SiOpcionVo.class);
            }

        } catch (DataAccessException e) {
            UtilLog4j.log.error(this, "", e);
        }

        return retVal;
    }*/
    
    public List<SiOpcionVo> totalRevPagina(String id, int idCampo, OrdenEstadoEnum[] estadosOrd) {
        List<SiOpcionVo> lo = new ArrayList<>();
        for (OrdenEstadoEnum ordenEstadoEnum : estadosOrd) {
            SiOpcionVo so = totalRevPagina(id, idCampo, ordenEstadoEnum.getId());
            if (so != null) {
                so.setIdCampo(idCampo);
                lo.add(so);
            }
        }
        return lo;
    }

    /**
     * Devolver información sobre las órdenes pendientes del usuario.
     *
     * @param id El identificador del usuario.
     * @return Un mapa por campo de los contadores de órdenes pendientes para el
     * usuario.
     */
    public Map<Integer, List<SiOpcionVo>> totalRevPagina(String id) {

        StringBuilder sql = new StringBuilder();
        sql.append(
                " (SELECT ord.ap_campo AS id_campo, o.nombre, o.pagina, count(ao.id) AS total \n"
                + "FROM si_opcion o\n"
                + "   INNER JOIN autorizaciones_orden ao ON ao.estatus = o.estatus_contar AND o.estatus_contar IS NOT NULL\n"
                + "   INNER JOIN orden ord ON ao.orden = ord.id\n"
                + "WHERE o.ELIMINADO = 'False' \n"
                + "   AND (\n"
                + "       (ao.estatus, ?) IN (\n"
        )
                // agregamos lo de los estatus y los campos específicos
                .append(generarEstatusUsuario())
                .append(
                        "   )\n"
                        + "   AND o.si_modulo = ?\n"
                        + "   AND ord.eliminado = 'False' \n"
                        + "GROUP by ord.ap_campo, o.nombre, o.pagina \n"
                        + "   HAVING count(ao.id) > 0\n"
                        + "ORDER BY ord.ap_campo) "
                );
        //out.println("sql: " + sql.toString());

        sql.append(
                "union ( "
                + "SELECT c.id AS id_campo, 'Evaluaciones' as nombrex, '/vistas/SiaWeb/evaluaciones/EvaluacionProveedor' as pagina, count(ev.id) AS total "
                + "FROM ap_campo c "
                + "   left JOIN convenio co on co.ap_campo = c.id and co.eliminado = false "
                + "   left JOIN cv_evaluacion ev on ev.convenio = co.id and ev.contestada = false AND ev.eliminado = false and ev.responsable = ? "
                + "WHERE c.ELIMINADO = 'False'              "
                + "GROUP by c.id, nombrex, pagina "
                + "   HAVING count(ev.id) > 0 "
                + "ORDER BY c.id) "
        );

        Map<Integer, List<SiOpcionVo>> retVal = null;

        try {

            List<SiOpcionVo> lo
                    = dslCtx.fetch(sql.toString(), id, id, id, Constantes.MODULO_COMPRA, id)
                            .into(SiOpcionVo.class);

            if (!lo.isEmpty()) {
                retVal = new HashMap();

                for (SiOpcionVo opcion : lo) {
                    if (retVal.get(opcion.getIdCampo()) == null) {
                        retVal.put(opcion.getIdCampo(), new ArrayList<SiOpcionVo>());
                    }

                    retVal.get(opcion.getIdCampo()).add(opcion);
                }
            }

        } catch (DataAccessException e) {
            UtilLog4j.log.fatal(this, "", e);
            retVal = Collections.emptyMap();
        }

        return retVal;
    }

    private String generarEstatusUsuario() {
        StringBuilder estatusUsuario = new StringBuilder();

        estatusUsuario.append("\t\t\t(").append(OrdenEstadoEnum.POR_SOLICITAR.getId())
                .append(", ord.analista),\n");

        estatusUsuario.append("\t\t\t(").append(OrdenEstadoEnum.POR_VOBO.getId())
                .append(", ao.autoriza_gerencia),\n");

        estatusUsuario.append("\t\t\t(").append(OrdenEstadoEnum.POR_REVISAR.getId())
                .append(", ao.autoriza_mpg),\n");

        estatusUsuario.append("\t\t\t(").append(OrdenEstadoEnum.POR_APROBAR_SOCIO.getId())
                .append(", (SELECT usuario FROM si_usuario_rol WHERE si_rol = 40 AND eliminado = 'False' AND ap_campo = ord.ap_campo AND usuario = ?)),\n");

        estatusUsuario.append("\t\t\t(").append(OrdenEstadoEnum.POR_APROBAR.getId())
                .append(", ao.autoriza_ihsa),\n");

        estatusUsuario.append("\t\t\t(").append(OrdenEstadoEnum.POR_AUTORIZAR.getId())
                .append(", ao.autoriza_compras),\n");

        estatusUsuario.append("\t\t\t(").append(OrdenEstadoEnum.POR_REVISAR_REPSE.getId())
                .append(", (SELECT ur.usuario FROM si_usuario_rol ur ")
                .append("     inner join si_rol r on ur.si_rol = r.id")
                .append(" WHERE r.codigo = '")
                .append(Constantes.COD_ROL_ORDEN_REPSE)
                .append("' AND ur.eliminado = false  AND ord.ap_campo = ur.ap_campo AND ur.usuario = ?)),\n");

        estatusUsuario.append("\t\t\t(").append(OrdenEstadoEnum.POR_ENVIAR_PROVEEDOR.getId())
                .append(", ord.analista),\n");

        estatusUsuario.append("\t\t\t(").append(OrdenEstadoEnum.POR_AUTORIZAR_1MMD.getId())
                .append(", ao.autoriza_licitacion),\n");

        estatusUsuario.append("\t\t\t(").append(OrdenEstadoEnum.POR_RECIBIR.getId())
                .append(", ord.analista)\n");

        //ao.estatus IN (165, 170, 175, 190)
        estatusUsuario.append("\t\t)\n")
                .append("\t\tOR ao.estatus IN (")
                .append(OrdenEstadoEnum.POR_RECIBIR_FACTURA.getId()).append(',')
                .append(OrdenEstadoEnum.OCS_RECEPCION_FACTURA.getId()).append(',')
                .append(OrdenEstadoEnum.OCS_PROCESO_FACTURA.getId()).append(',')
                .append(OrdenEstadoEnum.OCS_PAGADA.getId()).append(")\n");

        return estatusUsuario.toString();
    }

    
    public List<OrdenVO> traerOrdenPorStatusProveedor(int status, int idProveedor, int campo, String empresa) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(consultaOrden())
                    .append(" where ao.ESTATUS = ").append(status)
                    .append(" and o.proveedor = ").append(idProveedor)
                    .append(" and o.compania = '").append(empresa).append("'");
            if (campo > 0) {
                sb.append(" and o.ap_campo = ").append(campo);
            }
            sb.append(" order by o.fecha asc");
            UtilLog4j.log.info(this, "Script por  proveedor y status : : : : " + sb.toString());
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            List<OrdenVO> lord = null;
            if (lo != null) {
                lord = new ArrayList<>();
                for (Object[] o : lo) {
                    lord.add(castOrdenVOPorGerencia(o));
                }
            }//
            return lord;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al traer los gerencia :  :: : : : " + e.getMessage());
            return null;
        }
    }

    
    public void cambiarStatusOrden(int idOrden, String sesion, int idStatus) {
        AutorizacionesOrden ao = buscarPorOrden(idOrden);
        ao.setEstatus(new Estatus(idStatus));
        ao.setModifico(new Usuario(sesion));
        ao.setFechaModifico(new Date());
        ao.setHoraModifico(new Date());
        //
        edit(ao);
    }

    /**
     *
     * @param status
     * @param campo
     * @param proveedor
     * @return
     */
    
    public long totalOrdenPorEstatusProveedor(int status, int campo, int proveedor) {
        try {
            StringBuilder sb = new StringBuilder("SELECT count(o.ID) FROM orden o")
                    .append("   inner join autorizaciones_orden ao on ao.orden = o.id ")
                    .append("  WHERE ao.ESTATUS = ?1 and o.proveedor = ?2 and o.eliminado = false and o.ap_campo  ");
            if (campo > 0) {
                sb.append(" = ").append("?3");
            } else {
                sb.append(" > ").append("?3");
            }

            return (Long) em.createNativeQuery(sb.toString()).setParameter(1, status).setParameter(2, proveedor).setParameter(3, campo).getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return Constantes.CERO;
    }

    /**
     *
     * @param idProveedor
     * @param campo
     * @return
     */
    
    public List<OrdenVO> traerOrdenPorProveedor(int idProveedor, int campo) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(consultaOrden())
                    .append(" where o.proveedor = ").append(idProveedor)
                    .append(" and o.eliminado = false ")
                    .append(" and ao.estatus <> ").append(Constantes.ESTATUS_CANCELADA);
            if (campo > 0) {
                sb.append(" and o.ap_campo = ").append(campo);
            }
            sb.append(" order by o.fecha asc");
            UtilLog4j.log.info(this, "Script por  proveedor y status : : : : " + sb.toString());
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            List<OrdenVO> lord = null;
            if (lo != null) {
                lord = new ArrayList<>();
                for (Object[] o : lo) {
                    lord.add(castOrdenVOPorGerencia(o));
                }
            }//
            return lord;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al traer los gerencia :  :: : : : " + e.getMessage());
            return null;
        }
    }

    
    public List<OrdenVO> traerOrdenPorRangoStatusProveedor(int statusInicial, int statusFinal, int idProveedor, int campo) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(consultaOrden())
                    .append(" where ao.ESTATUS between ").append(statusInicial).append(" and ").append(statusFinal)
                    .append(" and o.proveedor = ").append(idProveedor);
            if (campo > 0) {
                sb.append(" and o.ap_campo = ").append(campo);
            }
            sb.append(" order by o.fecha asc");
            UtilLog4j.log.info(this, "Script por  proveedor y status : : : : " + sb.toString());
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            List<OrdenVO> lord = null;
            if (lo != null) {
                lord = new ArrayList<>();
                for (Object[] o : lo) {
                    lord.add(castOrdenVOPorGerencia(o));
                }
            }//
            return lord;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al traer los gerencia :  :: : : : " + e.getMessage());
            return null;
        }
    }

    
    public long totalOrdenPorEstatusProveedorEmpresa(int status, String compania, int proveedor) {
        long retVal = Constantes.CERO;
        final String sql
                = "SELECT count(o.ID) FROM orden o \n"
                + "   INNER JOIN autorizaciones_orden ao ON ao.orden = o.id \n"
                + "WHERE ao.ESTATUS = ? AND o.proveedor = ? \n"
                + "  AND o.eliminado = false AND o.compania = ?  ";

        try {
            retVal = (Long) em.createNativeQuery(sql)
                    .setParameter(1, status)
                    .setParameter(2, proveedor)
                    .setParameter(3, compania)
                    .getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }

        return retVal;
    }

    
    public List<OrdenVO> traerOrdenPorRangoStatusCompania(int statusInicial, int statusFinal, int idProveedor, String compania) {
        try {
            int campo = 0;
            return this.traerOrdenPorRangoStatusCompania(statusInicial, statusFinal, idProveedor, compania, campo);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al traer los gerencia :  :: : : : " + e.getMessage());
            return null;
        }
    }

    
    public List<OrdenVO> traerOrdenPorRangoStatusCompania(int statusInicial, int statusFinal, int idProveedor, String compania, int campo) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(consultaOrden())
                    .append(" where ao.ESTATUS between ").append(statusInicial).append(" and ").append(statusFinal)
                    .append(" and o.proveedor = ").append(idProveedor);

            if (campo > 0) {
                sb.append(" and cam.id = ").append(campo);
            }

            sb.append(" and o.compania = '").append(compania).append("'");
            sb.append(" order by o.fecha asc");

            UtilLog4j.log.info(this, "Script por  proveedor y status : : : : " + sb.toString());
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            List<OrdenVO> lord = null;
            if (lo != null) {
                lord = new ArrayList<>();
                for (Object[] o : lo) {
                    lord.add(castOrdenVOPorGerencia(o));
                }
            }//
            return lord;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al traer los gerencia :  :: : : : " + e.getMessage());
            return null;
        }
    }

    /**
     *
     * @param idProveedor
     * @param statusId
     * @return
     */
    
    public List<OrdenVO> traerOrdenPorProveedorStatus(int idProveedor, int statusId) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(consultaOrden())
                    .append(" where o.proveedor = ").append(idProveedor)
                    .append(" and ao.estatus = ").append(statusId)
                    .append(" and o.eliminado = false ")
                    .append(" order by o.fecha asc");
            UtilLog4j.log.info(this, "Script por  proveedor y status : : : : " + sb.toString());
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            List<OrdenVO> lord = null;
            if (lo != null) {
                lord = new ArrayList<>();
                for (Object[] o : lo) {
                    lord.add(castOrdenVOPorGerencia(o));
                }
            }//
            return lord;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al traer los gerencia :  :: : : : " + e.getMessage());
            return null;
        }
    }

    
    public List<OrdenVO> traerOrdenStatusCampo(int status, int campo) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(consultaOrden())
                    .append(" where ao.estatus = ").append(status)
                    .append(" and o.ap_campo = ").append(campo)
                    .append(" and o.eliminado  = false ")
                    .append(" order by p.nombre asc");
            //
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            List<OrdenVO> lord = null;
            if (lo != null) {
                lord = new ArrayList<>();
                for (Object[] o : lo) {
                    lord.add(castOrdenVOPorGerencia(o));
                }
            }//
            return lord;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al traer los estatus:  :: : : : " + e.getMessage());
            return null;
        }
    }

    
    public long totalOrdenesStatusCampo(int statusId, int campoId) {
        StringBuilder sb = new StringBuilder();
        sb.append("select count(*) from AUTORIZACIONES_ORDEN ao ");
        sb.append("     inner join orden o on ao.ORDEN = o.id ");
        sb.append(" where  ao.ESTATUS = ").append(statusId);
        sb.append(" and o.AP_CAMPO = ").append(campoId);
        sb.append(" and o.ELIMINADO = false ");
        sb.append(" and o.MONEDA is not null ");

        return ((Long) em.createNativeQuery(sb.toString()).getSingleResult());
    }

    
    public List<OrdenVO> traerOrdenReporteRepse(String inicio, String fin, String consecutivo, String proveedor, int campo) {
        try {

            String s = " select "
                    + " c.nombre, "
                    + " a.consecutivo,  "
                    + " a.referencia,  "
                    + " p.rfc, p.nombre,  "
                    + " case when substring(a.consecutivo from 0 for 3) = 'OC' then 'Orden de Compra' else 'Orden de Servicio' end as tipo_serv,  "
                    + " a.destino, "
                    + " a.total, "
                    + " m.nombre,  "
                    + " case when a.repse then 'Compra con Repse' else 'Compra sin Repse' end as repse,  "
                    + " ao.fecha_autorizo_compras, "
                    + " ao.fecha_aceptacion_carta,  "
                    + " ao.fecha_revisa_repse,  "
                    + " e.nombre, "
                    + " u.nombre " 
                    + " from orden a  "
                    + " inner join ap_campo c on c.id = a.ap_campo "
                    + " inner join proveedor p on p.id = a.proveedor "
                    + " inner join moneda m on m.id = a.moneda "
                    + " inner join autorizaciones_orden ao on ao.orden = a.id  "
                    + " inner join estatus e on e.id =ao.estatus "
                    + " inner join usuario u on u.id = a.analista "
                    + " where a.eliminado = false "
                    + " and a.ap_campo in (" + campo + ") "
                    + " and ao.estatus > 140 and ao.estatus < 170 ";

            if (campo < Constantes.AP_CAMPO_TIERRA_BLANCA || campo > Constantes.AP_CAMPO_SAN_ANDRES) {
                s += " and (ao.estatus = 145 or ao.fecha_aceptacion_carta is not null) ";
            }

            if (inicio != null && !inicio.isEmpty()) {
                s += " and ao.fecha_solicito >= '" + inicio + "' ";
            }

            if (fin != null && !fin.isEmpty()) {
                s += " and ao.fecha_solicito <= '" + fin + "' ";
            }

            if (consecutivo != null && !consecutivo.isEmpty()) {
                s += " and a.consecutivo = '" + consecutivo.trim() + "' ";
            }

            if (proveedor != null && !proveedor.isEmpty()) {
                s += " and (p.rfc like '%" + proveedor.trim() + "%' or upper(p.nombre) like upper('%" + proveedor.trim() + "%'))";
            }

            s += " order by a.ap_campo ";

            //
            List<Object[]> lo = em.createNativeQuery(s).getResultList();
            List<OrdenVO> lord = null;
            if (lo != null) {
                lord = new ArrayList<>();
                for (Object[] o : lo) {
                    lord.add(castOrdenReporteRepse(o));
                }
            }//
            return lord;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al traer los estatus:  :: : : : " + e.getMessage());
            return null;
        }
    }

    private OrdenVO castOrdenReporteRepse(Object[] objects) {
        OrdenVO o = new OrdenVO();

        o.setBloque((String) objects[0]);
        o.setConsecutivo((String) objects[1]);
        o.setReferencia((String) objects[2]);
        o.setProveedorRfc((String) objects[3]);
        o.setProveedor((String) objects[4]);
        o.setTipo((String) objects[5]);
        o.setDestino((String) objects[6]);
        o.setTotal((Double) objects[7]);
        o.setMonedaSiglas((String) objects[8]);
        o.setRepseTxt((String) objects[9]);
        o.setFechaAutoriza((Date) objects[10]);
        o.setFechaAceptaCarta((Date) objects[11]);
        o.setFechaRevisaRepse((Date) objects[12]);
        o.setEstatus((String) objects[13]);
        o.setAnalista((String) objects[14]);

        return o;
    }

}
