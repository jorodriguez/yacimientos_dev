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
import sia.modelo.GrSitio;
import sia.modelo.Usuario;
import sia.modelo.gr.vo.GrSitioVO;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Stateless 
public class GrSitioImpl extends AbstractFacade<GrSitio> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GrSitioImpl() {
        super(GrSitio.class);
    }

    
    public List<GrSitioVO> getSitios(boolean todos) {
        List<GrSitioVO> sitios = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" SELECT ID,NOMBRE,DESCRIPCION,LIGA,FECHA_GENERO,HORA_GENERO,ELIMINADO,MODIFICO,FECHA_MODIFICO,HORA_MODIFICO ");
            sb.append(" FROM GR_SITIO ");
            if(!todos){
                sb.append(" where ELIMINADO = 'False' ");
            }
            sb.append(" ORDER BY ID DESC ");
            UtilLog4j.log.info(this, "Q: : : : : : : : : : " + sb.toString());

            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null) {
                sitios = new ArrayList<GrSitioVO>();
                for (Object[] objects : lo) {
                    sitios.add(castSitio(objects));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            sitios = null;
        }
        return sitios;
    }

    
    public List<SelectItem> getSitiosItems() {
        List<SelectItem> sitios = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" SELECT ID,NOMBRE,DESCRIPCION,GENERO,FECHA_GENERO,HORA_GENERO,ELIMINADO,MODIFICO,FECHA_MODIFICO,HORA_MODIFICO ");
            sb.append(" FROM GR_SITIO ");
            sb.append(" where ELIMINADO = 'False' ");
            UtilLog4j.log.info(this, "Q: : : : : : : : : : " + sb.toString());

            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null) {
                sitios = new ArrayList<SelectItem>();
                for (Object[] objects : lo) {
                    sitios.add(castSitioItem(objects));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            sitios = null;
        }
        return sitios;
    }

    
    public GrSitioVO getSitio(int id) {
        GrSitioVO sitio = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" SELECT ID,NOMBRE,DESCRIPCION,LIGA,FECHA_GENERO,HORA_GENERO,ELIMINADO,MODIFICO,FECHA_MODIFICO,HORA_MODIFICO ");
            sb.append(" FROM GR_SITIO ");
            sb.append(" where ");
            sb.append(" ID = ").append(id).append(" ");
            UtilLog4j.log.info(this, "Q: : : : : : : : : : " + sb.toString());

            Object[] obj = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();
            if (obj != null) {
                sitio = castSitio(obj);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            sitio = null;
        }
        return sitio;
    }

    private GrSitioVO castSitio(Object[] obj) {
        GrSitioVO o = new GrSitioVO();
        o.setId((Integer) obj[0]);
        o.setNombre((String) obj[1]);
        o.setDescripcion((String) obj[2]);
        o.setLiga((String) obj[3]);
        o.setFech_Genero((Date) obj[4]);
        o.setHora_Genero((Date) obj[5]);
        o.setActivo(!(Boolean) obj[6]);
        return o;
    }

    private SelectItem castSitioItem(Object[] obj) {
        return new SelectItem((Integer) obj[0], (String) obj[1]);
    }

    
    public GrSitio crearSitio(GrSitioVO sitio, String usuarioID) {
        GrSitio nuevo = null;
        try {
            if (sitio.getId() == 0) {
                nuevo = new GrSitio();
                nuevo.setNombre(sitio.getNombre());
                nuevo.setDescripcion(sitio.getDescripcion());
                nuevo.setLiga(sitio.getLiga());
                nuevo.setGenero(new Usuario(usuarioID));
                nuevo.setFechaGenero(new Date());
                nuevo.setHoraGenero(new Date());
                nuevo.setEliminado(Constantes.BOOLEAN_FALSE);
                this.create(nuevo);
            } else {
                nuevo = this.find(sitio.getId());
                if (sitio.getNombre() != null && !sitio.getNombre().isEmpty() && !sitio.getNombre().equals(nuevo.getNombre())) {
                    nuevo.setNombre(sitio.getNombre());
                }
                if (sitio.getDescripcion() != null && !sitio.getDescripcion().isEmpty() && !sitio.getDescripcion().equals(nuevo.getDescripcion())) {
                    nuevo.setDescripcion(sitio.getDescripcion());
                }
                if (sitio.getLiga() != null && !sitio.getLiga().isEmpty() && !sitio.getLiga().equals(nuevo.getLiga())) {
                    nuevo.setLiga(sitio.getLiga());
                }
                if (!nuevo.isEliminado() && !sitio.isActivo()) {
                    nuevo.setEliminado(Constantes.BOOLEAN_TRUE);
                } else if (nuevo.isEliminado() && sitio.isActivo()) {
                    nuevo.setEliminado(Constantes.BOOLEAN_FALSE);
                }
                this.edit(nuevo);
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            nuevo = null;
        }
        return nuevo;
    }

}
