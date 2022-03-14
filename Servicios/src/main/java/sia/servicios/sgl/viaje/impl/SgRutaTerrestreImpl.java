/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.viaje.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.exception.MappingException;
import org.jooq.exception.TooManyRowsException;
import sia.constantes.Constantes;
import sia.modelo.SgOficina;
import sia.modelo.SgRutaTerrestre;
import sia.modelo.Usuario;
import sia.modelo.sgl.viaje.vo.RutaTerrestreVo;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.gr.impl.GrRutasZonasImpl;
import sia.servicios.sgl.impl.SgTipoEspecificoImpl;
import sia.servicios.sgl.semaforo.impl.SgEstadoSemaforoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SgRutaTerrestreImpl extends AbstractFacade<SgRutaTerrestre>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    @Inject
    private SgTipoEspecificoImpl sgTipoEspecificoRemote;
    @Inject
    private GrRutasZonasImpl grRutasZonasRemote;
    @Inject
    private SgViajeroImpl sgViajeroRemote;
    @Inject
    private SgViajeImpl sgViajeRemote;
    @Inject
    SgEstadoSemaforoImpl sgEstadoSemaforoRemote;

    @Inject
    DSLContext dslCtx;

    private StringBuilder bodyQuery = new StringBuilder();

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgRutaTerrestreImpl() {
        super(SgRutaTerrestre.class);
    }

    public void clearBodyQuery() {
        this.bodyQuery.delete(0, this.bodyQuery.length());
    }

    
    public List<SgRutaTerrestre> getAllRutaTerrestre(int idSgOficina, boolean eliminado) {
        UtilLog4j.log.info(this, "SiRutaTerrestreImpl.getAllRutaTerrestre()-");

        UtilLog4j.log.info(this, "idSgOficina: " + idSgOficina);
        UtilLog4j.log.info(this, "eliminado: " + eliminado);

        List<SgRutaTerrestre> result;

        String query = "SELECT p FROM SgRutaTerrestre p WHERE p.eliminado = :eliminado AND p.sgOficina.id = :ofi ";

        Query q = em.createQuery(query);
        q.setParameter("ofi", idSgOficina);
        q.setParameter("eliminado", eliminado);

        UtilLog4j.log.info(this, "query: " + q.toString());

        try {
            result = q.getResultList();

            UtilLog4j.log.info(this, "Se encontraron " + (result != null ? result.size() : null) + " SgRutaTerrestre");

            return result;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            return null;
        }
    }

    
    public void guardarRutaTerrestre(Usuario usuario, RutaTerrestreVo rutaTerrestreVo, int sgOficina, int idTipoEspecifico) {
        try {
            SgRutaTerrestre sgRutaTerrestre = new SgRutaTerrestre();
            sgRutaTerrestre.setNombre(rutaTerrestreVo.getNombre());
            sgRutaTerrestre.setTiempoViaje(rutaTerrestreVo.getTiempoViaje());
            sgRutaTerrestre.setSgOficina(new SgOficina(sgOficina));
            sgRutaTerrestre.setSgTipoEspecifico(sgTipoEspecificoRemote.find(idTipoEspecifico));
            sgRutaTerrestre.setGenero(usuario);
            sgRutaTerrestre.setFechaGenero(new Date());
            sgRutaTerrestre.setHoraGenero(new Date());
            sgRutaTerrestre.setEliminado(Constantes.NO_ELIMINADO);
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.set(Calendar.HOUR_OF_DAY, 7);
            c.set(Calendar.MINUTE, 30);
            c.set(Calendar.SECOND, 0);
            Calendar c1 = Calendar.getInstance();
            c1.setTime(new Date());
            c1.set(Calendar.HOUR_OF_DAY, 16);
            c1.set(Calendar.MINUTE, 30);
            c1.set(Calendar.SECOND, 0);
            sgRutaTerrestre.setHoraMinimaRuta(c.getTime());
            sgRutaTerrestre.setHoraMaximaRuta(c1.getTime());

            create(sgRutaTerrestre);
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }

    }

    
    public void modificarRutaTerrestre(Usuario usuario, RutaTerrestreVo rutaTerrestreVo, boolean eliminado) {
        try {
            SgRutaTerrestre sgRutaTerrestre = find(rutaTerrestreVo.getId());
            sgRutaTerrestre.setNombre(rutaTerrestreVo.getNombre());
            sgRutaTerrestre.setTiempoViaje(rutaTerrestreVo.getTiempoViaje());
            sgRutaTerrestre.setHoraMinimaRuta(rutaTerrestreVo.getHoraMinimaRuta());
            sgRutaTerrestre.setHoraMaximaRuta(rutaTerrestreVo.getHoraMaximaRuta());
            sgRutaTerrestre.setGenero(usuario);
            sgRutaTerrestre.setFechaGenero(new Date());
            sgRutaTerrestre.setHoraGenero(new Date());
            sgRutaTerrestre.setEliminado(eliminado);
            edit(sgRutaTerrestre);
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    
    public RutaTerrestreVo buscarPorNombre(String nombre) {
        try {
            clearBodyQuery();
            bodyQuery.append("SELECT p.id, p.nombre, p.tiempo_viaje, p.sg_tipo_especifico, p.HORA_MINIMARUTA, p.HORA_MAXIMARUTA FROM sg_ruta_terrestre p WHERE p.nombre = ").append("'").append(nombre).append("'");
            bodyQuery.append(" and p.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");

            Object[] objects = (Object[]) em.createNativeQuery(bodyQuery.toString()).getSingleResult();
            return castRutaTerrestreVO(objects, (Integer) objects[3], true);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            return null;
        }
    }

    private SgRutaTerrestre buscarPorNombreEliminado(String nombre) {
        try {
            return (SgRutaTerrestre) em.createQuery("SELECT p FROM SgRutaTerrestre p WHERE p.nombre = :nombre AND p.eliminado = :eli").setParameter("eli", Constantes.ELIMINADO).setParameter("nombre", nombre).getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            return null;
        }
    }

    
    public SgRutaTerrestre findSgRutaTerrestreBySgOficinaOrigenAndSgOficinaDestino(int idSgOficinaOrigen, int idDestino, int idTipo) {

        SgRutaTerrestre result = null;
        try {
            final StringBuilder sql = new StringBuilder(200);
            final String comunes
                    = " WHERE rt.SG_OFICINA = ?"
                    + " AND rt.ELIMINADO = ?"
                    + " AND dr.ELIMINADO = ?"
                    + " AND dr.DESTINO = ?";

            sql.append("SELECT rt.* FROM SG_RUTA_TERRESTRE rt");

            if (idTipo == Constantes.RUTA_TIPO_OFICINA) {
                sql.append(" inner join SG_DETALLE_RUTA_TERRESTRE dr on dr.SG_RUTA=rt.ID")
                        .append(comunes)
                        .append(" AND  dr.SG_OFICINA = ? ");
            } else if (idTipo == Constantes.RUTA_TIPO_CIUDAD) {
                sql.append(" inner join SG_DETALLE_RUTA_CIUDAD dr on dr.SG_RUTA_TERRESTRE = rt.ID")
                        .append(comunes)
                        .append(" AND  dr.SI_CIUDAD = ? ");
            }
            //Query q = em.createNativeQuery(bodyQuery.toString(), SgRutaTerrestre.class);

            Record record
                    = dslCtx.fetchOne(
                            sql.toString(),
                            idSgOficinaOrigen,
                            Constantes.BOOLEAN_FALSE,
                            Constantes.BOOLEAN_FALSE,
                            Constantes.BOOLEAN_TRUE,
                            idDestino
                    );

            if (record != null) {
                result = record.into(SgRutaTerrestre.class);

                if (result == null) {
                    UtilLog4j.log.debug(this, "No hay existen rutas entre {0} y {1} Query {2}", new Object[]{idSgOficinaOrigen, idDestino, bodyQuery});
                }
            }

        } catch (MappingException e) {
            UtilLog4j.log.fatal(this, bodyQuery.toString(), e);
        } catch (TooManyRowsException e) {
            UtilLog4j.log.fatal(this, "Se encontró mas de un resultado para ruta con origen_oficina: {0} y destino {1}",
                    new Object[]{idSgOficinaOrigen, idDestino}, e);
        }

        return result;
    }

    
    public List<RutaTerrestreVo> traerRutaTerrestrePorOficina(int idOficina, int tipoRuta) {
        List<RutaTerrestreVo> list = new ArrayList<RutaTerrestreVo>();
        try {
            clearBodyQuery();
            this.bodyQuery.append("SELECT rt.id, rt.nombre, rt.tiempo_viaje, rt.sg_tipo_especifico, rt.HORA_MINIMARUTA, rt.HORA_MAXIMARUTA FROM SG_RUTA_TERRESTRE rt ");
            this.bodyQuery.append(" WHERE rt.SG_OFICINA=").append(idOficina);
            this.bodyQuery.append(" AND rt.ELIMINADO='").append(Constantes.BOOLEAN_FALSE).append("' ");
            this.bodyQuery.append(" AND rt.sg_tipo_especifico =  ").append(tipoRuta);
            this.bodyQuery.append(" order by rt.nombre ");
            List<Object[]> l = em.createNativeQuery(this.bodyQuery.toString()).getResultList();
            for (Object[] objects : l) {
                list.add(castRutaTerrestreVO(objects, tipoRuta, true));
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }
        return list;
    }

    
    public RutaTerrestreVo traerRutaTerrestrePorID(int idRuta) {
        RutaTerrestreVo ruta = null;
        try {
            clearBodyQuery();
            this.bodyQuery.append("SELECT rt.id, rt.nombre, rt.tiempo_viaje, rt.sg_tipo_especifico, rt.HORA_MINIMARUTA, rt.HORA_MAXIMARUTA FROM SG_RUTA_TERRESTRE rt ");
            this.bodyQuery.append(" WHERE rt.id = ").append(idRuta);
            Object[] obj = (Object[]) em.createNativeQuery(this.bodyQuery.toString()).getSingleResult();
            if (obj != null) {
                ruta = castRutaTerrestreVO(obj, (Integer) obj[3], true);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }
        return ruta;
    }

    
    public List<RutaTerrestreVo> traerRutaTerrestrePorZona(int idZona, int semaforo) {
        List<RutaTerrestreVo> list = new ArrayList<RutaTerrestreVo>();
        try {
            String sql = "select rt.id, rt.nombre, rt.tiempo_viaje"
                    + " , case when rt.sg_tipo_especifico = 21 then 'a Oficina' else 'a Ciudad' end as tipo"
                    + " , rt.HORA_MINIMARUTA"
                    + " , rt.HORA_MAXIMARUTA"
                    + " from GR_RUTAS_ZONAS rz"
                    + " inner join SG_RUTA_TERRESTRE rt on rt.id = rz.SG_RUTA_TERRESTRE"
                    + " where rz.GR_MAPA = ?"
                    + " and  rz.ELIMINADO = ?"
                    + " order by rz.id desc ";

            list = dslCtx.fetch(sql, idZona, Constantes.FALSE).into(RutaTerrestreVo.class);

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }
        return list;
    }

    private RutaTerrestreVo castRutaTerrestreVO(Object[] objects, int tipo, boolean getTime) {
        RutaTerrestreVo rtv = new RutaTerrestreVo();
        rtv.setId((Integer) objects[0]);
        rtv.setNombre((String) objects[1]);
        rtv.setTiempoViaje((String) objects[2]);
        if (Constantes.RUTA_TIPO_OFICINA == tipo) {
            rtv.setTipo("a Oficina");
        } else if (Constantes.RUTA_TIPO_CIUDAD == tipo) {
            rtv.setTipo("a Ciudad");
        }
        if (getTime) {
            rtv.setHoraMinimaRuta((Date) objects[4]);
            rtv.setHoraMaximaRuta((Date) objects[5]);
        }
        return rtv;
    }

    
    public String traerCiudadDestinoRuta(int idRuta) {
        clearQuery();
        appendQuery("select ci.id, ci.nombre from SI_CIUDAD ci where ci.ID in ( select dir.SI_CIUDAD from SG_DIRECCION dir where dir.ID in");
        appendQuery(" (select o.SG_DIRECCION from SG_OFICINA o where o.ID in ( select drt.SG_OFICINA from SG_DETALLE_RUTA_TERRESTRE drt");
        appendQuery(" where drt.SG_RUTA in (select rt.id from SG_RUTA_TERRESTRE rt  where rt.ELIMINADO = 'False' and rt.ID =").append(idRuta);
        appendQuery(" and rt.SG_TIPO_ESPECIFICO = ").append(Constantes.RUTA_TIPO_OFICINA);
        appendQuery(" and drt.DESTINO = 'True' and drt.ELIMINADO ='False'))) )");
        Object[] obj = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
        try {
            String cad = "";
            if (obj != null) {
                cad = (String) obj[1];
            }
            UtilLog4j.log.info(this, "cad ,: " + cad);
            return cad;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            return null;
        }
    }

    
    public List<RutaTerrestreVo> traerRutaTerrestreViaje(int estatus, String usuarioGerente) {
        return traerRutaTerrestreViaje(estatus, usuarioGerente, 0);
    }

    
    public List<RutaTerrestreVo> traerRutaTerrestreViaje(int estatus, String usuarioGerente, int dias) {
        List<RutaTerrestreVo> list = new ArrayList<RutaTerrestreVo>();
        try {
            StringBuilder sb = new StringBuilder();
//                                      0               1         2               3                       4                     5
            sb.append(" select a.SG_RUTA_TERRESTRE, r.NOMBRE, r.TIEMPO_VIAJE, r.sg_tipo_especifico, r.HORA_MINIMARUTA, r.HORA_MAXIMARUTA, ");
            sb.append(" a.id, "); // 6
            sb.append(" a.codigo, ");// 7
            sb.append(" u.NOMBRE, ");// 8
            sb.append(" case when a.AUTOBUS = 'True' then 'Autobus' when a.VEHICULO_PROPIO = 'True' then 'Propio' when a.VEHICULO_ASIGNADO_EMPRESA = 'True' then 'Asignado' else 'Sin vehículo' end as tipoV, ");// 9
            sb.append(" case when vv.NUMERO_PLACA is NOT null then vv.NUMERO_PLACA when vv.NUMERO_PLACA is null then (SELECT ve.NUMERO_PLACA from SG_VIAJE_VEHICULO aa inner join SG_VEHICULO ve on ve.id = aa.SG_VEHICULO where aa.SG_VIAJE = a.ID) else 'N/A' END as placa, ");// 10
            sb.append(" u.TELEFONO, ");// 11
            sb.append(" a.co_noticia, ");// 12
            sb.append(" a.FECHA_SALIDA, ");// 13
            sb.append(" a.HORA_SALIDA, ");// 14
            sb.append(" a.FECHA_PROGRAMADA, ");// 15
            sb.append(" a.HORA_PROGRAMADA, ");// 16

            sb.append(" (SELECT count(vvv.ID) ");
            sb.append(" FROM SG_VIAJERO vvv ");
            sb.append(" inner join SG_VIAJE via on vvv.SG_VIAJE = via.ID ");
            sb.append(" WHERE vvv.sg_Viaje = a.id ");
            sb.append(" AND vvv.eliminado = false ");
            sb.append(" AND Vvv.ID not in ");
            sb.append(" ( ");
            sb.append(" select ");
            sb.append(" vvvr.ID ");
            sb.append(" from SG_VIAJE vva ");
            sb.append(" inner join SG_VIAJERO vvvr on vvvr.SG_VIAJE = vva.id and vva.ELIMINADO = false ");
            sb.append(" inner join SG_VIAJERO_SI_MOVIMIENTO vvvv on vvvv.SG_VIAJERO = vvvr.id    and vvvv.ELIMINADO = false ");
            sb.append(" inner join SI_MOVIMIENTO m on m.id = vvvv.SI_MOVIMIENTO and m.ELIMINADO = false and m.SI_OPERACION = 24 ");
            sb.append(" where vva.id = a.id ");
            sb.append(" )) as numViajeros, ");// 17

            sb.append(" (select ");
            sb.append(" s.COLOR ");
            sb.append(" from SG_SEMAFORO s ");
            sb.append(" where s.ID = ");
            sb.append(" ( ");
            sb.append(" SELECT ");
            sb.append(" xx ");
            sb.append(" from ");
            sb.append(" ( ");
            sb.append(" select ");
            sb.append(" ( ");
            sb.append(" SELECT ");
            sb.append(" ar.SG_SEMAFORO ");
            sb.append(" FROM SG_ESTADO_SEMAFORO ar ");
            sb.append(" where ar.ELIMINADO = 'False' ");
            sb.append(" and ar.GR_MAPA = rz.GR_MAPA ");
            sb.append(" ORDER BY ar.ID DESC LIMIT 1 ");
            sb.append(" ) ");
            sb.append(" as xx ");
            sb.append(" from GR_RUTAS_ZONAS rz ");
            sb.append(" where rz.SG_RUTA_TERRESTRE = a.SG_RUTA_TERRESTRE ");
            sb.append(" and rz.ELIMINADO = 'False' ");
            sb.append(" order by rz.SECUENCIA ");
            sb.append(" ) ");
            sb.append(" AS xxx ");
            sb.append(" group by xx ");
            sb.append(" order by xx desc LIMIT 1 ");
            sb.append(" )) as colSem, vv.capacidad_pasajeros ");// 18

            sb.append(" from SG_VIAJE a ");
            sb.append(" inner join SG_RUTA_TERRESTRE r on r.id = a.SG_RUTA_TERRESTRE and r.ELIMINADO = false ");
            sb.append(" inner join USUARIO u on u.id = a.RESPONSABLE and u.ELIMINADO = false  ");
            sb.append(" left join SG_VIAJE_VEHICULO av on av.SG_VIAJE = a.id and av.ELIMINADO = false ");
            sb.append(" left join SG_VEHICULO vv on vv.id = av.SG_VEHICULO and vv.ELIMINADO = false ");
            if (usuarioGerente != null && !usuarioGerente.isEmpty()) {
                sb.append(" inner join SG_VIAJERO v on v.SG_VIAJE = a.ID and v.ELIMINADO = 'False' ");
                sb.append(" inner join USUARIO u on u.id = v.USUARIO  and u.ELIMINADO = 'False' and u.GERENCIA in (select g.GERENCIA ");
                sb.append(" from AP_CAMPO_GERENCIA g ");
                sb.append(" where g.RESPONSABLE = '").append(usuarioGerente).append("' ");
                sb.append(" and g.ELIMINADO = 'False') ");
            }
            sb.append(" where a.ESTATUS = ").append(estatus);
            sb.append(" and a.ELIMINADO = 'False' ");
            sb.append(" and a.ID not in (select SG_VIAJE_A from GR_INTERSECCION where ELIMINADO = 'False')");
            sb.append(" and a.ID not in (select SG_VIAJE_B from GR_INTERSECCION where ELIMINADO = 'False')");

            if (dias > 0) {
                sb.append(" and a.FECHA_PROGRAMADA >= cast('now' as date)  "
                        + " and (a.FECHA_PROGRAMADA <= current_date + ").append(dias).append(") ");
            } else {
                sb.append(" and ((a.FECHA_PROGRAMADA is not null and a.FECHA_PROGRAMADA <= cast('Now' as date))  or (a.FECHA_SALIDA is not null and a.FECHA_SALIDA <= cast('Now' as date))) ");
            }

            sb.append(" group by a.SG_RUTA_TERRESTRE, r.NOMBRE, r.TIEMPO_VIAJE, r.sg_tipo_especifico, r.HORA_MINIMARUTA, r.HORA_MAXIMARUTA, ");
            sb.append(" a.id,a.codigo,u.NOMBRE,tipoV,placa, u.TELEFONO,a.co_noticia,a.FECHA_SALIDA,a.HORA_SALIDA,a.FECHA_PROGRAMADA,a.HORA_PROGRAMADA,numViajeros,colSem,vv.capacidad_pasajeros ");
            sb.append(" order by a.SG_RUTA_TERRESTRE, a.id ");

            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            int idRuta = 0;
            RutaTerrestreVo vo = null;
            if (lo != null) {
                for (Object[] objects : lo) {
                    if (idRuta != (Integer) objects[0]) {
                        if (idRuta > 0) {
                            list.add(vo);
                        }
                        vo = castRutaTerrestreVO(objects, (Integer) objects[3], true);
                        vo.setViajes(new ArrayList<>());
                        idRuta = (Integer) objects[0];
                    }
//                    vo.setZonas(grRutasZonasRemote.zonasPorRuta(vo, true));
//                    vo.setViajes(sgViajeRemote.traerViajesPorRuta(estatus, vo.getId(), dias, true, usuarioGerente, 0));
                    if (vo != null) {
                        if (vo.getViajes() != null) {
                            vo.getViajes().add(castViaje(objects, estatus));
                        }
                        vo.setColorSemaforo((String) objects[18]);
                    }
//                    for (int i = 0; i < vo.getViajes().size(); i++) {
//                        vo.getViajes().get(i).setListaViajeros(sgViajeroRemote.getTravellersByTravel(vo.getViajes().get(i).getId(), usuarioGerente));
//                    }
                    //vo.setColorSemaforo(sgEstadoSemaforoRemote.getColorSemaforoRuta(vo.getId()));

                }
                if (idRuta > 0) {
                    list.add(vo);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }
        return list;
    }

    private ViajeVO castViaje(Object[] objects, int estatus) {
        ViajeVO o = new ViajeVO();

        o.setId((Integer) objects[6]);
        o.setCodigo((String) objects[7]);
        o.setResponsable((String) objects[8]);
        o.setVehiculo((String) objects[9]);
        o.setVehiculoPlaca((String) objects[10]);
        o.setResponsableTel((String) objects[11]);
        o.setIdNoticia((Integer) objects[12]);
        o.setFechaSalida(objects[13] != null ? (Date) objects[13] : null);
        o.setHoraSalida(objects[14] != null ? (Date) objects[14] : null);
        o.setFechaProgramada(objects[15] != null ? (Date) objects[15] : null);
        o.setHoraProgramada(objects[16] != null ? (Date) objects[16] : null);
        o.setTiempoViaje((String) objects[2]);
        if (o.getFechaSalida() != null && o.getHoraSalida() != null) {
            o.setTiempoViajeRealVal();
        }
        o.setNumViajeros(((Long) objects[17]).intValue());
        o.setIdEstatus(estatus);
        o.setVehiculoVO(new VehiculoVO());
        o.getVehiculoVO().setCapacidadPasajeros(objects[19] != null ? (Integer) objects[19] : 0);
        
        return o;
    }

    
    public List<List<Object[]>> traerDestinosJson(int sgOficina) {
        List<List<Object[]>> list = new ArrayList<List<Object[]>>();
        List<Object[]> listaTerrestre;
        List<Object[]> listaAerea;

        clearQuery();
        query.append(" select r.id,");
        query.append(" case when r.SG_TIPO_ESPECIFICO = ").append(Constantes.RUTA_TIPO_OFICINA).append(" then");
        query.append(" (select o.nombre from SG_OFICINA o,SG_DETALLE_RUTA_TERRESTRE dr");
        query.append(" where o.id=dr.SG_OFICINA and dr.SG_RUTA=r.id");
        query.append(" and dr.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("' and dr.DESTINO = '").append(Constantes.BOOLEAN_TRUE).append("')");
        query.append(" else (select c.nombre from SI_CIUDAD c,SG_DETALLE_RUTA_CIUDAD dc");
        query.append(" where c.id=dc.SI_CIUDAD and dc.SG_RUTA_TERRESTRE=r.id");
        query.append(" and dc.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("' and dc.DESTINO = '").append(Constantes.BOOLEAN_TRUE).append("') end, ");
        query.append(" case when r.SG_TIPO_ESPECIFICO = ").append(Constantes.RUTA_TIPO_OFICINA).append(" then");
        query.append(" (select 'a Oficina' from SG_OFICINA o,SG_DETALLE_RUTA_TERRESTRE dr");
        query.append(" where o.id=dr.SG_OFICINA and dr.SG_RUTA=r.id");
        query.append(" and dr.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("' and dr.DESTINO = '").append(Constantes.BOOLEAN_TRUE).append("')");
        query.append(" else (select 'a Ciudad' from SI_CIUDAD c,SG_DETALLE_RUTA_CIUDAD dc");
        query.append(" where c.id=dc.SI_CIUDAD and dc.SG_RUTA_TERRESTRE=r.id");
        query.append(" and dc.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("' and dc.DESTINO = '").append(Constantes.BOOLEAN_TRUE).append("') end");
        query.append(" ,r.hora_minimaruta, r.hora_maximaruta ");
        query.append(" from SG_RUTA_TERRESTRE r");
        query.append(" where r.SG_OFICINA = ").append(sgOficina);
        query.append(" and r.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
        listaTerrestre = em.createNativeQuery(query.toString()).getResultList();
        list.add(listaTerrestre);

        clearQuery();
        query.append(" select c.ID,c.NOMBRE,e.NOMBRE,p.NOMBRE from SI_CIUDAD c");
        query.append(" inner join SI_ESTADO e on e.ID=c.SI_ESTADO");
        query.append(" inner join SI_PAIS p on p.ID=e.SI_PAIS");
        query.append(" where p.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append(" and e.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append(" and c.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append(" and c.id not in (select c.id from SI_CIUDAD c ");
        query.append(" inner join  SG_DETALLE_RUTA_CIUDAD rc on rc.SI_CIUDAD = c.id");
        query.append(" inner join SG_RUTA_TERRESTRE r on r.ID=rc.SG_RUTA_TERRESTRE");
        query.append(" where r.SG_OFICINA = ").append(sgOficina).append(" and r.SG_TIPO_ESPECIFICO = ").append(Constantes.RUTA_TIPO_CIUDAD)
                .append("and r.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("')");
        query.append(" and c.SI_ESTADO not in (1,6,30)");
        listaAerea = em.createNativeQuery(query.toString()).getResultList();
        list.add(listaAerea);

        return list;

    }

    
    public List<List<Object[]>> traerDestinosJson(int sgOficina, String rfc) {
        List<List<Object[]>> list = new ArrayList<List<Object[]>>();
        List<Object[]> listaTerrestre;
        List<Object[]> listaAerea;

        clearQuery();
        query.append(" select r.id,");
        query.append(" case when r.SG_TIPO_ESPECIFICO = ").append(Constantes.RUTA_TIPO_OFICINA).append(" then");
        query.append(" (select o.nombre from SG_OFICINA o,SG_DETALLE_RUTA_TERRESTRE dr");
        query.append(" where o.id=dr.SG_OFICINA and dr.SG_RUTA=r.id");
        query.append(" and dr.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("' and dr.DESTINO = '").append(Constantes.BOOLEAN_TRUE).append("')");
        query.append(" else (select c.nombre from SI_CIUDAD c,SG_DETALLE_RUTA_CIUDAD dc");
        query.append(" where c.id=dc.SI_CIUDAD and dc.SG_RUTA_TERRESTRE=r.id");
        query.append(" and dc.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("' and dc.DESTINO = '").append(Constantes.BOOLEAN_TRUE).append("') end, ");
        query.append(" case when r.SG_TIPO_ESPECIFICO = ").append(Constantes.RUTA_TIPO_OFICINA).append(" then");
        query.append(" (select 'a Oficina' from SG_OFICINA o,SG_DETALLE_RUTA_TERRESTRE dr");
        query.append(" where o.id=dr.SG_OFICINA and dr.SG_RUTA=r.id");
        query.append(" and dr.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("' and dr.DESTINO = '").append(Constantes.BOOLEAN_TRUE).append("')");
        query.append(" else (select 'a Ciudad' from SI_CIUDAD c,SG_DETALLE_RUTA_CIUDAD dc");
        query.append(" where c.id=dc.SI_CIUDAD and dc.SG_RUTA_TERRESTRE=r.id");
        query.append(" and dc.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("' and dc.DESTINO = '").append(Constantes.BOOLEAN_TRUE).append("') end");
        query.append(" ,r.hora_minimaruta, r.hora_maximaruta ");
        query.append(" from SG_RUTA_TERRESTRE r");
        query.append(" where r.SG_OFICINA = ").append(sgOficina);
        query.append(" and r.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
        listaTerrestre = em.createNativeQuery(query.toString()).getResultList();
        list.add(listaTerrestre);

        clearQuery();
        query.append(" select c.ID,c.NOMBRE,e.NOMBRE,p.NOMBRE from SI_CIUDAD c");
        query.append(" inner join SI_ESTADO e on e.ID=c.SI_ESTADO");
        query.append(" inner join SI_PAIS p on p.ID=e.SI_PAIS");
        query.append(" where (select count(rfc) from compania where rfc = '").append(rfc).append("' and viaje_aereo = true) > 0");
        query.append(" and p.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append(" and e.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append(" and c.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append(" and c.id not in (select c.id from SI_CIUDAD c ");
        query.append(" inner join  SG_DETALLE_RUTA_CIUDAD rc on rc.SI_CIUDAD = c.id");
        query.append(" inner join SG_RUTA_TERRESTRE r on r.ID=rc.SG_RUTA_TERRESTRE");
        query.append(" where r.SG_OFICINA = ").append(sgOficina).append(" and r.SG_TIPO_ESPECIFICO = ").append(Constantes.RUTA_TIPO_CIUDAD)
                .append("and r.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("')");
        query.append(" and c.SI_ESTADO not in (1,6,30)");
        listaAerea = em.createNativeQuery(query.toString()).getResultList();
        list.add(listaAerea);

        return list;

    }

    
    public List<List<Object[]>> traerOigenJson() {
        List<List<Object[]>> listas = new ArrayList<List<Object[]>>();
        List<Object[]> listaOficinas;
        List<Object[]> listaCiudades;
        clearQuery();

        query.append("SELECT o.id, o.nombre   FROM Sg_oficina o");
        query.append(" WHERE o.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append(" AND o.Visto_bueno = '").append(Constantes.BOOLEAN_TRUE).append("'");
        query.append(" order by o.nombre asc");
        listaOficinas = em.createNativeQuery(query.toString()).getResultList();
        listas.add(listaOficinas);

        clearQuery();
        query.append(" select c.ID,c.NOMBRE,e.NOMBRE,p.NOMBRE from SI_CIUDAD c");
        query.append(" inner join SI_ESTADO e on e.ID=c.SI_ESTADO");
        query.append(" inner join SI_PAIS p on p.ID=e.SI_PAIS");
        query.append(" where p.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append(" and e.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append(" and c.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
        //  query.append(" and c.SI_ESTADO not in (1,6,30)");
        listaCiudades = em.createNativeQuery(query.toString()).getResultList();
        listas.add(listaCiudades);

        return listas;

    }

    
    public List<List<Object[]>> traerOigenJson(String rfc) {
        List<List<Object[]>> listas = new ArrayList<List<Object[]>>();
        List<Object[]> listaOficinas;
        List<Object[]> listaCiudades;
        clearQuery();

        query.append("SELECT o.id, o.nombre   FROM Sg_oficina o");
        query.append(" WHERE o.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append(" AND o.Visto_bueno = '").append(Constantes.BOOLEAN_TRUE).append("'");
        query.append(" order by o.nombre asc");
        listaOficinas = em.createNativeQuery(query.toString()).getResultList();
        listas.add(listaOficinas);

        clearQuery();
        query.append(" select c.ID,c.NOMBRE,e.NOMBRE,p.NOMBRE from SI_CIUDAD c");
        query.append(" inner join SI_ESTADO e on e.ID=c.SI_ESTADO");
        query.append(" inner join SI_PAIS p on p.ID=e.SI_PAIS");
        query.append(" where (select count(rfc) from compania where rfc = '").append(rfc).append("' and viaje_aereo = true) > 0");
        query.append(" and p.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append(" and e.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append(" and c.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
        //  query.append(" and c.SI_ESTADO not in (1,6,30)");
        listaCiudades = em.createNativeQuery(query.toString()).getResultList();
        listas.add(listaCiudades);

        return listas;

    }

}
