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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.GrPunto;
import sia.modelo.Usuario;
import sia.modelo.gr.vo.GrPuntoVO;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Stateless 
public class GrPuntoImpl extends AbstractFacade<GrPunto> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GrPuntoImpl() {
        super(GrPunto.class);
    }

    
    public List<GrPuntoVO> getPuntos() {
        List<GrPuntoVO> puntos = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" SELECT ID,NOMBRE,DESCRIPCION,GENERO,FECHA_GENERO,HORA_GENERO,ELIMINADO,MODIFICO,FECHA_MODIFICO,HORA_MODIFICO ");
            sb.append(" FROM GR_PUNTO ");
            //sb.append(" where ELIMINADO = 'False' ");
            UtilLog4j.log.info(this, "Q: : : : : : : : : : " + sb.toString());

            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null) {
                puntos = new ArrayList<GrPuntoVO>();
                for (Object[] objects : lo) {
                    puntos.add(castPunto(objects));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            puntos = null;
        }
        return puntos;
    }
    
     
    public List<SelectItem> getPuntosItems(int idViaje) {
        List<SelectItem> puntos = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" select DISTINCT p.ID,p.NOMBRE,p.DESCRIPCION,p.GENERO,p.FECHA_GENERO,p.HORA_GENERO,p.ELIMINADO,p.MODIFICO,p.FECHA_MODIFICO,p.HORA_MODIFICO "
                    + " from SG_VIAJE a "
                    + " inner join SG_RUTA_TERRESTRE r on r.id = a.SG_RUTA_TERRESTRE and r.ELIMINADO = 'False' "
                    + " inner join GR_RUTAS_ZONAS rz on rz.SG_RUTA_TERRESTRE = r.id and rz.ELIMINADO = 'False' "
                    + " inner join GR_PUNTO p on p.id = rz.GR_PUNTO and p.ELIMINADO = 'False' ");
            
            if(idViaje > 0){
                    sb.append(" where a.id = ").append(idViaje);
            }
            
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null) {
                puntos = new ArrayList<SelectItem>();
                for (Object[] objects : lo) {
                    puntos.add(castPuntoItem(objects));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            puntos = null;
        }
        return puntos;
    }
    
    
    public List<SelectItem> getPuntosItems() {
        List<SelectItem> puntos = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" select p.ID,p.NOMBRE,p.DESCRIPCION,p.GENERO,p.FECHA_GENERO,p.HORA_GENERO,p.ELIMINADO,p.MODIFICO,p.FECHA_MODIFICO,p.HORA_MODIFICO "
                    + " from GR_PUNTO p "
                    + " where p.eliminado = false ");
            
            
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null) {
                puntos = new ArrayList<SelectItem>();
                for (Object[] objects : lo) {
                    puntos.add(castPuntoItem(objects));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            puntos = null;
        }
        return puntos;
    }
        
    
    public GrPuntoVO getPunto(int id) {
        GrPuntoVO punto = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" SELECT ID,NOMBRE,DESCRIPCION,GENERO,FECHA_GENERO,HORA_GENERO,ELIMINADO,MODIFICO,FECHA_MODIFICO,HORA_MODIFICO ");
            sb.append(" FROM GR_PUNTO ");
            sb.append(" where ");
            sb.append(" ID = ").append(id).append(" ");
            UtilLog4j.log.info(this, "Q: : : : : : : : : : " + sb.toString());

            Object[] obj = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();
            if (obj != null) {
                punto = castPunto(obj);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            punto = null;
        }
        return punto;
    }

    private GrPuntoVO castPunto(Object[] obj) {
        GrPuntoVO o = new GrPuntoVO();
        o.setId((Integer) obj[0]);
        o.setNombre((String) obj[1]);
        o.setDescripcion((String) obj[2]);
        o.setFechaGenero((Date) obj[4]);
        o.setHoraGenero((Date) obj[5]);
        o.setActiva(!(Boolean) obj[6]);
        return o;
    }

    private SelectItem castPuntoItem(Object[] obj) {
        return new SelectItem((Integer) obj[0], (String) obj[1]);
    }

    
    public GrPunto crearPunto(GrPuntoVO punto, String usuarioID) throws Exception{
        GrPunto nuevo = null;
        try {
            if (punto.getId() == 0) {
                nuevo = new GrPunto();
                nuevo.setNombre(punto.getNombre());
                nuevo.setDescripcion(punto.getDescripcion());
                nuevo.setGenero(new Usuario(usuarioID));
                nuevo.setFechaGenero(new Date());
                nuevo.setHoraGenero(new Date());
                nuevo.setEliminado(Constantes.BOOLEAN_FALSE);
                this.create(nuevo);
            } else {
                nuevo = this.find(punto.getId());
                if (punto.getNombre() != null && !punto.getNombre().isEmpty() && !punto.getNombre().equals(nuevo.getNombre())) {
                    nuevo.setNombre(punto.getNombre());
                }
                if (punto.getDescripcion() != null && !punto.getDescripcion().isEmpty() && !punto.getDescripcion().equals(nuevo.getDescripcion())) {
                    nuevo.setDescripcion(punto.getDescripcion());
                }
                if (!nuevo.isEliminado() && !punto.isActiva()) {
                    if(this.hasRefZonas(nuevo.getId())){
                        throw new Exception("hasRefZonas");
                    }else{
                        nuevo.setEliminado(Constantes.BOOLEAN_TRUE);
                    }                    
                } else if (nuevo.isEliminado() && punto.isActiva()) {
                    nuevo.setEliminado(Constantes.BOOLEAN_FALSE);
                }
                this.edit(nuevo);
            }

        } catch (Exception e) {
            if("hasRefZonas".equals(e.getMessage())){
                throw new Exception("hasRefZonas");
            }else{
                UtilLog4j.log.fatal(this, e);
                nuevo = null;
            }
            
        }
        return nuevo;
    }
    
    private boolean hasRefZonas(int idPunto) {
        boolean ret = false;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" SELECT ID");
            sb.append(" FROM GR_RUTAS_ZONAS ");
            sb.append(" where ELIMINADO = 'False' ");
            sb.append(" and GR_PUNTO = ").append(idPunto);
            UtilLog4j.log.info(this, "Q: : : : : : : : : : " + sb.toString());

            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null && lo.size() > 0) {
                ret = true;
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);            
        }
        return ret;
    }

}
