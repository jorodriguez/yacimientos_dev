/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.gr.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.GrInterseccion;
import sia.modelo.SgViaje;
import sia.modelo.Usuario;
import sia.modelo.gr.vo.GrIntercepcionVO;
import sia.modelo.gr.vo.GrPuntoVO;
import sia.modelo.sgl.viaje.vo.ItinerarioTerrestreVO;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sgl.viaje.impl.SgViajeImpl;
import sia.servicios.sgl.viaje.impl.SgViajeroImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@LocalBean 
public class GrInterseccionImpl extends AbstractFacade<GrInterseccion> {

    @Inject
    private SgViajeImpl sgViajeRemote;
    @Inject
    private GrPuntoImpl grPuntoRemote;
    @Inject
    private SgViajeroImpl sgViajeroRemote;

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GrInterseccionImpl() {
        super(GrInterseccion.class);
    }

    
    public GrInterseccion crearIntercepcionViajes(int viajeA, int viajeB, int pseguridadID, String usuario) {
        GrInterseccion nuevo = null;
        try {
            nuevo = new GrInterseccion();
            SgViaje va = sgViajeRemote.find(viajeA);
            SgViaje vb = sgViajeRemote.find(viajeB);
            nuevo.setSgViajeA(va);
            nuevo.setSgViajeB(vb);
            nuevo.setGenero(new Usuario(usuario));
            nuevo.setFechaGenero(new Date());
            nuevo.setHoraGenero(new Date());
            nuevo.setEliminado(Constantes.BOOLEAN_FALSE);
            if (pseguridadID > 0) {
                nuevo.setGrPunto(grPuntoRemote.find(pseguridadID));
            }
            if (!va.isConIntercepcion()) {
                va.setConIntercepcion(Constantes.BOOLEAN_TRUE);
                va.setRedondo(Constantes.BOOLEAN_FALSE);
                sgViajeRemote.edit(va);
                sgViajeroRemote.dejaUsuarioOficinaDestinoViajeSencillo(va.getId(), usuario);
            }
            if (!vb.isConIntercepcion()) {
                vb.setConIntercepcion(Constantes.BOOLEAN_TRUE);
                vb.setRedondo(Constantes.BOOLEAN_FALSE);
                sgViajeRemote.edit(vb);
                sgViajeroRemote.dejaUsuarioOficinaDestinoViajeSencillo(vb.getId(), usuario);
            }
            this.create(nuevo);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            nuevo = null;
        }
        return nuevo;
    }

    
    public GrInterseccion crearIntercepcionViajes(int viajeA, int viajeB, String usuario, GrIntercepcionVO intercepcion) {
        GrInterseccion nuevo = null;
        GrInterseccion viejo = null;
        try {
            viejo = this.find(intercepcion.getId());
            viejo.setEliminado(Constantes.BOOLEAN_TRUE);
            viejo.setModifico(new Usuario(usuario));
            viejo.setFechaModifico(new Date());
            viejo.setHoraModifico(new Date());
            edit(viejo);
            int viajeSencillo = 0;
            viajeSencillo = viejo.getSgViajeA().getId() == viajeA ? viejo.getSgViajeB().getId()
                    : viejo.getSgViajeB().getId() == viajeA ? viejo.getSgViajeA().getId()
                    : viejo.getSgViajeA().getId() == viajeB ? viejo.getSgViajeB().getId() : viejo.getSgViajeA().getId();

            if (viajeSencillo > 0) {
                SgViaje sencillo = sgViajeRemote.find(viajeSencillo);
                if (sencillo.isRedondo()) {
                    sencillo.setRedondo(Constantes.BOOLEAN_FALSE);
                    viejo.setModifico(new Usuario(usuario));
                    sencillo.setFechaModifico(new Date());
                    sencillo.setHoraModifico(new Date());
                    sgViajeRemote.edit(sencillo);
                }
            }

            nuevo = new GrInterseccion();
            SgViaje va = sgViajeRemote.find(viajeA);
            SgViaje vb = sgViajeRemote.find(viajeB);
            nuevo.setSgViajeA(va);
            nuevo.setSgViajeB(vb);
            nuevo.setGenero(new Usuario(usuario));
            nuevo.setFechaGenero(new Date());
            nuevo.setHoraGenero(new Date());
            nuevo.setEliminado(Constantes.BOOLEAN_FALSE);
            if (!va.isConIntercepcion()) {
                va.setConIntercepcion(Constantes.BOOLEAN_TRUE);
                va.setRedondo(Constantes.BOOLEAN_FALSE);
                sgViajeRemote.edit(va);
            }
            if (!vb.isConIntercepcion()) {
                vb.setConIntercepcion(Constantes.BOOLEAN_TRUE);
                vb.setRedondo(Constantes.BOOLEAN_FALSE);
                sgViajeRemote.edit(vb);
            }
            this.create(nuevo);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            nuevo = null;
        }
        return nuevo;
    }

    
    public List<GrIntercepcionVO> traerViajesPorInterceptar(int intercepcionID) {
        UtilLog4j.log.info(this, "traer todo los viajes por interceptar");
        List<GrIntercepcionVO> lv = null;
        clearQuery();
        appendQuery(" select i.ID, i.SG_VIAJE_A, i.SG_VIAJE_B, i.GR_PUNTO, p.NOMBRE ");
        appendQuery(" from GR_INTERSECCION i ");
        appendQuery(" inner join SG_VIAJE va on va.id = i.SG_VIAJE_A and va.ESTATUS in (").append(Constantes.ESTATUS_VIAJE_POR_SALIR).append(" , ").append(Constantes.ESTATUS_VIAJE_PROCESO).append(" , ").append(Constantes.ESTATUS_VIAJE_FINALIZAR).append(" , ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(" ) ");
        appendQuery(" inner join SG_VIAJE vb on vb.id = i.SG_VIAJE_B and vb.ESTATUS in (").append(Constantes.ESTATUS_VIAJE_POR_SALIR).append(" , ").append(Constantes.ESTATUS_VIAJE_PROCESO).append(" , ").append(Constantes.ESTATUS_VIAJE_FINALIZAR).append(" , ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(" ) ");
        appendQuery(" left join GR_PUNTO p on p.id = i.GR_PUNTO and p.ELIMINADO = 'False' ");
        appendQuery(" where i.ELIMINADO = 'False' ");
        appendQuery(" and ((va.ESTATUS = ").append(Constantes.ESTATUS_VIAJE_FINALIZAR).append(" and vb.ESTATUS < ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(") ");
        appendQuery(" or (va.ESTATUS < ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(" and vb.ESTATUS < ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(" ) ");

        appendQuery(" or (va.ESTATUS = ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(" and vb.ESTATUS < ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(" ) ");
        appendQuery(" or (va.ESTATUS < ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(" and vb.ESTATUS = ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(" ) ");

        appendQuery(" or (va.ESTATUS < ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(" and vb.ESTATUS = ").append(Constantes.ESTATUS_VIAJE_FINALIZAR).append(" )) ");

        if (intercepcionID > 0) {
            appendQuery(" AND i.ID = ").append(intercepcionID);
        }

        List<Object[]> l = em.createNativeQuery(query.toString()).getResultList();

        if (l != null) {
            lv = new ArrayList<GrIntercepcionVO>();
            for (Object[] objects : l) {
                GrIntercepcionVO o = new GrIntercepcionVO();
                o.setId((Integer) objects[0]);
                o.setViajeA(sgViajeRemote.buscarPorId((Integer) objects[1], true, true, false));
                o.setViajeB(sgViajeRemote.buscarPorId((Integer) objects[2], true, true, false));
                if ((Integer) objects[3] != null && (Integer) objects[3] > 0) {
                    o.setPuntoSeguridadID((Integer) objects[3]);
                }
                if ((String) objects[4] != null && !((String) objects[4]).isEmpty()) {
                    o.setPuntoSeguridadNombre((String) objects[4]);
                }
                lv.add(o);
            }
        }
        return lv;
    }

    
    public List<GrIntercepcionVO> traerViajesPorInterceptarSimp(int intercepcionID) {
        UtilLog4j.log.info(this, "traer todo los viajes por interceptar");
        List<GrIntercepcionVO> lv = null;
        clearQuery();
        appendQuery(" select i.ID, i.SG_VIAJE_A, i.SG_VIAJE_B, i.GR_PUNTO, p.NOMBRE, ");
        appendQuery(" rta.nombre, ra.nombre, ra.telefono,vvva.numero_placa,mara.NOMBRE, moa.nombre, teva.NOMBRE,va.fecha_programada, va.hora_programada, va.fecha_salida, va.hora_salida, ");
        appendQuery(" rtb.nombre, rb.nombre, rb.telefono,vvvb.numero_placa,marb.NOMBRE, mob.nombre, tevb.NOMBRE,vb.fecha_programada, vb.hora_programada, vb.fecha_salida, vb.hora_salida, ");
        appendQuery(" (SELECT count(vvv.ID)  ");
        appendQuery(" FROM SG_VIAJERO vvv  ");
        appendQuery(" inner join SG_VIAJE via on vvv.SG_VIAJE = via.ID  ");
        appendQuery(" WHERE vvv.sg_Viaje = va.id  ");
        appendQuery(" AND vvv.eliminado = false  ");
        appendQuery(" AND Vvv.ID not in  ");
        appendQuery(" (  ");
        appendQuery(" select  ");
        appendQuery(" vvvr.ID  ");
        appendQuery(" from SG_VIAJE navva  ");
        appendQuery(" inner join SG_VIAJERO vvvr on vvvr.SG_VIAJE = navva.id and navva.ELIMINADO = false  ");
        appendQuery(" inner join SG_VIAJERO_SI_MOVIMIENTO vvvv on vvvv.SG_VIAJERO = vvvr.id    and vvvv.ELIMINADO = false  ");
        appendQuery(" inner join SI_MOVIMIENTO m on m.id = vvvv.SI_MOVIMIENTO and m.ELIMINADO = false and m.SI_OPERACION = 24  ");
        appendQuery(" where navva.id = va.id  ");
        appendQuery(" )) as numViajerosA, ");
        appendQuery(" (SELECT count(vvv.ID)  ");
        appendQuery(" FROM SG_VIAJERO vvv  ");
        appendQuery(" inner join SG_VIAJE via on vvv.SG_VIAJE = via.ID  ");
        appendQuery(" WHERE vvv.sg_Viaje = vb.id  ");
        appendQuery(" AND vvv.eliminado = false  ");
        appendQuery(" AND Vvv.ID not in  ");
        appendQuery(" (  ");
        appendQuery(" select  ");
        appendQuery(" vvvr.ID  ");
        appendQuery(" from SG_VIAJE navva  ");
        appendQuery(" inner join SG_VIAJERO vvvr on vvvr.SG_VIAJE = navva.id and navva.ELIMINADO = false  ");
        appendQuery(" inner join SG_VIAJERO_SI_MOVIMIENTO vvvv on vvvv.SG_VIAJERO = vvvr.id    and vvvv.ELIMINADO = false  ");
        appendQuery(" inner join SI_MOVIMIENTO m on m.id = vvvv.SI_MOVIMIENTO and m.ELIMINADO = false and m.SI_OPERACION = 24  ");
        appendQuery(" where navva.id = vb.id  ");
        appendQuery(" )) as numViajerosB, va.codigo, vb.codigo ");
        appendQuery(" from GR_INTERSECCION i ");
        appendQuery(" inner join SG_VIAJE va on va.id = i.SG_VIAJE_A and va.ESTATUS in (").append(Constantes.ESTATUS_VIAJE_POR_SALIR).append(" , ").append(Constantes.ESTATUS_VIAJE_PROCESO).append(" , ").append(Constantes.ESTATUS_VIAJE_FINALIZAR).append(" , ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(" ) ");
        appendQuery(" left join usuario ra on ra.id = va.responsable ");
        appendQuery(" left join sg_ruta_terrestre rta on rta.id = va.sg_ruta_terrestre ");
        appendQuery(" left join sg_viaje_vehiculo vva on vva.sg_viaje = va.id ");
        appendQuery(" left join sg_vehiculo vvva on vvva.id = vva.sg_vehiculo ");
        appendQuery(" left join SG_MARCA mara on vvva.SG_MARCA = mara.ID ");
        appendQuery(" left join SG_MODELO moa on vvva.SG_MODELO = moa.id ");
        appendQuery(" left join SG_TIPO_ESPECIFICO teva on vvva.SG_TIPO_ESPECIFICO = teva.ID ");
        appendQuery(" inner join SG_VIAJE vb on vb.id = i.SG_VIAJE_B and vb.ESTATUS in (").append(Constantes.ESTATUS_VIAJE_POR_SALIR).append(" , ").append(Constantes.ESTATUS_VIAJE_PROCESO).append(" , ").append(Constantes.ESTATUS_VIAJE_FINALIZAR).append(" , ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(" ) ");
        appendQuery(" left join usuario rb on rb.id = vb.responsable ");
        appendQuery(" left join sg_ruta_terrestre rtb on rtb.id = vb.sg_ruta_terrestre ");
        appendQuery(" left join sg_viaje_vehiculo vvb on vvb.sg_viaje = vb.id ");
        appendQuery(" left join sg_vehiculo vvvb on vvvb.id = vvb.sg_vehiculo ");
        appendQuery(" left join SG_MARCA marb on vvvb.SG_MARCA = marb.ID ");
        appendQuery(" left join SG_MODELO mob on vvvb.SG_MODELO = mob.id ");
        appendQuery(" left join SG_TIPO_ESPECIFICO tevb on vvvb.SG_TIPO_ESPECIFICO = tevb.ID ");
        appendQuery(" left join GR_PUNTO p on p.id = i.GR_PUNTO and p.ELIMINADO = 'False' ");
        appendQuery(" where i.ELIMINADO = 'False' ");
        appendQuery(" and ((va.ESTATUS = ").append(Constantes.ESTATUS_VIAJE_FINALIZAR).append(" and vb.ESTATUS < ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(") ");
        appendQuery(" or (va.ESTATUS < ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(" and vb.ESTATUS < ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(" ) ");

        appendQuery(" or (va.ESTATUS = ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(" and vb.ESTATUS < ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(" ) ");
        appendQuery(" or (va.ESTATUS < ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(" and vb.ESTATUS = ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(" ) ");

        appendQuery(" or (va.ESTATUS < ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(" and vb.ESTATUS = ").append(Constantes.ESTATUS_VIAJE_FINALIZAR).append(" )) ");

        if (intercepcionID > 0) {
            appendQuery(" AND i.ID = ").append(intercepcionID);
        }

        List<Object[]> l = em.createNativeQuery(query.toString()).getResultList();

        if (l != null) {
            lv = new ArrayList<GrIntercepcionVO>();
            for (Object[] objects : l) {
                GrIntercepcionVO o = new GrIntercepcionVO();
                o.setId((Integer) objects[0]);
//                o.setViajeA(sgViajeRemote.buscarPorId((Integer) objects[1], true, true, false));
//                o.setViajeB(sgViajeRemote.buscarPorId((Integer) objects[2], true, true, false));
                if ((Integer) objects[3] != null && (Integer) objects[3] > 0) {
                    o.setPuntoSeguridadID((Integer) objects[3]);
                }
                if ((String) objects[4] != null && !((String) objects[4]).isEmpty()) {
                    o.setPuntoSeguridadNombre((String) objects[4]);
                }
                o.setViajeA(new ViajeVO());
                o.getViajeA().setId((Integer) objects[1]);
                o.getViajeA().setCodigo((String) objects[29]);
                o.getViajeA().setRuta((String) objects[5]);
                o.getViajeA().setResponsable((String) objects[6]);
                o.getViajeA().setResponsableTel((String) objects[7]);
                o.getViajeA().setVehiculoVO(new VehiculoVO());
                o.getViajeA().getVehiculoVO().setModelo((String) objects[10]);
                o.getViajeA().getVehiculoVO().setNumeroPlaca((String) objects[8]);
                o.getViajeA().setFechaProgramada((Date) objects[12]);
                o.getViajeA().setHoraProgramada((Date) objects[13]);
                o.getViajeA().setFechaSalida((Date) objects[14]);
                o.getViajeA().setHoraSalida((Date) objects[15]);
                o.getViajeA().setNumViajeros(((Long) objects[27]).intValue());

                o.setViajeB(new ViajeVO());
                o.getViajeB().setId((Integer) objects[2]);
                o.getViajeB().setCodigo((String) objects[30]);
                o.getViajeB().setRuta((String) objects[16]);
                o.getViajeB().setResponsable((String) objects[17]);
                o.getViajeB().setResponsableTel((String) objects[18]);
                o.getViajeB().setVehiculoVO(new VehiculoVO());
                o.getViajeB().getVehiculoVO().setModelo((String) objects[21]);
                o.getViajeB().getVehiculoVO().setNumeroPlaca((String) objects[19]);
                o.getViajeB().setFechaProgramada((Date) objects[23]);
                o.getViajeB().setHoraProgramada((Date) objects[24]);
                o.getViajeB().setFechaSalida((Date) objects[25]);
                o.getViajeB().setHoraSalida((Date) objects[26]);
                o.getViajeB().setNumViajeros(((Long) objects[28]).intValue());
                lv.add(o);
            }
        }
        return lv;
    }

    
    public GrInterseccion guardarPS(int grInterseccionID, int pSeguridadID, String usuario) {
        GrInterseccion nuevo = null;
        try {
            nuevo = this.find(grInterseccionID);
            if (nuevo != null && nuevo.getId() > 0 && pSeguridadID > 0) {
                nuevo.setGrPunto(grPuntoRemote.find(pSeguridadID));
                nuevo.setModifico(new Usuario(usuario));
                nuevo.setFechaModifico(new Date());
                nuevo.setHoraModifico(new Date());
                this.edit(nuevo);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            nuevo = null;
        }
        return nuevo;
    }

    
    public boolean viajeEnInterceptar(int viajeID) {
        UtilLog4j.log.info(this, "traer todo los viajes por interceptar");
        boolean v = false;
        clearQuery();
        appendQuery(" select id ");
        appendQuery(" from GR_INTERSECCION  ");
        appendQuery(" where (SG_VIAJE_A = ").append(viajeID).append(" or SG_VIAJE_B = ").append(viajeID).append(") ");

        List<Object[]> l = em.createNativeQuery(query.toString()).getResultList();

        if (l != null && l.size() > 0) {
            v = true;
        }
        return v;
    }

    
    public int viajeEnInterceptarRest(int intID, int viajeID) {
        UtilLog4j.log.info(this, "traer todo los viajes por interceptar");
        int idViajeRest = 0;
        clearQuery();
        appendQuery(" select sg_viaje_a, sg_viaje_b ");
        appendQuery(" from GR_INTERSECCION  ");
        appendQuery(" where id = ").append(intID).append(" ");

        List<Object[]> l = em.createNativeQuery(query.toString()).getResultList();

        if (l != null && l.size() > 0) {

            if ((Integer) l.get(0)[0] != null && (Integer) l.get(0)[1] != null) {
                int viajeA = 0;
                int viajeB = 0;
                if ((Integer) l.get(0)[0] > 0) {
                    viajeA = (Integer) l.get(0)[0];
                }
                if ((Integer) l.get(0)[1] > 0) {
                    viajeB = (Integer) l.get(0)[1];
                }
                if(viajeA == viajeID){
                    idViajeRest = viajeB;
                } else {
                    idViajeRest = viajeA;
                }                
            }
        }
        return idViajeRest;
    }

    
    public List<ItinerarioTerrestreVO> traerItinerarioIntercecciones(int intercepcionID, List<GrPuntoVO> puntos) {
        UtilLog4j.log.info(this, "traer todo los viajes por interceptar");
        List<ItinerarioTerrestreVO> lv = null;
        clearQuery();
        appendQuery(" select i.ID, i.SG_VIAJE_A, i.SG_VIAJE_B, i.GR_PUNTO, p.NOMBRE ");
        appendQuery(" from GR_INTERSECCION i ");
        appendQuery(" inner join SG_VIAJE va on va.id = i.SG_VIAJE_A and va.ESTATUS in (").append(Constantes.ESTATUS_VIAJE_POR_SALIR).append(" , ").append(Constantes.ESTATUS_VIAJE_PROCESO).append(" , ").append(Constantes.ESTATUS_VIAJE_FINALIZAR).append(" , ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(" ) ");
        appendQuery(" inner join SG_VIAJE vb on vb.id = i.SG_VIAJE_B and vb.ESTATUS in (").append(Constantes.ESTATUS_VIAJE_POR_SALIR).append(" , ").append(Constantes.ESTATUS_VIAJE_PROCESO).append(" , ").append(Constantes.ESTATUS_VIAJE_FINALIZAR).append(" , ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(" ) ");
        appendQuery(" left join GR_PUNTO p on p.id = i.GR_PUNTO and p.ELIMINADO = 'False' ");
        appendQuery(" where i.ELIMINADO = 'False' ");
        appendQuery(" and ((va.ESTATUS = ").append(Constantes.ESTATUS_VIAJE_FINALIZAR).append(" and vb.ESTATUS < ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(") ");
        appendQuery(" or (va.ESTATUS < ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(" and vb.ESTATUS < ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(" ) ");

        appendQuery(" or (va.ESTATUS = ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(" and vb.ESTATUS < ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(" ) ");
        appendQuery(" or (va.ESTATUS < ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(" and vb.ESTATUS = ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(" ) ");

        appendQuery(" or (va.ESTATUS < ").append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(" and vb.ESTATUS = ").append(Constantes.ESTATUS_VIAJE_FINALIZAR).append(" )) ");

        if (intercepcionID > 0) {
            appendQuery(" AND i.ID = ").append(intercepcionID);
        }

        List<Object[]> l = em.createNativeQuery(query.toString()).getResultList();

        if (l != null) {
            lv = new ArrayList<ItinerarioTerrestreVO>();
            for (Object[] objects : l) {
                ItinerarioTerrestreVO o = new ItinerarioTerrestreVO();
                o.setId((Integer) objects[0]);
                if ((Integer) objects[3] != null && (Integer) objects[3] > 0) {
                    o.setIdPS((Integer) objects[3]);
                }
                if ((String) objects[4] != null && !((String) objects[4]).isEmpty()) {
                    o.setNombrePS((String) objects[4]);
                }
                ViajeVO voA = sgViajeRemote.buscarPorId((Integer) objects[1], true, true, true);
                ViajeVO voB = sgViajeRemote.buscarPorId((Integer) objects[2], true, true, true);
                if (Constantes.ID_OFICINA_TORRE_MARTEL == voA.getIdOficinaOrigen()) {
                    o.setViajeMTY(voA);
                    o.getViajeMTY().setIndicePSvalor(o.getIdPS(), puntos);
                } else if (Constantes.ID_OFICINA_REY_PRINCIPAL == voA.getIdOficinaOrigen()) {
                    o.setViajeREY(voA);
                    o.getViajeREY().setIndicePSvalor(o.getIdPS(), puntos);
                } else if (Constantes.ID_OFICINA_SAN_FERNANDO == voA.getIdOficinaOrigen()) {
                    o.setViajeSF(voA);
                    o.getViajeSF().setIndicePSvalor(o.getIdPS(), puntos);
                }
                if (Constantes.ID_OFICINA_TORRE_MARTEL == voB.getIdOficinaOrigen()) {
                    o.setViajeMTY(voB);
                    o.getViajeMTY().setIndicePSvalor(o.getIdPS(), puntos);
                } else if (Constantes.ID_OFICINA_REY_PRINCIPAL == voB.getIdOficinaOrigen()) {
                    o.setViajeREY(voB);
                    o.getViajeREY().setIndicePSvalor(o.getIdPS(), puntos);
                } else if (Constantes.ID_OFICINA_SAN_FERNANDO == voB.getIdOficinaOrigen()) {
                    o.setViajeSF(voB);
                    o.getViajeSF().setIndicePSvalor(o.getIdPS(), puntos);
                }
                lv.add(o);
            }
        }
        return lv;
    }

    
    public GrInterseccion findInterseccionBySV(int sv) {
        try {
            StringBuilder sb = new StringBuilder();

            sb.append("SELECT a.ID, a.SG_VIAJE_A, a.SG_VIAJE_B,  case when a.GR_PUNTO is null then 0 else a.GR_PUNTO end as gr_punto"
                    + " FROM GR_INTERSECCION a"
                    + " WHERE a.SG_VIAJE_A = ? OR a.SG_VIAJE_B = ? AND a.ELIMINADO = ? "
                    + " order  by a.ID DESC limit 1 ");
            Object[] ob = (Object[]) em.createNativeQuery(sb.toString())
                    .setParameter(1, sv)
                    .setParameter(2, sv)
                    .setParameter(3, Constantes.NO_ELIMINADO)
                    .getSingleResult();
            GrInterseccion i = new GrInterseccion();
            i.setId((Integer) ob[0]);
            i.setSgViajeA(sgViajeRemote.find((Integer) ob[1]));
            i.setSgViajeB(sgViajeRemote.find((Integer) ob[2]));
            i.setGrPunto(grPuntoRemote.find((Integer) ob[3]));
            return i;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            return null;
        }

    }
}
