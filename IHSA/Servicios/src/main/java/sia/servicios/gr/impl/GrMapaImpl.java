/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.gr.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.GrMapa;
import sia.modelo.SgEstadoSemaforo;
import sia.modelo.SgSemaforo;
import sia.modelo.Usuario;
import sia.modelo.gr.vo.MapaVO;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sgl.semaforo.impl.SgEstadoSemaforoImpl;
import sia.servicios.sgl.semaforo.impl.SgSemaforoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Stateless 
public class GrMapaImpl extends AbstractFacade<GrMapa> {

    @Inject
    private SgEstadoSemaforoImpl sgEstadoSemaforoRemote;
    @Inject
    private SgSemaforoImpl sgSemaforoRemote;

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GrMapaImpl() {
        super(GrMapa.class);
    }

    
    public List<MapaVO> getMapas(String codigo) {
        List<MapaVO> mapas = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" SELECT ID,NOMBRE,DESCRIPCION,GENERO,FECHA_GENERO,HORA_GENERO,ELIMINADO,MODIFICO,FECHA_MODIFICO,HORA_MODIFICO,VISIBLE, CODIGO ");
            sb.append(" FROM GR_MAPA ");
            //sb.append(" where ELIMINADO = 'False' ");
            if(codigo != null && !codigo.isEmpty()){
                sb.append(" where upper(CODIGO) = upper('").append(codigo).append("')");
            }
            sb.append(" order by CODIGO ");
            UtilLog4j.log.info(this, "Q: : : : : : : : : : " + sb.toString());

            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null) {
                mapas = new ArrayList<MapaVO>();
                for (Object[] objects : lo) {
                    mapas.add(castMapa(objects));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            mapas = null;
        }
        return mapas;
    }
    
    
    public List<MapaVO> getMapasMenu() {
        List<MapaVO> mapas = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" SELECT ID,NOMBRE,DESCRIPCION,GENERO,FECHA_GENERO,HORA_GENERO,ELIMINADO,MODIFICO,FECHA_MODIFICO,HORA_MODIFICO,VISIBLE, CODIGO ");
            sb.append(" FROM GR_MAPA ");
            sb.append(" where ELIMINADO = 'False' ");
            sb.append(" and VISIBLE = 'True' ");
            sb.append(" and ID in (select GR_MAPA from GR_ARCHIVO where ELIMINADO = 'False') ");
            UtilLog4j.log.info(this, "Q: : : : : : : : : : " + sb.toString());

            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null) {
                mapas = new ArrayList<MapaVO>();
                for (Object[] objects : lo) {
                    mapas.add(castMapa(objects));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            mapas = null;
        }
        return mapas;
    }

    
    public List<SelectItem> getMapasItems(boolean all, boolean visible) {
        List<SelectItem> mapas = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" SELECT ID,NOMBRE,DESCRIPCION,GENERO,FECHA_GENERO,HORA_GENERO,ELIMINADO,MODIFICO,FECHA_MODIFICO,HORA_MODIFICO, CODIGO ");
            sb.append(" FROM GR_MAPA ");
            sb.append(" where ELIMINADO = 'False' ");
            if(!all){
                sb.append(" and VISIBLE = '").append(visible ? Constantes.BOOLEAN_TRUE:Constantes.BOOLEAN_FALSE).append("' ");
            }
            sb.append(" order by CODIGO ");
            UtilLog4j.log.info(this, "Q: : : : : : : : : : " + sb.toString());

            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null) {
                mapas = new ArrayList<SelectItem>();
                for (Object[] objects : lo) {
                    mapas.add(castMapaItem(objects));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            mapas = null;
        }
        return mapas;
    }
    
    
    public List<SelectItem> getZonasRutaItems(int rutaID) {
        List<SelectItem> mapas = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" SELECT ID,NOMBRE,DESCRIPCION,GENERO,FECHA_GENERO,HORA_GENERO,ELIMINADO,MODIFICO,FECHA_MODIFICO,HORA_MODIFICO, CODIGO ");
            sb.append(" FROM GR_MAPA ");
            sb.append(" where ELIMINADO = 'False' ");
            sb.append(" and ID not in (select GR_MAPA from GR_RUTAS_ZONAS where ELIMINADO = 'False' and SG_RUTA_TERRESTRE = ").append(rutaID).append(") ");
            UtilLog4j.log.info(this, "Q: : : : : : : : : : " + sb.toString());
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null) {
                mapas = new ArrayList<SelectItem>();
                for (Object[] objects : lo) {
                    mapas.add(castMapaItem(objects));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            mapas = null;
        }
        return mapas;
    }

    
    public MapaVO getMapa(int id) {
        MapaVO mapa = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" SELECT ID,NOMBRE,DESCRIPCION,GENERO,FECHA_GENERO,HORA_GENERO,ELIMINADO,MODIFICO,FECHA_MODIFICO,HORA_MODIFICO,VISIBLE, CODIGO ");
            sb.append(" FROM GR_MAPA ");
            sb.append(" where ");
            sb.append(" ID = ").append(id).append(" ");
            UtilLog4j.log.info(this, "Q: : : : : : : : : : " + sb.toString());

            Object[] obj = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();
            if (obj != null) {
                mapa = castMapa(obj);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            mapa = null;
        }
        return mapa;
    }

    private MapaVO castMapa(Object[] obj) {
        MapaVO o = new MapaVO();
        o.setId((Integer) obj[0]);
        o.setNombre((String) obj[1]);
        o.setDescripcion((String) obj[2]);
        o.setFechaGenero((Date) obj[4]);
        o.setHoraGenero((Date) obj[5]);
        o.setActiva(!(Boolean) obj[6]);
        o.setFechaModifico((Date) obj[8]);
        o.setHoraModifico((Date) obj[9]);        
        o.setVisible((Boolean) obj[10]);
        o.setCodigo((String) obj[11] != null ? (String) obj[11] : "");
        return o;
    }

    private SelectItem castMapaItem(Object[] obj) {
        return new SelectItem((Integer) obj[0], new StringBuilder().append((String) obj[10]).append("-").append((String) obj[1]).toString());
    }

    
    public GrMapa crearZona(MapaVO zona, String usuarioID) {
        GrMapa nuevo = null;
        try {
            if (zona.getId() == 0) {
                nuevo = new GrMapa();
                nuevo.setNombre(zona.getNombre());
                nuevo.setCodigo(zona.getCodigo());
                nuevo.setDescripcion(zona.getDescripcion());
                nuevo.setGenero(new Usuario(usuarioID));
                nuevo.setFechaGenero(new Date());
                nuevo.setHoraGenero(new Date());
                nuevo.setEliminado(Constantes.BOOLEAN_FALSE);                                
                nuevo.setVisible(Constantes.BOOLEAN_FALSE);
                this.create(nuevo);
                
                SgSemaforo semaforo = sgSemaforoRemote.find(Constantes.ID_COLOR_SEMAFORO_VERDE);
                SgEstadoSemaforo nuevoSemaforo = new SgEstadoSemaforo();
                nuevoSemaforo.setSgSemaforo(semaforo);
                nuevoSemaforo.setGrMapa(nuevo);
                nuevoSemaforo.setJustificacion("NUEVA ZONA");
                nuevoSemaforo.setGenero(new Usuario(usuarioID));
                nuevoSemaforo.setFechaGenero(new Date());
                nuevoSemaforo.setHoraGenero(new Date());
                nuevoSemaforo.setFechaInicio(new Date());
                nuevoSemaforo.setHoraInicio(new Date());
                nuevoSemaforo.setEliminado(Constantes.BOOLEAN_FALSE);
                sgEstadoSemaforoRemote.create(nuevoSemaforo);
                
            } else {
                nuevo = this.find(zona.getId());
                if (zona.getNombre() != null && !zona.getNombre().isEmpty() && !zona.getNombre().equals(nuevo.getNombre())) {
                    nuevo.setNombre(zona.getNombre());
                }
                if (zona.getDescripcion() != null && !zona.getDescripcion().isEmpty() && !zona.getDescripcion().equals(nuevo.getDescripcion())) {
                    nuevo.setDescripcion(zona.getDescripcion());
                }
                if (zona.getCodigo()!= null && !zona.getCodigo().isEmpty() && !zona.getCodigo().equals(nuevo.getCodigo())) {
                    nuevo.setCodigo(zona.getCodigo());
                }
                                
                nuevo.setEliminado(!zona.isActiva());
                nuevo.setVisible(zona.isVisible());                
                nuevo.setModifico(new Usuario(usuarioID));
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

}
